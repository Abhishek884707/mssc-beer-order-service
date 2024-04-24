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
        boolean sendResponse = true;

        ValidateOrderRequest request = (ValidateOrderRequest)  msg.getPayload();

        BeerOrderDto beerOrderDto = request.getBeerOrderDto();

        if(beerOrderDto.getCustomerRef() != null){
            // Condition to fail validation
            if(beerOrderDto.getCustomerRef().equals("fail-validation")){
                isValid = false;
            }else if (beerOrderDto.getCustomerRef().equals("don't-validate")){
                sendResponse = false;
            }
        }

        if(sendResponse){
            jmsTemplate.convertAndSend(JmsConfig.VALIDATE_ORDER_RESPONSE_QUEUE, ValidateOrderResult.builder()
                    .isValid(isValid)
                    .orderId(beerOrderDto.getId())
                    .build()
                );
        }


    }

}
