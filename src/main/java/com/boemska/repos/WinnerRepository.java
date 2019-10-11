package com.boemska.repos;

import com.boemska.data.Winner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WinnerRepository extends JpaRepository<Winner,Integer> {
    List<Winner> findByNumbers(String numbers);
}
