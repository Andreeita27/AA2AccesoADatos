package com.svalero.RosasTattoo.controller;

import com.svalero.RosasTattoo.domain.enums.AppointmentState;
import com.svalero.RosasTattoo.dto.AppointmentInDto;
import com.svalero.RosasTattoo.dto.AppointmentDto;
import com.svalero.RosasTattoo.exception.AppointmentNotFoundException;
import com.svalero.RosasTattoo.exception.ClientNotFoundException;
import com.svalero.RosasTattoo.exception.ErrorResponse;
import com.svalero.RosasTattoo.exception.ProfessionalNotFoundException;
import com.svalero.RosasTattoo.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @GetMapping("/appointments")
    public ResponseEntity<List<AppointmentDto>> getAll(
            @RequestParam(value = "state", required = false) AppointmentState state,
            @RequestParam(value = "clientId", required = false) Long clientId,
            @RequestParam(value = "professionalId", required = false) Long professionalId
    ) {

        List<AppointmentDto> appointments = appointmentService.findAll(state, clientId, professionalId);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/appointments/{id}")
    public ResponseEntity<AppointmentDto> getAppointment(@PathVariable long id) throws AppointmentNotFoundException {
        AppointmentDto appointment = appointmentService.findById(id);
        return ResponseEntity.ok(appointment);
    }

    @PostMapping("/appointments")
    public ResponseEntity<AppointmentDto> addAppointment(@Valid @RequestBody AppointmentInDto appointmentInDto) throws ClientNotFoundException, ProfessionalNotFoundException {
        return new ResponseEntity<>(appointmentService.add(appointmentInDto), HttpStatus.CREATED);
    }

    @PutMapping("/appointments/{id}")
    public ResponseEntity<AppointmentDto> modifyAppointment(@PathVariable long id, @Valid @RequestBody AppointmentInDto appointmentInDto)
            throws AppointmentNotFoundException, ClientNotFoundException, ProfessionalNotFoundException {
        AppointmentDto modifiedAppointment = appointmentService.modify(id, appointmentInDto);
        return ResponseEntity.ok(modifiedAppointment);
    }

    @DeleteMapping("/appointments/{id}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable long id) throws AppointmentNotFoundException {
        appointmentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(AppointmentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(AppointmentNotFoundException anfe) {
        ErrorResponse errorResponse = ErrorResponse.notFound(anfe.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ClientNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleClientException(ClientNotFoundException cnfe) {
        ErrorResponse errorResponse = ErrorResponse.notFound("Cliente no encontrado");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ProfessionalNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProfessionalException(ProfessionalNotFoundException pnfe) {
        ErrorResponse errorResponse = ErrorResponse.notFound("Profesional no encontrado");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException manve) {
        Map<String, String> errors = new HashMap<>();
        manve.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName, message);
        });
        return new ResponseEntity<>(ErrorResponse.validationError(errors), HttpStatus.BAD_REQUEST);
    }
}