package pl.calendar.interactive_calendar_backend.dto;

import java.time.LocalDateTime;

public record AppointmentDto(Long id,
                             LocalDateTime appointmentStart,
                             LocalDateTime appointmentEnd,
                             String notes,
                             Long doctorId,
                             String doctorName,
                             Long patientId,
                             String patientFirstName,
                             String patientLastName) {
}
