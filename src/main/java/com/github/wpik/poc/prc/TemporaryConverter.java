package com.github.wpik.poc.prc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.wpik.poc.prc.events.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class TemporaryConverter {
    private final ObjectMapper objectMapper;

    private Map<String, Class<? extends AbstractEvent>> eventToClassMap = new HashMap<>();

    @PostConstruct
    void initEvents() {
        new Reflections(Application.class.getPackage().getName())
                .getSubTypesOf(AbstractEvent.class)
                .stream()
                .filter(c -> !c.equals(AbstractEvent.class))
                .forEach(c -> {
                    try {
                        String name = c.getField("NAME").get(1).toString();
                        eventToClassMap.put(name, c);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

        eventToClassMap.forEach((k, v) -> log.info("Event found: name={}, class={}", k, v));
    }

    public AbstractEvent decodeEvent(String s) {
        log.debug("decodeEvent {}", s);
        AbstractEvent abstractEvent = fromString(s, AbstractEvent.class);
        if (eventToClassMap.containsKey(abstractEvent.getEvent())) {
            return fromString(s, eventToClassMap.get(abstractEvent.getEvent()));
        } else {
            throw new UnsupportedOperationException("Unknown event: " + abstractEvent.getEvent());
        }
    }

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

    public ContractCreateEvent stringToContractEvent(String s) {
        return fromString(s, ContractCreateEvent.class);
    }

    private <T> T fromString(String s, Class<T> clazz) {
        try {
            return objectMapper.readValue(s, clazz);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
