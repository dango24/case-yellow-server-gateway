package com.caseyellow.server.central.services.gateway;

import com.caseyellow.server.central.services.gateway.model.User;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

import java.util.List;

public interface GatewayRequests {

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    @GET("gateway/get-users")
    Call<List<User>> getUsers();
}
