package com.example.demo.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "job_offer")
public class JobOffer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    private String title;
    private String description;
    private String company;
    private String location;
    private Double salary;
    
    @ManyToOne
    @JoinColumn(name = "created_by")
    private UserApp createdBy;

    public JobOffer(String title, String description, String company, String location, Double salary, UserApp createdBy) {
        this.title = title;
        this.description = description;
        this.company = company;
        this.location = location;
        this.salary = salary;
        this.createdBy = createdBy;
    }
}