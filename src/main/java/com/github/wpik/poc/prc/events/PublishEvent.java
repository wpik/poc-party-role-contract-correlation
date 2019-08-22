package com.github.wpik.poc.prc.events;

import lombok.Data;
import lombok.NonNull;

@Data
public class PublishEvent extends AbstractEvent {
    public final static String NAME = "PublishEvent";

    private PublishEvent() {
        super(NAME);
    }

    public PublishEvent(String payload) {
        super(NAME);
        this.payload = payload;
    }

    @NonNull
    private String payload;
}
