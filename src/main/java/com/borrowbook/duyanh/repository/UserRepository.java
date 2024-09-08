package com.borrowbook.duyanh.repository;

import com.borrowbook.duyanh.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    boolean existsByUsername(String username);

    @Query("SELECT s FROM User s WHERE s.status LIKE 'ACTIVE' ")
    List<User> getAllUserActive();

    @Query("SELECT s FROM User s WHERE s.status LIKE 'DELETE' ")
    List<User> getAllUserDeleted();

    @Query("SELECT s FROM User s WHERE s.status LIKE 'BAN' ")
    List<User> getAllUserBanned();

    @Modifying
    @Query("UPDATE User u SET u.status = 'ACTIVE' WHERE c.id = :id")
    int activeUserById(@Param("id") int id);

    @Modifying
    @Query("UPDATE User u SET u.status = 'DELETE' WHERE c.id = :id")
    int deleteUserById(@Param("id") int id);

    @Modifying
    @Query("UPDATE User u SET u.status = 'BAN' WHERE c.id = :id")
    int banUserById(@Param("id") int id);
}
