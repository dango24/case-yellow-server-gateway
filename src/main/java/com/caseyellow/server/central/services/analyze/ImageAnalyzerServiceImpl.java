package com.caseyellow.server.central.services.analyze;

import com.caseyellow.server.central.domain.analyzer.image.AnalyzedImage;
import com.caseyellow.server.central.exceptions.AnalyzerException;
import com.caseyellow.server.central.services.infrastrucre.RequestHandler;
import com.caseyellow.server.central.services.infrastrucre.RetrofitBuilder;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import retrofit2.Retrofit;

import javax.annotation.PostConstruct;
import java.io.File;

import static java.util.Objects.isNull;
import static org.hibernate.internal.util.StringHelper.isEmpty;

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
    public double analyzeImage(String identifier, File image) {
        validateArgs(identifier, image);

        UploadImage uploadTest = createUploadImage(identifier, image);
        AnalyzedImage analyzedImage = requestHandler.execute(analysisRequests.uploadImage(uploadTest.getPayload(), uploadTest.getPart()));

        if (analyzedImage.isAnalyzed()) {
            return analyzedImage.getResult();
        } else {
            throw new AnalyzerException("Failed to analyze image for identifier: " + identifier + "for image: " + image.getName() + " , cause: " + analyzedImage.getMessage());
        }

    }

    private void validateArgs(String identifier, File image) {
        if (isEmpty(identifier) || isNull(image)) {
            throw new AnalyzerException("Failed to analyze image, identifier or image is null, identifier: " + identifier + " image: " + image);
        }
    }


    private UploadImage createUploadImage(String identifier, File image) {

        MultipartBody.Part part = createRequestBodyPart(image.getAbsolutePath().hashCode(), image);

        RequestBody payload = RequestBody.create(MultipartBody.FORM, identifier);

        return new UploadImage(payload, part);
    }

    private MultipartBody.Part createRequestBodyPart(int key, File imgFile ) {
        RequestBody imgRequestBody = RequestBody.create(MediaType.parse(".png"), imgFile);
        MultipartBody.Part imgPart = MultipartBody.Part.createFormData(String.valueOf(key), imgFile.getName(), imgRequestBody);

        return imgPart;
    }

    private static class UploadImage {

        private RequestBody payload;
        private MultipartBody.Part part;

        public UploadImage() {
        }

        public UploadImage(RequestBody payload, MultipartBody.Part part) {
            this.payload = payload;
            this.part = part;
        }

        public RequestBody getPayload() {
            return payload;
        }

        public void setPayload(RequestBody payload) {
            this.payload = payload;
        }

        public MultipartBody.Part getPart() {
            return part;
        }

        public void setPart(MultipartBody.Part part) {
            this.part = part;
        }
    }
}
