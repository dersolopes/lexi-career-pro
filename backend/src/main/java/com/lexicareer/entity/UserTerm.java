package com.lexicareer.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_term")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserTerm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "term_id", nullable = false)
    private Term term;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.NEW;

    @Enumerated(EnumType.STRING)
    private KnowledgeLevel knowledgeLevel;

    @Column(nullable = false)
    private boolean favorite = false;

    private LocalDateTime lastReviewAt;
    private LocalDateTime nextReviewAt;

    @Column(nullable = false)
    private int reviewCount = 0;

    @Column(nullable = false)
    private int correctAnswers = 0;

    @Column(nullable = false)
    private int totalQuizAttempts = 0;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum Status {
        NEW,
        PRESENTED,
        STUDIED,
        IN_REVIEW,
        MASTERED
    }

    public enum KnowledgeLevel {
        NEVER_SEEN,
        HEARD_NOT_EXPLAINED,
        KNOWN,
        USE_IN_WORK
    }
}
