package com.github.wpik.poc.prc;

import org.apache.kafka.streams.kstream.KStream;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;

public interface Topics {
    String PARTY_IN = "party_in";
    String ROLE_IN = "role_in";
    String CONTRACT_IN = "contract_in";

    String PARTY_PROCESS_OUT = "party_process_out";
    String PARTY_PROCESS_OUT_ROLE = "party_process_out_role";
    String PARTY_PROCESS_IN = "party_process_in";

    String ROLE_PROCESS_OUT = "role_process_out";
    String ROLE_PROCESS_OUT_PARTY = "role_process_out_party";
    String ROLE_PROCESS_OUT_CONTRACT = "role_process_out_contract";
    String ROLE_PROCESS_IN = "role_process_in";

    String CONTRACT_PROCESS_OUT = "contract_process_out";
    String CONTRACT_PROCESS_OUT_ROLE = "contract_process_out_role";
    String CONTRACT_PROCESS_IN = "contract_process_in";

    String PUBLISHED_OUT_PARTY = "published_out_party";
    String PUBLISHED_OUT_ROLE = "published_out_role";
    String PUBLISHED_OUT_CONTRACT = "published_out_contract";

    String PUBLISHED_IN = "published_in";

    @Input(PARTY_IN)
    KStream<?, ?> partyIn();

    @Input(ROLE_IN)
    KStream<?, ?> roleIn();

    @Input(CONTRACT_IN)
    KStream<?, ?> contractIn();

    @Input(PARTY_PROCESS_IN)
    KStream<?, ?> partyProcessQueueIn();

    @Output(PARTY_PROCESS_OUT)
    KStream<?, ?> partyProcessQueueOut();

    @Output(PARTY_PROCESS_OUT_ROLE)
    KStream<?, ?> partyProcessQueueOutRole();

    @Input(ROLE_PROCESS_IN)
    KStream<?, ?> roleProcessQueueIn();

    @Output(ROLE_PROCESS_OUT)
    KStream<?, ?> roleProcessQueueOut();

    @Output(ROLE_PROCESS_OUT_PARTY)
    KStream<?, ?> roleProcessQueueOutParty();

    @Output(ROLE_PROCESS_OUT_CONTRACT)
    KStream<?, ?> roleProcessQueueOutContract();

    @Input(CONTRACT_PROCESS_IN)
    KStream<?, ?> contractProcessQueueIn();

    @Output(CONTRACT_PROCESS_OUT)
    KStream<?, ?> contractProcessQueueOut();

    @Output(CONTRACT_PROCESS_OUT_ROLE)
    KStream<?, ?> contractProcessQueueOutRole();

    @Input(PUBLISHED_IN)
    KStream<?, ?> publishedIn();

    @Output(PUBLISHED_OUT_PARTY)
    KStream<?, ?> publishedOutParty();

    @Output(PUBLISHED_OUT_ROLE)
    KStream<?, ?> publishedOutRole();

    @Output(PUBLISHED_OUT_CONTRACT)
    KStream<?, ?> publishedOutContract();
}
