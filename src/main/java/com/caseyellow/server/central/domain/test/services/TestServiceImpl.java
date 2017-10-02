package com.caseyellow.server.central.domain.test.services;

import com.caseyellow.server.central.common.DAOConverter;
import com.caseyellow.server.central.domain.test.model.Test;
import com.caseyellow.server.central.persistence.repository.TestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class TestServiceImpl implements TestService {

    private TestRepository testRepository;

    @Autowired
    public TestServiceImpl(TestRepository testRepository) {
        this.testRepository = testRepository;
    }

    @Override
    public void saveTest(Test test) {
        CompletableFuture.supplyAsync(() -> test)
//                         .exceptionally(this::saveTestExceptionHandler)
                         .thenApply(DAOConverter::convertTestToTestDAO)
                         .thenAccept(testRepository::save);
    }
}
