package com.bt1.qltv1.entity;

import com.bt1.qltv1.enumeration.SessionStatus;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
//@Data
//@EqualsAndHashCode(callSuper = false)
@Getter
@Setter

@Entity
@Table(name = "session")
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "jti", unique = true)
    private String jti;

    @Column(name = "expired_date")
    private LocalDateTime expiredDate;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private SessionStatus status;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "account_id", referencedColumnName = "id")
    private Account accountId;

    public boolean isActive(){
        return this.status.equals(SessionStatus.ACTIVE);
    }
    public boolean isBlock(){
        return this.status.equals(SessionStatus.BLOCK);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Session)) return false;
        return id != null && id.equals(((Session) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
