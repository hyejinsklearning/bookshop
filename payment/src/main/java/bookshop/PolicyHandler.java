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
    PaymentRepository paymentRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void onStringEventListener(@Payload String eventString){

    }

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverOrderCanceled_Cancel(@Payload OrderCanceled orderCanceled){

        if(orderCanceled.isMe()) {
            System.out.println("##################################################");
            System.out.println("##### listener Cancel : " + orderCanceled.toJson());
            System.out.println("##################################################");
            // order canceled 이벤트 발생 시 payment cancel (pub/sub)
            Payment payment = paymentRepository.findByOrderId(orderCanceled.getId());
//            Payment payment = new Payment();
            System.out.println("##### payment id : " + payment.getId() );
            System.out.println("##### payment orderId : " + payment.getOrderId() );
            System.out.println("##### payment status : " + payment.getStatus() );

//            payment.setOrderId(orderCanceled.getId());
            payment.setStatus("PAY CANCELED");
//            payment.setQty(orderCanceled.getQty());

            paymentRepository.save(payment);
            

        }
    }

}
