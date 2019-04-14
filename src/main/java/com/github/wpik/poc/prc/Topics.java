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
    String PARTY_PROCESS_IN_ROLE = "party_process_in_role";

    String ROLE_PROCESS_OUT = "role_process_out";
    String ROLE_PROCESS_OUT_PARTY_1 = "role_process_out_party_1";
    String ROLE_PROCESS_OUT_PARTY_2 = "role_process_out_party_2";
    String ROLE_PROCESS_IN = "role_process_in";

    String CONTRACT_PROCESS_OUT = "contract_process_out";
    String CONTRACT_PROCESS_OUT_ROLE = "contract_process_out_role";
    String CONTRACT_PROCESS_IN = "contract_process_in";

    String CORRELATED_OUT_PARTY = "correlated_out_party";
//    String CORRELATED_OUT_ROLE = "correlated_out_role";
//    String CORRELATED_OUT_CONTRACT = "correlated_out_contract";

    String CORRELATED_IN = "correlated_in";

    @Input(PARTY_IN)
    KStream<?, ?> partyIn();

    @Input(ROLE_IN)
    KStream<?, ?> roleIn();

    @Input(CONTRACT_IN)
    KStream<?, ?> contractIn();

    @Input(PARTY_PROCESS_IN)
    KStream<?, ?> partyProcessQueueIn();

    @Input(PARTY_PROCESS_IN_ROLE)
    KStream<?, ?> partyProcessQueueInRole();

    @Output(PARTY_PROCESS_OUT)
    KStream<?, ?> partyProcessQueueOut();

    @Output(PARTY_PROCESS_OUT_ROLE)
    KStream<?, ?> partyProcessQueueOutRole();

    @Input(ROLE_PROCESS_IN)
    KStream<?, ?> roleProcessQueueIn();

    @Output(ROLE_PROCESS_OUT)
    KStream<?, ?> roleProcessQueueOut();

    @Output(ROLE_PROCESS_OUT_PARTY_1)
    KStream<?, ?> roleProcessQueueOutParty1();

    @Output(ROLE_PROCESS_OUT_PARTY_2)
    KStream<?, ?> roleProcessQueueOutParty2();

    @Input(CONTRACT_PROCESS_IN)
    KStream<?, ?> contractProcessQueueIn();

    @Output(CONTRACT_PROCESS_OUT)
    KStream<?, ?> contractProcessQueueOut();

    @Output(CONTRACT_PROCESS_OUT_ROLE)
    KStream<?, ?> contractProcessQueueOutRole();

    @Input(CORRELATED_IN)
    KStream<?, ?> correlatedIn();

    @Output(CORRELATED_OUT_PARTY)
    KStream<?, ?> correlatedOutParty();

//    @Output(CORRELATED_OUT_ROLE)
//    KStream<?, ?> correlatedOutRole();

//    @Output(CORRELATED_OUT_CONTRACT)
//    KStream<?, ?> correlatedOutContract();
}
