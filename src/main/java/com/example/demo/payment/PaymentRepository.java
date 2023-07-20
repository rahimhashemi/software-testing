package com.example.demo.payment;

import org.springframework.data.repository.CrudRepository;

public interface PaymentRepository extends CrudRepository<Payment, Long> {
}
