package com.bt1.qltv1.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@AllArgsConstructor
@SuperBuilder
@NoArgsConstructor
@Getter
@Setter

@Entity
@Table(name = "admin")
public class Admin extends Account{

    @Column(name = "position", length = 100)
    private String position;

}
