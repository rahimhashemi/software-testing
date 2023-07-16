package com.example.demo.customer;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;


class CustomerRegistrationServiceTest {

    @Mock
    private CustomerRepository customerRepository;
    private CustomerRegistrationService underTest;
    @Captor
    private ArgumentCaptor<Customer> customerArgumentCaptor;
    private Faker faker;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        underTest = new CustomerRegistrationService(customerRepository);
        faker = new Faker();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void itShouldRegisterCustomer() {
        //given
        String phoneNumber = faker.phoneNumber().phoneNumber();
        UUID id = UUID.randomUUID();
        Customer customer = new Customer(id, faker.name().fullName(), phoneNumber);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);
        given(customerRepository.selectCustomerByTel(phoneNumber)).willReturn(Optional.empty());

        //when
        underTest.registerCustomer(request);

        //then
		then(customerRepository).should().save(customerArgumentCaptor.capture());
        Customer customerArgumentCaptorValue = customerArgumentCaptor.getValue();
        assertThat(customerArgumentCaptorValue).isEqualTo(customer);

    }
}