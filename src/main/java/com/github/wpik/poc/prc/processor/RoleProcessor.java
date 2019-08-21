package com.github.wpik.poc.prc.processor;

import com.github.wpik.poc.prc.TemporaryConverter;
import com.github.wpik.poc.prc.Topics;
import com.github.wpik.poc.prc.db.RoleRepository;
import com.github.wpik.poc.prc.events.*;
import com.github.wpik.poc.prc.model.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class RoleProcessor {
    private final TemporaryConverter temporaryConverter;

    private final RoleRepository roleRepository;

    @StreamListener
    @SendTo({Topics.PARTY_PROCESS_OUT_ROLE, Topics.CONTRACT_PROCESS_OUT_ROLE, Topics.CORRELATED_OUT_ROLE})
    public KStream<String, AbstractEvent>[] processRole(@Input(Topics.ROLE_PROCESS_IN) KStream<String, String> input) {
        return input
                .peek((k, v) -> log.debug("Received: key={}, value={}", k, v))
                .mapValues(temporaryConverter::decodeEvent)
                .flatMapValues(this::handleRoleEvent)
                .selectKey((k, v) -> v.getKey())
                .branch(
                        (k, v) -> v instanceof AreYouInDbEvent && ((AreYouInDbEvent) v).getEntityName().equals("party"),
                        (k, v) -> v instanceof AreYouInDbEvent && ((AreYouInDbEvent) v).getEntityName().equals("contract"),
                        (k, v) -> v instanceof CorrelatedOperationEvent
                );
    }

    private Iterable<AbstractEvent> handleRoleEvent(AbstractEvent event) {
        //FIXME those instanceof are ugly
        if (event instanceof RoleCreateEvent) {
            return handleEvent((RoleCreateEvent) event);
        } else if (event instanceof RoleUpdateEvent) {
            return handleEvent((RoleUpdateEvent) event);
        } else if (event instanceof RoleDeleteEvent) {
            return handleEvent((RoleDeleteEvent) event);
        }

        return List.of();
    }

    private Iterable<AbstractEvent> handleEvent(RoleCreateEvent event) {
        Role role = updateRoleInDbAndReturnIt(event.getPayload());

        var result = new ArrayList<AbstractEvent>();
        if (!role.isPartyInDb()) {
            result.add(new AreYouInDbEvent("party", role.getPartyKey(), role.getRoleKey()));
        }
        if (!role.isContractInDb()) {
            result.add(new AreYouInDbEvent("contract", role.getContractKey(), role.getRoleKey()));
        }
        return result;
    }

    private Role updateRoleInDbAndReturnIt(Role role) {
        Optional<Role> roleDbOptional = roleRepository.findById(role.getRoleKey());
        if (roleDbOptional.isPresent()) {
            Role roleDb = roleDbOptional.get();
            if (!role.getPartyKey().equals(roleDb.getPartyKey()) ||
                    !role.getContractKey().equals(roleDb.getContractKey())) {
                log.warn("Received role event with mismatched partyKey/contractKey");
            }
            roleDb.setType(role.getType());
            return roleRepository.save(roleDb);
        } else {
            return roleRepository.save(role);
        }
    }

    private Iterable<AbstractEvent> handleEvent(RoleUpdateEvent event) {
        Role role = updateRoleInDbAndReturnIt(event.getPayload());

        if (role.isPartyInCorrelated() && role.isContractInCorrelated()) {
            return List.of(new CorrelatedOperationEvent("Upsert role " + role.toString()));
        } else {
            return List.of();
        }
    }

    private Iterable<AbstractEvent> handleEvent(RoleDeleteEvent event) {
        String roleKey = event.getPayload().getRoleKey();

        Optional<Role> roleInDbOptional = roleRepository.findById(roleKey);

        //FIXME better optional handling
//        if (roleInDbOptional.isPresent()) {
//            Role roleInDb = roleInDbOptional.get();
            //TODO notify party/contract to remove from correlated
//        }

        roleRepository.deleteById(roleKey);

        return List.of();
    }
}
