package org.demo.transport.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

/**
 * Represents a Transport entity.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Transport(
        Long id,
        String description,
        Type type,
        @JsonProperty("starttimestamp") LocalDateTime startTimestamp,
        @JsonProperty("endtimestamp") LocalDateTime endTimestamp) {

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    public enum Type {
        AIRPLANE("Airplane"), SHIP("Ship"), TRAIN("Train"), TRUCK("Truck");

        @JsonValue
        private final String value;

        @JsonCreator
        public static Type fromValue(final String value) {
            for (Type type : values()) {
                if (type.value.equalsIgnoreCase(value)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Unknown transport type '%s'. ".formatted(value));
        }

        @Override
        public String toString() {
            return value;
        }
    }
}
