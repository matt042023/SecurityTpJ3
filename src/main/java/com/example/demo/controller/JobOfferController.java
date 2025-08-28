package com.example.demo.controller;

import com.example.demo.models.JobOffer;
import com.example.demo.models.UserApp;
import com.example.demo.repositories.JobOfferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/jobs")
public class JobOfferController {

    @Autowired
    JobOfferRepository jobOfferRepository;

    @GetMapping
    public ResponseEntity<List<JobOffer>> getAllJobs() {
        return ResponseEntity.ok(jobOfferRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobOffer> getJobById(@PathVariable Integer id) {
        Optional<JobOffer> jobOffer = jobOfferRepository.findById(id);
        if (jobOffer.isPresent()) {
            return ResponseEntity.ok(jobOffer.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<JobOffer> createJob(@RequestBody JobOffer jobOffer, Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(401).build();
        }
        
        UserApp currentUser = (UserApp) authentication.getPrincipal();
        jobOffer.setCreatedBy(currentUser);
        
        JobOffer savedJob = jobOfferRepository.save(jobOffer);
        return ResponseEntity.ok(savedJob);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJob(@PathVariable Integer id, Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(401).build();
        }

        Optional<JobOffer> jobOffer = jobOfferRepository.findById(id);
        if (!jobOffer.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        UserApp currentUser = (UserApp) authentication.getPrincipal();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        
        boolean isOwner = jobOffer.get().getCreatedBy().getId().equals(currentUser.getId());

        if (isAdmin || isOwner) {
            jobOfferRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.status(403).build();
    }

    @DeleteMapping("/{id}/admin")
    public ResponseEntity<Void> deleteJobAsAdmin(@PathVariable Integer id) {
        Optional<JobOffer> jobOffer = jobOfferRepository.findById(id);
        if (!jobOffer.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        jobOfferRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}