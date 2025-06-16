package pl.calendar.interactive_calendar_backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.calendar.interactive_calendar_backend.dto.ApiResponse;
import pl.calendar.interactive_calendar_backend.dto.DoctorDto;
import pl.calendar.interactive_calendar_backend.service.AppointmentService;
import pl.calendar.interactive_calendar_backend.service.DoctorService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor

public class DoctorController {

    private final DoctorService doctorService;
    private final AppointmentService appointmentService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<DoctorDto>>> getAllDoctors() {
        List<DoctorDto> doctors = doctorService.getAllDoctors();
        ApiResponse<List<DoctorDto>> response = new ApiResponse<>(true, "Got a list of doctors", doctors);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{doctorId}/available-slots")
    public ResponseEntity<ApiResponse<List<LocalDateTime>>> getAvailableSlots(
            @PathVariable Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        List<LocalDateTime> slots = appointmentService.findAvailableSlots(doctorId, date);
        ApiResponse<List<LocalDateTime>> response = new ApiResponse<>(true, "Got the available dates ", slots);
        return ResponseEntity.ok(response);
    }
}