package com.github.wpik.poc.prc.events;

import lombok.Data;
import lombok.NonNull;

@Data
public class IAmInDbEvent extends AbstractEvent {
    public final static String NAME = "IAmInDbEvent";

    private IAmInDbEvent() {
        super(NAME);
    }

    public IAmInDbEvent(@NonNull String entityName, @NonNull String entityKey, @NonNull String roleKey) {
        super(NAME);
        this.entityName = entityName;
        this.entityKey = entityKey;
        this.roleKey = roleKey;
    }

    public static IAmInDbEvent from(AreYouInDbEvent event) {
        return new IAmInDbEvent(
                event.getEntityName(),
                event.getEntityKey(),
                event.getRoleKey()
        );
    }

    @NonNull
    private String entityName;
    @NonNull
    private String entityKey;
    @NonNull
    private String roleKey;
}
