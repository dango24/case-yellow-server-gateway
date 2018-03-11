package com.caseyellow.server.central.persistence.test.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "failed_test")
public class FailedTestDAO {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String ip;
    private String path;
    private String errorMessage;

    @Column(name = "client_version")
    private String clientVersion;

    @Column(name = "user_name")
    private String user;

    @Column(name = "timestamp")
    private Long timestamp;
}
