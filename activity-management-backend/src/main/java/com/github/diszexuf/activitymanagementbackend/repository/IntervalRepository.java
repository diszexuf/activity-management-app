package com.github.diszexuf.activitymanagementbackend.repository;

import com.github.diszexuf.activitymanagementbackend.model.Interval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface IntervalRepository extends JpaRepository<Interval, UUID> {

    @Query("select count(i) > 0 from Interval i where i.start < :end and i.end > :start")
    boolean existsOverlapping(@Param("start") Integer start, @Param("end") Integer end);

}
