package restapp.model;

import restapp.exception.ShopUnitImportException;

import java.util.UUID;

public class ShopUnitImport {

    private UUID id;
    private String type;
    private String name;
    private UUID parentId;
    private Long price;

    public void validate() throws ShopUnitImportException {
        ProductType type;
        try {
            type = ProductType.valueOf(this.type);
        } catch (IllegalArgumentException e){
            throw new ShopUnitImportException("Задан некорректный тип продукта");
        }
        if(type.equals(ProductType.CATEGORY)){
            if(price != null)
                throw new IllegalArgumentException("Поле price для **категории** должно быть null");
        }
        if(id == parentId)
            throw new ShopUnitImportException("Объект ссылается сам на себя");
    }

    public ShopUnitImport() {
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
}
