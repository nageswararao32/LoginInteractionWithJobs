package com.strms.demo.Repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.strms.demo.Entites.Stats;


@Repository
public interface StatsRepository extends JpaRepository<Stats, String> {
}

