package com.github.wpik.poc.prc.events;

import com.github.wpik.poc.prc.model.Contract;
import lombok.Data;
import lombok.NonNull;

@Data
public class ContractUpdateEvent extends AbstractEvent {
    public final static String NAME = "ContractUpdateEvent";

    private ContractUpdateEvent() {
        super(NAME);
    }

    public ContractUpdateEvent(@NonNull Contract payload) {
        super(NAME);
        this.payload = payload;
    }

    @Override
    public String getKey() {
        return getPayload().getContractKey();
    }

    @NonNull
    private Contract payload;
}
