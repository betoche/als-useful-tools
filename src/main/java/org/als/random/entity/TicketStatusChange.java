package org.als.random.entity;

import jakarta.persistence.*;
import org.als.random.enums.TicketStatusEnum;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Date;

@Entity
public class TicketStatusChange {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private Long id;

    @JdbcTypeCode(SqlTypes.INTEGER)
    private Ticket ticket;
    private TicketStatusEnum initialStatus;
    private TicketStatusEnum finalStatus;
    private Date changeDate;
    private String comment;
    @JdbcTypeCode(SqlTypes.INTEGER)
    @Column(name = "user_id")
    private RandomUser user;
}
