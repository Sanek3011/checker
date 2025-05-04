package org.example.model.util;

import lombok.Data;
import org.example.model.Property;

import java.util.List;

@Data

public class HouseData {
    List<Property> hasOwner;
    List<Property> noOwner;
    List<Property> onAuction;
}
