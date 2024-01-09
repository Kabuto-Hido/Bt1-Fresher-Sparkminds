package com.bt1.qltv1.entity;

import com.bt1.qltv1.enumeration.UserStatus;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvIgnore;
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
    @CsvIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CsvBindByName(column = "Fullname", required = true)
    @NotBlank(message = "Name should not be null")
    @Column(name = "fullname", length = 100, nullable = false)
    private String fullName;

    @CsvBindByName(column = "Email", required = true)
    @Email(message = "Please enter the valid email")
    @Column(name = "email", unique = true)
    private String email;

    @CsvIgnore
    @NotBlank(message = "Password should not be null")
    @Column(name = "password", nullable = false)
    private String password;

    //Wrong number of login attempts
    @CsvIgnore
    @Column(name = "failed_attempt")
    @Builder.Default
    private int failedAttempt = 0;

    //account lock period
    @CsvIgnore
    @Column(name = "lock_time")
    private LocalDateTime lockTime;

    @CsvIgnore
    @Column(name="status")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private UserStatus status = UserStatus.ACTIVE;

    @CsvIgnore
    @Column(name = "otp", length = 6)
    private String otp;

    @CsvIgnore
    @Column(name = "otp_expired")
    private LocalDateTime otpExpired;

    @CsvIgnore
    @Column(name = "mfa_enabled")
    @Builder.Default
    private boolean mfaEnabled = false;

    @CsvIgnore
    @Column(name = "secret")
    private String secret;

    @CsvIgnore
    @Column(name = "verify_mail")
    @Builder.Default
    private boolean verifyMail = false;

    @CsvIgnore
    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinTable(name = "accounts_roles", // table link two relationship
            joinColumns = @JoinColumn(name = "acc_id"), // Key is link with table Accounts
            inverseJoinColumns = @JoinColumn(name = "role_id")) //Key is link with table Roles
    @Builder.Default
    private Set<Role> roleSet = new HashSet<>();

    //relationship with session account
    @CsvIgnore
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
