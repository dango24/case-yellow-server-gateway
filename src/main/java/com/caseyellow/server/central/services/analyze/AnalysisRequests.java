package com.caseyellow.server.central.services.analyze;

import com.caseyellow.server.central.domain.analyzer.image.AnalyzedImage;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.*;


public interface AnalysisRequests {

    @Multipart
    @POST("analyze-image")
    Call<AnalyzedImage> uploadImage(@Part("identifier") RequestBody identifier, @Part MultipartBody.Part imgFile);
}
