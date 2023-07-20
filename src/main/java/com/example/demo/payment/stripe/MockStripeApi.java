package com.example.demo.payment.stripe;

import com.example.demo.payment.CardPaymentCharge;
import com.example.demo.payment.CardPaymentCharger;
import com.example.demo.payment.Currency;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
@Service
@ConditionalOnProperty(
        value = "stripe.enabled",
        havingValue = "false"
)
public class MockStripeApi implements CardPaymentCharger {
    @Override
    public CardPaymentCharge chargeCard(String cardSource,
                                        BigDecimal amount,
                                        Currency currency,
                                        String description) {
        return new CardPaymentCharge(true);
    }
}
