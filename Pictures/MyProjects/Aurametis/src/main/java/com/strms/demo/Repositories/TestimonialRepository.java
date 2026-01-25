package com.strms.demo.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.strms.demo.Entites.Testimonial;

@Repository
public interface TestimonialRepository extends JpaRepository<Testimonial, String> {
}

