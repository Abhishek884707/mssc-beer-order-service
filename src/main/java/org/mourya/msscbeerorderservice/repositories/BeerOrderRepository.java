package org.mourya.msscbeerorderservice.repositories;

import org.mourya.msscbeerorderservice.domain.BeerOrder;
import org.mourya.msscbeerorderservice.domain.BeerOrderStatusEnum;
import org.mourya.msscbeerorderservice.domain.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;


public interface BeerOrderRepository  extends JpaRepository<BeerOrder, UUID> {

    Page<BeerOrder> findAllByCustomer(Customer customer, Pageable pageable);

    List<BeerOrder> findAllByOrderStatus(BeerOrderStatusEnum orderStatusEnum);

   /* @Lock(LockModeType.PESSIMISTIC_WRITE)
    BeerOrder findOneById(UUID id);*/
}