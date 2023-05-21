package com.example.demo.domain;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;

import java.util.HashMap;
import java.util.Map;

//@Data
@RegisterReflectionForBinding
//@AllArgsConstructor(onConstructor_ = {@JsonCreator(mode = JsonCreator.Mode.PROPERTIES)})

public class PricingElem {
    private Map<String, Object> props = new HashMap<>();

    @JsonAnyGetter
    public Map<String, Object> props() {
        return props;
    }

    @JsonAnySetter
    public void props(String key, Object value) {
        props.put(key, value);
    }

//    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
//    public PricingElem(Map<String, Object> props) {
//        this.props = props;
//    }


}
