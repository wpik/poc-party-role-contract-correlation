package com.github.wpik.poc.prc.events;

import com.github.wpik.poc.prc.events.payload.RoleDeletePayload;
import lombok.Data;
import lombok.NonNull;

@Data
public class RoleDeleteEvent extends AbstractEvent {
    public final static String NAME = "RoleDeleteEvent";

    private RoleDeleteEvent() {
        super(NAME);
    }

    public RoleDeleteEvent(@NonNull RoleDeletePayload payload) {
        super(NAME);
        this.payload = payload;
    }

    @Override
    public String getKey() {
        return payload.getRoleKey();
    }

    @NonNull
    private RoleDeletePayload payload;
}
