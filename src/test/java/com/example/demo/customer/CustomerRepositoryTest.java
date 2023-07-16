package com.example.demo.customer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository underTest;

    @Test
    void itShouldSelectCustomerByPhoneNumber() {
        //given
        //when
        //then
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
                .hasValueSatisfying(c -> {
                            assertThat(c.getId().equals(id));
                            assertThat(c.getName().equals("Rahim"));
                            assertThat(c).isEqualToComparingFieldByField(rahim);
                        }
                );
    }
}