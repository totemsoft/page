package com.totemsoft.page.config;

import java.util.List;

import org.jspecify.annotations.Nullable;
import org.springframework.aot.generate.GeneratedTypeReference;
import org.springframework.aot.hint.ExecutableMode;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.javapoet.ClassName;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;

import com.totemsoft.page.exchangerates.v1.model.CurrencyDto;
import com.totemsoft.page.exchangerates.v1.model.ExchangeRateSymbols;
import com.totemsoft.page.exchangerates.v1.model.ExchangeRates;
import com.totemsoft.page.marketstack.v2.model.EODBarDto;
import com.totemsoft.page.marketstack.v2.model.EODResponse;
import com.totemsoft.page.marketstack.v2.model.ExchangeDto;
import com.totemsoft.page.marketstack.v2.model.ExchangeMicEod;
import com.totemsoft.page.marketstack.v2.model.ExchangeMicEodData;
import com.totemsoft.page.marketstack.v2.model.ExchangeResponse;
import com.totemsoft.page.marketstack.v2.model.ExchangeTickerData;
import com.totemsoft.page.marketstack.v2.model.ExchangeTickerDto;
import com.totemsoft.page.marketstack.v2.model.ExchangeTickerResponse;
import com.totemsoft.page.marketstack.v2.model.PaginationDto;

public class CustomRuntimeHintsRegistrar implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(RuntimeHints hints, @Nullable ClassLoader classLoader) {
        // Register for reflection
        hints.reflection()
            .registerType(AbstractEnvironment.class, builder -> builder
                .withMethod("getProperty", List.of(GeneratedTypeReference.of(ClassName.get(String.class))), ExecutableMode.INVOKE))
            .registerType(OAuth2AuthenticationToken.class, builder -> builder
                .withMethod("getPrincipal", List.of(), ExecutableMode.INVOKE))
            .registerType(OAuth2AuthenticatedPrincipal.class, builder -> builder
                .withMethod("getAttributes", List.of(), ExecutableMode.INVOKE))
        ;
        // Register for serialization
        hints.serialization()
            // exchangerates api
            .registerType(CurrencyDto.class)
            .registerType(ExchangeRates.class)
            .registerType(ExchangeRateSymbols.class)
            // marketstack api
            .registerType(EODBarDto.class)
            .registerType(EODResponse.class)
            .registerType(ExchangeDto.class)
            .registerType(ExchangeMicEod.class)
            .registerType(ExchangeMicEodData.class)
            .registerType(ExchangeResponse.class)
            .registerType(ExchangeTickerData.class)
            .registerType(ExchangeTickerDto.class)
            .registerType(ExchangeTickerResponse.class)
            .registerType(PaginationDto.class)
        ;
        // Register for resources
        //hints.resources().registerPattern("*.properties");
    }

}
