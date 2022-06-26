package restapp.entity;


import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import restapp.model.ShopUnitImport;

import javax.management.DescriptorKey;
import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "products")
public class ProductEntity {
    @Id
    @NonNull
    private UUID  id;
    @Column(nullable = false)
    private String type;
    @Column(nullable = false)
    private String name;
    @Column
    @Nullable
    private Long price;
    @Column
    private Date date;
    @Column
    @Nullable
    private UUID parentId;
    @Column
    private Long subTreeNodesQuantity;
    @Column
    private Long subTreeSum;


    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "product")
    List<HistoryEntity> productHistory;


    public ProductEntity() {
    }

    public ProductEntity(ShopUnitImport importProduct){
        this.id = importProduct.getId();
        this.name = importProduct.getName();
        this.type = importProduct.getType();
        this.parentId = importProduct.getParentId();
        this.price = importProduct.getPrice();
    }

    @NonNull
    public UUID getId() {
        return id;
    }

    public void setId(@NonNull UUID id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @Nullable
    public Long getPrice() {
        return price;
    }

    public void setPrice(@Nullable Long price) {
        this.price = price;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public UUID getParentId() {
        return parentId;
    }

    public void setParentId(UUID parentId) {
        this.parentId = parentId;
    }

    public boolean hasParent(){
        return parentId != null;
    }

    public Long getSubTreeNodesQuantity() {
        return subTreeNodesQuantity;
    }

    public void setSubTreeNodesQuantity(Long subTreeNodesQuantity) {
        this.subTreeNodesQuantity = subTreeNodesQuantity;
    }

    public long getSubTreeSum() {
        return subTreeSum;
    }

    public void setSubTreeSum(long subTreeSum) {
        this.subTreeSum = subTreeSum;
    }
}
