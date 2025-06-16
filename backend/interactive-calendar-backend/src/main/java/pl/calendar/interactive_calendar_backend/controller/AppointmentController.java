package pl.calendar.interactive_calendar_backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.calendar.interactive_calendar_backend.dto.ApiResponse;
import pl.calendar.interactive_calendar_backend.dto.AppointmentDto;
import pl.calendar.interactive_calendar_backend.dto.CreateAppointmentRequest;
import pl.calendar.interactive_calendar_backend.service.AppointmentService;
import java.util.List;

import java.security.Principal;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor

public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    public ResponseEntity<ApiResponse<AppointmentDto>> createAppointment(
            @Valid @RequestBody CreateAppointmentRequest request,
            Principal principal
    ) {
        AppointmentDto createdAppointment = appointmentService.createAppointment(request, principal.getName());
        ApiResponse<AppointmentDto> response = new ApiResponse<>(true, "Appointment got successfully created", createdAppointment);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{appointmentId}")
    public ResponseEntity<ApiResponse<AppointmentDto>> updateAppointment(
            @PathVariable Long appointmentId,
            @Valid @RequestBody CreateAppointmentRequest request,
            Principal principal // Dodaj ten parametr
    ) {
        AppointmentDto updatedAppointment = appointmentService.updateAppointment(appointmentId, request, principal);
        ApiResponse<AppointmentDto> response = new ApiResponse<>(true, "Appointment got updated", updatedAppointment);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{appointmentId}")
    public ResponseEntity<ApiResponse<Void>> deleteAppointment(
            @PathVariable Long appointmentId,
            Principal principal // Dodaj ten parametr
    ) {
        appointmentService.deleteAppointment(appointmentId, principal);
        ApiResponse<Void> response = new ApiResponse<>(true, "Appointment got deleted", null);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint dla zalogowanego użytkownika do pobrania listy swoich własnych wizyt.
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<AppointmentDto>>> getMyAppointment(Principal principal) {
        List<AppointmentDto> appointments = appointmentService.getAppointmentsForUser(principal.getName());
        ApiResponse<List<AppointmentDto>> response = new ApiResponse<>(true, "Pobrano wizyty użytkownika", appointments);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint tylko dla administratora do pobrania wszystkich wizyt w systemie.
     */
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<AppointmentDto>>> getAllAppointments() {
        List<AppointmentDto> appointments = appointmentService.getAllAppointments();
        ApiResponse<List<AppointmentDto>> response = new ApiResponse<>(true, "Pobrano wszystkie wizyty", appointments);
        return ResponseEntity.ok(response);
    }
}