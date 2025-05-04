package org.example.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Data

@Embeddable
public class PropertyKey implements Serializable {
    Integer propertyId;
    Integer serverNumber;
    @Enumerated(EnumType.STRING)
    Type type;

}
