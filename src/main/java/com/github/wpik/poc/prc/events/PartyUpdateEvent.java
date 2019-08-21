package com.github.wpik.poc.prc.events;

import com.github.wpik.poc.prc.model.Party;
import lombok.Data;
import lombok.NonNull;

@Data
public class PartyUpdateEvent extends AbstractEvent {
    public final static String NAME = "PartyUpdateEvent";

    private PartyUpdateEvent() {
        super(NAME);
    }

    public PartyUpdateEvent(@NonNull Party payload) {
        super(NAME);
        this.payload = payload;
    }

    @Override
    public String getKey() {
        return getPayload().getPartyKey();
    }

    @NonNull
    private Party payload;
}
