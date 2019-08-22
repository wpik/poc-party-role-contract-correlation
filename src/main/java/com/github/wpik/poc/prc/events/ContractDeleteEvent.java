package com.github.wpik.poc.prc.events;

import com.github.wpik.poc.prc.events.payload.ContractDeletePayload;
import lombok.Data;
import lombok.NonNull;

@Data
public class ContractDeleteEvent extends AbstractEvent {
    public final static String NAME = "ContractDeleteEvent";

    private ContractDeleteEvent() {
        super(NAME);
    }

    public ContractDeleteEvent(@NonNull ContractDeletePayload payload) {
        super(NAME);
        this.payload = payload;
    }

    @Override
    public String getKey() {
        return payload.getContractKey();
    }

    @NonNull
    private ContractDeletePayload payload;
}
