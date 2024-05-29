package com.thread_exec.thread_executor.quartz.job;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class EmailJob extends QuartzJobBean {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private MailProperties mailProperties;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        JobDataMap jobDataMap = context.getMergedJobDataMap();

        String subject = jobDataMap.getString("subject");
        String body = jobDataMap.getString("body");
        String recipient = jobDataMap.getString("email");

        sendEmail(mailProperties.getUsername(),recipient,subject, body);
    }
    private void sendEmail(String from , String to, String subject, String body){
       try{
           MimeMessage mimeMessage = mailSender.createMimeMessage();
           MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, StandardCharsets.UTF_8.toString());
           messageHelper.setSubject(subject);
           messageHelper.setText(body, true);
           messageHelper.setFrom(from);
           messageHelper.setText(to);

           mailSender.send(mimeMessage);
       }catch (MessagingException e){
            log.error("Exception while sending the email userInfo : " + " from "+ from+" to "+ to + " subject "+ subject + " body "+ body);
            e.printStackTrace();
       }catch (Exception e){
           log.error("Exception while sending the email ");
           e.printStackTrace();
       }
    }
}
