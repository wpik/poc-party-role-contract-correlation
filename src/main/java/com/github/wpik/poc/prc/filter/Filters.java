package com.github.wpik.poc.prc.filter;

import com.github.wpik.poc.prc.TemporaryConverter;
import com.github.wpik.poc.prc.Topics;
import com.github.wpik.poc.prc.events.AbstractEvent;
import com.github.wpik.poc.prc.events.ContractCreateEvent;
import com.github.wpik.poc.prc.events.RoleCreateEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
@Slf4j
public class Filters {
    private final TemporaryConverter temporaryConverter;

    private Pattern KEY_PATTERN = Pattern.compile("^(pk|rk|ck)[0-9]*$");
    private Pattern PARTY_NAME_PATTERN = Pattern.compile("^[A-Z].*$");

    @StreamListener
    @SendTo(Topics.PARTY_PROCESS_OUT)
    public KStream<String, AbstractEvent> filterParty(@Input(Topics.PARTY_IN) KStream<String, String> input) {
        return input
                .peek((k, v) -> log.debug("Party filter received: key={}, value={}", k, v))
                .mapValues(temporaryConverter::decodeEvent);
//                .filter((k, v) -> v instanceof PartyCreateEvent)
//                .mapValues(v -> (PartyCreateEvent) v)
//                .filter((key, partyEvent) -> partyEvent != null)
//                .filter((key, partyEvent) -> partyEvent.getPayload() != null)
//                .filter((key, partyEvent) -> keyIsValid(partyEvent.getPayload().getPartyKey()))
//                .filter((key, partyEvent) -> StringUtils.hasLength(partyEvent.getPayload().getName()))
//                .filter((key, partyEvent) -> PARTY_NAME_PATTERN.matcher(partyEvent.getPayload().getName()).matches());
    }

    @StreamListener
    @SendTo(Topics.ROLE_PROCESS_OUT)
    public KStream<String, AbstractEvent> filterRole(@Input(Topics.ROLE_IN) KStream<String, String> input) {
        return input
                .peek((k, v) -> log.debug("Role filter received: key={}, value={}", k, v))
                .mapValues(temporaryConverter::decodeEvent);
//                .filter((k, v) -> v instanceof RoleCreateEvent)
//                .mapValues(v -> (RoleCreateEvent) v)
//                .filter((key, roleEvent) -> roleEvent != null)
//                .filter((key, roleEvent) -> roleEvent.getPayload() != null)
//                .filter((key, roleEvent) -> keyIsValid(roleEvent.getPayload().getRoleKey()))
//                .filter((key, roleEvent) -> keyIsValid(roleEvent.getPayload().getPartyKey()))
//                .filter((key, roleEvent) -> keyIsValid(roleEvent.getPayload().getContractKey()))
//                .filter((key, roleEvent) -> roleEvent.getPayload().getType() != null)
//                .filter((key, roleEvent) -> roleEvent.getPayload().getType() <= 2);
    }

    @StreamListener
    @SendTo(Topics.CONTRACT_PROCESS_OUT)
    public KStream<String, AbstractEvent> filterContract(@Input(Topics.CONTRACT_IN) KStream<String, String> input) {
        return input
                .peek((k, v) -> log.debug("Contract filter received: key={}, value={}", k, v))
                .mapValues(temporaryConverter::decodeEvent);
//                .filter((key, contractCreateEvent) -> contractCreateEvent != null)
//                .filter((key, contractCreateEvent) -> contractCreateEvent.getPayload() != null)
//                .filter((key, contractCreateEvent) -> keyIsValid(contractCreateEvent.getPayload().getContractKey()))
//                .filter((key, contractCreateEvent) -> contractCreateEvent.getPayload().isActive());
    }

    private boolean keyIsValid(String key) {
        return StringUtils.hasLength(key) && KEY_PATTERN.matcher(key).matches();
    }
}
