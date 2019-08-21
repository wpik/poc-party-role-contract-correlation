package com.github.wpik.poc.prc.events;

import lombok.Data;
import lombok.NonNull;

@Data
public class CorrelatedOperationEvent extends AbstractEvent {
    public final static String NAME = "CorrelatedOperationEvent";

    private CorrelatedOperationEvent() {
        super(NAME);
    }

    public CorrelatedOperationEvent(String payload) {
        super(NAME);
        this.payload = payload;
    }

    @Override
    public String getKey() {
        return payload;
    }

    @NonNull
    private String payload;
}
