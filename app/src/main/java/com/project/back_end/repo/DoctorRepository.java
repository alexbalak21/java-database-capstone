package com.project.back_end.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.project.back_end.model.Doctor;
import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    /**
     * Find a doctor by their email address.
     * 
     * @param email the doctor's email
     * @return the Doctor with the specified email, or empty if not found
     */
    Optional<Doctor> findByEmail(String email);

    /**
     * Find doctors by partial name match using LIKE pattern matching.
     * 
     * @param name the partial or full doctor name
     * @return List of doctors whose name matches the pattern
     */
    @Query("SELECT d FROM Doctor d WHERE d.name LIKE CONCAT('%', :name, '%')")
    List<Doctor> findByNameLike(@Param("name") String name);

    /**
     * Filter doctors by partial name (case-insensitive) and exact specialty (case-insensitive).
     * Uses LOWER and CONCAT for case-insensitive partial matching on name and specialty.
     * 
     * @param name the partial or full doctor name (case-insensitive)
     * @param specialty the doctor's specialty (case-insensitive)
     * @return List of doctors matching both criteria
     */
    @Query("SELECT d FROM Doctor d WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%')) AND LOWER(d.specialty) LIKE LOWER(CONCAT('%', :specialty, '%'))")
    List<Doctor> findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(
            @Param("name") String name,
            @Param("specialty") String specialty
    );

    /**
     * Find doctors by specialty, ignoring case sensitivity.
     * 
     * @param specialty the doctor's specialty (case-insensitive)
     * @return List of doctors with the specified specialty
     */
    List<Doctor> findBySpecialtyIgnoreCase(String specialty);

}