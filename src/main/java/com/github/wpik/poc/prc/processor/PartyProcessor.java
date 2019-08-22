package com.github.wpik.poc.prc.processor;

import com.github.wpik.poc.prc.TemporaryConverter;
import com.github.wpik.poc.prc.Topics;
import com.github.wpik.poc.prc.db.PartyRepository;
import com.github.wpik.poc.prc.db.RoleRepository;
import com.github.wpik.poc.prc.events.*;
import com.github.wpik.poc.prc.events.internal.*;
import com.github.wpik.poc.prc.model.Party;
import com.github.wpik.poc.prc.model.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

@Component
@RequiredArgsConstructor
@Slf4j
public class PartyProcessor {
    private final TemporaryConverter temporaryConverter;
    private final PartyRepository partyRepository;
    //FIXME is it ok to access roles here?
    private final RoleRepository roleRepository;

    @StreamListener
    @SendTo({Topics.ROLE_PROCESS_OUT_PARTY, Topics.PUBLISHED_OUT_PARTY})
    public KStream<String, AbstractEvent>[] processParty(@Input(Topics.PARTY_PROCESS_IN) KStream<String, String> input) {
        return input
                .peek((k, v) -> log.debug("Received: key={}, value={}", k, v))
                .mapValues(temporaryConverter::decodeEvent)
                .flatMapValues(this::handlePartyEvent)
                .selectKey((k, v) -> v.getKey())
                .branch(
                        (k, v) -> v instanceof IAmInDbEvent || v instanceof IAmRemovedFromDbEvent || v instanceof IAmPublishedEvent,
                        (k, v) -> v instanceof PublishEvent || v instanceof UnpublishEvent);
    }

    private Iterable<AbstractEvent> handlePartyEvent(AbstractEvent event) {
        //FIXME those instanceof are ugly
        if (event instanceof PartyCreateEvent) {
            return handleEvent((PartyCreateEvent) event);
        } else if (event instanceof PartyUpdateEvent) {
            return handleEvent((PartyUpdateEvent) event);
        } else if (event instanceof PartyDeleteEvent) {
            return handleEvent((PartyDeleteEvent) event);
        } else if (event instanceof AreYouInDbEvent) {
            return handleEvent((AreYouInDbEvent) event);
        } else if (event instanceof NewTripleEvent) {
            return handleEvent((NewTripleEvent) event);
        } else if (event instanceof DeleteTripleEvent) {
            return handleEvent((DeleteTripleEvent) event);
        }

        return List.of();
    }

    private Iterable<AbstractEvent> handleEvent(PartyCreateEvent event) {
        Optional<Party> partyFromDb = partyRepository.findById(event.getPayload().getPartyKey());

        partyRepository.save(event.getPayload());

        Iterable<Role> roles = partyFromDb
                .map((p) -> (Iterable)Collections.emptyList())
                .orElseGet(() -> roleRepository.findByPartyKey(event.getPayload().getPartyKey()));

        return StreamSupport.stream(roles.spliterator(), false)
                .map(r -> new IAmInDbEvent("party", r.getPartyKey(), r.getRoleKey()))
                .collect(toList());
    }

    private Iterable<AbstractEvent> handleEvent(PartyUpdateEvent event) {
        return partyRepository
                .findById(event.getPayload().getPartyKey())
                .map(party -> {
                    party.update(event.getPayload());
                    partyRepository.save(party);
                    if (party.getTriplesCounter() > 0) {
                        return List.<AbstractEvent>of(new PublishEvent(party.toString()));
                    } else {
                        return List.<AbstractEvent>of();
                    }
                })
                .orElse(List.of());
    }

    private Iterable<AbstractEvent> handleEvent(PartyDeleteEvent event) {
        var partyKey = event.getPayload().getPartyKey();

        partyRepository.deleteById(partyKey);

        Iterable<Role> roles = roleRepository.findByPartyKey(partyKey);

        var resultEvents = StreamSupport.stream(roles.spliterator(), false)
                .map(r -> new IAmRemovedFromDbEvent("party", partyKey, r.getRoleKey()))
                .map(e->(AbstractEvent)e)
                .collect(toList());

        resultEvents.add(new UnpublishEvent("party", partyKey));

        return resultEvents;
    }

    private Iterable<AbstractEvent> handleEvent(AreYouInDbEvent event) {
        return partyRepository
                .findById(event.getEntityKey())
                .map(x -> List.<AbstractEvent>of(IAmInDbEvent.from(event)))
                .orElse(List.of());
    }

    private Iterable<AbstractEvent> handleEvent(NewTripleEvent event) {
        return partyRepository
                .findById(event.getEntityKey())
                .map(party -> {
                    party.incrementTriplesCounter();
                    partyRepository.save(party);

                    List<AbstractEvent> resultEvents = new ArrayList<>();
                    if (party.getTriplesCounter() == 1) {
                        resultEvents.add(new PublishEvent(party.toString()));
                    }
                    resultEvents.add(new IAmPublishedEvent("party", party.getPartyKey(), event.getRoleKey()));
                    return resultEvents;
                })
                .orElse(List.of());
    }

    private Iterable<AbstractEvent> handleEvent(DeleteTripleEvent event) {
        return partyRepository
                .findById(event.getEntityKey())
                .map(party -> {
                    party.decrementTriplesCounter();
                    partyRepository.save(party);

                    if (party.getTriplesCounter() == 0) {
                        return List.<AbstractEvent>of(new UnpublishEvent("party", party.getPartyKey()));
                    } else {
                        return List.<AbstractEvent>of();
                    }
                })
                .orElse(List.of());
    }
}
