package org.mourya.msscbeerorderservice.web.model;

import lombok.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public class BeerOrderDto extends BaseItem {

    public BeerOrderDto(){}

    @Builder
    public BeerOrderDto(UUID id, Integer version, OffsetDateTime createdDate, OffsetDateTime lastModifiedDate, UUID customerId, List<BeerOrderLineDto> beerOrderLines,
                        OrderStatusEnum orderStatus, String orderStatusCallbackUrl, String customerRef) {
        super(id, version, createdDate, lastModifiedDate);
        this.customerId = customerId;
        this.beerOrderLines = beerOrderLines;
        this.orderStatus = orderStatus;
        this.orderStatusCallbackUrl = orderStatusCallbackUrl;
        this.customerRef = customerRef;
    }

    private UUID customerId;
    private String customerRef;
    private List<BeerOrderLineDto> beerOrderLines;
    private OrderStatusEnum orderStatus;
    private String orderStatusCallbackUrl;
}