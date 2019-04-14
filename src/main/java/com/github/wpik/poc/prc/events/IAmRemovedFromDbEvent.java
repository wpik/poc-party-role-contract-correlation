package com.github.wpik.poc.prc.events;

import lombok.Data;
import lombok.NonNull;

@Data
public class IAmRemovedFromDbEvent extends AbstractEvent {
    public final static String NAME = "IAmRemovedFromDbEvent";

    private IAmRemovedFromDbEvent() {
        super(NAME);
    }

    public IAmRemovedFromDbEvent(@NonNull String entityName, @NonNull String entityKey, @NonNull String roleKey) {
        super(NAME);
        this.entityName = entityName;
        this.entityKey = entityKey;
        this.roleKey = roleKey;
    }

    @NonNull
    private String entityName;
    @NonNull
    private String entityKey;
    @NonNull
    private String roleKey;
}
