package com.caseyellow.server.central.services.analyze;

import com.caseyellow.server.central.domain.analyzer.model.AnalyzedImage;
import com.caseyellow.server.central.domain.analyzer.model.GoogleVisionRequest;
import com.caseyellow.server.central.exceptions.AnalyzerException;
import com.caseyellow.server.central.services.infrastrucre.RequestHandler;
import com.caseyellow.server.central.services.infrastrucre.RetrofitBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import retrofit2.Retrofit;

import javax.annotation.PostConstruct;

@Service
public class ImageAnalyzerServiceImpl implements ImageAnalyzerService {

    @Value("${analysis_url}")
    private String analysisUrl;

    private RequestHandler requestHandler;
    private AnalysisRequests analysisRequests;

    @PostConstruct
    public void init() {
        Retrofit retrofit = RetrofitBuilder.Retrofit(analysisUrl)
                                           .build();

        analysisRequests = retrofit.create(AnalysisRequests.class);
    }

    @Autowired
    public void setRequestHandler(RequestHandler requestHandler) {
        this.requestHandler = requestHandler;
    }

    @Override
    public double analyzeImage(String identifier, GoogleVisionRequest googleVisionRequest) {
        String md5 = googleVisionRequest.getRequests().get(0).getImage().getMd5();
        AnalyzedImage analyzedImage = requestHandler.execute(analysisRequests.uploadImage(identifier, md5, googleVisionRequest));

        if (analyzedImage.isAnalyzed()) {
            return analyzedImage.getResult();
        } else {
            throw new AnalyzerException("Failed to analyze image for identifier: " + identifier + ", cause: " + analyzedImage.getMessage());
        }

    }
}
