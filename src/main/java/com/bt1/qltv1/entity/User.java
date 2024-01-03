package com.bt1.qltv1.entity;


import com.bt1.qltv1.enumeration.UserStatus;
import com.bt1.qltv1.util.Global;
import com.bt1.qltv1.validation.Phone;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@SuperBuilder
@NoArgsConstructor
//@Data
//@EqualsAndHashCode(callSuper = true)
@Getter
@Setter

@Entity
@Table(name = "user")
public class User extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name should not be null")
    @Column(name = "fullname", length = 100, nullable = false)
    private String fullName;

    @Column(name = "avatar")
    @Builder.Default
    private String avatar = Global.DEFAULT_AVATAR;

    @Phone
    @Column(name = "phone", unique = true)
    private String phone;

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

    @Column(name = "mfa_enabled")
    @Builder.Default
    private boolean mfaEnabled = false;

    @Column(name = "secret")
    private String secret;

    @Column(name = "verify_mail")
    @Builder.Default
    private boolean verifyMail = false;

    @Column(name = "otp", length = 6)
    private String otp;

    @Column(name = "otp_expired")
    private LocalDateTime otpExpired;

    //relationship with role
    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinTable(name = "users_roles", // table link two relationship
            joinColumns = @JoinColumn(name = "userId"), // Key is link with table Users
            inverseJoinColumns = @JoinColumn(name = "roleId")) //Key is link with table Roles
    @Builder.Default
    private Set<Role> roleSet = new HashSet<>();

    //relationship with session user
    @OneToMany(mappedBy = "userId", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Session> listSession = new ArrayList<>();

    public boolean isActive(){
        return this.status.equals(UserStatus.ACTIVE);
    }
    public boolean isBlock(){
        return this.status.equals(UserStatus.BLOCK);
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
        if (this == o) {
            return true;
        }
        if (!(o instanceof User)) {
            return false;
        }
        return id != null && id.equals(((User) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
