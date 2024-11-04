package com.example.hundredtrygoogle.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.UUID;

@Entity
@Getter @Setter
public class LoginProvider {

    @Id
    private Long id;

    private String providerName;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User userId;

    private String providerUserId;

    @CreationTimestamp
    private Date createdAt;

    @UpdateTimestamp
    private Date updatedAt;



}
