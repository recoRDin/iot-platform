package com.iot.server.auth.token;

public class TokenResult {

    private final String accessToken;
    private final String tokenType;
    private final long expiresIn;

    public TokenResult(String accessToken, long expiresIn) {
        this.accessToken = accessToken;
        this.tokenType = "Bearer";
        this.expiresIn = expiresIn;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public long getExpiresIn() {
        return expiresIn;
    }
}
