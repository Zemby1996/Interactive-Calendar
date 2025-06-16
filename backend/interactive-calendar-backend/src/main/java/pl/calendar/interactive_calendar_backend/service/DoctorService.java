package pl.calendar.interactive_calendar_backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.calendar.interactive_calendar_backend.dto.DoctorDto;
import pl.calendar.interactive_calendar_backend.repository.DoctorRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class DoctorService {

    private final DoctorRepository doctorRepository;

    public List<DoctorDto> getAllDoctors() {
        return doctorRepository.findAll().stream()
                .map(doctor -> new DoctorDto(doctor.getId(),
                        doctor.getName(),
                        doctor.getSpecialty()))
                .collect(Collectors.toList());
    }
}
