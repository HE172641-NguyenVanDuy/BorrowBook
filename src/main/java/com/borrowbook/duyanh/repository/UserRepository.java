package com.borrowbook.duyanh.repository;

import com.borrowbook.duyanh.dto.request.SearchUserDTO;
import com.borrowbook.duyanh.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

//    User getUserById(int id);

    @Query("SELECT i.username FROM User i")
    List<String> findAllUsername();

    boolean existsByUsername(String username);

    Optional<User> findByUsername(String username);

    @Query("SELECT u FROM User u " +
            "JOIN u.informationOfUser i " +
            "JOIN u.role r " +
            "WHERE (:username IS NULL OR u.username LIKE %:username%) " +
            "AND (:phoneNumber IS NULL OR i.phoneNumber LIKE %:phoneNumber%) " +
            "AND (:email IS NULL OR i.email LIKE %:email%) " +
            "AND (:roleName IS NULL OR r.roleName LIKE %:roleName%)")
    Page<User> searchUsers(@Param("username") String username,
                           @Param("phoneNumber") String phoneNumber,
                           @Param("email") String email,
                           @Param("roleName") String roleName,
                           Pageable pageable);

    @Query("SELECT u FROM User u " +
            "LEFT JOIN u.informationOfUser i " +
            "LEFT JOIN u.role r " +
            "WHERE u.username LIKE %:keyword% OR " +
            "r.roleName LIKE %:keyword% OR " +
            "i.email LIKE %:keyword% OR " +
            "i.phoneNumber LIKE %:keyword% OR " +
            "CAST(i.dob AS string) LIKE %:keyword% OR " +
            "u.status LIKE %:keyword%")
    Page<User> searchAdvancedUser(Pageable pageable, String keyword);

    @Query("SELECT u FROM User u " +
            "LEFT JOIN u.informationOfUser i " +
            "LEFT JOIN u.role r " +
            "WHERE u.username LIKE %:keyword% OR " +
            "r.roleName LIKE %:keyword% OR " +
            "i.email LIKE %:keyword% OR " +
            "i.phoneNumber LIKE %:keyword% OR " +
            "CAST(i.dob AS string) LIKE %:keyword% OR " +
            "u.status LIKE %:keyword%")
    List<User> searchAdvancedUser(String keyword);

    @Query("SELECT s  FROM User s WHERE s.status LIKE 'ACTIVE' ")
    Page<User> getAllUserActive(Pageable pageable);

    @Query("SELECT s  FROM User s WHERE s.status LIKE 'DELETE' ")
    Page<User> getAllUserDeleted(Pageable pageable);

    @Query("SELECT s  FROM User s WHERE s.status LIKE 'BAN' ")
    Page<User> getAllUserBanned(Pageable pageable);

    @Modifying
    @Query("UPDATE User u SET u.status = 'ACTIVE' WHERE u.id = :id")
    int activeUserById(@Param("id") int id);

    @Modifying
    @Query("UPDATE User u SET u.status = 'DELETE' WHERE u.id = :id")
    int deleteUserById(@Param("id") int id);

    @Modifying
    @Query("UPDATE User u SET u.status = 'BAN' WHERE u.id = :id")
    int banUserById(@Param("id") int id);


}
