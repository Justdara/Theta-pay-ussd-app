package com.thetapay.ussdapp.apiModels;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class NewKeyRequest {
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
        @JsonProperty("password")
        private String password;
    }
}
