package com.bt1.qltv1.criteria;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

import java.io.Serializable;
import java.util.Objects;



@Getter
@Setter
@ToString
public class UserCriteria extends Throwable implements Serializable, Criteria {
    private LongFilter id;
    private StringFilter fullName;
    private StringFilter phone;
    private StringFilter email;
    private StringFilter role;
    //private RangeFilter<UserStatus> status;
    private BooleanFilter mfaEnabled;
    private BooleanFilter verifyMail;
    private Boolean distinct;

    public UserCriteria(UserCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.fullName = other.fullName == null ? null : other.fullName.copy();
        this.phone = other.phone == null ? null : other.phone.copy();
        this.email = other.email == null ? null : other.email.copy();
        this.role = other.role == null ? null : other.role.copy();
        this.verifyMail = other.verifyMail == null ? null : other.verifyMail.copy();
        this.mfaEnabled = other.mfaEnabled == null ? null : other.mfaEnabled.copy();
        //this.status = other.status == null ? null : other.status.copy();
        this.distinct = other.distinct;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public StringFilter fullName() {
        if (fullName == null) {
            fullName = new StringFilter();
        }
        return fullName;
    }

    public StringFilter phone() {
        if (phone == null) {
            phone = new StringFilter();
        }
        return phone;
    }

    public StringFilter email() {
        if (email == null) {
            email = new StringFilter();
        }
        return email;
    }

    public StringFilter role() {
        if (role == null) {
            role = new StringFilter();
        }
        return role;
    }

    public BooleanFilter verifyMail(){
        if (verifyMail == null) {
            verifyMail = new BooleanFilter();
        }
        return verifyMail;
    }

    public BooleanFilter mfaEnabled(){
        if (mfaEnabled == null) {
            mfaEnabled = new BooleanFilter();
        }
        return mfaEnabled;
    }

//    public RangeFilter<UserStatus> status() {
//        if (status == null) {
//            status = new RangeFilter<>();
//        }
//        return status;
//    }


    @Override
    public Criteria copy() {
        return new UserCriteria(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserCriteria that)) return false;
        return Objects.equals(getId(), that.getId())
                && Objects.equals(getFullName(), that.getFullName())
                && Objects.equals(getPhone(), that.getPhone())
                && Objects.equals(getEmail(), that.getEmail())
//                && Objects.equals(getStatus(), that.getStatus())
                && Objects.equals(getRole(), that.getRole())
                && Objects.equals(getMfaEnabled(), that.getMfaEnabled())
                && Objects.equals(getVerifyMail(), that.getVerifyMail())
                && Objects.equals(getDistinct(), that.getDistinct());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getFullName(), getPhone(),
                getEmail(), /*getStatus(),*/ getMfaEnabled(), getVerifyMail(), getRole(),
                getDistinct());
    }
}
