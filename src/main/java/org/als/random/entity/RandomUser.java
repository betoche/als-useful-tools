package org.als.random.entity;

import jakarta.persistence.*;
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

    private String firstName;
    private String lastName;
    private String userName;
    private String email;


    @JdbcTypeCode(SqlTypes.INTEGER)
    private RandomCompany company;

    @OneToMany(mappedBy = "assignee", cascade = CascadeType.ALL)
    private Set<Ticket> assignedTickets;
    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL)
    private Set<Ticket> createdTickets;
}
