package com.borrowbook.duyanh.service;

import com.borrowbook.duyanh.dto.request.AuthenticationRequest;
import com.borrowbook.duyanh.dto.request.IntrospectRequest;
import com.borrowbook.duyanh.dto.request.LogoutRequest;
import com.borrowbook.duyanh.dto.response.AuthenticationResponse;
import com.borrowbook.duyanh.dto.response.IntrospectResponse;
//import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEException;
import org.springframework.stereotype.Service;

import java.text.ParseException;

@Service
public interface AuthenticationService {
    AuthenticationResponse authenticate(AuthenticationRequest request);
    IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException;
    void logout(LogoutRequest request) throws ParseException, JOSEException;
}
