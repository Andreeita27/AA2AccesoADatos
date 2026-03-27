package com.svalero.RosasTattoo.service;

import com.svalero.RosasTattoo.domain.Client;
import com.svalero.RosasTattoo.dto.ClientInDto;
import com.svalero.RosasTattoo.dto.ClientDto;
import com.svalero.RosasTattoo.dto.ClientV2Dto;
import com.svalero.RosasTattoo.dto.ClientV2InDto;
import com.svalero.RosasTattoo.exception.ClientNotFoundException;
import com.svalero.RosasTattoo.repository.ClientRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private ModelMapper modelMapper;

    public List<ClientDto> findAll(String name, String surname, Boolean showPhoto) {
        List<Client> clients = clientRepository.findByFilters(name, surname, showPhoto);
        return modelMapper.map(clients, new TypeToken<List<ClientDto>>() {}.getType());
    }

    public ClientDto findById(long id) throws ClientNotFoundException {
        Client client = clientRepository.findById(id)
                .orElseThrow(ClientNotFoundException::new);

        return modelMapper.map(client, ClientDto.class);
    }

    public ClientDto add(ClientInDto clientInDto) {
        Client client = modelMapper.map(clientInDto, Client.class);
        Client saved = clientRepository.save(client);
        return modelMapper.map(saved, ClientDto.class);
    }

    public ClientDto modify(long id, ClientInDto clientInDto) throws ClientNotFoundException {
        Client existing = clientRepository.findById(id)
                .orElseThrow(ClientNotFoundException::new);

        modelMapper.map(clientInDto, existing);
        existing.setId(id);

        Client saved = clientRepository.save(existing);
        return modelMapper.map(saved, ClientDto.class);
    }

    public void delete(long id) throws ClientNotFoundException {
        Client client = clientRepository.findById(id)
                .orElseThrow(ClientNotFoundException::new);

        clientRepository.delete(client);
    }


    // VERSION 2

    public ClientV2Dto findByIdV2(long id) throws ClientNotFoundException {
        Client client = clientRepository.findById(id)
                .orElseThrow(ClientNotFoundException::new);

        return toV2Dto(client);
    }

    public ClientV2Dto addV2(ClientV2InDto clientV2InDto) {
        Client client = modelMapper.map(clientV2InDto, Client.class);
        Client saved = clientRepository.save(client);
        return toV2Dto(saved);
    }

    public ClientV2Dto modifyV2(long id, ClientV2InDto clientV2InDto) throws ClientNotFoundException {
        Client existing = clientRepository.findById(id)
                .orElseThrow(ClientNotFoundException::new);

        modelMapper.map(clientV2InDto, existing);
        existing.setId(id);

        Client saved = clientRepository.save(existing);
        return toV2Dto(saved);
    }

    public void deleteV2(long id) throws ClientNotFoundException {
        Client client = clientRepository.findById(id)
                .orElseThrow(ClientNotFoundException::new);

        clientRepository.delete(client);
    }

    private ClientV2Dto toV2Dto(Client client) {
        ClientV2Dto dto = modelMapper.map(client, ClientV2Dto.class);

        // fullName
        dto.setFullName(client.getClientName() + " " + client.getClientSurname());

        // age + adult
        if (client.getBirthDate() != null) {
            int age = java.time.Period
                    .between(client.getBirthDate(), java.time.LocalDate.now())
                    .getYears();
            dto.setAge(age);
            dto.setAdult(age >= 18);
        } else {
            dto.setAge(null);
            dto.setAdult(false);
        }

        return dto;
    }
}