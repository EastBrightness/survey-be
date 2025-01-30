package com.testing.survey.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class OrganizationTempDTO {
    private Long id;
    private String orgName;
    private String fullName;
    private String oCode;
    private String upCode;
    private int employeeCount;
    private Boolean isDeleted;
}