package com.github.wpik.poc.prc.events;

import lombok.Data;
import lombok.NonNull;

@Data
public class NewTripleEvent extends AbstractEvent {
    public final static String NAME = "NewTripleEvent";

    private NewTripleEvent() {
        super(NAME);
    }

    public NewTripleEvent(@NonNull String entityName, @NonNull String entityKey, @NonNull String roleKey) {
        super(NAME);
        this.entityName = entityName;
        this.entityKey = entityKey;
        this.roleKey = roleKey;
    }

    @Override
    public String getKey() {
        return entityKey;
    }

    @NonNull
    private String entityName;

    @NonNull
    private String entityKey;

    @NonNull
    private String roleKey;
}
