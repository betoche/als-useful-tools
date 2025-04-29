package org.als.random.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
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
public class RandomMunicipality {
    @Id
    @GeneratedValue
    private Long id;
    private String name;

    @ManyToOne( fetch = FetchType.LAZY )
    @JdbcTypeCode(SqlTypes.INTEGER)
    @JoinColumn(name = "department_id")
    private RandomDepartment department;

    @OneToMany( cascade = CascadeType.ALL, orphanRemoval = true )
    private Set<RandomAddress> addresses;
}
