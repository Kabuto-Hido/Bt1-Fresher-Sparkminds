package com.bt1.qltv1.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Builder
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
    @Column(name = "name")
    private String name;

    @ManyToMany(mappedBy = "roleSet")
    @JsonBackReference
    private Set<User> userSet;

}
