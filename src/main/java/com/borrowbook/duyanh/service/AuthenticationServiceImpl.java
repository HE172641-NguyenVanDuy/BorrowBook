package com.borrowbook.duyanh.service;

import com.borrowbook.duyanh.dto.request.AuthenticationRequest;
import com.borrowbook.duyanh.dto.request.IntrospectRequest;
import com.borrowbook.duyanh.dto.response.AuthenticationResponse;
import com.borrowbook.duyanh.dto.response.IntrospectResponse;

import com.borrowbook.duyanh.entity.User;
import com.borrowbook.duyanh.exception.AppException;
import com.borrowbook.duyanh.exception.ErrorCode;
import com.borrowbook.duyanh.repository.UserRepository;
//import com.nimbusds.jose.*;
//import com.nimbusds.jose.crypto.MACSigner;
//import com.nimbusds.jose.crypto.MACVerifier;
//import com.nimbusds.jwt.JWTClaimsSet;
//import com.nimbusds.jwt.SignedJWT;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Service
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService{

//    @NonFinal
//    @Value("${jwt.secret}")
//    private String SIGN_KEY;
//   // private static final Logger log = LoggerFactory.getLogger(AuthenticationServiceImpl.class);
//
//    private UserRepository userRepository;
//
//    @Autowired
//    public AuthenticationServiceImpl(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }
//
//    @Override
//    public AuthenticationResponse authenticate(AuthenticationRequest request) {
//        var user = userRepository.findByUsername(request.getUsername())
//                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
//        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);
//
//        // xác định xem user có đăng nhập thành công hay không
//        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());
//
//        if(!authenticated) {
//            throw  new AppException(ErrorCode.UNAUTHORIZED);
//        }
//        var token = generateToken(user);
//        return AuthenticationResponse.builder()
//                .token(token)
//                .authenticated(true)
//                .build();
//    }
//
//    @Override
//    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
//        var token = request.getToken();
//        JWSVerifier verifier = new MACVerifier(SIGN_KEY.getBytes());
//
//        SignedJWT signedJWT =  SignedJWT.parse(token);
//
//        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
//
//        var verified =  signedJWT.verify(verifier);
//
//        return IntrospectResponse.builder()
//                .valid(verified && expiryTime.after(new Date()))
//                .build();
//    }
//
//    // method để gene token
//    private String generateToken(User user) {
//        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
//
//        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
//                .subject(user.getUsername()) //đại dienejj cho user đăng nhập
//                .issuer("duyanh.com") // xác định token được issue từ ai, thường là domain của service
//                .issueTime(new Date())
//                .expirationTime(new Date(
//                        Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()
//                ))
//                .claim("scope",user.getRole().getRoleName())
//                .jwtID(UUID.randomUUID().toString())
//                .build();
//        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
//
//        // cần 2 param là header và payload
//        JWSObject jwsObject = new JWSObject(header,payload);
//
//        try {
//            jwsObject.sign(new MACSigner(SIGN_KEY.getBytes()));
//            return jwsObject.serialize();
//        } catch (JOSEException e) {
//            log.error("Cannot create tokem",e);
//            throw new RuntimeException(e);
//        }
//
//    }
}
