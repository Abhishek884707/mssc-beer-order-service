package org.mourya.msscbeerorderservice.web.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BeerOrderLineDto extends BaseItem {

    @Builder
    public BeerOrderLineDto(UUID id, Integer version, OffsetDateTime createdDate, OffsetDateTime lastModifiedDate,
                            String upc, String beerName, BeerStyleEnum beerStyle, UUID beerId, Integer orderQuantity, BigDecimal price) {
        super(id, version, createdDate, lastModifiedDate);
        this.upc = upc;
        this.beerName = beerName;
        this.beerStyle = beerStyle;
        this.beerId = beerId;
        this.orderQuantity = orderQuantity;
        this.price = price;
    }

    private String upc;
    private String beerName;
    private BeerStyleEnum beerStyle;
    private UUID beerId;
    private Integer orderQuantity = 0;
    private BigDecimal price;
}