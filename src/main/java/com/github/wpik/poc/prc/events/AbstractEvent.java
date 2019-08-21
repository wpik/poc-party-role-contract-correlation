package com.github.wpik.poc.prc.events;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AbstractEvent {
    @NonNull
    private String event;

    public String getKey() {
        return "";
    }
}
