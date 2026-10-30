package com.eventhosting.repository;

import com.eventhosting.entity.Organizer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrganizerRepository extends JpaRepository<Organizer, Long> {

    Optional<Organizer> findByOrganizerEmailIgnoreCase(String organizerEmail);
}
