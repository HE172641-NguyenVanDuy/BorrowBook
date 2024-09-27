package com.borrowbook.duyanh.service;

import com.borrowbook.duyanh.dto.request.UserDTO;
import com.borrowbook.duyanh.entity.InformationOfUser;
import com.borrowbook.duyanh.entity.User;
import com.borrowbook.duyanh.exception.AppException;
import com.borrowbook.duyanh.exception.ErrorCode;
import com.borrowbook.duyanh.repository.InformationOfUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;

@Service
public class InformationOfUserServiceImpl implements InformationOfUserService {

    @Autowired
    private InformationOfUserRepository repository;

    @Override
    public InformationOfUser getInformationOfUserByUserId(int id) {
        return repository.findById(id).orElseThrow(
                () -> new RuntimeException(ErrorCode.NOT_FOUND.getMessage())
        );
    }

    @Override
    public InformationOfUser saveInformationOfUser(UserDTO dto, User user) {
        InformationOfUser informationOfUser = new InformationOfUser();
        informationOfUser.setUser(user);
        informationOfUser.setDob(dto.getDob());
        informationOfUser.setEmail(dto.getEmail());
        informationOfUser.setPhoneNumber(dto.getPhoneNumber());
        informationOfUser.setUserId(user.getId());
        repository.save(informationOfUser);
        return informationOfUser;
    }

    @Override
    @Modifying
    public InformationOfUser updateInformationOfUser(UserDTO dto, User user) {
        InformationOfUser informationOfUser = getInformationOfUserByUserId(user.getId());
        informationOfUser.setUser(user);
        if (!dto.getDob().toString().isEmpty() && dto.getDob() != null) {
            informationOfUser.setDob(dto.getDob());
        }

        if (!dto.getEmail().isEmpty() && dto.getEmail() != null) {
            if (!repository.existsByEmail(dto.getEmail())) {
                informationOfUser.setEmail(dto.getEmail());
            } else throw new AppException(ErrorCode.EXIST_EMAIL);
        }

        if (!dto.getPhoneNumber().isEmpty() && dto.getPhoneNumber() != null) {
            if (!repository.existsByPhoneNumber(dto.getPhoneNumber())) {
                informationOfUser.setPhoneNumber(dto.getPhoneNumber());
            } else throw new AppException(ErrorCode.EXIST_PHONE_NUMBER);
        }
        informationOfUser.setUserId(user.getId());
        repository.saveAndFlush(informationOfUser);
        return informationOfUser;
    }
}
