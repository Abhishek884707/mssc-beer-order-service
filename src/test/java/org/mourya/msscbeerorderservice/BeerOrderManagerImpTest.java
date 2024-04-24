package org.mourya.msscbeerorderservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mourya.brewery.model.BeerDto;
import org.mourya.brewery.model.events.AllocationFailureEvent;
import org.mourya.msscbeerorderservice.config.JmsConfig;
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
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@SpringBootTest
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

    @Autowired
    JmsTemplate jmsTemplate;

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
        String upc = "123457";
        BeerDto beerDto = BeerDto.builder().id(beerId).upc(upc).build();

        wireMockServer.stubFor(WireMock.get(BeerServiceImpl.BEER_UPC_PATH_V1 + beerDto.getUpc())
                .willReturn(WireMock.okJson(objectMapper.writeValueAsString(beerDto))));

        BeerOrder beerOrder = createBeerOrder(upc);
//        beerOrder.setCustomerRef("validation-pass");
        BeerOrder savedBeerOrder = beerOrderManager.newBeerOrder(beerOrder);

        Awaitility.await().untilAsserted(() -> {
            BeerOrder foundedOrder = beerOrderRepository.findById(beerOrder.getId()).get();

            assertEquals(BeerOrderStatusEnum.ALLOCATED, foundedOrder.getOrderStatus());
        });

        BeerOrder savedBeerOrder2 = beerOrderRepository.findById(beerOrder.getId()).get();

         assertNotNull(savedBeerOrder2);
         assertEquals(BeerOrderStatusEnum.ALLOCATED, savedBeerOrder2.getOrderStatus());
        savedBeerOrder2.getBeerOrderLines().forEach(beerOrderLine -> {
            assertEquals(beerOrderLine.getOrderQuantity(), beerOrderLine.getQuantityAllocated());
        });
    }

    @Test
    void testFailedAllocation() throws JsonProcessingException {
        String upc = "123456";
        BeerDto beerDto = BeerDto.builder().id(beerId).upc(upc).build();

        wireMockServer.stubFor(WireMock.get(BeerServiceImpl.BEER_UPC_PATH_V1 + beerDto.getUpc())
                .willReturn(WireMock.okJson(objectMapper.writeValueAsString(beerDto))));

        BeerOrder beerOrder = createBeerOrder(upc);
        beerOrder.setCustomerRef("fail-allocation");
        BeerOrder savedBeerOrder = beerOrderManager.newBeerOrder(beerOrder);

        Awaitility.await().untilAsserted(() -> {
            BeerOrder foundedOrder = beerOrderRepository.findById(beerOrder.getId()).get();

            assertEquals(BeerOrderStatusEnum.ALLOCATION_EXCEPTION, foundedOrder.getOrderStatus());
        });

        AllocationFailureEvent allocationFailureEvent = (AllocationFailureEvent) jmsTemplate.receiveAndConvert(JmsConfig.ALLOCATE_FAILURE_QUEUE);

        assertNotNull(allocationFailureEvent);

        Assertions.assertThat(allocationFailureEvent.getOrderId()).isEqualTo(savedBeerOrder.getId());
    }

    @Test
    void testPartialAllocation() throws JsonProcessingException {
        String upc = "123456";
        BeerDto beerDto = BeerDto.builder().id(beerId).upc(upc).build();

        wireMockServer.stubFor(WireMock.get(BeerServiceImpl.BEER_UPC_PATH_V1 + beerDto.getUpc())
                .willReturn(WireMock.okJson(objectMapper.writeValueAsString(beerDto))));

        BeerOrder beerOrder = createBeerOrder(upc);
        beerOrder.setCustomerRef("partial-allocation");
        BeerOrder savedBeerOrder = beerOrderManager.newBeerOrder(beerOrder);

        Awaitility.await().untilAsserted(() -> {
            BeerOrder foundedOrder = beerOrderRepository.findById(beerOrder.getId()).get();

            assertEquals(BeerOrderStatusEnum.PENDING_INVENTORY, foundedOrder.getOrderStatus());
        });

    }

    @Test
    void testFailedValidation() throws JsonProcessingException {
        String upc = "123456";
        BeerDto beerDto = BeerDto.builder().id(beerId).upc(upc).build();

        wireMockServer.stubFor(WireMock.get(BeerServiceImpl.BEER_UPC_PATH_V1 + beerDto.getUpc())
                .willReturn(WireMock.okJson(objectMapper.writeValueAsString(beerDto))));

        BeerOrder beerOrder = createBeerOrder(upc);
        beerOrder.setCustomerRef("fail-validation");
        BeerOrder savedBeerOrder = beerOrderManager.newBeerOrder(beerOrder);

        Awaitility.await().untilAsserted(() -> {
            BeerOrder foundedOrder = beerOrderRepository.findById(beerOrder.getId()).get();

            assertEquals(BeerOrderStatusEnum.VALIDATION_EXCEPTION, foundedOrder.getOrderStatus());
        });

    }
    @Test
    void testNewToPickedUp() throws JsonProcessingException {
        String upc = "123456";

        BeerDto beerDto = BeerDto.builder().id(beerId).upc(upc).build();

        wireMockServer.stubFor(WireMock.get(BeerServiceImpl.BEER_UPC_PATH_V1 + beerDto.getUpc())
                .willReturn(WireMock.okJson(objectMapper.writeValueAsString(beerDto))));

        BeerOrder beerOrder = createBeerOrder(upc);
//        beerOrder.setCustomerRef("validation-pass");
        BeerOrder savedBeerOrder = beerOrderManager.newBeerOrder(beerOrder);

        Awaitility.await().untilAsserted(() -> {
            BeerOrder foundedOrder = beerOrderRepository.findById(beerOrder.getId()).get();

            assertEquals(BeerOrderStatusEnum.ALLOCATED, foundedOrder.getOrderStatus());
        });

        beerOrderManager.beerOrderPickedUp(savedBeerOrder.getId());

        Awaitility.await().untilAsserted(() -> {
            BeerOrder foundedOrder = beerOrderRepository.findById(beerOrder.getId()).get();

            assertEquals(BeerOrderStatusEnum.PICKED_UP, foundedOrder.getOrderStatus());
        });

        BeerOrder pickedUpOrder = beerOrderRepository.findById(savedBeerOrder.getId()).get();

        assertEquals(BeerOrderStatusEnum.PICKED_UP, pickedUpOrder.getOrderStatus());

    }

    public BeerOrder createBeerOrder(String upc){
        BeerOrder beerOrder = BeerOrder.builder()
                .customer(testCustomer)
                .build();

        Set<BeerOrderLine> lines = new HashSet<>();
        lines.add(BeerOrderLine.builder()
                .beerId(beerId)
                .upc(upc)
                .orderQuantity(1)
                .beerOrder(beerOrder)
                .build());

        beerOrder.setBeerOrderLines(lines);

        return beerOrder;
    }
}
