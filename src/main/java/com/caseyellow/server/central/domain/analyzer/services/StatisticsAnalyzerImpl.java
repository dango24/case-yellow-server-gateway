package com.caseyellow.server.central.domain.analyzer.services;

import com.caseyellow.server.central.configuration.UrlConfig;
import com.caseyellow.server.central.domain.analyzer.model.IdentifierDetails;
import com.caseyellow.server.central.domain.test.services.TestService;
import com.caseyellow.server.central.exceptions.AnalyzerException;
import com.caseyellow.server.central.persistence.test.dao.ComparisonInfoDAO;
import com.caseyellow.server.central.persistence.test.dao.TestDAO;
import com.caseyellow.server.central.persistence.test.repository.UserDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.function.Function;

import static com.caseyellow.server.central.common.Utils.calculateDownloadRateFromMbpsToKBps;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.*;

@Service
public class StatisticsAnalyzerImpl implements StatisticsAnalyzer {

    private UrlConfig urlMapper;
    private TestService testService;
    private UserDetailsRepository userDetailsRepository;

    @Autowired
    public StatisticsAnalyzerImpl(TestService testService, UserDetailsRepository userDetailsRepository, UrlConfig urlMapper) {
        this.urlMapper = urlMapper;
        this.testService = testService;
        this.userDetailsRepository = userDetailsRepository;
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
                               .map(entry -> createIdentifierDetails(entry.getKey(), entry.getValue()))
                               .filter(this::isValidIdentifierDetails)
                               .collect(toMap(IdentifierDetails::getIdentifier, Function.identity()));
    }

    @Override
    public long userLastTest(String user) {
        if (nonNull(userDetailsRepository.findByUserName(user))) {
            return testService.userLastTest(user);

        } else {
            throw new AnalyzerException(String.format("User: %s not exist", user));
        }
    }

    @Override
    public long userLastFailedTest(String user) {
        if (nonNull(userDetailsRepository.findByUserName(user))) {
            return testService.userLastFailedTest(user);

        } else {
            throw new AnalyzerException(String.format("User: %s not exist", user));
        }
    }

    private IdentifierDetails createIdentifierDetails(String identifier, List<ComparisonInfoDAO> comparisonInfos) {
        int size = comparisonInfos.size();
        double meanRatio = getMeanRatio(comparisonInfos);

        return new IdentifierDetails(identifier, meanRatio, size);
    }

    private boolean isValidIdentifierDetails(IdentifierDetails identifierDetails) {
        return identifierDetails.getMeanRatio() > 0;
    }

    private Map<String, List<ComparisonInfoDAO>> getComparisons() {
        List<TestDAO> tests = testService.getAllDAOTests();

        return tests.stream()
                    .flatMap(test -> test.getComparisonInfoDAOTests().stream())
                    .collect(groupingBy(c -> c.getSpeedTestWebSiteDAO().getSpeedTestIdentifier()));
    }

    private double getMeanRatio(List<ComparisonInfoDAO> comparisons){

        OptionalDouble optionalDouble =
                comparisons.stream()
                          .filter(this::isValidSubTest)
                          .mapToDouble(this::getRatio)
                          .filter(this::isNotOutlier)
                          .average();

        if (optionalDouble.isPresent()) {
            return optionalDouble.getAsDouble();
        } else {
            return -1;
        }
    }

    private boolean isValidSubTest(ComparisonInfoDAO comparisonInfo) {
        if (isNull(comparisonInfo.getSpeedTestWebSiteDAO()) ||
            isNull(comparisonInfo.getSpeedTestWebSiteDAO())) {
            return false;
        }

        double speedTestRate = comparisonInfo.getSpeedTestWebSiteDAO().getDownloadRateInMbps();
        double downloadRate = comparisonInfo.getFileDownloadInfoDAO().getFileDownloadRateKBPerSec();

        return speedTestRate > 0 && downloadRate > 0;
    }

    private double getRatio(ComparisonInfoDAO comprison){
        double speedTestRate = calculateDownloadRateFromMbpsToKBps(comprison.getSpeedTestWebSiteDAO().getDownloadRateInMbps());
        double downloadRate = comprison.getFileDownloadInfoDAO().getFileDownloadRateKBPerSec();
        double ratio = downloadRate / speedTestRate;

        return ratio;
    }

    private boolean isNotOutlier(double ratio) {
        return ratio < 100 && ratio > 0.01;
    }
}
