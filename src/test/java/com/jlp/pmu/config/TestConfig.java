package com.jlp.pmu.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.jlp.pmu.utility.JwtTokenUtil;
import com.jlp.pmu.service.PrinterService;
import com.jlp.pmu.service.UserService;

import org.mockito.Mockito;

@Configuration
public class TestConfig {

    @Bean
    public JwtTokenUtil jwtTokenUtil() {
        return new JwtTokenUtil();
    }

    @Bean
    public PrinterService printerService() {
        return Mockito.mock(PrinterService.class);
    }
    @Bean
    public UserService userService() {
        return Mockito.mock(UserService.class);
    }
}