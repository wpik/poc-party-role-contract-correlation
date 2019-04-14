package com.github.wpik.poc.prc.events;

import com.github.wpik.poc.prc.model.Role;
import lombok.Data;
import lombok.NonNull;

@Data
public class RoleCreateEvent extends AbstractEvent {
    public final static String NAME = "RoleCreateEvent";

    private RoleCreateEvent() {
        super(NAME);
    }

    public RoleCreateEvent(@NonNull Role payload) {
        super(NAME);
        this.payload = payload;
    }

    @NonNull
    private Role payload;
}
