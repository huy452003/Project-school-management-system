package com.common.QLGV.configurations;

import com.common.enums.Gender;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addConverter(new AbstractConverter<String, Gender>() {
            @Override
            protected Gender convert(String source) {
                if (source == null) return null;
                return Gender.valueOf(source.toUpperCase());
            }
        });
        return modelMapper;
    }
}
