package com.thetapay.ussdapp.apiModels;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class GetAccountResponse {
    @JsonProperty("jsonrpc")
    private String jsonrpc;

    @JsonProperty("id")
    private int id;

    @JsonProperty("result")
    private Result result;

    @Data
    public static class Result {
        @JsonProperty("sequence")
        private String sequence;

        @JsonProperty("coins")
        private Map<String, String> coins;

        @JsonProperty("reserved_funds")
        private List<Object> reservedFunds;

        @JsonProperty("last_updated_block_height")
        private String lastUpdatedBlockHeight;

        @JsonProperty("root")
        private String root;

        @JsonProperty("code")
        private String code;
    }
}
