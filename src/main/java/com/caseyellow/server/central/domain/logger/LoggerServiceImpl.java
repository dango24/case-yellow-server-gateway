package com.caseyellow.server.central.domain.logger;

import com.amazonaws.auth.*;
import com.caseyellow.server.central.configuration.AWSConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequestInterceptor;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.isNull;

@Slf4j
@Service
@Profile("prod")
public class LoggerServiceImpl implements LoggerService {

    private final static String serviceName = "es";
    private final static String region = "eu-central-1";
    private final static String index = "logs";
    private final static String type = "log";

    @Value("${aes_endpoint}")
    private String aesEndpoint;

    private RestHighLevelClient esClient;

    @Autowired
    public void LoggerServiceImpl(AWSConfiguration configurationManager) {

        AWS4Signer signer = new AWS4Signer();
        signer.setServiceName(serviceName);
        signer.setRegionName(region);

        AWSCredentials credentials = new BasicAWSCredentials(configurationManager.accessKeyID(), configurationManager.secretAccessKey());
        HttpRequestInterceptor interceptor = new AWSRequestSigningApacheInterceptor(serviceName, signer, new AWSStaticCredentialsProvider(credentials));

        esClient = new RestHighLevelClient(RestClient.builder(HttpHost.create(aesEndpoint)).setHttpClientConfigCallback(hacb -> hacb.addInterceptorLast(interceptor)));
    }

    @Override
    public void uploadLogData(String user, LogData logData) {
        try {

            IndexRequest request = new IndexRequest(index, type).source(buildDocument(logData));
            IndexResponse response = esClient.index(request);

            log.info(response.toString());

        } catch (IOException e) {
            log.error(String.format("Failed to upload log: %s", e.getMessage()), e);
        }
    }

    private Map<String, String> buildDocument(LogData logData) {
        Map<String, String> document = new HashMap<>();

        document.put("date", logData.getDate());
        document.put("thread", logData.getThread());
        document.put("level", logData.getLevel());
        document.put("class_L", logData.getClassL());
        document.put("correlation_id", logData.getCorrelationId());
        document.put("message", logData.getMessage());
        document.put("user", logData.getUser());
        document.put("version", logData.getVersion());

        document.entrySet().removeIf( e -> isNull(e.getValue()) );

        return document;
    }

}
