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

import com.totemsoft.page.model.exchange.ExchangeRateSymbols;
import com.totemsoft.page.model.exchange.ExchangeRates;

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
            .registerType(ExchangeRates.class)
            .registerType(ExchangeRateSymbols.class)
        ;
        // Register for resources
        //hints.resources().registerPattern("*.properties");
    }

}
