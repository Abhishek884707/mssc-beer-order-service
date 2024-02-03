package org.mourya.msscbeerorderservice.sm.action;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mourya.brewery.model.events.AllocateOrderRequest;
import org.mourya.msscbeerorderservice.config.JmsConfig;
import org.mourya.msscbeerorderservice.domain.BeerOrder;
import org.mourya.msscbeerorderservice.domain.BeerOrderEventEnum;
import org.mourya.msscbeerorderservice.domain.BeerOrderStatusEnum;
import org.mourya.msscbeerorderservice.repositories.BeerOrderRepository;
import org.mourya.msscbeerorderservice.services.BeerOrderManagerImpl;
import org.mourya.msscbeerorderservice.web.mapper.BeerOrderMapper;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AllocateOrderAction implements Action<BeerOrderStatusEnum, BeerOrderEventEnum> {

    private final JmsTemplate jmsTemplate;
    private final BeerOrderRepository beerOrderRepository;
    private final BeerOrderMapper beerOrderMapper;

    @Override
    public void execute(StateContext<BeerOrderStatusEnum, BeerOrderEventEnum> stateContext) {
        BeerOrder beerOrder = beerOrderRepository.findOneById(UUID.fromString((String)stateContext.getMessage().getHeaders().get(BeerOrderManagerImpl.ORDER_ID_HEADER)));
        jmsTemplate.convertAndSend(JmsConfig.VALIDATE_ORDER_QUEUE, AllocateOrderRequest.builder()
                .beerOrderDto(beerOrderMapper.beerOrderToDto(beerOrder))
                .build());
        log.info("Send Allocation Request for order id : " + beerOrder.getId());
    }
}
