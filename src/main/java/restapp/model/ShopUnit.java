package restapp.model;

import restapp.entity.ProductEntity;
import restapp.util.ISO_8601;

import java.util.List;
import java.util.UUID;

public class ShopUnit {

    private UUID id;
    private String type;
    private String name;
    private UUID parentId;
    private Long price;
    private String date;
    private List<ShopUnit> children;

    public ShopUnit() {
    }
    public ShopUnit(ProductEntity productEntity){
        this.id = productEntity.getId();
        this.name = productEntity.getName();
        this.type = productEntity.getType();
        this.parentId = productEntity.getParentId();
        this.price = productEntity.getPrice();
        this.date = ISO_8601.dateToStr(productEntity.getDate());
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getParentId() {
        return parentId;
    }

    public void setParentId(UUID parentId) {
        this.parentId = parentId;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public List<ShopUnit> getChildren() {
        return children;
    }

    public void setChildren(List<ShopUnit> children) {
        this.children = children;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
