package com.awbd.lab7.repositories.security;

import com.awbd.lab7.domain.security.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<Authority, Integer> {
}