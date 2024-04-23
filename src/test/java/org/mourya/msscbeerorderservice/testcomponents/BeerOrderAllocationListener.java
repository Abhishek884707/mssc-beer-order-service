package org.mourya.msscbeerorderservice.testcomponents;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mourya.brewery.model.events.AllocateOrderRequest;
import org.mourya.brewery.model.events.AllocateOrderResult;
import org.mourya.msscbeerorderservice.config.JmsConfig;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BeerOrderAllocationListener {

    private final JmsTemplate jmsTemplate;

    @JmsListener(destination = JmsConfig.ALLOCATE_ORDER_QUEUE)
    public void list(Message msg) {
        AllocateOrderRequest request = (AllocateOrderRequest) msg.getPayload();

        request.getBeerOrderDto().getBeerOrderLines().forEach(beerOrderLineDto -> {
            beerOrderLineDto.setQuantityAllocated(beerOrderLineDto.getOrderQuantity());
        });

        jmsTemplate.convertAndSend(JmsConfig.ALLOCATE_ORDER_RESPONSE_QUEUE, AllocateOrderResult.builder()
                        .beerOrderDto(request.getBeerOrderDto())
                        .pendingInventory(false)
                        .allocationError(false)
                        .build()
        );

    }

}