package org.mourya.msscbeerorderservice.web.mapper;

import org.mapstruct.Mapper;
import org.mourya.msscbeerorderservice.domain.BeerOrderLine;
import org.mourya.msscbeerorderservice.web.model.BeerOrderLineDto;

@Mapper(uses = {DateMapper.class})
public interface BeerOrderLineMapper {
    BeerOrderLineDto beerOrderLineToDto(BeerOrderLine line);

    BeerOrderLine dtoToBeerOrderLine(BeerOrderLineDto dto);
}