package org.example.model.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.example.model.Property;

import java.util.List;
import java.util.Map;

@Data
public class BusinessData {
    List<Property> onAuction;
    @JsonProperty("noAuction")
    private Map<String, List<Property>> noAuction;
}
