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
package com.github.donniexyz.demo.med.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.donniexyz.demo.med.converter.MonetaryAmountDeserializer;
import com.github.donniexyz.demo.med.converter.MonetaryAmountSerializer;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.jackson.datatype.money.MoneyModule;

import javax.money.MonetaryAmount;

@Configuration
public class MedDemoApplicationConfiguration {

    //@Primary
    @Bean
    public ObjectMapper objectMapper(@Value("${donniexyz.med.MonetaryAmount.serializeAsString.enabled:false}") boolean serMonetaryAmountAsString) {
        ObjectMapper objectMapper = new ObjectMapper();
        return registerOurModules(objectMapper, serMonetaryAmountAsString);
    }

    @NotNull
    public static ObjectMapper registerOurModules(ObjectMapper objectMapper, boolean serMonetaryAmountAsString) {
        objectMapper.registerModule(new Hibernate6Module());
        objectMapper.registerModule(new MoneyModule());
        objectMapper.registerModule(new JavaTimeModule());
        // TODO: write unit test that make sure the custom ser & deser is used by objectMapper instead of MoneyModule's
        SimpleModule module = new SimpleModule("MonetaryAmountAsStringModule");
        module.addDeserializer(MonetaryAmount.class, new MonetaryAmountDeserializer());
        if (serMonetaryAmountAsString)  module.addSerializer(MonetaryAmount.class, new MonetaryAmountSerializer());
        objectMapper.registerModule(module);
        return objectMapper;
    }

}
