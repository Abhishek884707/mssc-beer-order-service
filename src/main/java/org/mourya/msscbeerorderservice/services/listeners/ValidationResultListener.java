package org.mourya.msscbeerorderservice.services.listeners;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mourya.brewery.model.events.ValidateOrderResult;
import org.mourya.msscbeerorderservice.config.JmsConfig;
import org.mourya.msscbeerorderservice.services.BeerOrderManager;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class ValidationResultListener {

    private final BeerOrderManager beerOrderManager;

    @JmsListener(destination = JmsConfig.VALIDATE_ORDER_RESPONSE_QUEUE)
    public void listen(ValidateOrderResult validateOrderResult){
        final UUID beerOrderId = validateOrderResult.getOrderId();
        log.info("Validation Result for Order Id " + validateOrderResult.getOrderId() + " : " + validateOrderResult.isValid());

        beerOrderManager.processValidationResult(beerOrderId, validateOrderResult.isValid());
    }

}
