package com.caseyellow.server.central.persistence.repository;

import com.caseyellow.server.central.persistence.model.FileDownloadInfoDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

/**
 * Created by dango on 9/19/17.
 */
public interface FileDownloadInfoRepository extends JpaRepository<FileDownloadInfoDAO, Long> {

    String SELECT_ALL_IDENTIFIERS_QUERY = "SELECT DISTINCT FILE_NAME from FILE_DOWNLOAD_INFO";

    List<FileDownloadInfoDAO> findByFileName(String fileName);
    List<FileDownloadInfoDAO> findByFileURL(String fileUrl);

    @Query(value = SELECT_ALL_IDENTIFIERS_QUERY, nativeQuery = true)
    List<String> getAllFileIdentifiers();

    default Map<String, Long> groupingFileDownloadInfoByUrl() {
        return findAll().stream()
                        .map(FileDownloadInfoDAO::getFileURL)
                        .collect(groupingBy(Function.identity(), counting()));
    }
}
