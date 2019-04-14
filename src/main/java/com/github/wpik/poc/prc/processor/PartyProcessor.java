package com.github.wpik.poc.prc.processor;

import com.github.wpik.poc.prc.TemporaryConverter;
import com.github.wpik.poc.prc.Topics;
import com.github.wpik.poc.prc.db.PartyRepository;
import com.github.wpik.poc.prc.db.RoleRepository;
import com.github.wpik.poc.prc.events.*;
import com.github.wpik.poc.prc.model.Party;
import com.github.wpik.poc.prc.model.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

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
    @SendTo({Topics.ROLE_PROCESS_OUT_PARTY_1, Topics.CORRELATED_OUT_PARTY})
    public KStream<String, AbstractEvent>[] processParty(@Input(Topics.PARTY_PROCESS_IN) KStream<String, String> input) {
        var branches = input
                .mapValues(temporaryConverter::stringToPartyEvent)
                .flatMapValues(this::handlePartyEvent)
                .branch(
                        (k, v) -> v instanceof IAmInDbEvent,
                        (k, v) -> v instanceof CorrelatedOperationEvent);

        branches[0] = branches[0].selectKey((k, v) -> ((IAmInDbEvent) v).getRoleKey());
        branches[1] = branches[1].selectKey((k, v) -> ((CorrelatedOperationEvent) v).getPayload());

        return branches;
    }

    private Iterable<AbstractEvent> handlePartyEvent(AbstractEvent event) {
        //FIXME those instanceof are ugly
        if (event instanceof PartyCreateEvent) {
            return handleCreateEvent((PartyCreateEvent) event);
        } else if (event instanceof PartyUpdateEvent) {
            return handleUpdateEvent((PartyUpdateEvent) event);
        } else if (event instanceof PartyDeleteEvent) {
            return handleDeleteEvent((PartyDeleteEvent) event);
        }

        return List.of();
    }

    private Iterable<AbstractEvent> handleCreateEvent(PartyCreateEvent event) {
        log.info("handleCreateEvent {}", event);
        Optional<Party> partyFromDb = partyRepository.findById(event.getPayload().getPartyKey());

        partyRepository.save(event.getPayload());

        Iterable<Role> roles = partyFromDb
                .map((p) -> (Iterable)Collections.emptyList())
                .orElseGet(() -> roleRepository.findByPartyKey(event.getPayload().getPartyKey()));

        return StreamSupport.stream(roles.spliterator(), false)
                .map(r -> new IAmInDbEvent("party", r.getRoleKey(), event.getPayload().getPartyKey()))
                .collect(toList());
    }

    private Iterable<AbstractEvent> handleUpdateEvent(PartyUpdateEvent event) {
        log.info("handleUpdateEvent {}", event);

        Optional<Party> partyFromDb = partyRepository.findById(event.getPayload().getPartyKey());

        //FIXME better optional handling
        if (partyFromDb.isPresent()) {
            if (partyFromDb.get().getTriplesCounter() > 0) {
                return List.of(new CorrelatedOperationEvent("Upsert " + partyFromDb.get().toString()));
            }
        }

        return List.of();
    }

    private Iterable<AbstractEvent> handleDeleteEvent(PartyDeleteEvent event) {
        log.info("handleDeleteEvent {}", event);

        var partyKey = event.getPayload().getPartyKey();

        Iterable<Role> roles = roleRepository.findByPartyKey(partyKey);

        var resultEvents = StreamSupport.stream(roles.spliterator(), false)
                .map(r -> new IAmRemovedFromDbEvent("party", partyKey, r.getRoleKey()))
                .map(e->(AbstractEvent)e)
                .collect(toList());

        resultEvents.add(new CorrelatedOperationEvent("Delete party: " + partyKey));

        return resultEvents;
    }

    @StreamListener
    @SendTo(Topics.ROLE_PROCESS_OUT_PARTY_2)
    public KStream<String, IAmInDbEvent> processPartyAreYouInDb(@Input(Topics.PARTY_PROCESS_IN_ROLE) KStream<String, String> input) {
        return input
                .mapValues(temporaryConverter::stringToPartyEvent)
                .filter((k, v) -> v instanceof AreYouInDbEvent)
                .mapValues(v -> (AreYouInDbEvent) v)
                .peek((k, v) -> log.debug("PARTY_PROCESS_IN_ROLE: Party processor received {}-{}", k, v))
                .mapValues(this::checkPartyInDbAndCreateResponse)
                .filter((k, v) -> v != null);
    }

    private IAmInDbEvent checkPartyInDbAndCreateResponse(AreYouInDbEvent event) {
        return partyRepository
                .findById(event.getEntityKey())
                .map(x -> IAmInDbEvent.from(event))
                .orElse(null);
    }
}
