package org.mourya.msscbeerorderservice.web.mapper;

import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mourya.msscbeerorderservice.domain.BeerOrderLine;
import org.mourya.brewery.model.BeerOrderLineDto;

@Mapper(uses = {DateMapper.class})
@DecoratedWith(BeerOrderLineMapperDecorator.class)
public interface BeerOrderLineMapper {
    BeerOrderLineDto beerOrderLineToDto(BeerOrderLine line);

    BeerOrderLine dtoToBeerOrderLine(BeerOrderLineDto dto);
}