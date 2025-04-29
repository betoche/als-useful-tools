package org.als.random.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
public class RandomAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private boolean primaryAddress = false;

    @ManyToOne( fetch = FetchType.LAZY )
    @JdbcTypeCode(SqlTypes.INTEGER)
    @JoinColumn(name = "country_id")
    private RandomCountry country;

    @ManyToOne( fetch = FetchType.LAZY )
    @JdbcTypeCode(SqlTypes.INTEGER)
    @JoinColumn(name = "state_id")
    private RandomState state;

    @ManyToOne( fetch = FetchType.LAZY )
    @JdbcTypeCode(SqlTypes.INTEGER)
    @JoinColumn(name = "city_id")
    private RandomCity city;

    @ManyToOne( fetch = FetchType.LAZY )
    @JdbcTypeCode(SqlTypes.INTEGER)
    @JoinColumn(name = "zip_code_id")
    private RandomZipCode zipCode;

    @ManyToOne( fetch = FetchType.LAZY )
    @JdbcTypeCode(SqlTypes.INTEGER)
    @JoinColumn(name = "department_id")
    private RandomDepartment department;

    @ManyToOne( fetch = FetchType.LAZY )
    @JdbcTypeCode(SqlTypes.INTEGER)
    @JoinColumn(name = "municipality_id")
    private RandomMunicipality municipality;

    @ManyToOne( fetch = FetchType.LAZY )
    @JdbcTypeCode(SqlTypes.INTEGER)
    @JoinColumn(name = "contact_id")
    private RandomContact contact;
}
