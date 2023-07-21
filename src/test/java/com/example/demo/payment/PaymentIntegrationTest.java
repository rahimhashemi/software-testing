package com.example.demo.payment;

import com.example.demo.customer.Customer;
import com.example.demo.customer.CustomerRegistrationRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PaymentIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    private Faker faker;

    //it's not recommended
    @Autowired
    private PaymentRepository paymentRepository;

    @BeforeEach
    void setUp() {
        faker = new Faker();
    }

    @Test
    void itShouldCreatePaymentSuccessfully() throws Exception {
        //given
        UUID customerId = UUID.randomUUID();
        Customer customer = new Customer(
                customerId,
                faker.name().fullName(),
                "+989354532600");

        CustomerRegistrationRequest registrationRequest = new CustomerRegistrationRequest(customer);

        long paymentId = 1L;
        Currency currency = Currency.USD;
        String cardSource = "tok_mastercard";
        Payment payment = new Payment(
                paymentId,
                customerId,
                new BigDecimal("100"),
                currency,
                cardSource,
                "visa");
        PaymentRequest paymentRequest = new PaymentRequest(payment);

        ResultActions resultActions = mockMvc.perform(put("/api/v1/customer-registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(objectToJson(registrationRequest)))
        );

        ResultActions paymentResultActions = mockMvc.perform(post("/api/v1/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(objectToJson(paymentRequest)))
        );
        //then
        resultActions.andExpect(status().is2xxSuccessful());
        paymentResultActions.andExpect(status().is2xxSuccessful());
        assertThat(paymentRepository.findById(paymentId))
                .isPresent()
                .hasValueSatisfying(p -> {
                            assertThat(p.getCustomerId()).isEqualTo(customerId);
                            assertThat(p.getCurrency()).isEqualTo(currency);
                        }
                );
    }

    private String objectToJson(Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            fail("failed convert object to json");
            return null;
        }
    }
}
