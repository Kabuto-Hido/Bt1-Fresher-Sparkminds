package com.bt1.qltv1.entity;


import com.bt1.qltv1.config.Global;
import com.bt1.qltv1.config.UserStatus;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString

@Entity
@Table(name = "user")
public class User extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name should not be null")
    @Column(name = "fullname")
    private String fullName;

    @Pattern(regexp = "^\\d{10}$")
    @Column(name = "phone", unique = true)
    private String phone;

    @Column(name = "avatar")
    private String avatar;

    @Email(message = "Please enter the valid email")
    @Column(name = "email", unique = true)
    private String email;

    @NotBlank(message = "Password should not be null")
    @Column(name = "password")
    private String password;

    @Column(name="status")
    @Enumerated(EnumType.STRING)
    private UserStatus status = UserStatus.BLOCK;
    //Wrong number of login attempts
    @Column(name = "failed_attempt")
    private int failedAttempt = 0;

    //account lock period
    @Column(name = "lock_time")
    private LocalDateTime lockTime;

    //relationship with role
    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinTable(name = "users_roles", // table link two relationship
            joinColumns = @JoinColumn(name = "userId"), // Key is link with table Users
            inverseJoinColumns = @JoinColumn(name = "roleId")) //Key is link with table Roles
    private Set<Role> roleSet;

    //relationship with session user
    @OneToMany(mappedBy = "userId", cascade = CascadeType.ALL)
    private List<Session> listSession;

    public boolean isActive(){
        return this.status.toString().equalsIgnoreCase("active");
    }
    public boolean isBlock(){
        return this.status.toString().equalsIgnoreCase("block");
    }

    public boolean isLockTimeExpired(){
        if(this.getLockTime() == null){
            return false;
        }
        long lockTimeInMillis = this.getLockTime()
                .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long currentTimeInMillis = System.currentTimeMillis();

        return (lockTimeInMillis + Global.LOCK_TIME_DURATION) < currentTimeInMillis;
    }
}
