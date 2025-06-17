package pl.calendar.interactive_calendar_backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.AccessDeniedException;
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

    /**
     * Wyszukuje wolne terminy dla danego lekarza w określonym dniu.
     */
    public List<LocalDateTime> findAvailableSlots(Long doctorId, LocalDate date) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono lekarza o ID: " + doctorId));

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

    /**
     * Pobiera wizyty dla danego lekarza w podanym zakresie dat - WERSJA PUBLICZNA.
     * Nie ujawnia danych pacjentów.
     */
    @Transactional(readOnly = true)
    public List<AppointmentDto> getPublicAppointments(Long doctorId, LocalDateTime start, LocalDateTime end) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono lekarza o ID: " + doctorId));

        return appointmentRepository.findByDoctorAndAppointmentStartBetween(doctor, start, end)
                .stream()
                .map(this::mapToPublicDto) // Używamy nowej, bezpiecznej metody mapowania
                .collect(Collectors.toList());
    }

    /**
     * Tworzy nową wizytę dla zalogowanego użytkownika.
     */
    @Transactional
    public AppointmentDto createAppointment(CreateAppointmentRequest request, String userEmail) {
        User patient = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono użytkownika o emailu: " + userEmail));
        Doctor doctor = doctorRepository.findById(request.doctorId())
                .orElseThrow(() -> new RuntimeException("Nie znaleziono lekarza o ID: " + request.doctorId()));

        if (appointmentRepository.existsOverlappingAppointment(doctor, request.appointmentStart(), request.appointmentEnd())) {
            throw new IllegalStateException("Wybrany termin jest już zajęty.");
        }

        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setAppointmentStart(request.appointmentStart());
        appointment.setAppointmentEnd(request.appointmentEnd());
        appointment.setNotes(request.notes());

        Appointment savedAppointment = appointmentRepository.save(appointment);

        String subject = "Potwierdzenie rezerwacji wizyty";
        String text = String.format("Witaj %s,\n\nTwoja wizyta u %s została pomyślnie zarezerwowana na %s o godzinie %s.",
                patient.getFirstName(), doctor.getName(), savedAppointment.getAppointmentStart().toLocalDate(), savedAppointment.getAppointmentStart().toLocalTime());
        emailService.sendEmail(patient.getEmail(), subject, text);

        return mapToFullDto(savedAppointment);
    }

    /**
     * Aktualizuje istniejącą wizytę, sprawdzając uprawnienia.
     */
    @Transactional
    public AppointmentDto updateAppointment(Long appointmentId, CreateAppointmentRequest request, Principal principal) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono wizyty o ID: " + appointmentId));
        User currentUser = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));

        if (!appointment.getPatient().getId().equals(currentUser.getId()) && !currentUser.getRole().equals("ROLE_ADMIN")) {
            throw new AccessDeniedException("Brak uprawnień do edycji tej wizyty.");
        }

        Doctor doctor = doctorRepository.findById(request.doctorId())
                .orElseThrow(() -> new RuntimeException("Nie znaleziono lekarza o ID: " + request.doctorId()));

        appointment.setDoctor(doctor);
        appointment.setAppointmentStart(request.appointmentStart());
        appointment.setAppointmentEnd(request.appointmentEnd());
        appointment.setNotes(request.notes());

        Appointment updatedAppointment = appointmentRepository.save(appointment);
        emailService.sendUpdateNotification(updatedAppointment);
        return mapToFullDto(updatedAppointment);
    }

    /**
     * Anuluje (usuwa) istniejącą wizytę, sprawdzając uprawnienia.
     */
    @Transactional
    public void deleteAppointment(Long appointmentId, Principal principal) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono wizyty o ID: " + appointmentId));
        User currentUser = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));

        if (!appointment.getPatient().getId().equals(currentUser.getId()) && !currentUser.getRole().equals("ROLE_ADMIN")) {
            throw new AccessDeniedException("Brak uprawnień do anulowania tej wizyty.");
        }

        appointmentRepository.deleteById(appointmentId);
        emailService.sendCancellationNotification(appointment);
    }

    /**
     * Pobiera wszystkie wizyty dla konkretnego, zalogowanego użytkownika.
     */
    @Transactional(readOnly = true)
    public List<AppointmentDto> getAppointmentsForUser(String userEmail) {
        User patient = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono użytkownika o emailu: " + userEmail));

        return appointmentRepository.findByPatientOrderByAppointmentStartDesc(patient)
                .stream()
                .map(this::mapToFullDto) // Używamy pełnego mapowania, bo to wizyty właściciela
                .collect(Collectors.toList());
    }

    /**
     * Pobiera wszystkie wizyty w całym systemie (dla admina).
     */
    @Transactional(readOnly = true)
    public List<AppointmentDto> getAllAppointments() {
        return appointmentRepository.findAll(Sort.by(Sort.Direction.DESC, "appointmentStart"))
                .stream()
                .map(this::mapToFullDto) // Admin widzi wszystkie szczegóły
                .collect(Collectors.toList());
    }

    /**
     * Zaplanowane zadanie do wysyłania przypomnień.
     */
    @Scheduled(cron = "0 0 8 * * ?") // Codziennie o 8:00 rano
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

    // --- METODY MAPUJĄCE ---

    /**
     * Mapuje encję Appointment na DTO z pełnymi danymi (dla właściciela lub admina).
     */
    private AppointmentDto mapToFullDto(Appointment appointment) {
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
     * Mapuje encję Appointment na DTO z danymi publicznymi (ukrywa dane pacjenta).
     */
    private AppointmentDto mapToPublicDto(Appointment appointment) {
        return new AppointmentDto(
                appointment.getId(),
                appointment.getAppointmentStart(),
                appointment.getAppointmentEnd(),
                null, // Nie pokazujemy notatek publicznie
                appointment.getDoctor().getId(),
                appointment.getDoctor().getName(),
                null, // NIE POKAZUJEMY ID PACJENTA
                "Zajęty", // Zamiast imienia i nazwiska, ogólny tekst
                "Termin"
        );
    }
}