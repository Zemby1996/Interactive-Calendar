package pl.calendar.interactive_calendar_backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pl.calendar.interactive_calendar_backend.model.Doctor;
import pl.calendar.interactive_calendar_backend.model.User;
import pl.calendar.interactive_calendar_backend.repository.DoctorRepository;
import pl.calendar.interactive_calendar_backend.repository.UserRepository;

import java.util.List;

@Component
@RequiredArgsConstructor

public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (doctorRepository.count() == 0) {
            System.out.println("List of doctors is empty. Creating doctors!");

            Doctor laryngolog = new Doctor();
            laryngolog.setName("Dr. Anna Nowak");
            laryngolog.setSpecialty("Laryngolog");

            Doctor psycholog = new Doctor();
            psycholog.setName("Dr. Jan Kowalski");
            psycholog.setSpecialty("Psycholog");

            Doctor kardiolog = new Doctor();
            kardiolog.setName("Dr. Ewa Wiśniewska");
            kardiolog.setSpecialty("Kardiolog");

            Doctor onkolog = new Doctor();
            onkolog.setName("Dr. Karol Bezbek");
            onkolog.setSpecialty("Onkolog");

            Doctor okulista = new Doctor();
            okulista.setName("Dr. Maciej Niewidz");
            okulista.setSpecialty("Okulista");

            doctorRepository.saveAll(List.of(laryngolog, psycholog, kardiolog, onkolog, okulista));
            System.out.println("Added " + doctorRepository.count() + " doctors to database.");
        }

        if (userRepository.findByEmail("admin@app.com").isEmpty()) {
            System.out.println("Admin user not found. Creating admin user!");

            User admin = new User();
            admin.setEmail("admin@app.com");
            admin.setPassword(passwordEncoder.encode("admin123")); // Zawsze szyfruj hasła!
            admin.setFirstName("Admin");
            admin.setLastName("Aplikacji");
            admin.setRole("ROLE_ADMIN");

            userRepository.save(admin);
            System.out.println("Admin account has been created.");
        }
    }

}
