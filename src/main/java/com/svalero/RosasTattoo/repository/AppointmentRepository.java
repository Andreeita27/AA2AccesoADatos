package com.svalero.RosasTattoo.repository;

import com.svalero.RosasTattoo.domain.Appointment;
import com.svalero.RosasTattoo.domain.enums.AppointmentState;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentRepository extends CrudRepository<Appointment, Long> {

    List<Appointment> findAll();

    @Query("SELECT a FROM appointment a WHERE " +
            "(:state IS NULL OR a.state = :state) AND " +
            "(:clientId IS NULL OR a.client.id = :clientId) AND " +
            "(:professionalId IS NULL OR a.professional.id = :professionalId)")
    List<Appointment> findByFilters(
            @Param("state") AppointmentState state,
            @Param("clientId") Long clientId,
            @Param("professionalId") Long professionalId
    );
}
