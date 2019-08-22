package com.github.wpik.poc.prc.events;

import lombok.Data;
import lombok.NonNull;

@Data
public class UnpublishEvent extends AbstractEvent {
    public final static String NAME = "UnpublishEvent";

    private UnpublishEvent() {
        super(NAME);
    }

    public UnpublishEvent(@NonNull String entityName, @NonNull String entityKey) {
        super(NAME);
        this.entityName = entityName;
        this.entityKey = entityKey;
    }

    @NonNull
    private String entityName;

    @NonNull
    private String entityKey;
}
