/*
 * MIT License
 *
 * Copyright (c) 2024 (https://github.com/donniexyz)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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
