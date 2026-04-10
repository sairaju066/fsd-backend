package com.scholarlink.controller;

import com.scholarlink.dto.Dto.*;
import com.scholarlink.model.Application;
import com.scholarlink.model.User;
import com.scholarlink.repository.ApplicationRepository;
import com.scholarlink.repository.ScholarshipRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    @Autowired private ApplicationRepository applicationRepository;
    @Autowired private ScholarshipRepository scholarshipRepository;

    // POST /api/applications/:scholarshipId — Student
    @PostMapping("/{scholarshipId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> apply(@PathVariable Long scholarshipId,
                                   @Valid @RequestBody ApplicationRequest req,
                                   @AuthenticationPrincipal User user) {
        return scholarshipRepository.findById(scholarshipId).map(scholarship -> {
            if (applicationRepository.findByStudentAndScholarship(user, scholarship).isPresent()) {
                return ResponseEntity.badRequest()
                        .<Object>body(new MessageResponse("You have already applied for this scholarship"));
            }

            Application app = new Application();
            app.setStudent(user);
            app.setScholarship(scholarship);
            app.setApplicationText(req.getApplicationText());

            Application saved = applicationRepository.save(app);

            ApplicationResponse resp = new ApplicationResponse();
            resp.setId(saved.getId());
            resp.setStudentId(user.getId());
            resp.setScholarshipId(scholarshipId);
            resp.setStatus(saved.getStatus().name());
            resp.setApplicationText(saved.getApplicationText());
            resp.setAppliedAt(saved.getAppliedAt());

            return ResponseEntity.status(HttpStatus.CREATED).<Object>body(resp);
        }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Scholarship not found")));
    }

    // GET /api/applications/my-applications — Authenticated student
    @GetMapping("/my-applications")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getMyApplications(@AuthenticationPrincipal User user) {
        List<MyApplicationResponse> list = applicationRepository
                .findByStudentOrderByAppliedAtDesc(user)
                .stream()
                .map(app -> {
                    MyApplicationResponse r = new MyApplicationResponse();
                    r.setId(app.getId());
                    r.setScholarshipId(app.getScholarship().getId());
                    r.setTitle(app.getScholarship().getTitle());
                    r.setAmount(app.getScholarship().getAmount());
                    r.setStatus(app.getStatus().name());
                    r.setApplicationText(app.getApplicationText());
                    r.setAppliedAt(app.getAppliedAt());
                    return r;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(list);
    }

    // GET /api/applications — Admin only
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllApplications() {
        List<AllApplicationResponse> list = applicationRepository.findAllWithDetails()
                .stream()
                .map(app -> {
                    AllApplicationResponse r = new AllApplicationResponse();
                    r.setId(app.getId());
                    r.setScholarshipTitle(app.getScholarship().getTitle());
                    r.setStudentName(app.getStudent().getName());
                    r.setStudentEmail(app.getStudent().getEmail());
                    r.setStatus(app.getStatus().name());
                    r.setApplicationText(app.getApplicationText());
                    r.setAppliedAt(app.getAppliedAt());
                    return r;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(list);
    }

    // PUT /api/applications/:id/status — Admin only
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateStatus(@PathVariable Long id,
                                          @RequestBody StatusUpdateRequest req) {
        try {
            Application.Status newStatus = Application.Status.valueOf(req.getStatus());
            return applicationRepository.findById(id).map(app -> {
                app.setStatus(newStatus);
                applicationRepository.save(app);
                return ResponseEntity.ok((Object) new MessageResponse("Application status updated successfully"));
            }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Application not found")));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Invalid status"));
        }
    }
}
