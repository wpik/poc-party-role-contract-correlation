package com.github.wpik.poc.prc.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class Role {
    @Id
    private String roleKey;
    private String partyKey;
    private String contractKey;
    private Integer type;

    @JsonIgnore
    private boolean partyInDb;
    @JsonIgnore
    private boolean contractInDb;
    @JsonIgnore
    private boolean partyPublished;
    @JsonIgnore
    private boolean contractPublished;

    public void update(Role other) {
        this.type = other.type;
    }
}
