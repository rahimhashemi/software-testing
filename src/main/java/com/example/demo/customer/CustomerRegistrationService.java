package com.example.demo.customer;

import com.example.demo.utils.PhoneNumberValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class CustomerRegistrationService {

    private final CustomerRepository customerRepository;
    private final PhoneNumberValidator phoneNumberValidator;

    @Autowired
    public CustomerRegistrationService(CustomerRepository customerRepository,
                                       PhoneNumberValidator phoneNumberValidator) {
        this.customerRepository = customerRepository;
        this.phoneNumberValidator = phoneNumberValidator;
    }

    public void registerCustomer(CustomerRegistrationRequest request) {
        String phoneNumber = request.getCustomer().getPhoneNumber();

        if (!phoneNumberValidator.validate(phoneNumber))
            throw new IllegalStateException(String.format("phone number [%S] is not valid.", phoneNumber));

        Optional<Customer> customerOptional = customerRepository.selectCustomerByPhoneNumber(phoneNumber);
        if (customerOptional.isPresent()) {
            if (customerOptional.get().getName().equals(request.getCustomer().getName())) {
                return;
            }
            throw new IllegalStateException(String.format("phone number [%S] is taken", phoneNumber));
        }

        if (request.getCustomer().getId() == null)
            request.getCustomer().setId(UUID.randomUUID());

        customerRepository.save(request.getCustomer());
    }
}
