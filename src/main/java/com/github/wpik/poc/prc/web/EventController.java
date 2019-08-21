package com.github.wpik.poc.prc.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.wpik.poc.prc.events.ContractCreateEvent;
import com.github.wpik.poc.prc.events.PartyCreateEvent;
import com.github.wpik.poc.prc.events.RoleCreateEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@Slf4j
@RequiredArgsConstructor
public class EventController {

    private final KafkaTemplate kafkaTemplate;

    private final ObjectMapper objectMapper;

    @PostMapping("/party")
    @ResponseStatus(HttpStatus.ACCEPTED)
    void ingestParty(@Valid @RequestBody PartyCreateEvent partyCreateEvent) throws JsonProcessingException {
        log.info("Received {}", partyCreateEvent);
        sendToKafka("party", partyCreateEvent.getPayload().getPartyKey(), partyCreateEvent);
    }

    @PostMapping("/role")
    @ResponseStatus(HttpStatus.ACCEPTED)
    void ingestRole(@Valid @RequestBody RoleCreateEvent roleCreateEvent) throws JsonProcessingException {
        log.info("Received {}", roleCreateEvent);
        sendToKafka("role", roleCreateEvent.getPayload().getRoleKey(), roleCreateEvent);
    }

    @PostMapping("/contract")
    @ResponseStatus(HttpStatus.ACCEPTED)
    void ingestContract(@Valid @RequestBody ContractCreateEvent contractCreateEvent) throws JsonProcessingException {
        log.info("Received {}", contractCreateEvent);
        sendToKafka("contract", contractCreateEvent.getPayload().getContractKey(), contractCreateEvent);
    }

    private void sendToKafka(String topic, String key, Object value) throws JsonProcessingException {
        kafkaTemplate.send(topic, key, objectMapper.writeValueAsString(value));
    }
}
