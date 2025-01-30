package com.testing.survey.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
public class EmployeeTempDTO {
    private Long id;
    private String employeeNumber;
    private String personName;
    private String type;
    private String organizationName;
    private String gradeName;
    private String repGradeName;
    private String jobName;
    private LocalDateTime jobDate;
    private Boolean isDeleted;
    private Boolean selfYn;
    private Boolean othersTested;
    private Boolean othersTester;
    private Boolean completedSelf;
    private Boolean completedOthers;
    private Long workingMonths;  // 현 소속 근무 기간
}
