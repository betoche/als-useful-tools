package org.als.random.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import org.als.random.enums.TicketStatusEnum;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
public interface Ticket {
    @Id
    Long getId();
    void setId(Long id);
    String getKey();
    void setKey(String key);
    String getTitle();
    void setTitle(String title);
    @JdbcTypeCode(SqlTypes.INTEGER)
    RandomUser getCreatedBy();
    void setCreatedBy(RandomUser createdBy);
    @JdbcTypeCode(SqlTypes.INTEGER)
    RandomUser getAssignee();
    void setAssignee(RandomUser assignee);
    @JdbcTypeCode(SqlTypes.INTEGER)
    RandomCompany getCompany();
    void setCompany(RandomCompany company);
    TicketStatusEnum getStatus();
    void setStatus(TicketStatusEnum ticketStatusEnum);
}
