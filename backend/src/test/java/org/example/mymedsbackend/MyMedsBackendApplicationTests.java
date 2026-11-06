package org.example.mymedsbackend;

import org.example.mymedsbackend.medication.Medication;
import org.example.mymedsbackend.medication.MedicationController;
import org.example.mymedsbackend.medication.MedicationLogRepo;
import org.example.mymedsbackend.medication.MedicationRepo;
import org.example.mymedsbackend.security.SecurityLogService;
import org.example.mymedsbackend.user.DoctorPatientAssignmentRepo;
import org.example.mymedsbackend.user.MyUserDetailsService;
import org.example.mymedsbackend.user.User;
import org.example.mymedsbackend.user.UserController;
import org.example.mymedsbackend.user.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class MyMedsBackendApplicationTests {

    private final List<User> users = new ArrayList<>();
    private final List<Medication> medications = new ArrayList<>();
    private final PasswordEncoder encoder = Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();

    private UserRepo userRepo;
    private MedicationRepo medicationRepo;
    private DoctorPatientAssignmentRepo assignmentRepo;

    @BeforeEach
    void setUp() {
        users.clear();
        medications.clear();
        userRepo = mock(UserRepo.class);
        medicationRepo = mock(MedicationRepo.class);
        assignmentRepo = mock(DoctorPatientAssignmentRepo.class);

        when(userRepo.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            users.add(u);
            return u;
        });

        when(userRepo.findByUsername(anyString())).thenAnswer(inv ->
                users.stream().filter(u -> u.getUsername().equals(inv.getArgument(0))).findFirst().orElse(null)
        );

        when(userRepo.findById(anyInt())).thenAnswer(inv ->
                users.stream().filter(u -> u.getId() == (int) inv.getArgument(0)).findFirst()
        );

        when(medicationRepo.save(any(Medication.class))).thenAnswer(inv -> {
            Medication m = inv.getArgument(0);
            medications.add(m);
            return m;
        });
    }

    private User addUser(int id, String name, String password, String role) {
        User u = new User();
        u.setId(id);
        u.setUsername(name);
        u.setEmail(name + "@example.com");
        u.setPassword(encoder.encode(password));
        u.setRole(role);
        u.setActive(true);
        users.add(u);
        return u;
    }

    @Test
    void loginWithCorrectCredentials() {
        addUser(1, "Sparsh", "correct-password", "PATIENT");

        MyUserDetailsService service = new MyUserDetailsService();
        ReflectionTestUtils.setField(service, "repo", userRepo);

        UserDetails found = service.loadUserByUsername("Sparsh");
        assertTrue(encoder.matches("correct-password", found.getPassword()));
    }

    @Test
    void loginWithWrongCredentials() {
        addUser(1, "Sparsh", "correct-password", "PATIENT");

        MyUserDetailsService service = new MyUserDetailsService();
        ReflectionTestUtils.setField(service, "repo", userRepo);

        UserDetails found = service.loadUserByUsername("Sparsh");
        assertFalse(encoder.matches("wrong-password", found.getPassword()));
    }

    @Test
    void registerNewUser() {
        UserController controller = new UserController(userRepo, encoder, medicationRepo,
                mock(MedicationLogRepo.class), assignmentRepo, mock(SecurityLogService.class));

        User newUser = new User();
        newUser.setUsername("Henri");
        newUser.setEmail("Henri@example.com");
        newUser.setPassword("supersecret1");
        newUser.setRole("PATIENT");
        newUser.setActive(true);

        var response = controller.registerUser(newUser);

        assertEquals(201, response.getStatusCode().value());
        assertTrue(encoder.matches("supersecret1", users.get(0).getPassword()));
    }

    @Test
    void addMedication() {
        User doctor = addUser(1, "Sil", "doctor-password", "DOCTOR");
        User patient = addUser(2, "Thomas", "patient-password", "PATIENT");

        when(assignmentRepo.existsByDoctor_IdAndPatient_Id(1, 2)).thenReturn(true);

        MedicationController controller = new MedicationController(medicationRepo, userRepo, assignmentRepo);

        Medication med = new Medication();
        med.setName("Aspirin");
        med.setDosage("100mg");
        med.setPatient(patient);
        UserDetails principal = org.springframework.security.core.userdetails.User.withUsername("Sil")
                .password("doctor-password")
                .roles("DOCTOR")
                .build();

        var response = controller.addMedication(med, principal);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, medications.size());
        assertEquals("Aspirin", medications.get(0).getName());
    }
}