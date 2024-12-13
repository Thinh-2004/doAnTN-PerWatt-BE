package com.duantn.be_project.security.service;

import com.duantn.be_project.Repository.InvalidatedTokenRepository;
import com.duantn.be_project.Repository.UserRepository;
import com.duantn.be_project.model.User;
import com.duantn.be_project.model.Request_Response.authenticate.InvalidatedToken;
import com.duantn.be_project.security.model.AuthenticateRequest;
import com.duantn.be_project.security.model.AuthenticationResponse;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.text.ParseException;
import java.util.Date;

@Service
public interface authenticateService {
    AuthenticationResponse authenticate(AuthenticateRequest authenticationRequest);

    void Logout(String token);
}

@Service
@Slf4j
class authenticateImpl implements authenticateService {

    @Autowired
    UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final jwtService jwtService;
    private final InvalidatedTokenRepository invalidatedTokenRepository;

    public authenticateImpl(InvalidatedTokenRepository invalidatedTokenRepository,
            com.duantn.be_project.security.service.jwtService jwtService, PasswordEncoder passwordEncoder,
            UserRepository userRepository) {
        this.invalidatedTokenRepository = invalidatedTokenRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticateRequest authenticationRequest) {
        // Tìm người dùng qua email
        User user = userRepository.findByEmail(authenticationRequest.getEmail());

        // Kiểm tra nếu email không tồn tại
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Email không tồn tại");
        }

        if (authenticationRequest.getIsGoogleLogin() == null || !authenticationRequest.getIsGoogleLogin()) {
            // Kiểm tra mật khẩu
            boolean authenticated = passwordEncoder.matches(authenticationRequest.getPassword(), user.getPassword());
            // Nếu mật khẩu sai, trả về lỗi 401 Unauthorized
            if (!authenticated) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Mật khẩu không chính xác");
            }
        }

        Boolean role = user.getRolePermission().getRole().getNamerole().equals("Admin");

        // Tạo token sau khi xác thực thành công
        String token = jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(true)
                .booleanAuthentication(role)
                .build();
    }

    @Override
    public void Logout(String token) {
        try {
            SignedJWT signToken = jwtService.verifyToken(token, false);

            String jit = signToken.getJWTClaimsSet().getJWTID();
            Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();

            InvalidatedToken invalidatedToken = InvalidatedToken.builder().id(jit).expirytime(expiryTime).build();

            invalidatedTokenRepository.save(invalidatedToken);
        } catch (RuntimeException exception) {
            log.info("Token already expired");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }
}