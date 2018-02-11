package com.caseyellow.server.central.services.analyze;

import com.caseyellow.server.central.domain.analyzer.model.AnalyzedImage;
import com.caseyellow.server.central.domain.analyzer.model.GoogleVisionRequest;
import retrofit2.Call;
import retrofit2.http.*;


public interface AnalysisRequests {

    @POST("analyze-image")
    Call<AnalyzedImage> uploadImage(@Query("identifier") String identifier, @Body GoogleVisionRequest googleVisionRequest);
}
