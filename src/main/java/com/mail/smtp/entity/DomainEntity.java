package com.mail.smtp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(name = "tbl_domain")
public class DomainEntity
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "f_idx")
    private Integer idx;

    @Column(name = "f_name", columnDefinition = "varchar(255) unique not null")
    private String domainName;

    @JsonIgnore
    @Column(name = "f_status", columnDefinition = "integer default 0")
    private Integer status;
}
