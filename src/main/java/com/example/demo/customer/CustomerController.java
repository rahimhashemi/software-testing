package com.example.demo.customer;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/customer-registration")
public class CustomerController {

    @PutMapping
    public void customerRegistration(
            @RequestBody @Valid CustomerRegistrationRequest request){

    }

}


