package com.github.wpik.poc.prc.processor;

import com.github.wpik.poc.prc.TemporaryConverter;
import com.github.wpik.poc.prc.Topics;
import com.github.wpik.poc.prc.db.ContractRepository;
import com.github.wpik.poc.prc.db.RoleRepository;
import com.github.wpik.poc.prc.events.*;
import com.github.wpik.poc.prc.events.internal.*;
import com.github.wpik.poc.prc.model.Contract;
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
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

@Component
@RequiredArgsConstructor
@Slf4j
public class ContractProcessor {
    private final TemporaryConverter temporaryConverter;
    private final ContractRepository contractRepository;
    //FIXME is it ok to access roles here?
    private final RoleRepository roleRepository;

    @StreamListener
    @SendTo({Topics.ROLE_PROCESS_OUT_CONTRACT, Topics.PUBLISHED_OUT_CONTRACT})
    public KStream<String, AbstractEvent>[] processContract(@Input(Topics.CONTRACT_PROCESS_IN) KStream<String, String> input) {
        return input
                .peek((k, v) -> log.debug("Received: key={}, value={}", k, v))
                .mapValues(temporaryConverter::decodeEvent)
                .flatMapValues(this::handleContractEvent)
                .selectKey((k, v) -> v.getKey())
                .branch(
                        (k, v) -> v instanceof IAmInDbEvent || v instanceof IAmRemovedFromDbEvent || v instanceof IAmPublishedEvent,
                        (k, v) -> v instanceof PublishEvent || v instanceof UnpublishEvent);
    }

    private Iterable<AbstractEvent> handleContractEvent(AbstractEvent event) {
        //FIXME those instanceof are ugly
        if (event instanceof ContractCreateEvent) {
            return handleEvent((ContractCreateEvent) event);
        } else if (event instanceof ContractUpdateEvent) {
            return handleEvent((ContractUpdateEvent) event);
        } else if (event instanceof ContractDeleteEvent) {
            return handleEvent((ContractDeleteEvent) event);
        } else if (event instanceof AreYouInDbEvent) {
            return handleEvent((AreYouInDbEvent) event);
        } else if (event instanceof NewTripleEvent) {
            return handleEvent((NewTripleEvent) event);
        } else if (event instanceof DeleteTripleEvent) {
            return handleEvent((DeleteTripleEvent) event);
        }

        return List.of();
    }

    private Iterable<AbstractEvent> handleEvent(ContractCreateEvent event) {
        return handleCreateUpdateEvent(event.getPayload());
    }

    private Iterable<AbstractEvent> handleEvent(ContractUpdateEvent event) {
        return handleCreateUpdateEvent(event.getPayload());
    }

    private Iterable<AbstractEvent> handleCreateUpdateEvent(Contract payload) {
        return contractRepository.findById(payload.getContractKey())
                .map(handleCreateUpdateEventWhenContractInDb(payload))
                .orElseGet(handleCreateUpdateEventWhenContractNotInDb(payload));
    }

    private Function<Contract, List<AbstractEvent>> handleCreateUpdateEventWhenContractInDb(Contract payload) {
        return contract -> {
            contract.update(payload);
            contractRepository.save(contract);
            if (contract.getTriplesCounter() > 0) {
                return List.of(new PublishEvent(contract.toString()));
            } else {
                return List.of();
            }
        };
    }

    private Supplier<List<AbstractEvent>> handleCreateUpdateEventWhenContractNotInDb(Contract payload) {
        return () -> {
            contractRepository.save(payload);

            Iterable<Role> roles = roleRepository.findByContractKey(payload.getContractKey());

            return StreamSupport.stream(roles.spliterator(), false)
                    .map(r -> new IAmInDbEvent("contract", r.getContractKey(), r.getRoleKey()))
                    .collect(toList());
        };
    }

    private Iterable<AbstractEvent> handleEvent(ContractDeleteEvent event) {
        var contractKey = event.getPayload().getContractKey();

        contractRepository.deleteById(contractKey);

        Iterable<Role> roles = roleRepository.findByContractKey(contractKey);

        var resultEvents = StreamSupport.stream(roles.spliterator(), false)
                .map(r -> new IAmRemovedFromDbEvent("contract", contractKey, r.getRoleKey()))
                .map(e -> (AbstractEvent) e)
                .collect(toList());

        resultEvents.add(new UnpublishEvent("contract", contractKey));

        return resultEvents;
    }

    private Iterable<AbstractEvent> handleEvent(AreYouInDbEvent event) {
        return contractRepository
                .findById(event.getEntityKey())
                .map(x -> List.<AbstractEvent>of(IAmInDbEvent.from(event)))
                .orElse(List.of());
    }

    private Iterable<AbstractEvent> handleEvent(NewTripleEvent event) {
        return contractRepository
                .findById(event.getEntityKey())
                .map(contract -> {
                    contract.incrementTriplesCounter();
                    contractRepository.save(contract);

                    List<AbstractEvent> resultEvents = new ArrayList<>();
                    if (contract.getTriplesCounter() == 1) {
                        resultEvents.add(new PublishEvent(contract.toString()));
                    }
                    resultEvents.add(new IAmPublishedEvent("contract", contract.getContractKey(), event.getRoleKey()));
                    return resultEvents;
                })
                .orElse(List.of());
    }

    private Iterable<AbstractEvent> handleEvent(DeleteTripleEvent event) {
        return contractRepository
                .findById(event.getEntityKey())
                .map(contract -> {
                    contract.decrementTriplesCounter();
                    contractRepository.save(contract);

                    if (contract.getTriplesCounter() == 0) {
                        return List.<AbstractEvent>of(new UnpublishEvent("contract", contract.getContractKey()));
                    } else {
                        return List.<AbstractEvent>of();
                    }
                })
                .orElse(List.of());
    }
}
