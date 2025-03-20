package org.als.random.entity;

import static jakarta.persistence.GenerationType.IDENTITY;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;

@Entity
@Data
public class PaymentDetails {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private BigDecimal amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JdbcTypeCode(SqlTypes.INTEGER)
    @JoinColumn(name = "payment_id")
    private Payment payment;
    @ManyToOne(fetch = FetchType.LAZY)
    @JdbcTypeCode(SqlTypes.INTEGER)
    @JoinColumn(name = "bank_account_id")
    private BankAccount bankAccount;
    @ManyToOne(fetch = FetchType.LAZY)
    @JdbcTypeCode(SqlTypes.INTEGER)
    @JoinColumn(name = "credit_card_id")
    private CreditCard creditCard;

}
