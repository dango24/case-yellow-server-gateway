package com.caseyellow.server.central.domain.file.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Created by Dan on 04/10/2016.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FileDownloadInfo {

    private String fileName;
    private String fileURL;
    private long   fileSizeInBytes;
    private double fileDownloadRateKBPerSec;
    private long   fileDownloadedDurationTimeInMs;
    private long   startDownloadingTimestamp;
    private String traceRouteOutputPreviousDownloadFile;
    private String traceRouteOutputAfterDownloadFile;
    private Map<String, List<String>> headers;

    public FileDownloadInfo(String url) {
        fileURL = url;
    }

    public FileDownloadInfo(FileDownloadInfoBuilder fileDownloadInfoBuilder) {
        fileName = fileDownloadInfoBuilder.fileName;
        fileURL = fileDownloadInfoBuilder.fileURL;
        fileSizeInBytes = fileDownloadInfoBuilder.fileSizeInBytes;
        fileDownloadRateKBPerSec = fileDownloadInfoBuilder.fileDownloadRateKBPerSec;
        fileDownloadedDurationTimeInMs = fileDownloadInfoBuilder.fileDownloadedTimeInMs;
        startDownloadingTimestamp = fileDownloadInfoBuilder.startDownloadingTimestamp;
        traceRouteOutputPreviousDownloadFile = fileDownloadInfoBuilder.traceRouteOutputPreviousDownloadFile;
        traceRouteOutputAfterDownloadFile = fileDownloadInfoBuilder.traceRouteOutputAfterDownloadFile;
        headers = fileDownloadInfoBuilder.headers;
    }

    public String getFileURL() {
        return fileURL;
    }

    public long getFileSizeInBytes() {
        return fileSizeInBytes;
    }

    public void setFileSizeInBytes(long fileSizeInBytes) {
        this.fileSizeInBytes = fileSizeInBytes;
    }

    public long getFileDownloadedDurationTimeInMs() { return fileDownloadedDurationTimeInMs; }

    public void setFileDownloadedDurationTimeInMs(long fileDownloadedTimeInMs) {
        this.fileDownloadedDurationTimeInMs = fileDownloadedTimeInMs;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getStartDownloadingTimestamp() {
        return startDownloadingTimestamp;
    }

    public void setStartDownloadingTimestamp(long startDownloadingTimestamp) {
        this.startDownloadingTimestamp = startDownloadingTimestamp;
    }

    public double getFileDownloadRateKBPerSec() {
        return fileDownloadRateKBPerSec;
    }

    public void setFileDownloadRateKBPerSec(double fileDownloadRateKBPerSec) {
        this.fileDownloadRateKBPerSec = fileDownloadRateKBPerSec;
    }

    public void setFileURL(String fileURL) {
        this.fileURL = fileURL;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, List<String>> headers) {
        this.headers = headers;
    }

    public String getTraceRouteOutputPreviousDownloadFile() {
        return traceRouteOutputPreviousDownloadFile;
    }

    public void setTraceRouteOutputPreviousDownloadFile(String traceRouteOutputPreviousDownloadFile) {
        this.traceRouteOutputPreviousDownloadFile = traceRouteOutputPreviousDownloadFile;
    }

    public String getTraceRouteOutputAfterDownloadFile() {
        return traceRouteOutputAfterDownloadFile;
    }

    public void setTraceRouteOutputAfterDownloadFile(String traceRouteOutputAfterDownloadFile) {
        this.traceRouteOutputAfterDownloadFile = traceRouteOutputAfterDownloadFile;
    }

    @Override
    public String toString() {
        return "FileDownloadInfo{" +
                "fileName='" + fileName + '\'' +
                ", fileURL='" + fileURL + '\'' +
                ", fileSizeInBytes=" + fileSizeInBytes +
                ", fileDownloadRateKBPerSec=" + fileDownloadRateKBPerSec +
                ", fileDownloadedDurationTimeInMs=" + fileDownloadedDurationTimeInMs +
                ", startDownloadingTimestamp=" + startDownloadingTimestamp +
                ", traceRouteOutputPreviousDownloadFile='" + traceRouteOutputPreviousDownloadFile + '\'' +
                ", traceRouteOutputAfterDownloadFile='" + traceRouteOutputAfterDownloadFile + '\'' +
                ", headers=" + headers +
                '}';
    }

    public static class FileDownloadInfoBuilder {

        // Fields
        private String fileURL;
        private String fileName;
        private long   fileSizeInBytes;
        private double fileDownloadRateKBPerSec;
        private long   fileDownloadedTimeInMs;
        private long startDownloadingTimestamp;
        private String traceRouteOutputPreviousDownloadFile;
        private String traceRouteOutputAfterDownloadFile;
        private Map<String, List<String>> headers;

        public FileDownloadInfoBuilder(String fileName) {
            this.fileName = fileName;
        }

        public FileDownloadInfoBuilder addFileURL(String fileURL) {
            this.fileURL = fileURL;
            return this;
        }

        public FileDownloadInfoBuilder addFileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public FileDownloadInfoBuilder addFileSizeInBytes(long fileSizeInBytes) {
            this.fileSizeInBytes = fileSizeInBytes;
            return this;
        }

        public FileDownloadInfoBuilder addHeaders(Map<String, List<String>> headers) {
            this.headers = headers;
            return this;
        }

        public FileDownloadInfoBuilder addTraceRouteOutputPreviousDownloadFile(String traceRouteOutputPreviousDownloadFile) {
            this.traceRouteOutputPreviousDownloadFile = traceRouteOutputPreviousDownloadFile;
            return this;
        }

        public FileDownloadInfoBuilder addTraceRouteOutputAfterDownloadFile(String traceRouteOutputAfterDownloadFile) {
            this.traceRouteOutputAfterDownloadFile = traceRouteOutputAfterDownloadFile;
            return this;
        }

        public FileDownloadInfoBuilder addFileDownloadRateKBPerSec(double fileDownloadRateKBPerSec) {
            this.fileDownloadRateKBPerSec = fileDownloadRateKBPerSec;
            return this;
        }

        public FileDownloadInfoBuilder addFileDownloadedTimeInMs(long fileDownloadedTimeInMs) {
            this.fileDownloadedTimeInMs = fileDownloadedTimeInMs;
            return this;
        }

        public FileDownloadInfoBuilder addStartDownloadingTime(long startDownloadingTime) {
            this.startDownloadingTimestamp = startDownloadingTime;
            return this;
        }

        public FileDownloadInfo build() {
            return new FileDownloadInfo(this);
        }
    }
}
