package com.example.demo.payment.stripe;

import com.example.demo.payment.CardPaymentCharge;
import com.example.demo.payment.CardPaymentCharger;
import com.example.demo.payment.Currency;
import com.stripe.model.Charge;
import com.stripe.net.RequestOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@ConditionalOnProperty(
        value = "stripe.enabled",
        havingValue = "true"
)
public class StripeService implements CardPaymentCharger {
    private final StripeApi stripeApi;
    private final RequestOptions requestOptions = RequestOptions.builder()
            .setApiKey("sk_test_4eC39HqLyjWDarjtT1zdp7dc")
            .build();

    @Autowired
    public StripeService(StripeApi stripeApi) {
        this.stripeApi = stripeApi;
    }

    @Override
    public CardPaymentCharge chargeCard(String cardSource,
                                        BigDecimal amount,
                                        Currency currency,
                                        String description) {


        Map<String, Object> params = new HashMap<>();
        params.put("amount", amount);
        params.put("currency", currency);
        params.put("source", cardSource);
        params.put("description", description
        );

        try {
            Charge charge = stripeApi.create(params, requestOptions);
            return new CardPaymentCharge(charge.getPaid());
        } catch (Exception e) {
            throw new IllegalStateException("cant make stripe charge", e);
        }
    }
}
