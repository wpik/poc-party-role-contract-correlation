package com.github.wpik.poc.prc.events;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AbstractEvent {
    @NonNull
    private String event;

    @JsonIgnore
    public String getKey() {
        return "";
    }
}
