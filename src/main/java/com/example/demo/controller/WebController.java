package com.example.demo.controller;

import com.example.demo.models.JobOffer;
import com.example.demo.models.UserApp;
import com.example.demo.repositories.JobOfferRepository;
import com.example.demo.repositories.UserAppRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
public class WebController {

    @Autowired
    private JobOfferRepository jobOfferRepository;

    @Autowired
    private UserAppRepository userAppRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${jwt.cookie_name}")
    private String cookieName;

    @GetMapping("/")
    public String home(Model model, Authentication authentication) {
        List<JobOffer> jobs = jobOfferRepository.findAll();
        model.addAttribute("jobs", jobs);
        model.addAttribute("isAuthenticated", authentication != null);
        
        if (authentication != null && authentication.getPrincipal() instanceof UserApp) {
            model.addAttribute("currentUser", authentication.getPrincipal());
            model.addAttribute("isAdmin", authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
        } else {
            model.addAttribute("currentUser", null);
            model.addAttribute("isAdmin", false);
        }
        return "home";
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("userApp", new UserApp());
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("userApp", new UserApp());
        return "register";
    }

    @PostMapping("/web-register")
    public String webRegister(@ModelAttribute UserApp userApp, RedirectAttributes redirectAttributes) {
        try {
            Optional<UserApp> existingUser = userAppRepository.findByUsername(userApp.getUsername());
            if (existingUser.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Nom d'utilisateur déjà pris");
                return "redirect:/register";
            }

            userApp.setPassword(passwordEncoder.encode(userApp.getPassword()));
            if (userApp.getRole() == null || userApp.getRole().isEmpty()) {
                userApp.setRole("USER");
            }
            userAppRepository.save(userApp);
            redirectAttributes.addFlashAttribute("success", "Compte créé avec succès");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la création du compte");
            return "redirect:/register";
        }
    }

    @GetMapping("/create-job")
    public String createJobPage(Model model, Authentication authentication) {
        if (authentication == null) {
            return "redirect:/login";
        }
        model.addAttribute("jobOffer", new JobOffer());
        return "create-job";
    }

    @PostMapping("/web-create-job")
    public String webCreateJob(@ModelAttribute JobOffer jobOffer, Authentication authentication, 
                              RedirectAttributes redirectAttributes) {
        if (authentication == null) {
            return "redirect:/login";
        }

        try {
            UserApp currentUser = (UserApp) authentication.getPrincipal();
            jobOffer.setCreatedBy(currentUser);
            jobOfferRepository.save(jobOffer);
            redirectAttributes.addFlashAttribute("success", "Offre créée avec succès");
            return "redirect:/";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la création de l'offre");
            return "redirect:/create-job";
        }
    }

    @GetMapping("/job/{id}")
    public String jobDetail(@PathVariable Integer id, Model model, Authentication authentication) {
        Optional<JobOffer> jobOffer = jobOfferRepository.findById(id);
        if (!jobOffer.isPresent()) {
            return "redirect:/";
        }

        model.addAttribute("job", jobOffer.get());
        model.addAttribute("isAuthenticated", authentication != null);
        
        if (authentication != null && authentication.getPrincipal() instanceof UserApp) {
            UserApp currentUser = (UserApp) authentication.getPrincipal();
            model.addAttribute("currentUser", currentUser);
            
            boolean isOwner = jobOffer.get().getCreatedBy().getId().equals(currentUser.getId());
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
            
            model.addAttribute("canDelete", isOwner || isAdmin);
            model.addAttribute("isAdmin", isAdmin);
        } else {
            model.addAttribute("canDelete", false);
            model.addAttribute("isAdmin", false);
            model.addAttribute("currentUser", null);
        }
        
        return "job-detail";
    }

    @PostMapping("/web-delete-job/{id}")
    public String webDeleteJob(@PathVariable Integer id, Authentication authentication, 
                              RedirectAttributes redirectAttributes) {
        if (authentication == null) {
            return "redirect:/login";
        }

        try {
            Optional<JobOffer> jobOffer = jobOfferRepository.findById(id);
            if (!jobOffer.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Offre non trouvée");
                return "redirect:/";
            }

            UserApp currentUser = (UserApp) authentication.getPrincipal();
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
            boolean isOwner = jobOffer.get().getCreatedBy().getId().equals(currentUser.getId());

            if (isAdmin || isOwner) {
                jobOfferRepository.deleteById(id);
                redirectAttributes.addFlashAttribute("success", "Offre supprimée avec succès");
            } else {
                redirectAttributes.addFlashAttribute("error", "Vous n'êtes pas autorisé à supprimer cette offre");
            }
            
            return "redirect:/";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la suppression");
            return "redirect:/";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpServletResponse response, RedirectAttributes redirectAttributes) {
        // Supprimer le contexte de sécurité
        SecurityContextHolder.clearContext();
        
        // Créer un cookie expiré avec ResponseCookie pour être cohérent avec la connexion
        ResponseCookie expiredCookie = ResponseCookie.from(cookieName, "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .build();
        
        response.addHeader(HttpHeaders.SET_COOKIE, expiredCookie.toString());
        
        redirectAttributes.addFlashAttribute("success", "Déconnexion réussie");
        return "redirect:/";
    }
}