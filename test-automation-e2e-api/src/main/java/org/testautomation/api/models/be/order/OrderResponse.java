package org.testautomation.api.models.be.order;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderResponse {
    private String id;
    private String userId;
    private BigDecimal amount;
    private String currency;
    private String status;
    private String description;
    private String createdAt;
}
