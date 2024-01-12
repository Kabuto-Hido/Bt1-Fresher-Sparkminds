package com.bt1.qltv1.dto.borrow.email;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class EmailBook {
    private String name;
    private Integer quantity;
    private String fee;
    private String imageName;
}
