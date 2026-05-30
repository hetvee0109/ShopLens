package com.shoplens.shoplensbackend.repository;

import com.shoplens.shoplensbackend.model.Owner;
import com.shoplens.shoplensbackend.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

// JpaRepository<Owner, Long> means:
// - we are managing "Owner" entities
// - the primary key type is "Long"
public interface OwnerRepository extends JpaRepository<Owner, Long> {

    // Spring reads "findByEmail" and generates:
    // SELECT * FROM owners WHERE email = ?
    // Optional<Owner> means the result might be null (no owner with that email)
    Optional<Owner> findByEmail(String email);

    // SELECT * FROM owners WHERE role = ?
    // Used by admin to list all owners or all admins
    List<Owner> findByRole(Role role);

    // SELECT * FROM owners WHERE is_active = true
    // Used by admin to see only active accounts
    List<Owner> findAllByIsActiveTrue();
}
