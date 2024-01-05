package com.bt1.qltv1.criteria.filter;

import com.bt1.qltv1.enumeration.UserStatus;
import tech.jhipster.service.filter.Filter;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class UserStatusFilter extends Filter<UserStatus> implements Serializable {
    private static final long serialVersionUID = 1L;
    public UserStatusFilter() {
    }

    public UserStatusFilter(UserStatusFilter filter) {
        super(filter);
    }

    @Override
    public UserStatusFilter copy() {
        return new UserStatusFilter(this);
    }
}
