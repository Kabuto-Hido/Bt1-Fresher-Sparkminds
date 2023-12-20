package com.bt1.qltv1.entity;

import com.bt1.qltv1.config.SessionStatus;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString

@Entity
@Table(name = "session")
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String jti;

    @Column(name = "expired_date")
    private LocalDateTime expiredDate;

    @Column
    @Enumerated(EnumType.STRING)
    private SessionStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "userId", referencedColumnName = "id")
    private User userId;

    public boolean isActive(){
        return this.status.toString().equalsIgnoreCase("active");
    }
    public boolean isBlock(){
        return this.status.toString().equalsIgnoreCase("block");
    }
}
