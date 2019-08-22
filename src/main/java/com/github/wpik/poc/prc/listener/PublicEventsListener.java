package com.github.wpik.poc.prc.listener;

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
public class PublicEventsListener {
    @StreamListener
    public void logPublicEvents(@Input(Topics.PUBLISHED_IN) KStream<String, String> input) {
        input.foreach((key, value) -> log.info("New published operation: key={}, {}", key, value));
    }
}
