package com.borrowbook.duyanh;

import com.borrowbook.duyanh.entity.Borrow;
import com.borrowbook.duyanh.repository.BorrowRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Date;

@SpringBootTest
class DuyanhApplicationTests {

	@Autowired
	BorrowRepository borrowRepository;

	@Test
	void contextLoads() {
	}

}
