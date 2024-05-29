package com.thread_exec.thread_executor.controller;

import com.thread_exec.thread_executor.payload.EmailRequest;
import com.thread_exec.thread_executor.payload.EmailResponseDto;
import com.thread_exec.thread_executor.quartz.job.EmailJob;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;



@Slf4j
@RestController
@RequestMapping("api/core/schedular")
public class EmailSchedularController {

    @Autowired
    private Scheduler scheduler;


    @PostMapping(value = "/email")
    public ResponseEntity<EmailResponseDto> scheduleEmail(@RequestBody EmailRequest emailRequest) {
        try {

            ZonedDateTime dateTime = ZonedDateTime.of(emailRequest.getDateTime(), emailRequest.getZoneId());
            if (dateTime.isBefore(ZonedDateTime.now())) {
                EmailResponseDto responseDto = new EmailResponseDto(
                        false,
                        "date time must be after current date"
                );
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDto);
            }

            JobDetail jobDetail = buildJobDetails(emailRequest);
            Trigger trigger = buildTrigger(jobDetail, dateTime);
            scheduler.scheduleJob(jobDetail, trigger);
            EmailResponseDto emailResponseDto = new EmailResponseDto(
                    true,
                    jobDetail.getKey().getName(),
                    jobDetail.getKey().getGroup(),
                    "email scheduled successfully"
            );

            return ResponseEntity.status(HttpStatus.OK).body(emailResponseDto);


        } catch (SchedulerException se) {
            log.error("Error while scheduling the email");
            EmailResponseDto emailResponseDto = new EmailResponseDto(
                    false,
                    "Error while scheduling the email, Please try again !");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(emailResponseDto);

        }
    }

    private JobDetail buildJobDetails(EmailRequest scheduleEmailRequest) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("email", scheduleEmailRequest.getEmail());
        jobDataMap.put("subject", scheduleEmailRequest.getSubject());
        jobDataMap.put("body", scheduleEmailRequest.getBody());

        return JobBuilder.newJob(EmailJob.class)
                .withIdentity(UUID.randomUUID().toString())
                .withDescription("Sending Email job")
                .usingJobData(jobDataMap)
                .storeDurably()
                .build();
    }

    private Trigger buildTrigger(JobDetail jobDetail, ZonedDateTime startAt) {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(jobDetail.getKey().getName(), "eamil-triggers")
                .startAt(Date.from(startAt.toInstant()))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
                .build();
    }
}
