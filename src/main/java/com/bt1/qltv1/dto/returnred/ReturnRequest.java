package com.bt1.qltv1.dto.returnred;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ReturnRequest {
    @NotNull(message = "${loan.id.null}")
    private Long loanId;
    private List<BookReturnRequest> bookReturnRequests;
}
