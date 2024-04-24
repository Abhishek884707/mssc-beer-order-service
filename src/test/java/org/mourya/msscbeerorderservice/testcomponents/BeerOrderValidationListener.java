package org.mourya.msscbeerorderservice.testcomponents;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mourya.brewery.model.BeerOrderDto;
import org.mourya.brewery.model.events.ValidateOrderRequest;
import org.mourya.brewery.model.events.ValidateOrderResult;
import org.mourya.msscbeerorderservice.config.JmsConfig;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BeerOrderValidationListener {

    private final JmsTemplate jmsTemplate;

    @JmsListener(destination = JmsConfig.VALIDATE_ORDER_QUEUE)
    public void list(Message msg){

        boolean isValid = true;

        ValidateOrderRequest request = (ValidateOrderRequest)  msg.getPayload();

        // Condition to fail validation
        BeerOrderDto beerOrderDto = request.getBeerOrderDto();
        if(beerOrderDto.getCustomerRef() != null && beerOrderDto.getCustomerRef().equals("fail-validation")){
            isValid = false;
        }

        jmsTemplate.convertAndSend(JmsConfig.VALIDATE_ORDER_RESPONSE_QUEUE, ValidateOrderResult.builder()
                .isValid(isValid)
                .orderId(beerOrderDto.getId())
                .build()
            );

    }

}
