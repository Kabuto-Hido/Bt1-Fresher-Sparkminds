package com.bt1.qltv1.entity;

import com.bt1.qltv1.util.Global;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvIgnore;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.validator.constraints.ISBN;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter

@Entity
@Table(name = "book")
public class Book extends BaseEntity{
    @CsvIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CsvBindByName(column = "Isbn", required = true)
    @ISBN
    @Column(name = "isbn", unique = true)
    private String isbn;

    @CsvBindByName(column = "Title", required = true)
    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @CsvIgnore
    @Column(name = "image")
    @Builder.Default
    private String image = Global.DEFAULT_IMAGE;

    @CsvBindByName(column = "Description")
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @CsvBindByName(column = "Quantity", required = true)
    @Column(name = "quantity", nullable = false)
    @Min(value = 1, message = "{book.quantity.minimum}")
    @Builder.Default
    private Integer quantity = 1;

    @CsvIgnore
    @Column(name = "available")
    @Builder.Default
    private boolean available = true;

    @CsvIgnore
    @Column(name = "in_stock")
    @Builder.Default
    private boolean inStock = true;

    @CsvBindByName(column = "Price", required = true)
    @NotNull(message = "{book.price.null}")
    @Column(name = "price")
    private BigDecimal price;

    @CsvBindByName(column = "Loan Fee", required = true)
    @NotNull(message = "{book.loan-fee.null}")
    @Column(name = "loan_fee")
    private BigDecimal loanFee;

    @CsvBindByName(column = "Author")
    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "author_id", referencedColumnName = "id")
    private Author authorId;

    @CsvBindByName(column = "Genre")
    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "genre_id", referencedColumnName = "id")
    private Genre genreId;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<LoanDetail> listLoanDetail = new ArrayList<>();
}
