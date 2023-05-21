package com.example.demo.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;

import java.util.List;

@Data
//@RegisterReflectionForBinding
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor(onConstructor_ = {@JsonCreator(mode = JsonCreator.Mode.PROPERTIES)})
public class SomeSystemResponse {

    @JsonProperty("Response")
    private ResponsePayload responsePayload;

    @JsonProperty("pricingLineList")
    private List<PricingElem> pricingLineList;

}
