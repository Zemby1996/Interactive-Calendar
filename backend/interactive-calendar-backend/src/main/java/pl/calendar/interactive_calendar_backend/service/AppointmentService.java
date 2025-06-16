package pl.calendar.interactive_calendar_backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.calendar.interactive_calendar_backend.dto.AppointmentDto;
import pl.calendar.interactive_calendar_backend.dto.CreateAppointmentRequest;
import pl.calendar.interactive_calendar_backend.model.Appointment;
import pl.calendar.interactive_calendar_backend.model.Doctor;
import pl.calendar.interactive_calendar_backend.model.User;
import pl.calendar.interactive_calendar_backend.repository.AppointmentRepository;
import pl.calendar.interactive_calendar_backend.repository.DoctorRepository;
import pl.calendar.interactive_calendar_backend.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import java.security.Principal;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public List<LocalDateTime> findAvailableSlots(Long doctorId, LocalDate date) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Cannot find the doctor with ID: " + doctorId));

        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(17, 0);
        Duration slotDuration = Duration.ofMinutes(30);

        List<LocalDateTime> allPossibleSlots = new ArrayList<>();
        LocalDateTime currentSlot = LocalDateTime.of(date, startTime);
        while (currentSlot.toLocalTime().isBefore(endTime)) {
            allPossibleSlots.add(currentSlot);
            currentSlot = currentSlot.plus(slotDuration);
        }

        List<Appointment> existingAppointments = appointmentRepository
                .findByDoctorAndAppointmentStartBetween(doctor, date.atStartOfDay(), date.atTime(LocalTime.MAX));

        List<LocalDateTime> busySlots = existingAppointments.stream()
                .map(Appointment::getAppointmentStart)
                .toList();

        allPossibleSlots.removeAll(busySlots);
        return allPossibleSlots;
    }

    @Transactional
    public AppointmentDto createAppointment(CreateAppointmentRequest request, String userEmail) {
        User patient = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Cannot find the user with email: " + userEmail));
        Doctor doctor = doctorRepository.findById(request.doctorId())
                .orElseThrow(() -> new RuntimeException("Cannot find the doctor with ID: " + request.doctorId()));

        if (appointmentRepository.existsOverlappingAppointment(doctor, request.appointmentStart(), request.appointmentEnd())) {
            throw new IllegalStateException("Selected time is already occupied by another appointment. Please select another time for your appointment. .");
        }

        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setAppointmentStart(request.appointmentStart());
        appointment.setAppointmentEnd(request.appointmentEnd());
        appointment.setNotes(request.notes());

        Appointment savedAppointment = appointmentRepository.save(appointment);

        String subject = "Confirmation of your appointment";
        String text = String.format("Welcome %s,\n\nYour appointment with %s has been confirmed %s at %s.",
                patient.getFirstName(), doctor.getName(), savedAppointment.getAppointmentStart().toLocalDate(), savedAppointment.getAppointmentStart().toLocalTime());
        emailService.sendEmail(patient.getEmail(), subject, text);

        return mapToDto(savedAppointment);
    }

    @Transactional
    public AppointmentDto updateAppointment(Long appointmentId, CreateAppointmentRequest request, Principal principal) {
        // 1. Znajdź wizytę w bazie danych
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono wizyty o ID: " + appointmentId));

        // 2. Znajdź zalogowanego użytkownika
        User currentUser = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));

        // 3. Sprawdź uprawnienia: czy użytkownik jest właścicielem wizyty LUB czy jest adminem?
        if (!appointment.getPatient().getId().equals(currentUser.getId()) && !currentUser.getRole().equals("ROLE_ADMIN")) {
            throw new AccessDeniedException("Brak uprawnień do edycji tej wizyty.");
        }

        Doctor doctor = doctorRepository.findById(request.doctorId())
                .orElseThrow(() -> new RuntimeException("Nie znaleziono lekarza o ID: " + request.doctorId()));

        // 4. Zaktualizuj dane wizyty
        appointment.setDoctor(doctor);
        appointment.setAppointmentStart(request.appointmentStart());
        appointment.setAppointmentEnd(request.appointmentEnd());
        appointment.setNotes(request.notes());

        Appointment updatedAppointment = appointmentRepository.save(appointment);

        // 5. Wyślij powiadomienie o aktualizacji
        emailService.sendUpdateNotification(updatedAppointment);

        return mapToDto(updatedAppointment);
    }

    @Transactional
    public void deleteAppointment(Long appointmentId, Principal principal) {
        // 1. Znajdź wizytę w bazie danych
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono wizyty o ID: " + appointmentId));

        // 2. Znajdź zalogowanego użytkownika
        User currentUser = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));

        // 3. Sprawdź uprawnienia: czy użytkownik jest właścicielem wizyty LUB czy jest adminem?
        if (!appointment.getPatient().getId().equals(currentUser.getId()) && !currentUser.getRole().equals("ROLE_ADMIN")) {
            throw new AccessDeniedException("Brak uprawnień do anulowania tej wizyty.");
        }

        // 4. Usuń wizytę
        appointmentRepository.deleteById(appointmentId);

        // 5. Wyślij powiadomienie o anulowaniu
        emailService.sendCancellationNotification(appointment);
    }

    @Scheduled(cron = "0 0 8 * * ?") // Uruchamia się codziennie o 8:00 rano
    @Transactional(readOnly = true)
    public void sendAppointmentReminders() {
        LocalDateTime reminderWindowStart = LocalDateTime.now().plusHours(23);
        LocalDateTime reminderWindowEnd = LocalDateTime.now().plusHours(25);

        List<Appointment> upcomingAppointments = appointmentRepository.findAllByAppointmentStartBetween(reminderWindowStart, reminderWindowEnd);

        for (Appointment appointment : upcomingAppointments) {
            String subject = "Przypomnienie o wizycie lekarskiej";
            String text = String.format("Witaj %s,\n\nPrzypominamy o Twojej wizycie u %s jutro o godzinie %s.",
                    appointment.getPatient().getFirstName(), appointment.getDoctor().getName(), appointment.getAppointmentStart().toLocalTime());
            emailService.sendEmail(appointment.getPatient().getEmail(), subject, text);
        }
    }

    private AppointmentDto mapToDto(Appointment appointment) {
        User patient = appointment.getPatient();
        Doctor doctor = appointment.getDoctor();
        return new AppointmentDto(
                appointment.getId(),
                appointment.getAppointmentStart(),
                appointment.getAppointmentEnd(),
                appointment.getNotes(),
                doctor.getId(),
                doctor.getName(),
                patient.getId(),
                patient.getFirstName(),
                patient.getLastName()
        );
    }

    /**
     * Pobiera wszystkie wizyty dla konkretnego, zalogowanego użytkownika.
     * @param userEmail Email użytkownika (jego login).
     * @return Lista DTO wizyt.
     */
    @Transactional(readOnly = true)
    public List<AppointmentDto> getAppointmentsForUser(String userEmail) {
        User patient = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono użytkownika o emailu: " + userEmail));

        return appointmentRepository.findByPatientOrderByAppointmentStartDesc(patient)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // --- NOWA METODA ---
    /**
     * Pobiera wszystkie wizyty w całym systemie. Metoda przeznaczona dla administratora.
     * @return Lista DTO wszystkich wizyt.
     */
    @Transactional(readOnly = true)
    public List<AppointmentDto> getAllAppointments() {
        // Sortujemy wszystkie wizyty od najnowszej do najstarszej
        return appointmentRepository.findAll(Sort.by(Sort.Direction.DESC, "appointmentStart"))
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
}
