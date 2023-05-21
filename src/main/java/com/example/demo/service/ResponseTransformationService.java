package com.example.demo.service;

import com.example.demo.domain.ResponsePayload;
import com.example.demo.domain.SomeSystemResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import static java.util.Optional.ofNullable;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResponseTransformationService {
    private static final Log LOG = LogFactory.getLog(ResponseTransformationService.class);

    /**
     * Builds a map of column names to their corresponding data types based on a given system response.
     *
     * @param response The system response from which to extract column names and data types.
     * @return A map where the keys are column names and the values are the corresponding data types.
     * @throws NullPointerException if the response is null or if the pricing list cannot be found in the response.
     * */
    @Nonnull

    public Map<String, Class<?>> buildColumnsType(@Nonnull SomeSystemResponse response) {
        LOG.info("test");
        Objects.requireNonNull(response);

        var pricingList = ofNullable(response.getResponsePayload())
                .map(ResponsePayload::getPricingLineList)
                .orElse(response.getPricingLineList());

        Objects.requireNonNull(pricingList, "can't find any pricing list");

        return pricingList.stream()
                .flatMap(pe -> pe.props()
                        .entrySet()
                        .stream()
                        .filter(entry -> entry.getValue() != null))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().getClass(),
                        (left, right) -> left));
    }
}
