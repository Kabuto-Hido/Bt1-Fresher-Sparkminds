package com.bt1.qltv1.entity;


import com.bt1.qltv1.util.Global;
import com.bt1.qltv1.validation.Phone;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvIgnore;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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
    @CsvIgnore
    @Column(name = "avatar")
    @Builder.Default
    private String avatar = Global.DEFAULT_AVATAR;

    @CsvBindByName(column = "Phone", required = true)
    @Phone
    @Column(name = "phone", unique = true)
    private String phone;

    @CsvIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Loan> listLoan = new ArrayList<>();

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
