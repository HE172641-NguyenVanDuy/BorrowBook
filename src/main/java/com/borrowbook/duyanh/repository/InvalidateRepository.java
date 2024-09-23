package com.borrowbook.duyanh.repository;

import com.borrowbook.duyanh.entity.InvalidatedToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvalidateRepository extends JpaRepository<InvalidatedToken,String> {


}

