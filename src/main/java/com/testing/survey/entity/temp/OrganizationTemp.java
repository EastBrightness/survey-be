package com.testing.survey.entity.temp;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ORG_TBL_TEMP")
@Getter @Setter
@NoArgsConstructor
public class OrganizationTemp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "organization_id")
    private Long id;

    @Column(name = "org_name")
    private String orgName;  // 조직 이름 (개발과)

    @Column(name = "full_name")
    private String fullName;  // 전체 이름 (소스쿡 인천지부 지능단 개발과)

    @Column(name = "o_code")
    private String oCode;  // 조직 코드

    @Column(name = "up_code")
    private String upCode;  // 상위 조직 코드

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;
}
