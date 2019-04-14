package com.github.wpik.poc.prc.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class Party {
    @Id
    private String partyKey;
    private String name;

    @JsonIgnore
    private int triplesCounter;
}
