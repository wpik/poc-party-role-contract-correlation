package com.github.wpik.poc.prc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.wpik.poc.prc.events.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UncheckedIOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class TemporaryConverter {
    private final ObjectMapper objectMapper;

    public AbstractEvent stringToPartyEvent(String s) {
        log.debug("stringToPartyEvent {}", s);
        AbstractEvent abstractEvent = fromString(s, AbstractEvent.class);
        switch (abstractEvent.getEvent()) {
            case PartyCreateEvent.NAME:
                return fromString(s, PartyCreateEvent.class);
            case PartyUpdateEvent.NAME:
                return fromString(s, PartyUpdateEvent.class);
            case PartyDeleteEvent.NAME:
                return fromString(s, PartyDeleteEvent.class);
            case AreYouInDbEvent.NAME:
                return fromString(s, AreYouInDbEvent.class);
            default:
                throw new UnsupportedOperationException("Unknown event: " + abstractEvent.getEvent());
        }
    }

    public AbstractEvent stringToRoleEvent(String s) {
        log.debug("stringToRoleEvent {}", s);
        AbstractEvent abstractEvent = fromString(s, AbstractEvent.class);
        switch (abstractEvent.getEvent()) {
            case RoleCreateEvent.NAME:
                return fromString(s, RoleCreateEvent.class);
            case IAmInDbEvent.NAME:
                return fromString(s, IAmInDbEvent.class);
            default:
                throw new UnsupportedOperationException("Unknown event: " + abstractEvent.getEvent());
        }
    }

    public ContractEvent stringToContractEvent(String s) {
        return fromString(s, ContractEvent.class);
    }

    private <T> T fromString(String s, Class<T> clazz) {
        try {
            return objectMapper.readValue(s, clazz);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
