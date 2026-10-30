package com.eventhosting.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * A review written by a public user for a hosted event (movie show).
 * One event can collect many reviews (1:N).
 *
 * Public users are not a modelled entity in this system, so the reviewer's
 * name and email are stored on the review row itself rather than as a
 * foreign key.
 */
@Entity
@Table(
    name = "movie_review",
    indexes = {
        @Index(name = "idx_review_event_id", columnList = "event_id"),
        @Index(name = "idx_review_rating", columnList = "rating"),
        @Index(name = "idx_review_date", columnList = "review_date")
    }
)
public class MovieReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long reviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private HostingEvent hostingEvent;

    @Column(name = "reviewer_name", nullable = false, length = 100)
    private String reviewerName;

    @Column(name = "reviewer_email", length = 100)
    private String reviewerEmail;

    /**
     * Star rating from 1 to 5.
     */
    @Column(name = "rating", nullable = false)
    private Integer rating;

    @Column(name = "review_title", length = 150)
    private String reviewTitle;

    @Column(name = "review_text", columnDefinition = "TEXT")
    private String reviewText;

    @Column(name = "review_date", nullable = false)
    private LocalDateTime reviewDate;

    public MovieReview() {
    }

    public MovieReview(HostingEvent hostingEvent, String reviewerName, String reviewerEmail,
                       Integer rating, String reviewTitle, String reviewText) {
        this.hostingEvent = hostingEvent;
        this.reviewerName = reviewerName;
        this.reviewerEmail = reviewerEmail;
        this.rating = rating;
        this.reviewTitle = reviewTitle;
        this.reviewText = reviewText;
        this.reviewDate = LocalDateTime.now();
    }

    @PrePersist
    public void prePersist() {
        if (this.reviewDate == null) {
            this.reviewDate = LocalDateTime.now();
        }
    }

    // Getters and Setters

    public Long getReviewId() {
        return reviewId;
    }

    public void setReviewId(Long reviewId) {
        this.reviewId = reviewId;
    }

    public HostingEvent getHostingEvent() {
        return hostingEvent;
    }

    public void setHostingEvent(HostingEvent hostingEvent) {
        this.hostingEvent = hostingEvent;
    }

    public String getReviewerName() {
        return reviewerName;
    }

    public void setReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
    }

    public String getReviewerEmail() {
        return reviewerEmail;
    }

    public void setReviewerEmail(String reviewerEmail) {
        this.reviewerEmail = reviewerEmail;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getReviewTitle() {
        return reviewTitle;
    }

    public void setReviewTitle(String reviewTitle) {
        this.reviewTitle = reviewTitle;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public LocalDateTime getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(LocalDateTime reviewDate) {
        this.reviewDate = reviewDate;
    }
}
