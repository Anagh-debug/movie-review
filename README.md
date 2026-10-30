# EventSpot — Movie Review Module

Review endpoints for the Event Hosting and Approval System (Movie Booking Platform).
Spring Boot 3.3.4 · Java 17 · Spring Data JPA / Hibernate · H2 (default) or PostgreSQL 16.

## Run it

```bash
mvn spring-boot:run
```

The API comes up on `http://localhost:8080`. The default profile uses an **in-memory H2**
database, so nothing needs installing — but the data resets on every restart.

`src/main/resources/data.sql` seeds one organizer and two events on each start:

| event_id | event_name                | genre   | approval_status |
|----------|---------------------------|---------|-----------------|
| 1        | Interstellar Re-Release   | Sci-Fi  | APPROVED        |
| 2        | Midnight Horror Marathon  | Horror  | PENDING         |

Browse the tables at `http://localhost:8080/h2-console` (JDBC URL `jdbc:h2:mem:eventspot`,
user `sa`, no password).

### PostgreSQL instead

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=postgres
```

Expects a database named `eventspot` on `localhost:5432`; override credentials with the
`POSTGRES_USER` / `POSTGRES_PASSWORD` environment variables.

## Endpoints

Movies (events):

| Method | Path                                      | Purpose                                 | Success |
|--------|-------------------------------------------|-----------------------------------------|---------|
| POST   | `/api/events`                             | Submit a movie (lands as PENDING)       | 201     |
| GET    | `/api/events`                             | Every movie; `?status=` to narrow       | 200     |
| GET    | `/api/events/hosted`                      | Approved movies only                    | 200     |
| GET    | `/api/events/{id}`                        | One movie                               | 200     |
| DELETE | `/api/events/{id}`                        | Remove a movie **and all its reviews**  | 200     |
| PUT    | `/api/admin/events/{id}/approval`         | Approve or reject a submission          | 200     |

Reviews:

| Method | Path                                      | Purpose                                | Success |
|--------|-------------------------------------------|----------------------------------------|---------|
| POST   | `/api/events/{eventId}/reviews`           | Add a review to an event               | 201     |
| GET    | `/api/events/{eventId}/reviews`           | All reviews for an event, newest first | 200     |
| GET    | `/api/events/{eventId}/reviews/summary`   | Review count + average rating          | 200     |
| GET    | `/api/reviews/{reviewId}`                 | One review                             | 200     |
| DELETE | `/api/reviews/{reviewId}`                 | Remove a review                        | 200     |

Deleting a movie reports what it removed, so the cascade is visible:

```json
{ "errorCode": 0, "errorMsg": "Movie deleted successfully",
  "body": { "eventId": 4, "eventName": "Jawan", "reviewsDeleted": 2 } }
```

Every response — success or failure — uses the project's standard `ResponseBean` envelope:

```json
{ "errorCode": 0, "errorMsg": "Review submitted successfully", "body": { } }
```

`errorCode` is `0` on success and the HTTP status code on failure. A validation failure
returns `400` with a field-by-field map in `body`:

```json
{
  "errorCode": 400,
  "errorMsg": "Request validation failed",
  "body": { "rating": "Rating must not exceed 5", "reviewerName": "Reviewer name is required" }
}
```

## Postman

Import `postman/EventSpot-Movie-Reviews.postman_collection.json` and run the requests top
to bottom. `baseUrl` and `eventId` are collection variables; request 1 stores the new
`reviewId` so the later requests pick it up automatically.

## Data model

`MovieReview` hangs off `HostingEvent` with a `@ManyToOne` — one event collects many
reviews. Public users are not a modelled entity in this system, so `reviewer_name` and
`reviewer_email` live on the review row rather than behind a foreign key.

Indexes on `movie_review`: `idx_review_event_id`, `idx_review_rating`, `idx_review_date`.
