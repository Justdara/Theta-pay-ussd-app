package com.thetapay.ussdapp.apiModels;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UnlockKeyResponse {
    @JsonProperty("jsonrpc")
    private String jsonrpc;

    @JsonProperty("id")
    private int id;

    @JsonProperty("result")
    private Result result;

    @Data
    public static class Result {
        @JsonProperty("unlocked")
        private boolean unlocked;
    }
}

