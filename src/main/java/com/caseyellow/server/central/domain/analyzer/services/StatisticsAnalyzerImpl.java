package com.caseyellow.server.central.domain.analyzer.services;

import com.caseyellow.server.central.common.UrlMapper;
import com.caseyellow.server.central.domain.analyzer.model.IdentifierDetails;
import com.caseyellow.server.central.domain.test.services.TestService;
import com.caseyellow.server.central.persistence.test.dao.ComparisonInfoDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.caseyellow.server.central.common.Utils.calculateDownloadRateFromMbpsToKBps;
import static java.util.stream.Collectors.*;

@Service
public class StatisticsAnalyzerImpl implements StatisticsAnalyzer {

    private UrlMapper urlMapper;
    private TestService testService;

    @Autowired
    public StatisticsAnalyzerImpl(TestService testService, UrlMapper urlMapper) {
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

        return urlMapper.getSpeedTestIdentifiers()
                        .stream()
                        .map(this::createIdentifierDetails)
                        .collect(toMap(IdentifierDetails::getIdentifier, Function.identity()));
    }

    private IdentifierDetails createIdentifierDetails(String identifier) {
        List<ComparisonInfoDAO> comparisonInfos = getComparisons(identifier);
        int size = comparisonInfos.size();
        double meanRatio = getMeanRatio(comparisonInfos);

        return new IdentifierDetails(identifier, meanRatio, size);
    }

    private List<ComparisonInfoDAO> getComparisons(String speedtestWebsite){

        return testService.getAllDAOTests()
                          .stream()
                          .flatMap(test -> test.getComparisonInfoDAOTests().stream())
                          .filter(comp -> comp.getSpeedTestWebSiteDAO().getSpeedTestIdentifier().equals(speedtestWebsite))
                          .collect(toCollection(LinkedList::new));
    }

    private double getMeanRatio(List<ComparisonInfoDAO> comparisons){

        return comparisons.stream()
                          .mapToDouble(this::getRatio)
                          .average()
                          .getAsDouble();
    }

    private double getRatio(ComparisonInfoDAO comprison){

        double speedtestRate = calculateDownloadRateFromMbpsToKBps(comprison.getSpeedTestWebSiteDAO().getDownloadRateInMbps());
        double downloadRate = comprison.getFileDownloadInfoDAO().getFileDownloadRateKBPerSec();

        return downloadRate / speedtestRate;
    }
}
