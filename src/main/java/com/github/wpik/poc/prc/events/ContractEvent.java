package com.github.wpik.poc.prc.events;

import com.github.wpik.poc.prc.model.Contract;
import lombok.Data;
import lombok.NonNull;

@Data
public class ContractEvent extends AbstractEvent {
    public final static String NAME = "ContractEvent";

    private ContractEvent() {
        super(NAME);
    }

    public ContractEvent(@NonNull Contract payload) {
        super(NAME);
        this.payload = payload;
    }

    @NonNull
    private Contract payload;
}
