package com.github.wpik.poc.prc.processor;

import com.github.wpik.poc.prc.TemporaryConverter;
import com.github.wpik.poc.prc.Topics;
import com.github.wpik.poc.prc.db.RoleRepository;
import com.github.wpik.poc.prc.events.*;
import com.github.wpik.poc.prc.events.internal.AreYouInDbEvent;
import com.github.wpik.poc.prc.events.internal.IAmInDbEvent;
import com.github.wpik.poc.prc.events.internal.IAmPublishedEvent;
import com.github.wpik.poc.prc.events.internal.NewTripleEvent;
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
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

@Component
@RequiredArgsConstructor
@Slf4j
public class RoleProcessor {
    private final TemporaryConverter temporaryConverter;

    private final RoleRepository roleRepository;

    @StreamListener
    @SendTo({Topics.PARTY_PROCESS_OUT_ROLE, Topics.CONTRACT_PROCESS_OUT_ROLE, Topics.PUBLISHED_OUT_ROLE})
    public KStream<String, AbstractEvent>[] processRole(@Input(Topics.ROLE_PROCESS_IN) KStream<String, String> input) {
        return input
                .peek((k, v) -> log.debug("Received: key={}, value={}", k, v))
                .mapValues(temporaryConverter::decodeEvent)
                .flatMapValues(this::handleRoleEvent)
                .selectKey((k, v) -> v.getKey())
                .branch(
                        (k, v) -> v instanceof AreYouInDbEvent && ((AreYouInDbEvent) v).getEntityName().equals("party")
                                || v instanceof NewTripleEvent && ((NewTripleEvent) v).getEntityName().equals("party"),
                        (k, v) -> v instanceof AreYouInDbEvent && ((AreYouInDbEvent) v).getEntityName().equals("contract")
                                || v instanceof NewTripleEvent && ((NewTripleEvent) v).getEntityName().equals("contract"),
                        (k, v) -> v instanceof PublishEvent
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
        } else if (event instanceof IAmInDbEvent) {
            return handleEvent((IAmInDbEvent) event);
        } else if (event instanceof IAmPublishedEvent) {
            return handleEvent((IAmPublishedEvent) event);
        }

        return List.of();
    }


    private Iterable<AbstractEvent> handleEvent(RoleCreateEvent event) {
        return handleCreateUpdateEvent(event.getPayload());
    }

    private Iterable<AbstractEvent> handleEvent(RoleUpdateEvent event) {
        return handleCreateUpdateEvent(event.getPayload());
    }

    private Iterable<AbstractEvent> handleCreateUpdateEvent(Role payload) {
        return roleRepository.findById(payload.getRoleKey())
                .map(handleCreateUpdateEventWhenPartyInDb(payload))
                .orElseGet(handleCreateUpdateEventWhenNoPartyInDb(payload));
    }

    private Function<Role, List<AbstractEvent>> handleCreateUpdateEventWhenPartyInDb(Role payload) {
        return role -> {
            role.update(payload);
            roleRepository.save(role);

            if (role.isPartyPublished() && role.isContractPublished()) {
                return List.of(new PublishEvent(role.toString()));
            } else {
                return List.of();
            }
        };
    }

    private Supplier<List<AbstractEvent>> handleCreateUpdateEventWhenNoPartyInDb(Role payload) {
        return () -> {
            Role role = roleRepository.save(payload);

            var result = new ArrayList<AbstractEvent>();
            if (!role.isPartyInDb()) {
                result.add(new AreYouInDbEvent("party", role.getPartyKey(), role.getRoleKey()));
            }
            if (!role.isContractInDb()) {
                result.add(new AreYouInDbEvent("contract", role.getContractKey(), role.getRoleKey()));
            }
            return result;
        };
    }

    private Iterable<AbstractEvent> handleEvent(RoleDeleteEvent event) {
        String roleKey = event.getPayload().getRoleKey();

        Optional<Role> roleInDbOptional = roleRepository.findById(roleKey);

        //FIXME better optional handling
//        if (roleInDbOptional.isPresent()) {
//            Role roleInDb = roleInDbOptional.get();
            //TODO notify party/contract to unpublish themselves
//        }

        roleRepository.deleteById(roleKey);

        return List.of();
    }

    private Iterable<AbstractEvent> handleEvent(IAmInDbEvent event) {
        return roleRepository
                .findById(event.getRoleKey())
                .map(role -> {
                    if (event.getEntityName().equals("party")) {
                        role.setPartyInDb(true);
                        roleRepository.save(role);
                    } else if (event.getEntityName().equals("contract")) {
                        role.setContractInDb(true);
                        roleRepository.save(role);
                    }
                    if (role.isPartyInDb() && role.isContractInDb()) {
                        return List.<AbstractEvent>of(
                                new NewTripleEvent("party", role.getPartyKey(), role.getRoleKey()),
                                new NewTripleEvent("contract", role.getContractKey(), role.getRoleKey())
                        );
                    } else {
                        return List.<AbstractEvent>of();
                    }
                })
                .orElse(List.of());
    }

    private Iterable<AbstractEvent> handleEvent(IAmPublishedEvent event) {
        return roleRepository
                .findById(event.getRoleKey())
                .map(role -> {
                    boolean alreadyWasPublishable = isRolePublishable(role);

                    if (event.getEntityName().equals("party")) {
                        role.setPartyPublished(true);
                        roleRepository.save(role);
                    } else if (event.getEntityName().equals("contract")) {
                        role.setContractPublished(true);
                        roleRepository.save(role);
                    }
                    if (isRolePublishable(role) && !alreadyWasPublishable) {
                        return List.<AbstractEvent>of(new PublishEvent(role.toString()));
                    } else {
                        return List.<AbstractEvent>of();
                    }
                })
                .orElse(List.of());
    }

    private boolean isRolePublishable(Role role) {
        return role.isPartyPublished() && role.isContractPublished();
    }
}
