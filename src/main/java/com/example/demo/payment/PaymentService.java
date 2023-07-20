package com.example.demo.payment;

import com.example.demo.customer.Customer;
import com.example.demo.customer.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final CustomerRepository customerRepository;
    private final CardPaymentCharger cardPaymentCharger;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository,
                          CustomerRepository customerRepository,
                          CardPaymentCharger cardPaymentCharger) {
        this.paymentRepository = paymentRepository;
        this.customerRepository = customerRepository;
        this.cardPaymentCharger = cardPaymentCharger;
    }

    void chargeCard(UUID customerId, PaymentRequest paymentRequest) {

        List<Currency> acceptableCurrency = Arrays.asList(Currency.USD, Currency.GDB);
        Optional<Customer> customerOptional = customerRepository.findById(customerId);
        if (customerOptional.isEmpty())
            throw new IllegalStateException(String.format("customer does not exist by id [%S]", customerId));

        Currency currency = paymentRequest.getPayment().getCurrency();
        boolean isSupported = acceptableCurrency.contains(currency);

        if (!isSupported)
            throw new IllegalStateException(String.format("currency does not support [%S]", currency));

        CardPaymentCharge cardPaymentCharge = cardPaymentCharger.chargeCard(
                paymentRequest.getPayment().getSource(),
                paymentRequest.getPayment().getAmount(),
                paymentRequest.getPayment().getCurrency(),
                paymentRequest.getPayment().getDescription()
        );

        boolean isCardDebited = cardPaymentCharge.isCardDebited();
        if (!isCardDebited)
            throw new IllegalStateException(String.format("card not debited for customer [%S]", customerId));

        paymentRequest.getPayment().setCustomerId(customerId);

        paymentRepository.save(paymentRequest.getPayment());
    }

}
