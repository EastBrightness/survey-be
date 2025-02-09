package com.testing.survey.entity.temp;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "EMPLOYEE_TBL_TEMP")
@Getter @Setter
@NoArgsConstructor
public class EmployeeTemp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "person_id")
    private Long id;

    @Column(name = "employee_number")
    private String employeeNumber;  // 사원번호

    @Column(name = "person_name")
    private String personName;

    @Column(name = "type")
    private String type;  // 현장직

    @Column(name = "organization_name")
    private String organizationName;  // 부서명

    @Column(name = "grade_name")
    private String gradeName;  // 과장

    @Column(name = "rep_grade_name")
    private String repGradeName;  // 과장(진급예정자)

    @Column(name = "job_name")
    private String jobName;  // 개발과장

    @Column(name = "job_date")
    private LocalDateTime jobDate;  // 전입일자

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @Column(name = "self_yn")
    private Boolean selfYn = true;  // 자가평가 대상자

    @Column(name = "others_tested")
    private Boolean othersTested = false;  // 타인평가 대상자

    @Column(name = "others_tester")
    private Boolean othersTester = false;  // 타인평가 평가자

    @Column(name = "completed_self")
    private Boolean completedSelf = false;  // 자가평가 완료

    @Column(name = "completed_others")
    private Boolean completedOthers = false;

    @Column(name = "organization_id")
    private Long organizationId;// 타인평가 완료
}
