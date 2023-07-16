package com.example.demo.customer;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends CrudRepository<Customer, UUID> {
    @Query(value = "select id,name, phoneNumber from Customer " +
            "where phoneNumber = :phoneNumber",
            nativeQuery = true
    )
    Optional<Customer> selectCustomerByTel(
            @Param("phoneNumber") String phoneNumber
    );

}
