package com.almou.payment.beans;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
@Data @AllArgsConstructor @NoArgsConstructor @Entity
public class Client implements Serializable{
    @Id
    private String phone;
    private String firstName;
    private String lastName;
    @OneToOne(cascade = CascadeType.ALL)
    private Account account;
    private String address;
    @Column(unique = true)
    private String email;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    @ManyToMany
    private List<Transaction> transactions=new ArrayList<>();
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable()
    private Collection<AppRole> userRoles=new ArrayList<>();
}
