package com.svalero.RosasTattoo.service;

import com.svalero.RosasTattoo.domain.Appointment;
import com.svalero.RosasTattoo.domain.Client;
import com.svalero.RosasTattoo.domain.Professional;
import com.svalero.RosasTattoo.domain.enums.AppointmentState;
import com.svalero.RosasTattoo.dto.AppointmentDto;
import com.svalero.RosasTattoo.dto.AppointmentInDto;
import com.svalero.RosasTattoo.exception.AppointmentNotFoundException;
import com.svalero.RosasTattoo.exception.ClientNotFoundException;
import com.svalero.RosasTattoo.exception.ProfessionalNotFoundException;
import com.svalero.RosasTattoo.repository.AppointmentRepository;
import com.svalero.RosasTattoo.repository.ClientRepository;
import com.svalero.RosasTattoo.repository.ProfessionalRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private ProfessionalRepository professionalRepository;
    @Autowired
    private ModelMapper modelMapper;

    public List<AppointmentDto> findAll(AppointmentState state, Long clientId, Long professionalId) {
        List<Appointment> appointments = appointmentRepository.findByFilters(state, clientId, professionalId);
        return modelMapper.map(appointments, new TypeToken<List<AppointmentDto>>() {}.getType());
    }

    public AppointmentDto findById(long id) throws AppointmentNotFoundException {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(AppointmentNotFoundException::new);

        return modelMapper.map(appointment, AppointmentDto.class);
    }

    public AppointmentDto add(AppointmentInDto appointmentInDto) throws ClientNotFoundException, ProfessionalNotFoundException {
        Client client = clientRepository.findById(appointmentInDto.getClientId())
                .orElseThrow(ClientNotFoundException::new);

        Professional professional = professionalRepository.findById(appointmentInDto.getProfessionalId())
                .orElseThrow(ProfessionalNotFoundException::new);


        Appointment appointment = new Appointment();
        modelMapper.map(appointmentInDto, appointment);

        appointment.setClient(client);
        appointment.setProfessional(professional);

        Appointment saved = appointmentRepository.save(appointment);
        return modelMapper.map(saved, AppointmentDto.class);
    }

    public AppointmentDto modify(long id, AppointmentInDto appointmentInDto)
            throws AppointmentNotFoundException, ClientNotFoundException, ProfessionalNotFoundException {

        Appointment existing = appointmentRepository.findById(id)
                .orElseThrow(AppointmentNotFoundException::new);

        Client client = clientRepository.findById(appointmentInDto.getClientId())
                .orElseThrow(ClientNotFoundException::new);

        Professional professional = professionalRepository.findById(appointmentInDto.getProfessionalId())
                .orElseThrow(ProfessionalNotFoundException::new);

        modelMapper.map(appointmentInDto, existing);
        existing.setId(id);
        existing.setClient(client);
        existing.setProfessional(professional);

        Appointment saved = appointmentRepository.save(existing);
        return modelMapper.map(saved, AppointmentDto.class);
    }

    public void delete(long id) throws AppointmentNotFoundException {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(AppointmentNotFoundException::new);

        appointmentRepository.delete(appointment);
    }
}