package com.caseyellow.server.central.persistence.file.dao;

import org.hibernate.annotations.Type;

import javax.persistence.*;

/**
 * Created by Dan on 04/10/2016.
 */
@Entity
@Table(name = "file_download_info")
public class FileDownloadInfoDAO {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String fileName;
    private String fileURL;
    private long   fileSizeInBytes;
    private double fileDownloadRateKBPerSec;
    private long   fileDownloadedTimeInMs;
    private long   startDownloadingTimestamp;

    @Column(name = "trace_pre")
    private String traceRouteOutputPreviousDownloadFile;

    @Column(name = "trace_post")
    private String traceRouteOutputAfterDownloadFile;

    @Type(type = "jsonb")
    @Column(name = "headers", columnDefinition = "jsonb")
    private String headers;

    public FileDownloadInfoDAO() {}

    public FileDownloadInfoDAO(String url) {
        this(null, url);
    }

    public FileDownloadInfoDAO(String name, String url) {
        this.fileURL = url;
        this.fileName = name;
    }

    public FileDownloadInfoDAO(FileDownloadInfoBuilder fileDownloadInfoBuilder) {
        fileName = fileDownloadInfoBuilder.fileName;
        fileURL = fileDownloadInfoBuilder.fileURL;
        fileSizeInBytes = fileDownloadInfoBuilder.fileSizeInBytes;
        fileDownloadRateKBPerSec = fileDownloadInfoBuilder.fileDownloadRateKBPerSec;
        fileDownloadedTimeInMs = fileDownloadInfoBuilder.fileDownloadedTimeInMs;
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

    public long getFileDownloadedTimeInMs() { return fileDownloadedTimeInMs; }

    public void setFileDownloadedTimeInMs(long fileDownloadedTimeInMs) {
        this.fileDownloadedTimeInMs = fileDownloadedTimeInMs;
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

    public String getHeaders() {
        return headers;
    }

    public void setHeaders(String headers) {
        this.headers = headers;
    }

    @Override
    public String toString() {
        return "FileDownloadInfoDAO{" +
                "fileURL='" + fileURL + '\'' +
                ", fileName='" + fileName + '\'' +
                ", fileSizeInBytes=" + fileSizeInBytes +
                ", fileDownloadedTimeInMs=" + fileDownloadedTimeInMs +
                ", startDownloadingTimestamp=" + startDownloadingTimestamp +
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
        private String headers;

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

        public FileDownloadInfoBuilder addTraceRouteOutputPreviousDownloadFile(String traceRouteOutputPreviousDownloadFile) {
            this.traceRouteOutputPreviousDownloadFile = traceRouteOutputPreviousDownloadFile;
            return this;
        }

        public FileDownloadInfoBuilder addTraceRouteOutputAfterDownloadFile(String traceRouteOutputAfterDownloadFile) {
            this.traceRouteOutputAfterDownloadFile = traceRouteOutputAfterDownloadFile;
            return this;
        }

        public FileDownloadInfoBuilder addHeaders(String headers) {
            this.headers = headers;
            return this;
        }

        public FileDownloadInfoDAO build() {
            return new FileDownloadInfoDAO(this);
        }
    }
}
