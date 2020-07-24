package bookshop;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;

@Entity
@Table(name="Order_table")
public class Order {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private Long productId;
    private Integer qty;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }
    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    @PostPersist
    public void onPostPersist(){
        OrderPlaced orderPlaced = new OrderPlaced();
        BeanUtils.copyProperties(this, orderPlaced);
        //order placed status setting
        orderPlaced.setStatus("ORDER PLACED");
        orderPlaced.publishAfterCommit();

        //Following code causes dependency to external APIs
        // it is NOT A GOOD PRACTICE. instead, Event-Policy mapping is recommended.

        bookshop.external.Payment payment = new bookshop.external.Payment();
        // mappings goes here
        payment.setOrderId(orderPlaced.getId());
        payment.setQty(orderPlaced.getQty());
        // payment status 추가
        payment.setStatus("PAY COMPLETED");
        OrderApplication.applicationContext.getBean(bookshop.external.PaymentService.class)
            .payment(payment);


    }

    @PreRemove
    public void onPreRemove() {
        // order cancel 은 delete method
        OrderCanceled orderCanceled = new OrderCanceled();
        BeanUtils.copyProperties(this, orderCanceled);
        //order canceled status setting
        orderCanceled.setStatus("ORDER CANCELED");
        orderCanceled.publishAfterCommit();

    }


}
