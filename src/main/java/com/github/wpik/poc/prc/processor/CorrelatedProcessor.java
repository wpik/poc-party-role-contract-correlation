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
public class CorrelatedProcessor {
    private final TemporaryConverter temporaryConverter;

    @StreamListener
    public void logCorrelatedEvents(@Input(Topics.CORRELATED_IN) KStream<String, String> input) {
        input
//                .peek((k, v) -> log.debug("CORRELATED_IN: Correlated Processor received {}-{}", k, v))
//                .mapValues(temporaryConverter::stringToCorrelatedOperation)
                .foreach((key, value) -> log.info("New correlated operation: key={}, {}", key, value));
    }
}
