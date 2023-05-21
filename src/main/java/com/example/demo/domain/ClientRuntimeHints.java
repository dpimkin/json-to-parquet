package com.example.demo.domain;

import lombok.SneakyThrows;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class ClientRuntimeHints implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        registerReflectionHints(hints);
    }

    @SneakyThrows
    private static void registerReflectionHints(RuntimeHints hints) {
        hints.reflection()
                .registerType(SomeSystemResponse.class, MemberCategory.INTROSPECT_DECLARED_CONSTRUCTORS)
                .registerType(SomeSystemResponse.class, MemberCategory.INTROSPECT_DECLARED_METHODS)
                .registerType(SomeSystemResponse.class, MemberCategory.DECLARED_FIELDS)
                .registerType(SomeSystemResponse.class, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS)
                .registerType(SomeSystemResponse.class, MemberCategory.INVOKE_DECLARED_METHODS)

                .registerType(ResponsePayload.class, MemberCategory.INTROSPECT_DECLARED_CONSTRUCTORS)
                .registerType(ResponsePayload.class, MemberCategory.INTROSPECT_DECLARED_METHODS)
                .registerType(ResponsePayload.class, MemberCategory.DECLARED_FIELDS)
                .registerType(ResponsePayload.class, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS)
                .registerType(ResponsePayload.class, MemberCategory.INVOKE_DECLARED_METHODS)


                .registerType(PricingElem.class, MemberCategory.INTROSPECT_DECLARED_CONSTRUCTORS)
                .registerType(PricingElem.class, MemberCategory.INTROSPECT_DECLARED_METHODS)
                .registerType(PricingElem.class, MemberCategory.DECLARED_FIELDS)
                .registerType(PricingElem.class, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS)
                .registerType(PricingElem.class, MemberCategory.INVOKE_DECLARED_METHODS)

                .registerType(org.apache.hadoop.conf.Configuration.class, MemberCategory.INTROSPECT_DECLARED_CONSTRUCTORS)
                .registerType(org.apache.hadoop.conf.Configuration.class, MemberCategory.INTROSPECT_DECLARED_METHODS)
                .registerType(org.apache.hadoop.conf.Configuration.class, MemberCategory.DECLARED_FIELDS)
                .registerType(org.apache.hadoop.conf.Configuration.class, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS)
                .registerType(org.apache.hadoop.conf.Configuration.class, MemberCategory.INVOKE_DECLARED_METHODS);
    }

}