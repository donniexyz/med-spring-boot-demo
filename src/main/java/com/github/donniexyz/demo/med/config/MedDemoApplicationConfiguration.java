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
