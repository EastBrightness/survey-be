package com.testing.survey.entity.org;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "ORG_TBL")
public class Organization {
    @Id
    @Column(name = "organization_id")
    private Long organizationId;

    private String orgName;
    private String fullName;
    private String ocCode;
    private String upCode;
}