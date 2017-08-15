package caseyellow.server.gateway.domain;

/**
 * Created by Dan on 12/10/2016.
 */
public class SpeedTestWebSiteDownloadInfo {

    private String urlAddress;
    private String speedTestIdentifier;
    private long startMeasuringTimestamp;

    public SpeedTestWebSiteDownloadInfo() {
    }

    public SpeedTestWebSiteDownloadInfo(String urlAddress, String speedTestIdentifier, long startMeasuringTimestamp) {
        this.urlAddress = urlAddress;
        this.speedTestIdentifier = speedTestIdentifier;
        this.startMeasuringTimestamp = startMeasuringTimestamp;
    }

    public String getUrlAddress() {
        return urlAddress;
    }

    public void setUrlAddress(String urlAddress) {
        this.urlAddress = urlAddress;
    }

    public String getSpeedTestIdentifier() {
        return speedTestIdentifier;
    }

    public void setSpeedTestIdentifier(String speedTestIdentifier) {
        this.speedTestIdentifier = speedTestIdentifier;
    }

    public long getStartMeasuringTimestamp() {
        return startMeasuringTimestamp;
    }

    public void setStartMeasuringTimestamp(long startMeasuringTimestamp) {
        this.startMeasuringTimestamp = startMeasuringTimestamp;
    }
}
