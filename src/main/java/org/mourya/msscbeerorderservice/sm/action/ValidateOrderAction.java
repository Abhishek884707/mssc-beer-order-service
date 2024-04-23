package org.mourya.msscbeerorderservice.sm.action;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mourya.brewery.model.events.ValidateOrderRequest;
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

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ValidateOrderAction implements Action<BeerOrderStatusEnum, BeerOrderEventEnum> {

    private final BeerOrderRepository beerOrderRepository;
    private final BeerOrderMapper beerOrderMapper;
    private final JmsTemplate jmsTemplate;
    @Override
    public void execute(StateContext<BeerOrderStatusEnum, BeerOrderEventEnum> stateContext) {
        Object orderId = stateContext.getMessage().getHeaders().get(BeerOrderManagerImpl.ORDER_ID_HEADER);
        Optional<BeerOrder> beerOrderOptional = beerOrderRepository.findById(UUID.fromString((String) orderId));

        beerOrderOptional.ifPresentOrElse(beerOrder -> {
            jmsTemplate.convertAndSend(JmsConfig.VALIDATE_ORDER_QUEUE, ValidateOrderRequest.builder()
                    .beerOrderDto(beerOrderMapper.beerOrderToDto(beerOrder))
                    .build());
        }, () -> log.error("Order Not Found. Id " + orderId));
    }
}
