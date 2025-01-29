package org.als.random.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.als.random.enums.TCEVersionEnum;
import org.als.random.enums.TicketStatusEnum;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Getter @Setter
@Builder
public class JiraTicket implements Ticket {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private Long id;

    private String key;
    private String title;
    private TCEVersionEnum tceProdVersion;
    private TCEVersionEnum tceDevVersion;
    private String stepsToReproduce;
    private String expectedResult;
    private String actualResult;
    private String ticketUrl;
    private TicketStatusEnum status;

    @ManyToOne( fetch = FetchType.LAZY )
    @JdbcTypeCode(SqlTypes.INTEGER)
    private RandomUser createdBy;

    @ManyToOne( fetch = FetchType.LAZY )
    @JdbcTypeCode(SqlTypes.INTEGER)
    private RandomUser assignee;

    @ManyToOne( fetch = FetchType.LAZY )
    @JdbcTypeCode(SqlTypes.INTEGER)
    private RandomCompany company;

}
