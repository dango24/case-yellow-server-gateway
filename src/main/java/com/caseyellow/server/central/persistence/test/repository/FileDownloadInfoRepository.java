package com.caseyellow.server.central.persistence.test.repository;

import com.caseyellow.server.central.persistence.test.model.FileDownloadInfoDAO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

/**
 * Created by dango on 9/19/17.
 */
public interface FileDownloadInfoRepository extends JpaRepository<FileDownloadInfoDAO, Long> {

    List<FileDownloadInfoDAO> findByFileName(String fileName);
    List<FileDownloadInfoDAO> findByFileURL(String fileUrl);

    default Map<String, Long> groupingFileDownloadInfoByUrl() {
        return findAll().stream()
                        .map(FileDownloadInfoDAO::getFileURL)
                        .collect(groupingBy(Function.identity(), counting()));
    }
}
