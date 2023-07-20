package com.example.demo.payment.stripe;

import com.example.demo.payment.CardPaymentCharge;
import com.example.demo.payment.Currency;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.net.RequestOptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

class StripeServiceTest {

    private StripeService underTest;
    @Mock
    private StripeApi stripeApi;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        underTest = new StripeService(stripeApi);
    }

    @Test
    void itShouldChargeCard() throws StripeException {
        //given
        String cardSource = "tok_mastercard";
        BigDecimal amount = new BigDecimal("100");
        Currency currency = Currency.USD;
        String description = "first test";

        Charge charge = new Charge();
        charge.setPaid(true);
        given(stripeApi.create(anyMap(), any())).willReturn(charge);
        //when
        CardPaymentCharge cardPaymentCharge = underTest.chargeCard(cardSource, amount, currency, description);
        //then
        ArgumentCaptor<Map<String, Object>> mapArgumentCaptor = ArgumentCaptor.forClass(Map.class);
        ArgumentCaptor<RequestOptions> optionsArgumentCaptor = ArgumentCaptor.forClass(RequestOptions.class);

        then(stripeApi).should().create(mapArgumentCaptor.capture(), optionsArgumentCaptor.capture());
        Map<String, Object> mapArgumentCaptorValue = mapArgumentCaptor.getValue();

        assertThat(mapArgumentCaptorValue.keySet()).hasSize(4);
        assertThat(mapArgumentCaptorValue.get("amount")).isEqualTo(amount);

        assertThat(cardPaymentCharge.isCardDebited()).isTrue();
    }

    @Test
    void itShouldThrowWhenStripeHasException() {
        //given
        //when
        //then
        assertThatThrownBy(() ->
                underTest.chargeCard(any(), any(), any(), any()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("cant make stripe charge");
    }
}