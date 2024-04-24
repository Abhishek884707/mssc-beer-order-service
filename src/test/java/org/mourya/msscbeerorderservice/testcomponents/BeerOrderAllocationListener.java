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

        boolean isPendingInventory = false;
        boolean isAllocationError = false;

        AllocateOrderRequest request = (AllocateOrderRequest) msg.getPayload();

        // set pending inventory
        if(request.getBeerOrderDto().getCustomerRef() != null && request.getBeerOrderDto().getCustomerRef().equals("partial-allocation")){
            isPendingInventory = true;
        }

        // set allocation error
        if(request.getBeerOrderDto().getCustomerRef() != null && request.getBeerOrderDto().getCustomerRef().equals("fail-allocation")){
            isAllocationError = true;
        }


        boolean finalPendingInventory = isPendingInventory;

        request.getBeerOrderDto().getBeerOrderLines().forEach(beerOrderLineDto -> {
            if(finalPendingInventory){
                beerOrderLineDto.setQuantityAllocated(beerOrderLineDto.getOrderQuantity() - 1);
            }else{
                beerOrderLineDto.setQuantityAllocated(beerOrderLineDto.getOrderQuantity());
            }
        });

        jmsTemplate.convertAndSend(JmsConfig.ALLOCATE_ORDER_RESPONSE_QUEUE, AllocateOrderResult.builder()
                        .beerOrderDto(request.getBeerOrderDto())
                        .pendingInventory(isPendingInventory)
                        .allocationError(isAllocationError)
                        .build()
        );

    }

}