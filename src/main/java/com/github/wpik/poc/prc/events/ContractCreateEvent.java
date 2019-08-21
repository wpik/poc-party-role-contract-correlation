package com.github.wpik.poc.prc.events;

import com.github.wpik.poc.prc.model.Contract;
import lombok.Data;
import lombok.NonNull;

@Data
public class ContractCreateEvent extends AbstractEvent {
    public final static String NAME = "ContractCreateEvent";

    private ContractCreateEvent() {
        super(NAME);
    }

    public ContractCreateEvent(@NonNull Contract payload) {
        super(NAME);
        this.payload = payload;
    }

    @Override
    public String getKey() {
        return payload.getContractKey();
    }

    @NonNull
    private Contract payload;
}
