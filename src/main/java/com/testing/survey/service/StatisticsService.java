package com.testing.survey.service;

import com.testing.survey.dto.statistics.StatisticsRequestDTO;
import com.testing.survey.dto.statistics.StatisticsResponseDTO;
import com.testing.survey.entity.SurveyResponse;
import com.testing.survey.entity.eval.EvaluationPeriod;
import com.testing.survey.entity.temp.EmployeeTemp;
import com.testing.survey.entity.temp.OrganizationTemp;
import com.testing.survey.repository.StatisticsEmployeeRepository;
import com.testing.survey.repository.StatisticsEvaluationPeriodRepository;
import com.testing.survey.repository.StatisticsOrganizationRepository;
import com.testing.survey.repository.StatisticsSurveyResponseRepository;
import com.testing.survey.util.ExcelUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsService {
    private final StatisticsEvaluationPeriodRepository evaluationPeriodRepository;
    private final StatisticsEmployeeRepository employeeRepository;
    private final StatisticsOrganizationRepository organizationRepository;
    private final StatisticsSurveyResponseRepository surveyResponseRepository;

    // 하위 조직 코드를 재귀적으로 찾아 리스트에 추가하는 헬퍼 메서드
    private void addSubOrganizationCodes(String parentCode, List<String> codes) {
        List<OrganizationTemp> subOrgs = organizationRepository.findSubOrganizations(parentCode);
        for (OrganizationTemp org : subOrgs) {
            codes.add(org.getOCode());
            addSubOrganizationCodes(org.getOCode(), codes); // 재귀 호출로 하위 조직도 찾음
        }
    }    @Transactional(readOnly = true)
    public List<String> getPersonTypes() {
        return employeeRepository.findDistinctPersonTypes();
    }

    @Transactional(readOnly = true)
    public List<String> getGrades() {
        return employeeRepository.findDistinctGrades();
    }

    @Transactional(readOnly = true)
    public List<String> getSexes() {
        return employeeRepository.findDistinctSexes();
    }

    @Transactional(readOnly = true)
    public List<String> getAvailableYears() {
        return evaluationPeriodRepository.findDistinctYears();
    }

    @Transactional(readOnly = true)
    public List<EvaluationPeriod> getEvaluationsByYear(String year) {
        return evaluationPeriodRepository.findEvaluationsByYear(year);
    }

    @Transactional(readOnly = true)
    public StatisticsResponseDTO calculateStatistics(StatisticsRequestDTO request) {
        // 평가 기간 ID 찾기 (요청된 연도와 평가명으로)
        EvaluationPeriod period = evaluationPeriodRepository.findEvaluationsByYear(request.getYear()).stream()
                .filter(p -> p.getEvaluationName().equals(request.getEvaluationName()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("해당 평가 기간을 찾을 수 없습니다."));

        Long periodId = period.getId();
        List<String> orgCodes = new ArrayList<>();

        // 조직 필터링
        if (!"all".equals(request.getOrganizationCode())) {
            // 특정 조직과 하위 조직 코드 수집
            orgCodes.add(request.getOrganizationCode());
            addSubOrganizationCodes(request.getOrganizationCode(), orgCodes);

            // 선택한 조직과 하위 조직에 대한 통계 조회
            return StatisticsResponseDTO.builder()
                    // 전체 평가 결과 종합
                    .averageSelfScore(employeeRepository.calculateAverageSelfScoreByOrganizations(periodId, orgCodes))
                    .averageOthersScore(employeeRepository.calculateAverageOthersScoreByOrganizations(periodId, orgCodes))
                    .selfCompletionRate(employeeRepository.calculateSelfCompletionRateByOrganizations(periodId, orgCodes))
                    .othersCompletionRate(employeeRepository.calculateOthersCompletionRateByOrganizations(periodId, orgCodes))

                    // 계급별 통계
                    .gradeStatistics(convertToDualMap(
                            employeeRepository.calculateGradeStatisticsByOrganizations(periodId, orgCodes)
                    ))

                    // 신분별 통계
                    .personTypeStatistics(convertToDualMap(
                            employeeRepository.calculatePersonTypeStatisticsByOrganizations(periodId, orgCodes)
                    ))

                    // 성별 통계
                    .sexStatistics(convertToDualMap(
                            employeeRepository.calculateSexStatisticsByOrganizations(periodId, orgCodes)
                    ))

                    // 조직별 통계 (선택한 조직과 하위 조직)
                    .organizationStatistics(convertToDualMap(
                            employeeRepository.calculateOrganizationStatisticsByOrganizations(periodId, orgCodes)
                    ))

                    // 문항별 통계 (자가평가와 타인평가 구분)
                    .questionStatistics(convertToQuestionMap(
                            surveyResponseRepository.calculateQuestionStatisticsByOrganizations(periodId, orgCodes)
                    ))
                    .build();
        } else {
            // 전체 조직에 대한 통계 조회 (기존 코드 유지)
            return StatisticsResponseDTO.builder()
                    // 전체 평가 결과 종합
                    .averageSelfScore(employeeRepository.calculateAverageSelfScore(periodId))
                    .averageOthersScore(employeeRepository.calculateAverageOthersScore(periodId))
                    .selfCompletionRate(employeeRepository.calculateSelfCompletionRate(periodId))
                    .othersCompletionRate(employeeRepository.calculateOthersCompletionRate(periodId))

                    // 계급별 통계
                    .gradeStatistics(convertToDualMap(
                            employeeRepository.calculateGradeStatistics(periodId)
                    ))

                    // 신분별 통계
                    .personTypeStatistics(convertToDualMap(
                            employeeRepository.calculatePersonTypeStatistics(periodId)
                    ))

                    // 성별 통계
                    .sexStatistics(convertToDualMap(
                            employeeRepository.calculateSexStatistics(periodId)
                    ))

                    // 조직별 통계
                    .organizationStatistics(convertToDualMap(
                            employeeRepository.calculateOrganizationStatistics(periodId)
                    ))

                    // 문항별 통계 (자가평가와 타인평가 구분)
                    .questionStatistics(convertToQuestionMap(
                            surveyResponseRepository.calculateQuestionStatisticsBothTypes(periodId)
                    ))
                    .build();
        }
    }

    // 쿼리 결과를 자가평가/타인평가로 구분된 이중 Map으로 변환
    private Map<String, Map<String, Double>> convertToDualMap(List<Object[]> statResults) {
        Map<String, Map<String, Double>> result = new HashMap<>();

        for (Object[] row : statResults) {
            String key = String.valueOf(row[0]);
            double selfScore = ((Number) row[1]).doubleValue();
            double othersScore = ((Number) row[2]).doubleValue();

            Map<String, Double> scores = new HashMap<>();
            scores.put("self", selfScore);
            scores.put("others", othersScore);

            result.put(key, scores);
        }

        return result;
    }

    // 문항별 통계 결과를 Map으로 변환
    private Map<String, Map<String, Double>> convertToQuestionMap(List<Object[]> statResults) {
        Map<String, Map<String, Double>> result = new HashMap<>();

        for (Object[] row : statResults) {
            String questionId = String.valueOf(row[0]);
            SurveyResponse.EvaluationType type = (SurveyResponse.EvaluationType) row[1];
            double score = ((Number) row[2]).doubleValue();

            result.computeIfAbsent(questionId, k -> new HashMap<>());

            if (type == SurveyResponse.EvaluationType.SELF) {
                result.get(questionId).put("self", score);
            } else if (type == SurveyResponse.EvaluationType.OTHERS) {
                result.get(questionId).put("others", score);
            }
        }

        return result;
    }

    // 엑셀 다운로드 메서드
    @Transactional(readOnly = true)
    public byte[] generateExcelReport(StatisticsRequestDTO request) {
        try {
            // 평가 기간 ID 찾기
            EvaluationPeriod period = evaluationPeriodRepository.findEvaluationsByYear(request.getYear()).stream()
                    .filter(p -> p.getEvaluationName().equals(request.getEvaluationName()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("해당 평가 기간을 찾을 수 없습니다."));

            Long periodId = period.getId();

            // 조직 코드로 필터링
            List<EmployeeTemp> filteredEmployees;
            if ("all".equals(request.getOrganizationCode())) {
                // 전체 선택 시
                filteredEmployees = employeeRepository.findAllByPeriodId(periodId);
            } else {
                // 특정 조직 선택 시 (하위 조직 포함)
                List<String> organizationCodes = new ArrayList<>();
                organizationCodes.add(request.getOrganizationCode());

                // 하위 조직 코드 추가 (재귀적으로 찾음)
                addSubOrganizationCodes(request.getOrganizationCode(), organizationCodes);

                filteredEmployees = employeeRepository.findEmployeesByPeriodAndOrganizations(
                        periodId,
                        organizationCodes
                );
            }

            // 추가 필터링 적용 (신분, 계급, 성별)
            if (request.getPersonType() != null && !request.getPersonType().isEmpty()) {
                filteredEmployees = filteredEmployees.stream()
                        .filter(e -> request.getPersonType().equals(e.getPersonTypeName()))
                        .collect(Collectors.toList());
            }

            if (request.getGrade() != null && !request.getGrade().isEmpty()) {
                filteredEmployees = filteredEmployees.stream()
                        .filter(e -> request.getGrade().equals(e.getRepGradeName()))
                        .collect(Collectors.toList());
            }

            if (request.getSex() != null && !request.getSex().isEmpty()) {
                filteredEmployees = filteredEmployees.stream()
                        .filter(e -> request.getSex().equals(e.getSex()))
                        .collect(Collectors.toList());
            }

            // 문항별 응답 데이터 가져오기
            List<SurveyResponse> surveyResponses = surveyResponseRepository.findAllByPeriodId(periodId);

            // 문항 ID를 정렬된 인덱스로 매핑 (자가평가, 타인평가 각각)
            List<Long> selfQuestionIds = surveyResponses.stream()
                    .filter(sr -> sr.getEvaluationType() == SurveyResponse.EvaluationType.SELF)
                    .map(SurveyResponse::getQuestionId)
                    .distinct()
                    .sorted()
                    .collect(Collectors.toList());

            List<Long> othersQuestionIds = surveyResponses.stream()
                    .filter(sr -> sr.getEvaluationType() == SurveyResponse.EvaluationType.OTHERS)
                    .map(SurveyResponse::getQuestionId)
                    .distinct()
                    .sorted()
                    .collect(Collectors.toList());

            // 직원별 문항 응답 데이터 매핑
            Map<String, Map<Long, Integer>> selfResponsesByEmployee = new HashMap<>();
            Map<String, Map<Long, Integer>> othersResponsesByEmployee = new HashMap<>();

            // 응답 데이터 구성
            for (SurveyResponse response : surveyResponses) {
                String employeeNumber = response.getRespondentNumber();
                Long questionId = response.getQuestionId();
                Integer score = response.getRespondentScore();

                if (response.getEvaluationType() == SurveyResponse.EvaluationType.SELF) {
                    selfResponsesByEmployee
                            .computeIfAbsent(employeeNumber, k -> new HashMap<>())
                            .put(questionId, score);
                } else if (response.getEvaluationType() == SurveyResponse.EvaluationType.OTHERS) {
                    othersResponsesByEmployee
                            .computeIfAbsent(employeeNumber, k -> new HashMap<>())
                            .put(questionId, score);
                }
            }

            // 문항별 통계 (전체 평균)
            Map<String, Map<String, Double>> questionStats = convertToQuestionMap(
                    surveyResponseRepository.calculateQuestionStatisticsBothTypes(periodId)
            );

            // 엑셀 파일 생성
            return ExcelUtility.generateEvaluationStatisticsExcel(
                    filteredEmployees,
                    selfQuestionIds,
                    othersQuestionIds,
                    selfResponsesByEmployee,
                    othersResponsesByEmployee,
                    questionStats,
                    request
            );
        } catch (Exception e) {
            throw new RuntimeException("엑셀 파일 생성 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }
}