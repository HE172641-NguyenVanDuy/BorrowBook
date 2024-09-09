package com.borrowbook.duyanh.repository;

import com.borrowbook.duyanh.entity.InformationOfUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InformationOfUserRepository extends JpaRepository<InformationOfUser,Integer> {
}
