package edu.iiitb.esdlab3.service;

import edu.iiitb.esdlab3.dto.CheckInRequest;
import edu.iiitb.esdlab3.dto.CreateRegistrationRequest;
import edu.iiitb.esdlab3.dto.TransferRegistrationRequest;
import edu.iiitb.esdlab3.entity.*;
import edu.iiitb.esdlab3.exception.*;
import edu.iiitb.esdlab3.repository.EventRepository;
import edu.iiitb.esdlab3.repository.RegistrationRepository;

import java.util.List;
import java.util.Locale;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final EventRepository eventRepository;
    private final RegistrationRepository registrationRepository;
    private final EventService eventService;

    @Transactional
    public Registration register(
            Long eventId,
            CreateRegistrationRequest request) {

        Event event = eventService.get(eventId);

        long activeCount = registrationRepository
                .countByEvent_IdAndStatus(
                        eventId,
                        RegistrationStatus.ACTIVE
                );

        if (activeCount >= event.getCapacity()) {
            throw new EventFullException();
        }

        String email = request.getStudentEmail()
                .trim()
                .toLowerCase(Locale.ROOT);

        boolean alreadyRegistered = registrationRepository
                .existsByEvent_IdAndStudentEmailIgnoreCaseAndStatus(
                        eventId,
                        email,
                        RegistrationStatus.ACTIVE
                );

        if (alreadyRegistered) {
            throw new DuplicateRegistrationException();
        }
        return registrationRepository.save(
                new Registration(
                        event,
                        email,
                        RegistrationStatus.ACTIVE
                )
        );
    }

    public List<Registration> activeForEvent(Long eventId) {
        eventService.get(eventId);
        return registrationRepository.findActiveByEventId(eventId);
    }

    @Transactional
    public void cancel(Long registrationId) {
        Registration registration = registrationRepository
                .findById(registrationId)
                .orElseThrow(
                        () -> new RegistrationNotFoundException(registrationId)
                );

        if (registration.getStatus() != RegistrationStatus.CANCELLED) {
            registration.setStatus(RegistrationStatus.CANCELLED);
        }
    }

    @Transactional
    public Registration transfer(
            Long registrationId,
            TransferRegistrationRequest request) {

        Registration registration = registrationRepository
                .findById(registrationId)
                .orElseThrow(
                        () -> new RegistrationNotFoundException(registrationId)
                );

        if (registration.getStatus() == RegistrationStatus.CANCELLED) {
            throw new RegistrationConflictException();
        }

        String email = request.getStudentEmail().trim();

        if (!registration.getStudentEmail().equalsIgnoreCase(email)) {
            throw new UnauthorizedOperationException();
        }

        Event target = eventService.get(request.getTargetEventId());

        long activeCount = registrationRepository
                .countByEvent_IdAndStatus(
                        target.getId(),
                        RegistrationStatus.ACTIVE
                );

        if (activeCount >= target.getCapacity()) {
            throw new EventFullException();
        }

        boolean alreadyRegistered = registrationRepository
                .existsByEvent_IdAndStudentEmailIgnoreCaseAndStatus(
                        target.getId(),
                        email,
                        RegistrationStatus.ACTIVE
                );

        if (alreadyRegistered) {
            throw new DuplicateRegistrationException();
        }

        registration.setStatus(RegistrationStatus.CANCELLED);

        return registrationRepository.save(
                new Registration(
                        target,
                        registration.getStudentEmail(),
                        RegistrationStatus.ACTIVE
                )
        );
    }

    @Transactional
    public Registration checkIn(
            Long eventId,
            CheckInRequest request) {

        eventService.get(eventId);

        Registration registration = registrationRepository
                .findByEvent_IdAndStudentEmailIgnoreCaseAndStatus(
                        eventId,
                        request.getStudentEmail().trim(),
                        RegistrationStatus.ACTIVE
                )
                .orElseThrow(RegistrationNotFoundException::new);

        if (registration.isCheckedIn()) {
            throw new AlreadyCheckedInException();
        }

        registration.markCheckedIn();
        return registrationRepository.save(registration);
    }
}