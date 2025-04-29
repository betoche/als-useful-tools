package org.als.random.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter @Setter
@Builder
public class RandomCountry {
    @Id
    @GeneratedValue
    private Long id;
    private String name;

    @OneToMany( cascade = CascadeType.ALL, orphanRemoval = true )
    private Set<RandomState> states;

    @OneToMany( cascade = CascadeType.ALL, orphanRemoval = true )
    private Set<RandomDepartment> departments;

    @OneToMany( cascade = CascadeType.ALL, orphanRemoval = true )
    private Set<RandomAddress> addresses;
}
