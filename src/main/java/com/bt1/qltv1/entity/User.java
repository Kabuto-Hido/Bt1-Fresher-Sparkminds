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
public class User extends Account{
    @Column(name = "avatar")
    @Builder.Default
    private String avatar = Global.DEFAULT_AVATAR;

    @Phone
    @Column(name = "phone", unique = true)
    private String phone;


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User)) {
            return false;
        }
        return getId() != null && getId().equals(((User) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
