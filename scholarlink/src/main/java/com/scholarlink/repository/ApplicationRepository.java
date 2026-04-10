package com.scholarlink.repository;

import com.scholarlink.model.Application;
import com.scholarlink.model.User;
import com.scholarlink.model.Scholarship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    List<Application> findByStudentOrderByAppliedAtDesc(User student);

    Optional<Application> findByStudentAndScholarship(User student, Scholarship scholarship);

    @Query("SELECT a FROM Application a JOIN FETCH a.student JOIN FETCH a.scholarship ORDER BY a.appliedAt DESC")
    List<Application> findAllWithDetails();
}
