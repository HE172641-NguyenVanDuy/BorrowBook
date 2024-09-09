package com.borrowbook.duyanh.service;

import com.borrowbook.duyanh.dto.request.InformationOfUserDTO;
import com.borrowbook.duyanh.dto.request.UserDTO;
import com.borrowbook.duyanh.entity.InformationOfUser;
import com.borrowbook.duyanh.entity.User;
import org.springframework.stereotype.Service;

@Service
public interface InformationOfUserService {
    InformationOfUser getInformationOfUserByUserId(int id);
    InformationOfUser saveInformationOfUser(UserDTO dto, User user);
    InformationOfUser updateInformationOfUser(UserDTO dto, User user);
}
