package com.github.donniexyz.demo.med.converter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import javax.money.MonetaryAmount;
import java.io.IOException;

public class MonetaryAmountSerializer extends StdSerializer<MonetaryAmount> {

    public MonetaryAmountSerializer() {
        super(MonetaryAmount.class);
    }

    public MonetaryAmountSerializer(Class<?> vc) {
        super(vc, false);
    }

    @Override
    public void serialize(MonetaryAmount value, JsonGenerator json, SerializerProvider provider) throws IOException {
        json.writeString(value.toString());
    }

}
