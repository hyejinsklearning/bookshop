package bookshop;

import bookshop.config.kafka.KafkaProcessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class PolicyHandler{

    @Autowired
    DeliveryRepository deliveryRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void onStringEventListener(@Payload String eventString){

    }

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverPayCompleted_Ship(@Payload PayCompleted payCompleted){

        if(payCompleted.isMe()){
            System.out.println("##### listener Ship : " + payCompleted.toJson());
            Delivery delivery = new Delivery();
            // pay completed 되면 delivery event 수행
            delivery.setOrderId(payCompleted.getOrderId());
            delivery.setQty(payCompleted.getQty());
            delivery.setStatus("SHIPPED");
            // payment id 있어야될까..

            deliveryRepository.save(delivery);
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverOrderCanceled_Cancel(@Payload OrderCanceled orderCanceled){

        if(orderCanceled.isMe()){
            System.out.println("##### listener Cancel : " + orderCanceled.toJson());
            // order cancel 되면 pub/sub으로 delivery cancel
            // order cancel ==> payment cancel, delivery cancel 둘다 안되고잇다..
            // 있는 항목을 update 해야된다..
//            Delivery delivery_c = new Delivery();
            Delivery delivery = deliveryRepository.findByOrderId(orderCanceled.getId());
//            delivery.setOrderId(orderCanceled.getId());
//            delivery.setQty(orderCanceled.getQty());
            delivery.setStatus("DELIVERY CANCELED");

            deliveryRepository.save(delivery);
        }
    }

}
