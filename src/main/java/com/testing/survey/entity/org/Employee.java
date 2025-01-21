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
@Table(name = "EMPLOYEE_TBL")
public class Employee {
    @Id
    @Column(name = "person_id")
    private Long personId;

    private String employeeNumber;
    private String personName;
    private String type;
    private Long organizationId;
    private String organizationName;
    private String jobName;
    private String positionName;
}