-- Seed data for the in-memory H2 database so the review endpoints have an
-- event to point at straight away. Ids are assigned by the identity columns,
-- so the first organizer is 1 and the events are 1 and 2.
-- Re-created on every application start; not used by the postgres profile.

INSERT INTO organizer (organizer_name, organizer_contact, organizer_email, password, terms_consent, created_at)
VALUES ('Pratham Gupta', '9876543210', 'pratham@example.com', 'seed-placeholder-not-a-login', TRUE, CURRENT_TIMESTAMP);

INSERT INTO hosting_event (organizer_id, event_name, event_type, event_genre, description,
                           base_ticket_price, attendee_name, attendee_role,
                           submission_date, approval_status, venue, event_date)
VALUES ((SELECT organizer_id FROM organizer WHERE organizer_email = 'pratham@example.com'),
        'Interstellar Re-Release', 'Movie', 'Sci-Fi',
        'IMAX re-release of the 2014 space epic.',
        250.00, 'Matthew McConaughey', 'Lead Actor',
        CURRENT_TIMESTAMP, 'APPROVED', 'PVR Phoenix, Pune', '2026-11-15 19:30');

INSERT INTO hosting_event (organizer_id, event_name, event_type, event_genre, description,
                           base_ticket_price, attendee_name, attendee_role,
                           submission_date, approval_status, venue, event_date)
VALUES ((SELECT organizer_id FROM organizer WHERE organizer_email = 'pratham@example.com'),
        'Midnight Horror Marathon', 'Movie', 'Horror',
        'Triple bill running from midnight to sunrise.',
        180.00, 'Anjali Rao', 'Host',
        CURRENT_TIMESTAMP, 'PENDING', 'Cinepolis Seasons Mall', '2026-12-02 00:00');
