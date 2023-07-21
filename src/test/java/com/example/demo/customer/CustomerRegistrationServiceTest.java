package com.example.demo.customer;

import com.example.demo.utils.PhoneNumberValidator;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;


class CustomerRegistrationServiceTest {

    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private PhoneNumberValidator phoneNumberValidator;
    private CustomerRegistrationService underTest;
    @Captor
    private ArgumentCaptor<Customer> customerArgumentCaptor;
    private Faker faker;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        underTest = new CustomerRegistrationService(customerRepository, phoneNumberValidator);
        faker = new Faker();
    }

    @Test
    void itShouldSaveCustomer() {
        //given
        String phoneNumber = faker.phoneNumber().phoneNumber();
        UUID id = UUID.randomUUID();
        Customer customer = new Customer(id, faker.name().fullName(), phoneNumber);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);
        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber)).willReturn(Optional.empty());

        given(phoneNumberValidator.validate(phoneNumber)).willReturn(true);

        //when
        underTest.registerCustomer(request);

        //then
        then(customerRepository).should().save(customerArgumentCaptor.capture());
        Customer customerArgumentCaptorValue = customerArgumentCaptor.getValue();
        assertThat(customerArgumentCaptorValue).isEqualTo(customer);
    }

    @Test
    void itShouldSaveCustomerWhenIdIsNull() {
        //given
        String phoneNumber = faker.phoneNumber().phoneNumber();
        Customer customer = new Customer(null, faker.name().fullName(), phoneNumber);

        given(phoneNumberValidator.validate(phoneNumber)).willReturn(true);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);
        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber)).willReturn(Optional.empty());
        //when
        underTest.registerCustomer(request);

        //then
        then(customerRepository).should().save(customerArgumentCaptor.capture());
        Customer customerArgumentCaptorValue = customerArgumentCaptor.getValue();
        assertThat(customerArgumentCaptorValue).isEqualTo(customer);
        assertThat(customerArgumentCaptorValue.getId()).isNotNull();
    }

    @Test
    void itShouldThrowExceptionWhenPhoneNumberIsTaken() {
        //given
        String phoneNumber = faker.phoneNumber().phoneNumber();
        UUID id = UUID.randomUUID();

        Customer customer = new Customer(id, faker.name().fullName(), phoneNumber);
        Customer customerTwo = new Customer(UUID.randomUUID(), faker.name().fullName(), phoneNumber);

        given(phoneNumberValidator.validate(phoneNumber)).willReturn(true);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);
        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber)).willReturn(Optional.of(customerTwo));

        //when
        //then
        assertThatThrownBy(() ->
                underTest.registerCustomer(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(String.format("phone number [%S] is taken", phoneNumber)
                );

        then(customerRepository).should(never()).save(any());
    }

    @Test
    void itShouldNotSaveCustomerWhenCustomerIsExisted() {
        //given
        String phoneNumber = faker.phoneNumber().phoneNumber();
        UUID id = UUID.randomUUID();

        Customer customer = new Customer(id, faker.name().fullName(), phoneNumber);

        given(phoneNumberValidator.validate(phoneNumber)).willReturn(true);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);
        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber)).willReturn(Optional.of(customer));

        //when
        underTest.registerCustomer(request);

        //then
        then(customerRepository).should(never()).save(any());
    }
}