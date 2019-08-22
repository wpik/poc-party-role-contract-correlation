package com.github.wpik.poc.prc.events.internal;

import com.github.wpik.poc.prc.events.AbstractEvent;
import lombok.Data;
import lombok.NonNull;

@Data
public class AreYouInDbEvent extends AbstractEvent {
    public final static String NAME = "AreYouInDbEvent";

    private AreYouInDbEvent() {
        super(NAME);
    }

    public AreYouInDbEvent(@NonNull String entityName, @NonNull String entityKey, @NonNull String roleKey) {
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
