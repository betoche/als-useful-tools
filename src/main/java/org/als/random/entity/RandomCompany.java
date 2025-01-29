package org.als.random.entity;

import jakarta.persistence.*;

import java.util.Set;

@Entity
public class RandomCompany {
    @Id
    @GeneratedValue( strategy = jakarta.persistence.GenerationType.IDENTITY )
    private Long id;

    private String name;
    @OneToMany( mappedBy = "company", cascade = CascadeType.ALL )
    private Set<Ticket> tickets;
}
