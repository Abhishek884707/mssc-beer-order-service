package org.mourya.msscbeerorderservice.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.sql.Timestamp;
import java.util.UUID;

/**
 * Created by jt on 2019-01-26.
 */
@Getter
@Setter
@Entity
public class BeerOrderLine extends BaseEntity {

    public BeerOrderLine(){}

    @Builder
    public BeerOrderLine(UUID id, Long version, Timestamp createdDate, Timestamp lastModifiedDate,
                         BeerOrder beerOrder, UUID beerId, Integer orderQuantity,
                         Integer quantityAllocated) {
        super(id, version, createdDate, lastModifiedDate);
        this.beerOrder = beerOrder;
        this.beerId = beerId;
        this.orderQuantity = orderQuantity;
        this.quantityAllocated = quantityAllocated;
    }

    @ManyToOne
    private BeerOrder beerOrder;

    private UUID beerId;
    private Integer orderQuantity = 0;
    private Integer quantityAllocated = 0;
}