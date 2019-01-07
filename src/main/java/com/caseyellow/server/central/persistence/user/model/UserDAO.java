package com.caseyellow.server.central.persistence.user.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDAO {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    private String userName;

    private String encodedPassword;
    private boolean enabled;
    private long dateCreated;
    private String phone;

    @Column(name = "has_computer")
    @JsonProperty("has_computer")
    private Boolean hasComputer;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<RoleDAO> roles;

    public UserDAO() {
        this(null);
    }

    public UserDAO(String userName) {
        this(userName, null);
    }

    public UserDAO(String userName, String encodedPassword) {
        this.userName = userName;
        this.encodedPassword = encodedPassword;
        this.enabled = true;
        this.dateCreated = System.currentTimeMillis();
        this.roles = new ArrayList<>();
    }
}
