package com.github.wpik.poc.prc.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Data
public class Contract {
    @Id
    private String contractKey;
    private boolean active;

    @JsonIgnore
    @Setter(AccessLevel.PRIVATE)
    private int triplesCounter;

    public void incrementTriplesCounter() {
        triplesCounter++;
    }

    public void decrementTriplesCounter() {
        triplesCounter--;
    }
}
