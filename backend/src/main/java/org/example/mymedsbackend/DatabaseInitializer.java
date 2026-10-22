package org.example.mymedsbackend;

import org.example.mymedsbackend.user.User;
import org.example.mymedsbackend.user.UserRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    private final UserRepo userRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseInitializer(UserRepo userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            User device = new User();
            device.setUsername("device");
            device.setPassword(passwordEncoder.encode(System.getenv("DEVICE_PASSWORD")));
            device.setRole("ROLE_DEVICE");
            System.out.println("Created device user");

            userRepository.save(device);


            User admin = new User();
            admin.setUsername("admin");
            String password =  new java.security.SecureRandom().ints(12, 33, 122).mapToObj(i -> String.valueOf((char) i)).collect(java.util.stream.Collectors.joining());
            admin.setPassword(passwordEncoder.encode(password));
            admin.setRole("ROLE_ADMIN");
            System.out.println("Created admin with password: \n" + password);

            userRepository.save(admin);
        }
    }
}
