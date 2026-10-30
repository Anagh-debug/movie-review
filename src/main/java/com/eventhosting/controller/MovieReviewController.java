package com.eventhosting.controller;

import com.eventhosting.dto.request.MovieReviewRequest;
import com.eventhosting.dto.response.MovieReviewResponse;
import com.eventhosting.dto.response.ResponseBean;
import com.eventhosting.dto.response.ReviewSummaryResponse;
import com.eventhosting.service.MovieReviewService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class MovieReviewController {

    private final MovieReviewService movieReviewService;

    public MovieReviewController(MovieReviewService movieReviewService) {
        this.movieReviewService = movieReviewService;
    }

    @PostMapping("/events/{eventId}/reviews")
    public ResponseEntity<ResponseBean<MovieReviewResponse>> addReview(
            @PathVariable Long eventId,
            @Valid @RequestBody MovieReviewRequest request) {
        MovieReviewResponse response = movieReviewService.addReview(eventId, request);
        return new ResponseEntity<>(
                ResponseBean.success("Review submitted successfully", response),
                HttpStatus.CREATED);
    }

    @GetMapping("/events/{eventId}/reviews")
    public ResponseEntity<ResponseBean<List<MovieReviewResponse>>> getReviews(
            @PathVariable Long eventId) {
        List<MovieReviewResponse> reviews = movieReviewService.getReviewsForEvent(eventId);
        return ResponseEntity.ok(
                ResponseBean.success("Reviews retrieved successfully", reviews));
    }

    @GetMapping("/events/{eventId}/reviews/summary")
    public ResponseEntity<ResponseBean<ReviewSummaryResponse>> getSummary(
            @PathVariable Long eventId) {
        ReviewSummaryResponse summary = movieReviewService.getSummaryForEvent(eventId);
        return ResponseEntity.ok(
                ResponseBean.success("Review summary retrieved", summary));
    }

    @GetMapping("/reviews/{reviewId}")
    public ResponseEntity<ResponseBean<MovieReviewResponse>> getReview(
            @PathVariable Long reviewId) {
        MovieReviewResponse review = movieReviewService.getReviewById(reviewId);
        return ResponseEntity.ok(
                ResponseBean.success("Review retrieved successfully", review));
    }

    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<ResponseBean<Object>> deleteReview(@PathVariable Long reviewId) {
        movieReviewService.deleteReview(reviewId);
        return ResponseEntity.ok(ResponseBean.success("Review deleted successfully", null));
    }
}
