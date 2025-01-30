package com.testing.survey.entity.temp;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "EVAL_ASSIGN_TBL")
@Getter @Setter
@NoArgsConstructor
public class EvalAssign {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tested")
    private String tested;  // 평가 대상자의 사원번호

    @Column(name = "tester")
    private String tester;  // 평가자의 사원번호
}