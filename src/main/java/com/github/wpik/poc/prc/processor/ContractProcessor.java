package com.github.wpik.poc.prc.processor;

import com.github.wpik.poc.prc.TemporaryConverter;
import com.github.wpik.poc.prc.Topics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ContractProcessor {
    private final TemporaryConverter temporaryConverter;

    @StreamListener
//    @SendTo(Topics.CORRELATED_OUT_CONTRACT)
    public void processContract(@Input(Topics.CONTRACT_PROCESS_IN) KStream<String, String> input) {
        input.foreach((k, v) -> log.debug("Received: key={}, value={}", k, v));
    }
}
