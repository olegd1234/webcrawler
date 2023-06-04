package com.assignment.webcrawler.repository;

import com.assignment.webcrawler.entity.Webpage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WebpageRepository extends JpaRepository<Webpage, Long> {
    Webpage findByUrl(String url);
}
