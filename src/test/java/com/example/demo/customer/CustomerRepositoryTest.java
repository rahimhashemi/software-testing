package com.example.demo.customer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest(properties = {
        "spring.jpa.properties.javax.persistence.validation.mode=none"
})
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository underTest;

    @Test
    void itShouldSelectCustomerByPhoneNumber() {
        //given
        UUID id = UUID.randomUUID();
        String number = "0936";
        Customer rahim = new Customer(id, "Rahim", number);
        //when
        underTest.save(rahim);
        //then
        Optional<Customer> customerOptional = underTest.selectCustomerByPhoneNumber(number);

        assertThat(customerOptional)
                .isPresent()
                .hasValueSatisfying(c -> assertThat(c).isEqualToComparingFieldByField(rahim)
                );
    }

    @Test
    void itShouldNotSelectCustomerByPhoneNumberWhenPhoneNumberDoesNotExists() {
        //given
        String number = "0936";
        //when
        //then
        Optional<Customer> customerOptional = underTest.selectCustomerByPhoneNumber(number);

        assertThat(customerOptional)
                .isNotPresent();
    }

    @Test
    void itShouldSaveCustomer() {
        //given
        UUID id = UUID.randomUUID();
        Customer rahim = new Customer(id, "Rahim", "0935");
        //when
        underTest.save(rahim);
        //then
        Optional<Customer> customerOptional = underTest.findById(id);
        assertThat(customerOptional)
                .isPresent()
                .hasValueSatisfying(c -> assertThat(c).isEqualToComparingFieldByField(rahim)
                );
    }

    @Test
    void itShouldNotSaveCustomerByNullName() {
        //given
        UUID id = UUID.randomUUID();
        Customer rahim = new Customer(id, null, "0935");
        //when
        //then
        assertThatThrownBy(() -> underTest.save(rahim))
                .hasMessageContaining("not-null property references a null or transient value")
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void itShouldNotSaveCustomerByNullPhoneNumber() {
        //given
        UUID id = UUID.randomUUID();
        Customer rahim = new Customer(id, "alex", null);
        //when
        //then
        assertThatThrownBy(() -> underTest.save(rahim))
                .hasMessageContaining("not-null property references a null or transient value")
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void itShouldNotSaveCustomerByDuplicatePhoneNumber() {
        //given
        UUID id = UUID.randomUUID();
        Customer rahim = new Customer(id, "Rahim", "0935");
        //when
        underTest.save(rahim);
        //then
    }
}