package com.caseyellow.server.central.domain.analyzer.model;

import lombok.Data;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static com.caseyellow.server.central.common.Utils.createImageBase64Encode;

@Data
public class VisionRequest {

    private Image image;
    private ImageContext imageContext;
    private List<Feature> features;

    public VisionRequest() {
        imageContext = ImageContext.createImageContextDefaultValues();
        features = Arrays.asList(Feature.createDefaultFeature());
    }

    public VisionRequest(String imgPath) throws IOException {
        this(imgPath, null);
    }

    public VisionRequest(String imgPath, String md5) throws IOException {
        this();
        image = new Image(new String(createImageBase64Encode(imgPath), "UTF-8"), md5);
    }
}
