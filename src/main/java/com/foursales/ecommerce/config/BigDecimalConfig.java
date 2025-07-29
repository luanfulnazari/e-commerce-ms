package com.foursales.ecommerce.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;

@Configuration
public class BigDecimalConfig {

    private static final DecimalFormat decimalFormat = new DecimalFormat("#0.00");

    static class BigDecimalSerializer extends StdScalarSerializer<BigDecimal> {

        protected BigDecimalSerializer() {
            super(BigDecimal.class);
        }

        @Override
        public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeString(decimalFormat.format(value));
        }
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer customBigDecimalSerializer() {
        return builder -> {
            SimpleModule module = new SimpleModule();
            module.addSerializer(BigDecimal.class, new BigDecimalSerializer());
            builder.modules(module);
        };
    }
}
