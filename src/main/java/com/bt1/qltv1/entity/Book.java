package com.bt1.qltv1.entity;

import com.bt1.qltv1.util.Global;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.validator.constraints.ISBN;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter

@Entity
@Table(name = "book")
public class Book extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ISBN
    @Column(name = "isbn", unique = true)
    private String isbn;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "image")
    @Builder.Default
    private String image = Global.DEFAULT_IMAGE;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "quantity", nullable = false)
    @Builder.Default
    private Integer quantity = 1;

    @Column(name = "available")
    @Builder.Default
    private boolean available = true;

    @Column(name = "price")
    private Double price;

    @Column(name = "loan_fee")
    private Double loanFee;

    @ManyToOne(fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "author_id", referencedColumnName = "id")
    private Author authorId;

    @ManyToOne(fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "genre_id", referencedColumnName = "id")
    private Genre genreId;
}
