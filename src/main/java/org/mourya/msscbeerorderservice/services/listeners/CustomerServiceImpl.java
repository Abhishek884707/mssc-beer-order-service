package org.mourya.msscbeerorderservice.services.listeners;

import lombok.RequiredArgsConstructor;
import org.mourya.brewery.model.CustomerPagedList;
import org.mourya.msscbeerorderservice.domain.Customer;
import org.mourya.msscbeerorderservice.repositories.CustomerRepository;
import org.mourya.msscbeerorderservice.services.CustomerService;
import org.mourya.msscbeerorderservice.web.mapper.CustomerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerMapper customerMapper;

    @Override
    public CustomerPagedList listCustomers(PageRequest of) {

        Page<Customer> customers = customerRepository.findAll(of);

        return new CustomerPagedList(customers
                .stream()
                .map(customer ->  customerMapper.customerToDto(customer))
                .toList(), PageRequest.of(customers.getPageable().getPageNumber(),
                customers.getPageable().getPageSize()),
                customers.getTotalElements());
    }
}
