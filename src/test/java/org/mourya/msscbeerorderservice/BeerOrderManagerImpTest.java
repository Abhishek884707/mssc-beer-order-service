package org.mourya.msscbeerorderservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import lombok.extern.slf4j.Slf4j;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mourya.brewery.model.BeerDto;
import org.mourya.msscbeerorderservice.domain.BeerOrder;
import org.mourya.msscbeerorderservice.domain.BeerOrderLine;
import org.mourya.msscbeerorderservice.domain.BeerOrderStatusEnum;
import org.mourya.msscbeerorderservice.domain.Customer;
import org.mourya.msscbeerorderservice.repositories.BeerOrderRepository;
import org.mourya.msscbeerorderservice.repositories.CustomerRepository;
import org.mourya.msscbeerorderservice.services.BeerOrderManager;
import org.mourya.msscbeerorderservice.services.beer.BeerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@SpringBootTest
//@AutoConfigureMockMvc
public class BeerOrderManagerImpTest {

    @Autowired
    BeerOrderManager beerOrderManager;

    @Autowired
    BeerOrderRepository beerOrderRepository;

    @Autowired
    CustomerRepository customerRepository;

    Customer testCustomer;
    UUID beerId = UUID.randomUUID();


    @Autowired
    ObjectMapper objectMapper;


    @BeforeEach()
    void setUp(){
        testCustomer = customerRepository.save(Customer.builder()
                .customerName("Abhishek")
                .build());
    }

    @RegisterExtension
    static WireMockExtension wireMockServer = WireMockExtension.newInstance()
            .options(WireMockConfiguration.wireMockConfig().port(8083))
            .build();

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry){
        registry.add("sfg.brewery.beerServiceHost", wireMockServer::baseUrl);
    }

    @Test
    void testNewToAllocated() throws JsonProcessingException, InterruptedException {
        BeerDto beerDto = BeerDto.builder().id(beerId).upc("12345").build();

//        BeerPagedList list = new BeerPagedList(Arrays.asList(beerDto));
        wireMockServer.stubFor(WireMock.get(BeerServiceImpl.BEER_UPC_PATH_V1 + beerDto.getUpc())
                .willReturn(WireMock.okJson(objectMapper.writeValueAsString(beerDto))));

        BeerOrder beerOrder = createBeerOrder();
        BeerOrder savedBeerOrder = beerOrderManager.newBeerOrder(beerOrder);

        Awaitility.await().untilAsserted(() -> {
            BeerOrder foundedOrder = beerOrderRepository.findById(beerOrder.getId()).get();

            // todo - Allocated Status
            assertEquals(BeerOrderStatusEnum.ALLOCATED, foundedOrder.getOrderStatus());
        });

        BeerOrder savedBeerOrder2 = beerOrderRepository.findById(beerOrder.getId()).get();

         assertNotNull(savedBeerOrder);
         assertEquals(BeerOrderStatusEnum.ALLOCATED, savedBeerOrder2.getOrderStatus());

    }

    public BeerOrder createBeerOrder(){
        BeerOrder beerOrder = BeerOrder.builder()
                .customer(testCustomer)
                .build();

        Set<BeerOrderLine> lines = new HashSet<>();
        lines.add(BeerOrderLine.builder()
                .beerId(beerId)
                .upc("12345")
                .orderQuantity(1)
                .beerOrder(beerOrder)
                .build());

        beerOrder.setBeerOrderLines(lines);

        return beerOrder;
    }
}
