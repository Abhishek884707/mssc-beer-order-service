package org.mourya.msscbeerorderservice.web.mapper;

import javax.annotation.processing.Generated;
import org.mourya.msscbeerorderservice.domain.BeerOrder;
import org.mourya.msscbeerorderservice.web.model.BeerOrderDto;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-12-14T12:46:35+0530",
    comments = "version: 1.3.0.Final, compiler: javac, environment: Java 17.0.6 (Oracle Corporation)"
)
@Component
public class BeerOrderMapperImpl implements BeerOrderMapper {

    @Override
    public BeerOrderDto beerOrderToDto(BeerOrder beerOrder) {
        if ( beerOrder == null ) {
            return null;
        }

        BeerOrderDto beerOrderDto = new BeerOrderDto();

        return beerOrderDto;
    }

    @Override
    public BeerOrder dtoToBeerOrder(BeerOrderDto dto) {
        if ( dto == null ) {
            return null;
        }

        BeerOrder beerOrder = new BeerOrder();

        return beerOrder;
    }
}
