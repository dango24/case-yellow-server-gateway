package com.caseyellow.server.central.services.gateway;

import com.caseyellow.server.central.services.gateway.model.User;
import com.caseyellow.server.central.services.infrastrucre.RequestHandler;
import com.caseyellow.server.central.services.infrastrucre.RetrofitBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import retrofit2.Retrofit;

import javax.annotation.PostConstruct;
import java.util.List;

public class GatewayServiceImpl implements GatewayService {

    @Value("${gateway.host}")
    private String gatewayUrl;

    private RequestHandler requestHandler;
    private GatewayRequests gatewayRequests;

    @Autowired
    public GatewayServiceImpl(RequestHandler requestHandler) {
        this.requestHandler = requestHandler;
    }

    @PostConstruct
    public void init() {
        Retrofit retrofit = RetrofitBuilder.Retrofit(gatewayUrl).build();
        gatewayRequests = retrofit.create(GatewayRequests.class);
    }

    @Override
    public List<User> getUsers() {
        return requestHandler.execute(gatewayRequests.getUsers());
    }


}
