package com.borrowbook.duyanh.service;

import com.borrowbook.duyanh.dto.request.InformationOfUserDTO;
import com.borrowbook.duyanh.entity.InformationOfUser;
import org.springframework.stereotype.Service;

@Service
public interface InformationOfUserService {
    InformationOfUser getInformationOfUserById(int id);
    InformationOfUser saveInformationOfUser(InformationOfUserDTO dto);
    InformationOfUser updateInformationOfUser(InformationOfUserDTO dto, int id);
}
