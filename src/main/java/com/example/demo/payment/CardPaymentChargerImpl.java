package com.example.demo.payment;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CardPaymentChargerImpl implements CardPaymentCharger {
    @Override
    public CardPaymentCharge chargeCard(String cardSource,
                                        BigDecimal amount,
                                        Currency currency,
                                        String description) {
        return null;
    }
}
