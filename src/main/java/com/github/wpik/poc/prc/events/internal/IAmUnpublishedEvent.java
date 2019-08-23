package com.github.wpik.poc.prc.events.internal;

import com.github.wpik.poc.prc.events.AbstractEvent;
import lombok.Data;
import lombok.NonNull;

@Data
public class IAmUnpublishedEvent extends AbstractEvent {
    public final static String NAME = "IAmUnpublishedEvent";

    private IAmUnpublishedEvent() {
        super(NAME);
    }

    public IAmUnpublishedEvent(@NonNull String entityName, @NonNull String entityKey, @NonNull String roleKey) {
        super(NAME);
        this.entityName = entityName;
        this.entityKey = entityKey;
        this.roleKey = roleKey;
    }

    public static IAmUnpublishedEvent from(AreYouInDbEvent event) {
        return new IAmUnpublishedEvent(
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
