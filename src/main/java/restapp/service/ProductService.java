package restapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import restapp.entity.HistoryEntity;
import restapp.model.ProductType;
import restapp.entity.ProductEntity;
import restapp.exception.ItemNotFoundException;
import restapp.exception.ShopUnitImportException;
import restapp.model.*;
import restapp.repository.HistoryRepo;
import restapp.repository.ProductRepo;
import restapp.util.ISO_8601;

import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepo productRepo;
    @Autowired
    private HistoryRepo historyRepo;

    public void importProducts(final ShopUnitImportRequest importRequest) throws ShopUnitImportException {

        List<ShopUnitImport> items = (List<ShopUnitImport>) importRequest.getItems();

        for(ShopUnitImport item : items){
            item.validate();
        }

        List<ProductEntity> productEntities = items.stream().map(ProductEntity::new).collect(Collectors.toList());


        Date updateDate;
        try{
            updateDate = ISO_8601.strToDate(importRequest.getUpdateDate());
        }
        catch (ParseException e){
            throw new ShopUnitImportException("Неверный формат времени");
        }

        Set<UUID> idSet = productEntities.stream().map(ProductEntity::getId).collect(Collectors.toSet());
        if(idSet.size() != productEntities.size())
            throw new ShopUnitImportException("Запрос содержит элементы с одинаковыми id");


        for(ProductEntity productEntity : productEntities){
            if(productEntity.hasParent()){
                Optional<ProductEntity> optionalproduct = productRepo.findById(productEntity.getId());
                if(optionalproduct.isPresent()){
                    ProductEntity product = optionalproduct.get();
                    if(!product.getType().equals(productEntity.getType()))
                        throw new ShopUnitImportException("Нельзя менять изменять тип элемента!");
                }
                UUID parentId = productEntity.getParentId();
                if(parentId == null)
                    continue;

                Optional<ProductEntity> optionalParent = productRepo.findById(parentId);
                ProductEntity parent;
                if(optionalParent.isPresent())
                    parent = optionalParent.get();
                else
                    parent = productEntities.stream().filter(obj->obj.getId().equals(parentId)).findFirst()
                                        .orElseThrow(()->new ShopUnitImportException("И импортируемого объекта нет родителя"));
                if(!parent.getType().equals(ProductType.CATEGORY.toString()))
                    throw new ShopUnitImportException("Импортируемый объект ссылыется на объект типа **товар** в качестве родительского");

            }
        }

        productEntities.forEach(x->x.setDate(updateDate));
        productEntities.stream().map(newProduct->productRepo.findById(newProduct.getId())).filter(Optional::isPresent).map(Optional::get)
                .map(oldProduct->historyRepo.save(new HistoryEntity(oldProduct)));
        //productEntities.forEach(this::updateDates);
        productRepo.saveAll(productEntities);
        productRepo.findAllByParentId(null).forEach(this::updatePrices);
    }

    private void updateDates(ProductEntity node){
        if(node.getParentId() == null)
            return;
        Optional <ProductEntity> optionalParentNode = productRepo.findById(node.getParentId());
        if(optionalParentNode.isPresent()){
            ProductEntity parentNode = optionalParentNode.get();
            //historyRepo.save(new HistoryEntity(parentNode));
            parentNode.setDate(node.getDate());
            productRepo.save(parentNode);
            updateDates(parentNode);
        }

    }
    private void  updatePrices(ProductEntity node){
        if(node.getType().equals(ProductType.OFFER.toString())){
            node.setSubTreeSum(node.getPrice());
            node.setSubTreeNodesQuantity(1L);
            productRepo.save(node);
            return;
        }
        List<ProductEntity> children = productRepo.findAllByParentId(node.getId());
        if(children.isEmpty()){
            node.setSubTreeNodesQuantity(0L);
            node.setSubTreeSum(0);
            productRepo.save(node);
            return;
        }

        children.forEach(this::updatePrices);

        long childrenCount = children.stream().mapToLong(ProductEntity::getSubTreeNodesQuantity).sum();
        long subTreeSum = children.stream().mapToLong(ProductEntity::getSubTreeSum).sum();
        Date maxChildrenDate = children.stream().map(ProductEntity::getDate).max(Date::compareTo).get();

        if(maxChildrenDate.after(node.getDate())){
            historyRepo.save(new HistoryEntity(node));
            node.setDate(maxChildrenDate);
        }
        node.setSubTreeNodesQuantity(childrenCount);
        node.setSubTreeSum(subTreeSum);
        node.setPrice(subTreeSum/childrenCount);
        productRepo.save(node);
    }



    private void deleteSubtree(ProductEntity node){
        if(node.getType().equals(ProductType.OFFER.toString())){
            productRepo.delete(node);
            return;
        }
        productRepo.findAllByParentId(node.getId()).forEach(this::deleteSubtree);
        productRepo.delete(node);
    }

    public void deleteProduct(UUID id) throws Exception {
        ProductEntity root = productRepo.findById(id).orElseThrow(()->new ItemNotFoundException("Item: " + id.toString() + " not found"));
        deleteSubtree(root);
        productRepo.findAllByParentId(null).forEach(this::updatePrices);
    }


    private void setChildrenToShopUnit(ShopUnit shopUnit){
        shopUnit.setChildren(productRepo.findAllByParentId(shopUnit.getId()).stream().map(ShopUnit::new).collect(Collectors.toList()));
        if(shopUnit.getChildren().isEmpty()) {
            if (shopUnit.getType().equals(ProductType.OFFER.toString()))
                shopUnit.setChildren(null);
        }
        else{
            shopUnit.getChildren().forEach(this::setChildrenToShopUnit);
        }
    }
    public ShopUnit getProductNodeInfo(UUID id) throws Exception {
        ProductEntity root = productRepo.findById(id).orElseThrow(()->new ItemNotFoundException("Item: " + id.toString() + " not found"));
        //updatePrices(root);
        ShopUnit rootUnit = new ShopUnit(root);
        setChildrenToShopUnit(rootUnit);
        return rootUnit;
    }

    public ShopUnitStatisticResponse getSales(Date beforeDate){
        Date afterDate = ISO_8601.getPrevDay(beforeDate);
        List<ProductEntity> list = productRepo.findAllByDateBetween(afterDate, beforeDate);
        return new ShopUnitStatisticResponse(list.stream().map(ShopUnitStatisticUnit::new).collect(Collectors.toList()));
    }

    public ShopUnitStatisticResponse getStatistic(UUID id, Date afterDate, Date beforeDate) throws ItemNotFoundException {
        ProductEntity product = productRepo.findById(id).orElseThrow(()-> new ItemNotFoundException("Item: " + id.toString() + " not found"));

        beforeDate = new Date(beforeDate.getTime()-1);

        List<HistoryEntity> list = historyRepo.findAllByProductIdAndInterval(id, afterDate, beforeDate);
        List<ShopUnitStatisticUnit> items = list.stream().map(ShopUnitStatisticUnit::new).collect(Collectors.toList());

        long currentProductTime = product.getDate().getTime();
        if(afterDate.getTime() <= currentProductTime && currentProductTime <= beforeDate.getTime())
            items.add(new ShopUnitStatisticUnit(product));

        return new ShopUnitStatisticResponse(items);
    }
}
