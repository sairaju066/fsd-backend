package com.scholarlink.controller;

import com.scholarlink.dto.Dto.*;
import com.scholarlink.model.Scholarship;
import com.scholarlink.repository.ScholarshipRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scholarships")
public class ScholarshipController {

    @Autowired
    private ScholarshipRepository scholarshipRepository;

    // GET /api/scholarships — Public
    @GetMapping
    public ResponseEntity<List<Scholarship>> getAll() {
        return ResponseEntity.ok(scholarshipRepository.findAllByOrderByCreatedAtDesc());
    }

    // GET /api/scholarships/:id — Public
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return scholarshipRepository.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Scholarship not found")));
    }

    // POST /api/scholarships — Admin only
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> create(@Valid @RequestBody ScholarshipRequest req) {
        Scholarship s = new Scholarship();
        s.setTitle(req.getTitle());
        s.setDescription(req.getDescription());
        s.setAmount(req.getAmount());
        s.setDeadline(req.getDeadline());
        s.setEligibilityCriteria(req.getEligibilityCriteria());

        Scholarship saved = scholarshipRepository.save(s);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT /api/scholarships/:id — Admin only
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody ScholarshipRequest req) {
        return scholarshipRepository.findById(id).map(s -> {
            if (req.getTitle() != null)                s.setTitle(req.getTitle());
            if (req.getDescription() != null)          s.setDescription(req.getDescription());
            if (req.getAmount() != null)               s.setAmount(req.getAmount());
            if (req.getDeadline() != null)             s.setDeadline(req.getDeadline());
            if (req.getEligibilityCriteria() != null)  s.setEligibilityCriteria(req.getEligibilityCriteria());
            scholarshipRepository.save(s);
            return ResponseEntity.ok((Object) new MessageResponse("Scholarship updated successfully"));
        }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Scholarship not found")));
    }

    // DELETE /api/scholarships/:id — Admin only
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return scholarshipRepository.findById(id).map(s -> {
            scholarshipRepository.delete(s);
            return ResponseEntity.ok((Object) new MessageResponse("Scholarship deleted"));
        }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Scholarship not found")));
    }
}
