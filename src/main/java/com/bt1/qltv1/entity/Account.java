package com.bt1.qltv1.entity;

import com.bt1.qltv1.enumeration.UserStatus;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter

@Entity
@Table(name = "account")
@Inheritance(strategy = InheritanceType.JOINED)
public class Account extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name should not be null")
    @Column(name = "fullname", length = 100, nullable = false)
    private String fullName;

    @Email(message = "Please enter the valid email")
    @Column(name = "email", unique = true)
    private String email;

    @NotBlank(message = "Password should not be null")
    @Column(name = "password", nullable = false)
    private String password;

    //Wrong number of login attempts
    @Column(name = "failed_attempt")
    @Builder.Default
    private int failedAttempt = 0;

    //account lock period
    @Column(name = "lock_time")
    private LocalDateTime lockTime;

    @Column(name="status")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private UserStatus status = UserStatus.ACTIVE;

    @Column(name = "otp", length = 6)
    private String otp;

    @Column(name = "otp_expired")
    private LocalDateTime otpExpired;

    @Column(name = "mfa_enabled")
    @Builder.Default
    private boolean mfaEnabled = false;

    @Column(name = "secret")
    private String secret;

    @Column(name = "verify_mail")
    @Builder.Default
    private boolean verifyMail = false;

    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinTable(name = "accounts_roles", // table link two relationship
            joinColumns = @JoinColumn(name = "acc_id"), // Key is link with table Accounts
            inverseJoinColumns = @JoinColumn(name = "role_id")) //Key is link with table Roles
    @Builder.Default
    private Set<Role> roleSet = new HashSet<>();

    //relationship with session account
    @OneToMany(mappedBy = "accountId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Session> listSession = new ArrayList<>();

    public boolean isActive(){
        return this.getStatus().equals(UserStatus.ACTIVE);
    }
    public boolean isBlock(){
        return this.getStatus().equals(UserStatus.BLOCK);
    }

    public boolean isLockTimeExpired(){
        if(this.getLockTime() == null){
            return false;
        }
        long lockTimeInMillis = this.getLockTime()
                .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long currentTimeInMillis = System.currentTimeMillis();

        return lockTimeInMillis < currentTimeInMillis;
    }
}
