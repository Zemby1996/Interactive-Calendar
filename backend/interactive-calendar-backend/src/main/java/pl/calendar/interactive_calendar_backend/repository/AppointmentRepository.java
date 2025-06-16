package pl.calendar.interactive_calendar_backend.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.calendar.interactive_calendar_backend.model.Appointment;
import pl.calendar.interactive_calendar_backend.model.Doctor;
import pl.calendar.interactive_calendar_backend.model.User;
import java.time.LocalDateTime;
import java.util.List;

@Repository

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    /**
     * Znajduje wszystkie wizyty dla danego lekarza w określonym przedziale czasowym.
     * @param doctor Obiekt lekarza, dla którego szukamy wizyt.
     * @param start Data i godzina rozpoczęcia przedziału.
     * @param end Data i godzina zakończenia przedziału.
     * @return Lista pasujących wizyt.
     */
    List<Appointment> findByDoctorAndAppointmentStartBetween(Doctor doctor, LocalDateTime start, LocalDateTime end);

    /**
     * Sprawdza, czy istnieje jakakolwiek wizyta dla danego lekarza,
     * która nakłada się na podany przedział czasowy.
     * Jest to kluczowe do walidacji podczas tworzenia nowej wizyty.
     * @param doctor Obiekt lekarza.
     * @param start Czas rozpoczęcia potencjalnej nowej wizyty.
     * @param end Czas zakończenia potencjalnej nowej wizyty.
     * @return true, jeśli termin jest zajęty; false w przeciwnym razie.
     */
    @Query("SELECT COUNT(a) > 0 FROM Appointment a WHERE a.doctor = :doctor AND a.appointmentStart < :end AND a.appointmentEnd > :start")
    boolean existsOverlappingAppointment(@Param("doctor") Doctor doctor, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    List<Appointment> findAllByAppointmentStartBetween(LocalDateTime start, LocalDateTime end);

    /**
     * Znajduje wszystkie wizyty dla danego pacjenta (użytkownika),
     * posortowane od najnowszej do najstarszej.
     * @param patient Obiekt użytkownika, dla którego szukamy wizyt.
     * @return Lista wizyt pacjenta.
     */
    List<Appointment> findByPatientOrderByAppointmentStartDesc(User patient);
}


