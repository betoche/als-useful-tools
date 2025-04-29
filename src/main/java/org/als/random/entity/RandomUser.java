package org.als.random.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Set;

@Entity
@Getter @Setter
@Builder
public class RandomUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userName;
    private String password;

    @ManyToOne(fetch = FetchType.LAZY)
    @JdbcTypeCode(SqlTypes.INTEGER)
    @JoinColumn(name = "contact_id")
    private RandomContact contact;

    @ManyToOne(fetch = FetchType.LAZY)
    @JdbcTypeCode(SqlTypes.INTEGER)
    @JoinColumn(name = "company_id")
    private RandomCompany company;

    @OneToMany(mappedBy = "assignee", cascade = CascadeType.ALL)
    private Set<Ticket> assignedTickets;
    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL)
    private Set<Ticket> createdTickets;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<BankAccount> bankAccounts;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CreditCard> creditCards;

    @OneToMany(mappedBy = "spender", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Expense> expenses;
}
