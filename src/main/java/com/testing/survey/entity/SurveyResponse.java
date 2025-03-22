package com.testing.survey.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "survey_responses")
@Getter
@Setter
@NoArgsConstructor
public class SurveyResponse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long periodId;

    @Column(nullable = false)
    private Long questionId;

    @Column(nullable = false)
    private String respondentNumber;

    private String testedNumber;

    @Column(nullable = false)
    private Integer selectedAnswer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EvaluationType evaluationType;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "text_answer", length = 1000)
    private String textAnswer;

    @Column(name = "tested_rank")
    private String testedRank;

    @Column(name = "category")
    private String category;

    @Column(name = "respondent_score")
    private Integer respondentScore;

    public enum EvaluationType {
        SELF, OTHERS
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}