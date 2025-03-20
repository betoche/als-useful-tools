package org.als.random.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "GLOBAL_TEXT_SEARCH")
public class GlobalTextSearch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
