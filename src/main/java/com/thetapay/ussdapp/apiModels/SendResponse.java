package com.thetapay.ussdapp.apiModels;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SendResponse {
    @JsonProperty("jsonrpc")
    private String jsonRpc;

    @JsonProperty("id")
    private int id;

    @JsonProperty("result")
    private ThetaCliResultData result;

    @Data
    public static class ThetaCliResultData {
        @JsonProperty("hash")
        private String hash;

        @JsonProperty("block")
        private ThetaCliBlock block;

        @Data
        public static class ThetaCliBlock {
            @JsonProperty("ChainID")
            private String chainId;

            @JsonProperty("Epoch")
            private int epoch;

            @JsonProperty("Height")
            private int height;

            @JsonProperty("Parent")
            private String parent;

            @JsonProperty("HCC")
            private ThetaCliHCC hcc;

            @JsonProperty("TxHash")
            private String txHash;

            @JsonProperty("ReceiptHash")
            private String receiptHash;

            @JsonProperty("Bloom")
            private String bloom;

            @JsonProperty("StateHash")
            private String stateHash;

            @JsonProperty("Timestamp")
            private long timestamp;

            @JsonProperty("Proposer")
            private String proposer;

            @JsonProperty("Signature")
            private String signature;

            @Data
            public static class ThetaCliHCC {
                @JsonProperty("Votes")
                private Object votes;

                @JsonProperty("BlockHash")
                private String blockHash;
            }
        }
    }
}
