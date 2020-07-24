package bookshop;

import org.springframework.data.repository.CrudRepository;

public interface DeliveryRepository extends CrudRepository<Delivery, Long> {

    Delivery findByOrderId(Long orderId);

}