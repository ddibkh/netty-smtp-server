package com.mail.smtp.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Table
(
    name = "tbl_path",
    uniqueConstraints =
    {
        @UniqueConstraint(columnNames = {"f_path"})
    }
)

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "tbl_path")
public class PathEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "f_idx")
    private Integer idx;

    @Column(name = "f_path")
    private String path;
}
