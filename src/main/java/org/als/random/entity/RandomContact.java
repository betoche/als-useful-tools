package org.als.random.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter @Setter
@Builder
public class RandomContact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private String phone;

    @OneToMany( cascade = CascadeType.ALL, orphanRemoval = true )
    private Set<RandomAddress> addresses;

    @OneToMany( cascade = CascadeType.ALL, orphanRemoval = true )
    private Set<RandomUser> users;
}
