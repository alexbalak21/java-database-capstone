package com.project.back_end.repo;

import com.project.back_end.models.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

    /**
     * Find an Admin by username.
     *
     * @param username the admin's username
     * @return the matching Admin or null if not found
     */
    Admin findByUsername(String username);
}
