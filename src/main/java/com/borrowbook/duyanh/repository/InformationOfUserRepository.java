package com.borrowbook.duyanh.repository;

import com.borrowbook.duyanh.entity.InformationOfUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InformationOfUserRepository extends JpaRepository<InformationOfUser,Integer> {

    @Query("SELECT i.email FROM InformationOfUser i")
    List<String> findAllEmail();
    public boolean existsByPhoneNumber(String phoneNumber);
    public boolean existsByEmail(String email);
}
