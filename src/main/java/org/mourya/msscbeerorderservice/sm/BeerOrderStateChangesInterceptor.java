package org.mourya.msscbeerorderservice.sm;

import lombok.extern.slf4j.Slf4j;
import org.mourya.msscbeerorderservice.domain.BeerOrder;
import org.mourya.msscbeerorderservice.domain.BeerOrderEventEnum;
import org.mourya.msscbeerorderservice.domain.BeerOrderStatusEnum;
import org.mourya.msscbeerorderservice.repositories.BeerOrderRepository;
import org.mourya.msscbeerorderservice.services.BeerOrderManagerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
//@RequiredArgsConstructor
public class BeerOrderStateChangesInterceptor extends StateMachineInterceptorAdapter<BeerOrderStatusEnum, BeerOrderEventEnum> {

    @Autowired
    BeerOrderRepository beerOrderRepository;

    @Transactional
    @Override
    public void preStateChange(State<BeerOrderStatusEnum, BeerOrderEventEnum> state, Message<BeerOrderEventEnum> message,
                               Transition<BeerOrderStatusEnum, BeerOrderEventEnum> transition, StateMachine<BeerOrderStatusEnum, BeerOrderEventEnum> stateMachine, StateMachine<BeerOrderStatusEnum, BeerOrderEventEnum> rootStateMachine) {
        Optional.ofNullable(message).flatMap(msg -> Optional.ofNullable(msg.getHeaders().getOrDefault(BeerOrderManagerImpl.ORDER_ID_HEADER, "")))
                .ifPresent(orderId -> {

                    log.info("Saving state for order id : " + orderId + " Status : " + state.getId());

                    Optional<BeerOrder> beerOrderOptional = beerOrderRepository.findById(UUID.fromString((String) orderId));

                    beerOrderOptional.ifPresentOrElse(beerOrder -> {
                        beerOrder.setOrderStatus(state.getId());
                        beerOrderRepository.saveAndFlush(beerOrder);
                    }, () -> log.error("Order Not Found. Id " + orderId));
                });
    }
}
