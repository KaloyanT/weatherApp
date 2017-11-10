package com.weatherapp.model.jwt;

import com.weatherapp.model.base.ModelBase;

public class JwtAuthenticationResponse extends ModelBase {

    private static final long serialVersionUID = -6624726180748515507L;
    private String token;

    public JwtAuthenticationResponse() {
        super();
    }

    public JwtAuthenticationResponse(String token) {
        this.setToken(token);
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
