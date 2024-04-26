package org.mourya.msscbeerorderservice.services;

import org.mourya.brewery.model.CustomerPagedList;
import org.springframework.data.domain.PageRequest;

public interface CustomerService {
    CustomerPagedList listCustomers(PageRequest of);
}
