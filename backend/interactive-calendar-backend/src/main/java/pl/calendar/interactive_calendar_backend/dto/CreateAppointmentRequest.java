package pl.calendar.interactive_calendar_backend.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record CreateAppointmentRequest(@NotNull(message = "Czas rozpoczęcia wizyty jest wymagany")
                                        @Future(message = "Data wizyty musi być w przyszłości")
                                        LocalDateTime appointmentStart,

                                       @NotNull(message = "Czas zakończenia wizyty jest wymagany")
                                        @Future(message = "Data wizyty musi być w przyszłości")
                                        LocalDateTime appointmentEnd,

                                       @NotNull(message = "ID lekarza jest wymagane")
                                        Long doctorId,

                                       String notes) {
}
