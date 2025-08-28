package com.example.demo.repositories;

import com.example.demo.models.JobOffer;
import com.example.demo.models.UserApp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobOfferRepository extends JpaRepository<JobOffer, Integer> {
    List<JobOffer> findByCreatedBy(UserApp createdBy);
}