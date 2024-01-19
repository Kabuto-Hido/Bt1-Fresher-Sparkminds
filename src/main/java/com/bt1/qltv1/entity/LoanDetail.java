package com.bt1.qltv1.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.Min;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter

@Entity
@Table(name = "loan_detail")
public class LoanDetail extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "quantity", nullable = false)
    @Min(value = 1, message = "{book.quantity.minimum}")
    @Builder.Default
    private Integer quantity = 1;

    @Column(name = "actual_return_date")
    private LocalDateTime actualReturnDate;


    @Column(name = "unit_price", nullable = false)
    private BigDecimal unitPrice;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "loan_id", referencedColumnName = "id")
    private Loan loan;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "book_id", referencedColumnName = "id")
    private Book book;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LoanDetail)) return false;
        return id != null && id.equals(((LoanDetail) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
