package org.mourya.msscbeerorderservice.web.mapper;

import javax.annotation.processing.Generated;
import org.mourya.msscbeerorderservice.domain.BeerOrderLine;
import org.mourya.msscbeerorderservice.web.model.BeerOrderLineDto;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-12-14T12:46:35+0530",
    comments = "version: 1.3.0.Final, compiler: javac, environment: Java 17.0.6 (Oracle Corporation)"
)
@Component
public class BeerOrderLineMapperImpl implements BeerOrderLineMapper {

    @Override
    public BeerOrderLineDto beerOrderLineToDto(BeerOrderLine line) {
        if ( line == null ) {
            return null;
        }

        BeerOrderLineDto beerOrderLineDto = new BeerOrderLineDto();

        return beerOrderLineDto;
    }

    @Override
    public BeerOrderLine dtoToBeerOrderLine(BeerOrderLineDto dto) {
        if ( dto == null ) {
            return null;
        }

        BeerOrderLine beerOrderLine = new BeerOrderLine();

        return beerOrderLine;
    }
}
