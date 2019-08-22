package com.github.wpik.poc.prc.events.internal;

import com.github.wpik.poc.prc.events.AbstractEvent;
import lombok.Data;
import lombok.NonNull;

@Data
public class IAmPublishedEvent extends AbstractEvent {
    public final static String NAME = "IAmPublishedEvent";

    private IAmPublishedEvent() {
        super(NAME);
    }

    public IAmPublishedEvent(@NonNull String entityName, @NonNull String entityKey, @NonNull String roleKey) {
        super(NAME);
        this.entityName = entityName;
        this.entityKey = entityKey;
        this.roleKey = roleKey;
    }

    public static IAmPublishedEvent from(AreYouInDbEvent event) {
        return new IAmPublishedEvent(
                event.getEntityName(),
                event.getEntityKey(),
                event.getRoleKey()
        );
    }

    @Override
    public String getKey() {
        return roleKey;
    }

    @NonNull
    private String entityName;
    @NonNull
    private String entityKey;
    @NonNull
    private String roleKey;
}
