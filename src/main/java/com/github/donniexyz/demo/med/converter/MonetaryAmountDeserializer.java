package com.github.donniexyz.demo.med.converter;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.github.donniexyz.demo.med.lib.CashAccountConstants;
import org.javamoney.moneta.Money;

import javax.money.MonetaryAmount;
import java.io.IOException;
import java.math.BigDecimal;

public class MonetaryAmountDeserializer extends StdDeserializer<MonetaryAmount> {

    public MonetaryAmountDeserializer() {
        this((Class<?>) null);
    }

    public MonetaryAmountDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public MonetaryAmount deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        JsonNode node = p.getCodec().readTree(p);
        if (node.isTextual())
            return Money.parse(node.asText());
        if (node.isObject()) {
            JsonNode amount = node.get("amount");
            JsonNode currency = node.get("currency");
            return Money.of(new BigDecimal(amount.asText("0")), CashAccountConstants.MA.CURRENCY_UNIT_MAP.get(currency.asText(CashAccountConstants.MA.ALLOWED_CURRENCY_NAMES.get(0))));
        }
        return null;
    }
}
