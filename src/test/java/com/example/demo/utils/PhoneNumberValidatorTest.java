package com.example.demo.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

public class PhoneNumberValidatorTest {

    private PhoneNumberValidator underTest;

    @BeforeEach
    void setUp() {
        underTest = new PhoneNumberValidator();
    }

    @ParameterizedTest
    @CsvSource({
            "+989354532600,true",
            "+9354532600,false",
            "+9893545326000,false",
            "+989900151890,true",
            "+98990015189a,false",
            ",false",
            "null,false",
    })
    void itShouldValidatePhoneNumber(String phoneNumber, boolean expected) {
        //when
        boolean actual = underTest.validate(phoneNumber);
        //then
        assertThat(actual).isEqualTo(expected);
    }
}
