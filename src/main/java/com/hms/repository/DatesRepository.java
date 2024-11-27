package com.hms.repository;

import com.hms.entity.Dates;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.Optional;

public interface DatesRepository extends JpaRepository<Dates, Long> {

  @Query("SELECT d FROM Dates d WHERE d.date_lists = :date")
  Optional<Dates> findByDateLists(@Param("date") Date date);
}