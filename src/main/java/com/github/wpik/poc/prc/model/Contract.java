package com.github.wpik.poc.prc.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class Contract {
    @Id
    private String contractKey;
    private boolean active;

    @JsonIgnore
    private int triplesCounter;
}
