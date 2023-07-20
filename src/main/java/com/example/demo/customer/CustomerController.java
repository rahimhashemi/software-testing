package com.example.demo.customer;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/customer-registration")
public class CustomerController {

    private final CustomerRegistrationService customerRegistrationService;

    @Autowired
    public CustomerController(CustomerRegistrationService customerRegistrationService) {
        this.customerRegistrationService = customerRegistrationService;
    }

    @PutMapping
    public void customerRegistration(@RequestBody @Valid CustomerRegistrationRequest request) {
        customerRegistrationService.registerCustomer(request);
    }
}


