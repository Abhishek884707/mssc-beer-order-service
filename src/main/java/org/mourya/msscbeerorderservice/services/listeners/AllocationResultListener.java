package org.mourya.msscbeerorderservice.services.listeners;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mourya.brewery.model.BeerOrderDto;
import org.mourya.brewery.model.events.AllocateOrderResult;
import org.mourya.msscbeerorderservice.config.JmsConfig;
import org.mourya.msscbeerorderservice.services.BeerOrderManager;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class AllocationResultListener {

    private final BeerOrderManager beerOrderManager;

    @JmsListener(destination = JmsConfig.VALIDATE_ORDER_RESPONSE_QUEUE)
    public void listen(AllocateOrderResult result){
        BeerOrderDto beerOrderDto = result.getBeerOrderDto();
        log.info("Allocation Result for Order Id " + beerOrderDto.getId() + " : " + result.getAllocationError()  + " : " + result.getPendingInventory());
        if (!result.getAllocationError() && !result.getPendingInventory()){ // allocated
            beerOrderManager.beerOrderAllocationPassed(beerOrderDto);
        } else if (!result.getAllocationError() && result.getPendingInventory()) { // pending inventory
            beerOrderManager.beerOrderAllocationPendingInventory(beerOrderDto);
        }else if(result.getAllocationError()){ // failed
            beerOrderManager.beerOrderAllocationFailed(beerOrderDto);
        }
    }

}
