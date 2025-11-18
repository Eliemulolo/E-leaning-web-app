package com.elie.quizapp.config;

import com.elie.quizapp.entity.User;
import com.elie.quizapp.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initializeData(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Créer un utilisateur admin par défaut s'il n'existe pas
            if (!userRepository.existsByUsername("admin")) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setAdmin(true);
                admin.setEnabled(true);
                userRepository.save(admin);
                System.out.println("Utilisateur admin créé: admin / admin123");
            }

            // Créer un utilisateur de test s'il n'existe pas
            if (!userRepository.existsByUsername("user")) {
                User user = new User();
                user.setUsername("user");
                user.setPassword(passwordEncoder.encode("user123"));
                user.setAdmin(false);
                user.setEnabled(true);
                userRepository.save(user);
                System.out.println("Utilisateur test créé: user / user123");
            }
        };
    }
}