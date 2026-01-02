package com.github.diszexuf.activitymanagementbackend.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "intervals")
public class Interval {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    UUID id;

    @Column(name = "start", updatable = false, nullable = false)
    Integer start;

    @Column(name = "\"end\"", updatable = false, nullable = false)
    Integer end;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", updatable = false, nullable = false)
    ActivityType type;

    @Column(name = "created_at", updatable = false, nullable = false)
    OffsetDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
    }
}
