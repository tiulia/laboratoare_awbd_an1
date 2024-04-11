package com.awbd.lab6.repositories.security;

import com.awbd.lab6.domain.security.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<Authority, Integer> {
}