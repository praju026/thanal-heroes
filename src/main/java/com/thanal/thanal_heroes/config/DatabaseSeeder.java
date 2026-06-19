package com.thanal.thanal_heroes.config;

import com.thanal.thanal_heroes.model.User;
import com.thanal.thanal_heroes.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (!userRepository.existsByUsername("prajuAdmin")) {
            User admin = User.builder()
                    .username("prajuAdmin")
                    .password(passwordEncoder.encode("m1887m1887"))
                    .role("ADMIN")
                    .build();
            userRepository.save(admin);
            System.out.println("Admin user initialized: prajuAdmin / m1887m1887");
        }
    }
}
