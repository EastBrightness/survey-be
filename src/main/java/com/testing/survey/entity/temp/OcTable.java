package com.testing.survey.entity.temp;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "OC_TBL")
@Getter @Setter
@NoArgsConstructor
public class OcTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_number")
    private String employeeNumber;  // 평가 대상자의 사원번호

    @Column(name = "email")
    private String email;  // 평가자의 사원번호
}