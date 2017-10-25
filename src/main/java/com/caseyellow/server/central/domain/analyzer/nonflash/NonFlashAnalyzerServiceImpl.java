package com.caseyellow.server.central.domain.analyzer.nonflash;

import com.caseyellow.server.central.common.UrlMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class NonFlashAnalyzerServiceImpl implements NonFlashAnalyzerService {

    private UrlMapper urlMapper;
    private NonFlashAnalyzerSupplier nonFlashAnalyzerSupplier;

    @Autowired
    public NonFlashAnalyzerServiceImpl(UrlMapper urlMapper, NonFlashAnalyzerSupplier nonFlashAnalyzerSupplier) {
        this.urlMapper = urlMapper;
        this.nonFlashAnalyzerSupplier = nonFlashAnalyzerSupplier;
    }

    @Override
    public double analyze(String identifier, String nonFlashResult) {
        NonFlashAnalyzer nonFlashAnalyzer = nonFlashAnalyzerSupplier.getNonFlashAnalyzer(identifier);

        return nonFlashAnalyzer.analyze(nonFlashResult);
    }

    @Override
    public boolean isNonFlashAble(String identifier) {
        return urlMapper.isNonFlashAble(identifier);
    }
}
