package com.bt1.qltv1.entity;

import com.bt1.qltv1.enumeration.UserStatus;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvIgnore;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

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
    @NotNull(message = "{account.full-name.null}")
    @Column(name = "fullname", length = 100, nullable = false)
    private String fullName;

    @CsvBindByName(column = "Email", required = true)
    @Email(message = "{user.email.invalid}")
    @Column(name = "email", unique = true)
    private String email;

    @CsvIgnore
    @NotNull(message = "{user.password.null}")
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

    @CsvIgnore
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Otp> listOtp = new ArrayList<>();

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Account)) return false;
        return id != null && id.equals(((Account) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
