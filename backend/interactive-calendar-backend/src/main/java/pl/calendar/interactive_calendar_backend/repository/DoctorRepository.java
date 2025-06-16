package pl.calendar.interactive_calendar_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.calendar.interactive_calendar_backend.model.Doctor;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
}
