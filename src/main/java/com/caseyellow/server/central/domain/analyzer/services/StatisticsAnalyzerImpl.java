package com.caseyellow.server.central.domain.analyzer.services;

import com.caseyellow.server.central.configuration.UrlConfig;
import com.caseyellow.server.central.domain.analyzer.model.IdentifierDetails;
import com.caseyellow.server.central.domain.test.services.TestService;
import com.caseyellow.server.central.persistence.test.dao.ComparisonInfoDAO;
import com.caseyellow.server.central.persistence.test.dao.TestDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.caseyellow.server.central.common.Utils.calculateDownloadRateFromMbpsToKBps;
import static java.util.stream.Collectors.*;

@Service
public class StatisticsAnalyzerImpl implements StatisticsAnalyzer {

    private UrlConfig urlMapper;
    private TestService testService;

    @Autowired
    public StatisticsAnalyzerImpl(TestService testService, UrlConfig urlMapper) {
        this.testService = testService;
        this.urlMapper = urlMapper;
    }

    @Override
    @Cacheable("countIPs")
    public Map<String, Long> countIPs(){

        return testService.getAllDAOTests()
                          .stream()
                          .map(s -> s.getSystemInfo().getPublicIP())
                          .collect(groupingBy(Function.identity(), counting()));

    }

    @Override
    @Cacheable("identifiersDetails")
    public Map<String, IdentifierDetails> createIdentifiersDetails() {

        return getComparisons().entrySet()
                               .stream()
                               .collect(toMap(Map.Entry::getKey, entry -> createIdentifierDetails(entry.getKey(), entry.getValue())));
    }

    private IdentifierDetails createIdentifierDetails(String identifier, List<ComparisonInfoDAO> comparisonInfos) {
        int size = comparisonInfos.size();
        double meanRatio = getMeanRatio(comparisonInfos);

        return new IdentifierDetails(identifier, meanRatio, size);
    }

    private Map<String, List<ComparisonInfoDAO>> getComparisons() {
        List<TestDAO> tests = testService.getAllDAOTests();

        return tests.stream()
                    .flatMap(test -> test.getComparisonInfoDAOTests().stream())
                    .collect(groupingBy(c -> c.getSpeedTestWebSiteDAO().getSpeedTestIdentifier()));
    }

    private double getMeanRatio(List<ComparisonInfoDAO> comparisons){

        return comparisons.stream()
                          .mapToDouble(this::getRatio)
                          .filter(this::isNotOutlier)
                          .average()
                          .getAsDouble();
    }

    private double getRatio(ComparisonInfoDAO comprison){

        double speedtestRate = calculateDownloadRateFromMbpsToKBps(comprison.getSpeedTestWebSiteDAO().getDownloadRateInMbps());
        double downloadRate = comprison.getFileDownloadInfoDAO().getFileDownloadRateKBPerSec();

        double dango = downloadRate / speedtestRate;

        if (dango > 100) {
            System.out.println("dango");
        }
        return dango;
    }

    private boolean isNotOutlier(double ratio) {
        return ratio < 100 && ratio > 0.01;
    }
}
