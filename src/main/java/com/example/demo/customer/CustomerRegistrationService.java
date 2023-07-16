package com.example.demo.customer;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomerRegistrationService {

    private final CustomerRepository customerRepository;

    public CustomerRegistrationService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public void registerCustomer(CustomerRegistrationRequest request) {
        String phoneNumber = request.getCustomer().getPhoneNumber();
        Optional<Customer> customerOptional = customerRepository.selectCustomerByTel(phoneNumber);
        if (customerOptional.isPresent()) {
            if (customerOptional.get().getName().equals(request.getCustomer().getName())) {
                return;
            }
            throw new IllegalStateException(String.format("phone number [%S] is taken", phoneNumber));
        }
        customerRepository.save(request.getCustomer());
    }
}
