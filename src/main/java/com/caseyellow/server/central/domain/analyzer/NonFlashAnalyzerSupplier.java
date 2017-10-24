package com.caseyellow.server.central.domain.analyzer;

import com.caseyellow.server.central.common.UrlMapper;
import com.caseyellow.server.central.exceptions.AnalyzerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.function.Function;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toMap;

@Component
public class NonFlashAnalyzerSupplier {

    @Value("${non_flash_analyzer_suffix}")
    private String nonFlashAnalyzerSuffix;

    private UrlMapper urlMapper;
    private ApplicationContext appContext;
    private Map<String, NonFlashAnalyzer> nonFlashAnalyzerMap;

    @Autowired
    public NonFlashAnalyzerSupplier(UrlMapper urlMapper, ApplicationContext appContext) {
        this.urlMapper = urlMapper;
        this.appContext = appContext;
    }

    @PostConstruct
    private void init() {
        buildAnalyzers();
    }

    public NonFlashAnalyzer getNonFlashAnalyzer(String identifier) {
        NonFlashAnalyzer nonFlashAnalyzer = nonFlashAnalyzerMap.get(identifier);

        if (isNull(nonFlashAnalyzer)) {
            throw new AnalyzerException("There is no nonFlashAnalyzer for " + identifier + " identifier");
        }

        return nonFlashAnalyzer;
    }

    private void buildAnalyzers() {

        nonFlashAnalyzerMap =
                urlMapper.getNonFlashIdentifiers()
                         .stream()
                         .filter(analyzer -> nonNull(appContext.getBean(analyzer + nonFlashAnalyzerSuffix)))
                         .collect(toMap(Function.identity(), analyzer -> (NonFlashAnalyzer)appContext.getBean(analyzer + nonFlashAnalyzerSuffix)));
    }

}
