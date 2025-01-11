package org.coder.err.programming._2_design_chapter.apidesgin.apiresponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderInfo {
    private String status;
    private long orderId;
}
