package com.bt1.qltv1.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
//@Data
//@EqualsAndHashCode(callSuper = false)
@Getter
@Setter

@Entity
@Table(name = "role")
public class Role extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", nullable = false)
    private String name;

    @ManyToMany(mappedBy = "roleSet", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Account> accountSet = new HashSet<>();

}
