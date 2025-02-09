package com.testing.survey.service;

import com.testing.survey.entity.temp.EmployeeTemp;
import com.testing.survey.entity.temp.OrganizationTemp;
import com.testing.survey.repository.EmployeeTempRepository;
import com.testing.survey.repository.OrganizationTempRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class SurveyManageService {

    private final EmployeeTempRepository employeeRepository;
    private final OrganizationTempRepository organizationRepository;
    private final JavaMailSender mailSender;

    public SurveyManageService(
            EmployeeTempRepository employeeRepository,
            OrganizationTempRepository organizationRepository,
            JavaMailSender mailSender
    ) {
        this.employeeRepository = employeeRepository;
        this.organizationRepository = organizationRepository;
        this.mailSender = mailSender;
    }

    public List<OrganizationTemp> getAllOrganizations() {
        return organizationRepository.findAllByIsDeletedFalse();
    }

    public List<EmployeeTemp> getEmployeesByOrganization(Long orgId) {
        return employeeRepository.findByOrganizationIdAndIsDeletedFalse(orgId);
    }

    @Transactional
    public void sendReminderEmails(Long orgId, String message, String type) {
        List<EmployeeTemp> employees = employeeRepository.findByOrganizationIdAndIsDeletedFalse(orgId);

        for (EmployeeTemp employee : employees) {
            boolean shouldSendEmail = type.equals("self") ? !employee.getCompletedSelf() : !employee.getCompletedOthers();

            if (shouldSendEmail) {
                String email = getEmployeeEmail(employee.getEmployeeNumber());
                if (email != null) {
                    sendEmail(email, message);
                }
            }
        }
    }

    private String getEmployeeEmail(String employeeNumber) {
        // Implement the logic to fetch email from OC_TBL using employee number
        return employeeRepository.findEmailByEmployeeNumber(employeeNumber);
    }

    private void sendEmail(String to, String message) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(to);
        mailMessage.setSubject("Survey Completion Reminder");
        mailMessage.setText(message);
        mailSender.send(mailMessage);
    }

    public Map<String, Integer> getIncompleteCount() {
        List<EmployeeTemp> allEmployees = employeeRepository.findAllByIsDeletedFalse();

        int selfIncomplete = (int) allEmployees.stream()
                .filter(e -> !e.getCompletedSelf())
                .count();

        int othersIncomplete = (int) allEmployees.stream()
                .filter(e -> !e.getCompletedOthers())
                .count();

        return Map.of(
                "self", selfIncomplete,
                "others", othersIncomplete
        );
    }

    @Transactional
    public void sendGlobalReminderEmails(String message, String type) {
        List<EmployeeTemp> allEmployees = employeeRepository.findAllByIsDeletedFalse();

        for (EmployeeTemp employee : allEmployees) {
            boolean shouldSendEmail = type.equals("self") ?
                    !employee.getCompletedSelf() : !employee.getCompletedOthers();

            if (shouldSendEmail) {
                String email = getEmployeeEmail(employee.getEmployeeNumber());
                if (email != null) {
                    sendEmail(email, message);
                }
            }
        }
    }

}