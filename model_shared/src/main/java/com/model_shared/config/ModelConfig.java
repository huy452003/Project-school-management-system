package com.model_shared.config;

import com.model_shared.enums.Gender;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelConfig {
    
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        
        // Configure STANDARD matching (default behavior)
        // Standard: Balance giữa strict và loose
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STANDARD)
                .setSkipNullEnabled(true);
        
        // Custom converter cho Gender
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
