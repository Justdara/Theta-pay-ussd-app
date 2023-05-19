package com.thetapay.ussdapp.apiModels;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class SendRequest {
    @JsonProperty("jsonrpc")
    private String jsonrpc;

    @JsonProperty("method")
    private String method;

    @JsonProperty("params")
    private List<Params> params;

    @JsonProperty("id")
    private int id;

    @Data
    public static class Params {
        @JsonProperty("chain_id")
        private String chainId;

        @JsonProperty("from")
        private String from;

        @JsonProperty("to")
        private String to;

        @JsonProperty("thetawei")
        private String thetaWei;

        @JsonProperty("tfuelwei")
        private String tFuelWei;

        @JsonProperty("fee")
        private String fee;

        @JsonProperty("sequence")
        private String sequence;

        @JsonProperty("async")
        private boolean async;
    }
}
