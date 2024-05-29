package com.thread_exec.thread_executor.repo;

import com.thread_exec.thread_executor.model.Tasks;
import jakarta.transaction.Transactional;
import org.hibernate.annotations.Parent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskRepo extends JpaRepository<Tasks, Long> {

    Optional<Tasks> findByTaskId(String taskId);

    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Tasks t set t.processedCount = t.processedCount + 1 where t.taskId = :task_id")
    void updateProcessCount(@Param("task_id") String taskId);

    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Tasks t set t.status = :status where t.taskId=:task_id")
    void updateTaskStatus(@Param("status") String status, @Param("task_id") String taskId);

    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Tasks t set t.status = :status,t.totalTime =:total_time where t.taskId=:task_id")
    void updateTaskStatusWithTime(@Param("status") String status, @Param("task_id") String taskId, @Param("total_time") Long total_time);


}
