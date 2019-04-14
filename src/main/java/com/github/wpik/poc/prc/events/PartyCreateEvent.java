package com.github.wpik.poc.prc.events;

import com.github.wpik.poc.prc.model.Party;
import lombok.Data;
import lombok.NonNull;

@Data
public class PartyCreateEvent extends AbstractEvent {
    public final static String NAME = "PartyCreateEvent";

    private PartyCreateEvent() {
        super(NAME);
    }

    public PartyCreateEvent(@NonNull Party payload) {
        super(NAME);
        this.payload = payload;
    }

    @NonNull
    private Party payload;
}
