package com.example.demo.config;

import com.example.demo.models.JobOffer;
import com.example.demo.models.UserApp;
import com.example.demo.repositories.JobOfferRepository;
import com.example.demo.repositories.UserAppRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserAppRepository userAppRepository;

    @Autowired
    private JobOfferRepository jobOfferRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userAppRepository.count() == 0) {
            initializeUsers();
            initializeJobOffers();
        }
    }

    private void initializeUsers() {
        UserApp admin = new UserApp("admin", passwordEncoder.encode("admin123"), "ADMIN");
        UserApp user1 = new UserApp("john", passwordEncoder.encode("password123"), "USER");
        UserApp user2 = new UserApp("marie", passwordEncoder.encode("password123"), "USER");
        UserApp user3 = new UserApp("paul", passwordEncoder.encode("password123"), "USER");

        userAppRepository.save(admin);
        userAppRepository.save(user1);
        userAppRepository.save(user2);
        userAppRepository.save(user3);

        System.out.println("Utilisateurs créés:");
        System.out.println("- admin / admin123 (ADMIN)");
        System.out.println("- john / password123 (USER)");
        System.out.println("- marie / password123 (USER)");
        System.out.println("- paul / password123 (USER)");
    }

    private void initializeJobOffers() {
        UserApp admin = userAppRepository.findByUsername("admin").orElse(null);
        UserApp john = userAppRepository.findByUsername("john").orElse(null);
        UserApp marie = userAppRepository.findByUsername("marie").orElse(null);

        if (admin != null && john != null && marie != null) {
            JobOffer job1 = new JobOffer(
                "Développeur Java Senior",
                "Nous recherchons un développeur Java avec 5+ ans d'expérience pour rejoindre notre équipe. Maîtrise de Spring Boot requise.",
                "TechCorp",
                "Paris",
                65000.0,
                john
            );

            JobOffer job2 = new JobOffer(
                "Data Scientist",
                "Poste de data scientist pour analyser les données clients et développer des modèles prédictifs. Python et ML requis.",
                "DataSoft",
                "Lyon",
                55000.0,
                marie
            );

            JobOffer job3 = new JobOffer(
                "Chef de Projet IT",
                "Management d'équipes de développement et coordination de projets techniques. Expérience en méthodologie Agile.",
                "InnovaTech",
                "Marseille",
                70000.0,
                admin
            );

            JobOffer job4 = new JobOffer(
                "Développeur Frontend React",
                "Création d'interfaces utilisateur modernes avec React.js. Connaissance de TypeScript et des outils de build modernes.",
                "WebAgency",
                "Toulouse",
                45000.0,
                john
            );

            JobOffer job5 = new JobOffer(
                "DevOps Engineer",
                "Automatisation des déploiements et gestion de l'infrastructure cloud. Docker, Kubernetes, AWS.",
                "CloudFirst",
                "Nantes",
                60000.0,
                marie
            );

            JobOffer job6 = new JobOffer(
                "Analyste Cybersécurité",
                "Protection des systèmes informatiques et analyse des menaces. Certifications CISSP ou CEH appréciées.",
                "SecureIT",
                "Bordeaux",
                58000.0,
                admin
            );

            jobOfferRepository.save(job1);
            jobOfferRepository.save(job2);
            jobOfferRepository.save(job3);
            jobOfferRepository.save(job4);
            jobOfferRepository.save(job5);
            jobOfferRepository.save(job6);

            System.out.println("6 offres d'emploi créées par différents utilisateurs");
        }
    }
}