package org.testautomation.commons.builders;

import lombok.Getter;
import org.testautomation.commons.utils.usercredentials.FakeDataHelper;

import java.math.BigDecimal;

/**
 * Fluent builder for assembling an Order test-data object.
 *
 * <p>Usage:
 * <pre>
 *   OrderBuilder order = OrderBuilder.random()
 *       .withAmount(new BigDecimal("99.99"))
 *       .withCurrency("EUR");
 * </pre>
 */
@Getter
public class OrderBuilder {

    private String orderId;
    private String userId;
    private BigDecimal amount;
    private String currency;
    private String status;
    private String description;

    private OrderBuilder() {}

    /** Creates a builder pre-filled with random fake data. */
    public static OrderBuilder random() {
        return new OrderBuilder()
                .withOrderId("ORD-" + FakeDataHelper.alphanumeric(8).toUpperCase())
                .withUserId(FakeDataHelper.uuid())
                .withAmount(BigDecimal.valueOf(FakeDataHelper.numberBetween(1, 500)))
                .withCurrency("EUR")
                .withStatus("PENDING")
                .withDescription("Test order — " + FakeDataHelper.alphanumeric(6));
    }

    public static OrderBuilder empty() {
        return new OrderBuilder();
    }

    public OrderBuilder withOrderId(String orderId)       { this.orderId     = orderId;     return this; }
    public OrderBuilder withUserId(String userId)         { this.userId      = userId;      return this; }
    public OrderBuilder withAmount(BigDecimal amount)     { this.amount      = amount;      return this; }
    public OrderBuilder withCurrency(String currency)     { this.currency    = currency;    return this; }
    public OrderBuilder withStatus(String status)         { this.status      = status;      return this; }
    public OrderBuilder withDescription(String description){ this.description = description; return this; }
}
