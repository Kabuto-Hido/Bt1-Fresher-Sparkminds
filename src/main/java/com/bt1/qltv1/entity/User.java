package com.bt1.qltv1.entity;


import com.bt1.qltv1.util.Global;
import com.bt1.qltv1.enumeration.UserStatus;
import com.bt1.qltv1.validation.Phone;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;



@AllArgsConstructor
@Builder
@NoArgsConstructor
//@Data
//@EqualsAndHashCode(callSuper = true)
@Getter
@Setter

@Entity
@Table(name = "user")
public class User extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name should not be null")
    @Column(name = "fullname", length = 100)
    private String fullName;

    @Phone
    @Column(name = "phone", unique = true)
    private String phone;

    @Column(name = "avatar")
    @Builder.Default
    private String avatar = Global.DEFAULT_AVATAR;

    @Email(message = "Please enter the valid email")
    @Column(name = "email", unique = true)
    private String email;

    @NotBlank(message = "Password should not be null")
    @Column(name = "password")
    private String password;

    @Column(name="status")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private UserStatus status = UserStatus.ACTIVE;

    //Wrong number of login attempts
    @Column(name = "failed_attempt")
    @Builder.Default
    private int failedAttempt = 0;

    //account lock period
    @Column(name = "lock_time")
    private LocalDateTime lockTime;

    @Column(name = "mfa_enabled")
    @Builder.Default
    private boolean mfaEnabled = false;

    @Column(name = "secret")
    private String secret;

    @Column(name = "verify_mail")
    @Builder.Default
    private boolean verifyMail = false;

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
}
