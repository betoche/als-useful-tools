package org.als.random.entity;

import jakarta.persistence.*;

import static jakarta.persistence.GenerationType.IDENTITY;

import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;

@Entity
@Data
public class CreditCard {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private String holderName;
    private String cardNumber;
    private String expirationDate;
    private Boolean active = true;

    @OneToMany(mappedBy = "creditCard", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PaymentDetails> paymentDetails;

    @ManyToOne(fetch = FetchType.LAZY)
    @JdbcTypeCode(SqlTypes.INTEGER)
    @JoinColumn(name = "owner_id")
    private RandomUser owner;

}
