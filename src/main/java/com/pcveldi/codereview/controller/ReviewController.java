package com.pcveldi.codereview.controller;

import com.pcveldi.codereview.model.CodeReviewRequest;
import com.pcveldi.codereview.model.CodeReviewResult;
import com.pcveldi.codereview.service.CodeAnalysisService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
@Slf4j
public class ReviewController {

    private final CodeAnalysisService codeAnalysisService;

    @PostMapping
    public ResponseEntity<CompletableFuture<CodeReviewResult>> reviewCode(
            @Valid @RequestBody CodeReviewRequest request) {
        log.info("Code review requested for language: {}", request.getLanguage());
        CompletableFuture<CodeReviewResult> result = codeAnalysisService.analyzeCode(
                request.getCode(), request.getLanguage(), request.getContext());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<CodeReviewResult> getReview(@PathVariable Long reviewId) {
        return codeAnalysisService.getReviewById(reviewId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<CodeReviewResult>> getAllReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(codeAnalysisService.getAllReviews(page, size));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId) {
        codeAnalysisService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }
}
