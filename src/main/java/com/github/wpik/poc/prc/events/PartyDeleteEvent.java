package com.github.wpik.poc.prc.events;

import com.github.wpik.poc.prc.events.payload.PartyDeletePayload;
import lombok.Data;
import lombok.NonNull;

@Data
public class PartyDeleteEvent extends AbstractEvent {
    public final static String NAME = "PartyDeleteEvent";

    private PartyDeleteEvent() {
        super(NAME);
    }

    public PartyDeleteEvent(@NonNull PartyDeletePayload payload) {
        super(NAME);
        this.payload=payload;
    }

    @Override
    public String getKey() {
        return payload.getPartyKey();
    }

    @NonNull
    private PartyDeletePayload payload;
}
