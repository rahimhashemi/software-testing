package com.example.demo.customer;

import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class CustomerRegistrationService {

    private final CustomerRepository customerRepository;

    public CustomerRegistrationService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public void registerCustomer(CustomerRegistrationRequest request) {
        String phoneNumber = request.getCustomer().getPhoneNumber();
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
