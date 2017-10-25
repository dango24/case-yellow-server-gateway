package com.caseyellow.server.central.services.infrastrucre;

import com.caseyellow.server.central.exceptions.RequestFailureException;
import retrofit2.Call;

public interface RequestHandler {

    void cancelRequest();
    <T extends Object> T execute(Call<T> request) throws RequestFailureException;
}
