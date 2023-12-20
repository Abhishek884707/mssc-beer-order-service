package org.mourya.msscbeerorderservice.services.beer;

import org.mourya.msscbeerorderservice.web.model.BeerDto;


import java.util.Optional;
import java.util.UUID;

public interface BeerService {

    Optional<BeerDto> getBeerById(UUID uuid);

    Optional<BeerDto> getBeerByUpc(String upc);
}