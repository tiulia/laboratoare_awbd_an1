package com.awbd.lab5.repositories.security;

import com.awbd.lab5.domain.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<Authority, Integer> {
}