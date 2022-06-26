package restapp.entity;

import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "history")
public class HistoryEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "product_id")
    ProductEntity product;

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


    public HistoryEntity() {
    }

    public HistoryEntity(ProductEntity product) {
        this.name = product.getName();
        this.type = product.getType();
        this.price = product.getPrice();
        this.parentId = product.getParentId();
        this.date = product.getDate();
        this.product = product;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    @Nullable
    public UUID getParentId() {
        return parentId;
    }

    public void setParentId(@Nullable UUID parentId) {
        this.parentId = parentId;
    }

    public ProductEntity getProduct() {
        return product;
    }

    public void setProduct(ProductEntity product) {
        this.product = product;
    }
}
