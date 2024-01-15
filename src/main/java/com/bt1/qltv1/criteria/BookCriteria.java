package com.bt1.qltv1.criteria;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class BookCriteria extends Throwable implements Serializable, Criteria {
    private LongFilter id;
    private StringFilter isbn;
    private StringFilter title;
    private StringFilter description;
    private IntegerFilter quantity;
    private BigDecimalFilter price;
    private BigDecimalFilter loanFee;
    private LongFilter authorId;
    private LongFilter genreId;
    private Boolean distinct;

    private BookCriteria(BookCriteria other){
        this.id = other.id == null ? null : other.id.copy();
        this.isbn = other.isbn == null ? null : other.isbn.copy();
        this.title = other.title == null ? null : other.title.copy();
        this.description = other.description == null ? null : other.description.copy();
        this.quantity = other.quantity == null ? null : other.quantity.copy();
        this.price = other.price == null ? null : other.price.copy();
        this.loanFee = other.loanFee == null ? null : other.loanFee.copy();
        this.authorId = other.authorId == null ? null : other.authorId.copy();
        this.genreId = other.genreId == null ? null : other.genreId.copy();
        this.distinct = other.distinct;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public LongFilter authorId() {
        if (authorId == null) {
            authorId = new LongFilter();
        }
        return authorId;
    }

    public LongFilter genreId() {
        if (genreId == null) {
            genreId = new LongFilter();
        }
        return genreId;
    }

    public StringFilter isbn() {
        if (isbn == null) {
            isbn = new StringFilter();
        }
        return isbn;
    }

    public StringFilter title() {
        if (title == null) {
            title = new StringFilter();
        }
        return title;
    }

    public StringFilter description() {
        if (description == null) {
            description = new StringFilter();
        }
        return description;
    }

    public IntegerFilter quantity(){
        if (quantity == null){
            quantity = new IntegerFilter();
        }
        return quantity;
    }

    public BigDecimalFilter price(){
        if (price == null){
            price = new BigDecimalFilter();
        }
        return price;
    }

    public BigDecimalFilter loanFee(){
        if (loanFee == null){
            loanFee = new BigDecimalFilter();
        }
        return loanFee;
    }

    @Override
    public Criteria copy() {
        return new BookCriteria(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BookCriteria that)) return false;
        return Objects.equals(getId(), that.getId())
                && Objects.equals(getIsbn(), that.getIsbn())
                && Objects.equals(getTitle(), that.getTitle())
                && Objects.equals(getDescription(), that.getDescription())
                && Objects.equals(getQuantity(), that.getQuantity())
                && Objects.equals(getPrice(), that.getPrice())
                && Objects.equals(getLoanFee(), that.getLoanFee())
                && Objects.equals(getAuthorId(), that.getAuthorId())
                && Objects.equals(getGenreId(), that.getGenreId())
                && Objects.equals(getDistinct(), that.getDistinct());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getIsbn(), getTitle(), getDescription(), getQuantity()
                , getPrice(), getLoanFee(), getAuthorId(), getGenreId(), getDistinct());
    }
}
