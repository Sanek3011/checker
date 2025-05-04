package org.example.model;


import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;


@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data

@AttributeOverrides({
        @AttributeOverride(name="key.propertyId", column=@Column(name="property_id")),
        @AttributeOverride(name="key.serverNumber", column=@Column(name="server")),
        @AttributeOverride(name="key.type", column=@Column(name="type")),
        @AttributeOverride(name="isAuction", column=@Column(name="is_auction"))
})
@Entity
@Table(name = "property", schema = "public")
public class Property {
    @EmbeddedId
    PropertyKey key;
    @Transient
    @JsonProperty("id")
    Integer propertyId;
    @Column(name = "owner")
    String owner;
    @Column(name = "name")
    String name;
    @Column(name = "is_auction")
    Boolean isAuction;
    @JsonIgnore
    @Transient
    Integer serverNumber;

    @Override
    public String toString() {
        int id = getKey().getPropertyId();
        String label = String.format("id: %-4d", id);
        if (Boolean.TRUE.equals(isAuction)) {
            label += "АУК";
        }
        if (key.getType().equals(Type.Business)) {
            label += name;
        }

        return String.format("%-15s ", label);
    }
}
