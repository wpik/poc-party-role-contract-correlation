package com.github.wpik.poc.prc.events;

import com.github.wpik.poc.prc.model.Role;
import lombok.Data;
import lombok.NonNull;

@Data
public class RoleUpdateEvent extends AbstractEvent {
    public final static String NAME = "RoleUpdateEvent";

    private RoleUpdateEvent() {
        super(NAME);
    }

    public RoleUpdateEvent(@NonNull Role payload) {
        super(NAME);
        this.payload = payload;
    }

    @Override
    public String getKey() {
        return payload.getRoleKey();
    }

    @NonNull
    private Role payload;
}
