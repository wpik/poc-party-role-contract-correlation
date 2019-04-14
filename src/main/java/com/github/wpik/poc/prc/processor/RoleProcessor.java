package com.github.wpik.poc.prc.processor;

import com.github.wpik.poc.prc.TemporaryConverter;
import com.github.wpik.poc.prc.Topics;
import com.github.wpik.poc.prc.db.RoleRepository;
import com.github.wpik.poc.prc.events.AreYouInDbEvent;
import com.github.wpik.poc.prc.events.RoleCreateEvent;
import com.github.wpik.poc.prc.model.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class RoleProcessor {
    private final TemporaryConverter temporaryConverter;

    private final RoleRepository roleRepository;

    @StreamListener
    @SendTo({Topics.PARTY_PROCESS_OUT_ROLE, Topics.CONTRACT_PROCESS_OUT_ROLE})
    public KStream<String, AreYouInDbEvent>[] processRole(@Input(Topics.ROLE_PROCESS_IN) KStream<String, String> input) {
        return input
                .peek((k, v) -> log.debug("Received: key={}, value={}", k, v))
                .mapValues(temporaryConverter::stringToRoleEvent)
                .filter((k, v) -> v instanceof RoleCreateEvent)
                .mapValues(v -> (RoleCreateEvent) v)
                .peek((k, v) -> log.debug("IN: Role processor received {}-{}", k, v))
                .mapValues(this::updateDbAndReturnRoleFromDb)
                .flatMapValues(this::mapToAreYouInDb)
                .filter((k, v) -> v != null)
                .selectKey((k, v) -> v.getEntityName())
                .branch(
                        (k, v) -> v.getEntityName().equals("party"),
                        (k, v) -> v.getEntityName().equals("contract")
                );
    }

    private Role updateDbAndReturnRoleFromDb(RoleCreateEvent event) {
        Optional<Role> roleDbOptional = roleRepository.findById(event.getPayload().getRoleKey());
        if (roleDbOptional.isPresent()) {
            Role roleDb = roleDbOptional.get();
            if (event.getPayload().getPartyKey() != roleDb.getPartyKey() || event.getPayload().getContractKey() != roleDb.getContractKey()) {
                log.warn("Received role event with mismatched partyKey/contractKey");
            }
            roleDb.setType(event.getPayload().getType());
            return roleRepository.save(roleDb);
        } else {
            return roleRepository.save(event.getPayload());
        }
    }

    private Iterable<AreYouInDbEvent> mapToAreYouInDb(Role role) {
        var result = new ArrayList<AreYouInDbEvent>();

        if (!role.isPartyInDb()) {
            result.add(new AreYouInDbEvent("party", role.getPartyKey(), role.getRoleKey()));
        }
        if (!role.isContractInDb()) {
            result.add(new AreYouInDbEvent("contract", role.getContractKey(), role.getRoleKey()));
        }
        return result;
    }
}
