package org.mourya.msscbeerorderservice.web.mapper;

import org.mapstruct.Mapper;
import org.mourya.brewery.model.CustomerDto;
import org.mourya.msscbeerorderservice.domain.Customer;

@Mapper(uses = DateMapper.class)
public interface CustomerMapper {
    CustomerDto customerToDto(Customer customer);

    Customer dtoToCustomer(CustomerDto customerDto);

}
