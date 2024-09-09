package com.borrowbook.duyanh.service;

import com.borrowbook.duyanh.dto.request.InformationOfUserDTO;
import com.borrowbook.duyanh.entity.InformationOfUser;
import com.borrowbook.duyanh.exception.ErrorCode;
import com.borrowbook.duyanh.repository.InformationOfUserRepository;
import com.borrowbook.duyanh.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InformationOfUserServiceImpl implements InformationOfUserService{

    @Autowired
    private InformationOfUserRepository repository;

    @Override
    public InformationOfUser getInformationOfUserById(int id) {
        return repository.findById(id).orElseThrow(
                () -> new RuntimeException(ErrorCode.NOT_FOUND.getMessage())
        );
    }

    @Override
    public InformationOfUser saveInformationOfUser(InformationOfUserDTO dto) {
        InformationOfUser informationOfUser = new InformationOfUser();
        informationOfUser.setUserId(dto.getUserId());
        informationOfUser.setDob(dto.getDob());
        informationOfUser.setEmail(dto.getEmail());
        informationOfUser.setPhoneNumber(dto.getPhoneNumber());
        return repository.save(informationOfUser);
    }

    @Override
    public InformationOfUser updateInformationOfUser(InformationOfUserDTO dto, int id) {
        InformationOfUser informationOfUser = getInformationOfUserById(id);
        informationOfUser.setUserId(dto.getUserId());
        informationOfUser.setDob(dto.getDob());
        informationOfUser.setEmail(dto.getEmail());
        informationOfUser.setPhoneNumber(dto.getPhoneNumber());
        return repository.saveAndFlush(informationOfUser);
    }
}
