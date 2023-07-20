package com.example.demo.payment;

import com.example.demo.customer.Customer;
import com.example.demo.customer.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

class PaymentServiceTest {
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private CardPaymentCharger cardPaymentCharger;
    private PaymentService underTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        underTest = new PaymentService(
                paymentRepository,
                customerRepository,
                cardPaymentCharger);
    }

    @Test
    void itShouldChargeCardSuccessfully() {
        //given
        UUID customerId = UUID.randomUUID();
        given(customerRepository.findById(customerId)).willReturn(Optional.of(mock(Customer.class)));

        Currency currency = Currency.USD;
        Payment payment = new Payment(
                null,
                null,
                new BigDecimal("10.00"),
                currency,
                "payment1",
                "donation");
        PaymentRequest paymentRequest = new PaymentRequest(payment);
        given(cardPaymentCharger.chargeCard(paymentRequest.getPayment().getSource(),
                paymentRequest.getPayment().getAmount(),
                paymentRequest.getPayment().getCurrency(),
                paymentRequest.getPayment().getDescription()))
                .willReturn(new CardPaymentCharge(true));

        //when
        underTest.chargeCard(customerId, paymentRequest);
        //then
        ArgumentCaptor<Payment> paymentArgumentCaptor = ArgumentCaptor.forClass(Payment.class);

        then(paymentRepository).should().save(paymentArgumentCaptor.capture());
        Payment paymentArgumentCaptorValue = paymentArgumentCaptor.getValue();
        assertThat(paymentArgumentCaptorValue)
                .isEqualToIgnoringGivenFields(
                        payment,
                        "customerId");
        assertThat(paymentArgumentCaptorValue.getCurrency()).isEqualTo(currency);
        assertThat(paymentArgumentCaptorValue.getCustomerId()).isEqualTo(customerId);
    }

    @Test
    void itShouldThrowWhenCustomerNotExists() {
        //given
        UUID customerId = UUID.randomUUID();
        given(customerRepository.findById(customerId)).willReturn(Optional.empty());

        // when
        //then
        assertThatThrownBy(() ->
                underTest.chargeCard(customerId,
                        new PaymentRequest(new Payment())))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(String.format("customer does not exist by id [%S]", customerId));

        then(paymentRepository).shouldHaveNoInteractions();
        then(cardPaymentCharger).shouldHaveNoInteractions();
    }

    @Test
    void itShouldThrowWhenCurrencyNotAcceptable() {
        //given
        UUID customerId = UUID.randomUUID();
        given(customerRepository.findById(customerId)).willReturn(Optional.of(mock(Customer.class)));

        Currency wrongCurrency = Currency.IR;
        Payment payment = new Payment(
                null,
                customerId,
                new BigDecimal("10.00"),
                wrongCurrency,
                "payment1",
                "donation");
        PaymentRequest paymentRequest = new PaymentRequest(payment);

        // when
        assertThatThrownBy(() ->
                underTest.chargeCard(customerId, paymentRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(String.format("currency does not support [%S]", wrongCurrency));

        //then
        then(cardPaymentCharger).shouldHaveNoInteractions();
        then(paymentRepository).shouldHaveNoInteractions();
    }

    @Test
    void itShouldThrowWhenCardNotDebited() {
        //given
        UUID customerId = UUID.randomUUID();
        given(customerRepository.findById(customerId)).willReturn(Optional.of(mock(Customer.class)));

        Currency currency = Currency.USD;
        Payment payment = new Payment(
                null,
                customerId,
                new BigDecimal("10.00"),
                currency,
                "payment1",
                "donation");
        PaymentRequest paymentRequest = new PaymentRequest(payment);
        given(cardPaymentCharger.chargeCard(
                paymentRequest.getPayment().getSource(),
                paymentRequest.getPayment().getAmount(),
                paymentRequest.getPayment().getCurrency(),
                paymentRequest.getPayment().getDescription()))
                .willReturn(new CardPaymentCharge(false));

        //when
        //then
        assertThatThrownBy(() ->
                underTest.chargeCard(customerId, paymentRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(String.format("card not debited for customer [%S]", customerId));

        then(paymentRepository).should(never()).save(any(Payment.class));
    }
}