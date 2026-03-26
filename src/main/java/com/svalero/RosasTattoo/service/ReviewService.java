package com.svalero.RosasTattoo.service;

import com.svalero.RosasTattoo.domain.Appointment;
import com.svalero.RosasTattoo.domain.Review;
import com.svalero.RosasTattoo.domain.enums.AppointmentState;
import com.svalero.RosasTattoo.dto.ReviewDto;
import com.svalero.RosasTattoo.dto.ReviewInDto;
import com.svalero.RosasTattoo.exception.AppointmentNotFoundException;
import com.svalero.RosasTattoo.exception.ReviewNotFoundException;
import com.svalero.RosasTattoo.repository.AppointmentRepository;
import com.svalero.RosasTattoo.repository.ReviewRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    private ModelMapper modelMapper;

    public List<ReviewDto> findAll(Integer rating, Long professionalId, Boolean wouldRecommend) {
        List<Review> reviews = reviewRepository.findByFilters(rating, professionalId, wouldRecommend);
        return modelMapper.map(reviews, new TypeToken<List<ReviewDto>>() {}.getType());
    }

    public ReviewDto findById(long id) throws ReviewNotFoundException {
        Review review = reviewRepository.findById(id)
                .orElseThrow(ReviewNotFoundException::new);

        return modelMapper.map(review, ReviewDto.class);
    }

    public ReviewDto add(ReviewInDto reviewInDto) throws AppointmentNotFoundException {
        Appointment appointment = appointmentRepository.findById(reviewInDto.getAppointmentId())
                .orElseThrow(AppointmentNotFoundException::new);

        Review review = new Review();
        modelMapper.map(reviewInDto, review);

        review.setAppointment(appointment);
        review.setCreatedAt(LocalDateTime.now());

        Review saved = reviewRepository.save(review);

        appointment.setState(AppointmentState.COMPLETED);
        appointmentRepository.save(appointment);

        return modelMapper.map(saved, ReviewDto.class);
    }

    public ReviewDto modify(long id, ReviewInDto reviewInDto) throws ReviewNotFoundException {
        Review existing = reviewRepository.findById(id)
                .orElseThrow(ReviewNotFoundException::new);

        // Para que nunca se pierda la relación ni se toque createdAt
        Appointment keepAppointment = existing.getAppointment();
        LocalDateTime keepCreatedAt = existing.getCreatedAt();

        modelMapper.map(reviewInDto, existing);
        existing.setId(id);
        existing.setAppointment(keepAppointment);
        existing.setCreatedAt(keepCreatedAt);

        Review saved = reviewRepository.save(existing);
        return modelMapper.map(saved, ReviewDto.class);
    }

    public void delete(long id) throws ReviewNotFoundException {
        Review review = reviewRepository.findById(id)
                .orElseThrow(ReviewNotFoundException::new);

        reviewRepository.delete(review);
    }
}