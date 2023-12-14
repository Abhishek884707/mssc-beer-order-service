package org.mourya.msscbeerorderservice.web.mapper;

import org.mapstruct.Mapper;
import org.mourya.msscbeerorderservice.domain.BeerOrder;
import org.mourya.msscbeerorderservice.web.model.BeerOrderDto;

@Mapper(uses = {DateMapper.class, BeerOrderLineMapper.class})
public interface BeerOrderMapper {

    BeerOrderDto beerOrderToDto(BeerOrder beerOrder);

    BeerOrder dtoToBeerOrder(BeerOrderDto dto);
}