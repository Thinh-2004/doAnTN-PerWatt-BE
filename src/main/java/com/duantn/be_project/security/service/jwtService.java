package com.duantn.be_project.security.service;

import com.duantn.be_project.Repository.InvalidatedTokenRepository;
import com.duantn.be_project.Repository.RolePermissionReponsitory;
import com.duantn.be_project.Repository.UserRepository;
import com.duantn.be_project.model.Role;
import com.duantn.be_project.model.RolePermission;
import com.duantn.be_project.model.User;
import com.duantn.be_project.model.Request_Response.authenticate.InvalidatedToken;
import com.duantn.be_project.security.model.AuthenticationResponse;
import com.duantn.be_project.security.model.InstropecReponsee;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

@Service
public interface jwtService {
    String generateToken(User user);

    AuthenticationResponse refeshToken(String token) throws JOSEException, ParseException;

    InstropecReponsee instropecReponsee(String token) throws ParseException, JOSEException;

    SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException;
}

@Slf4j
@Service

class jwtServiceImpl implements jwtService {

    private final UserRepository userRepository;
    private final InvalidatedTokenRepository invalidatedTokenRepository;
    @Autowired
    RolePermissionReponsitory rolePermissionReponsitory;

    @Value("${jwt.secretKey}")
    private String SignerKey;

    @NonFinal
    @Value("${jwt.valid-duration}")
    protected long VALID_DURATION;

    @NonFinal
    @Value("${jwt.refreshable-duration}")
    protected long REFRESHABLE_DURATION;

    public jwtServiceImpl(UserRepository userRepository,
            InvalidatedTokenRepository invalidatedTokenRepository) {
        this.userRepository = userRepository;
        this.invalidatedTokenRepository = invalidatedTokenRepository;
    }

    @Override
    public String generateToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getEmail())// đối tượng của token
                .issuer("BitzNomad.com")// Tên nhà phát hành
                .issueTime(new Date())// Thời gian tạo token
                .expirationTime(new Date(
                        Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli()))// Thời gian dead token
                .jwtID(UUID.randomUUID().toString())// id token
                .claim("scope", buildScope(user))// Tùy chỉnh token
                .build();
        // Tạo payload
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(SignerKey.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("JWT Exception", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public AuthenticationResponse refeshToken(String token) throws JOSEException, ParseException {
        var SingJWT = verifyToken(token, true);

        var jit = SingJWT.getJWTClaimsSet().getJWTID();

        var expirationTime = SingJWT.getJWTClaimsSet().getExpirationTime();

        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(jit)
                .expirytime(expirationTime)
                .build();

        invalidatedTokenRepository.save(invalidatedToken);

        var u = SingJWT.getJWTClaimsSet().getSubject();

        User user = userRepository.findByEmail(u);
        // .orElseThrow(
        // () -> new RuntimeException("ErrorCode.UNAUTHORIZED + User Not Found")
        // );

        String newtoken = generateToken(user);
        // Lấy vai trò từ claims
        String role = SingJWT.getJWTClaimsSet().getStringClaim("scope");
        Boolean booleanRole = role.equals("Admin");
        return AuthenticationResponse.builder()
                .token(newtoken)
                .authenticated(true)
                .booleanAuthentication(booleanRole)
                .build();
    }

    @Override
    public InstropecReponsee instropecReponsee(String token) throws ParseException, JOSEException {
        boolean isValid = true;

        try {
            verifyToken(token, false);
        } catch (RuntimeException | ParseException | JOSEException exception) {
            isValid = false;
        }

        return InstropecReponsee.builder()
                .valid(isValid)
                .expiration(verifyToken(token, false).getJWTClaimsSet().getExpirationTime())
                .build();
    }

    // Kiểm tra tính hợp lệ của token
    public SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {
        // Khởi tạo xác minh token
        JWSVerifier verifier = new MACVerifier(SignerKey.getBytes());

        // Khởi tạo phân tích token
        SignedJWT signedJWT = SignedJWT.parse(token);

        // If isResh = true get ExpriTime to Refesh token
        // neu la refesh Expritime = GetissueTime + REFRESHABLE_DURATION
        // neu ko phai resh expriTime = signedJWT.expritime

        // nếu true là token refresh ngược lại là token bth
        Date expriTime = (isRefresh) ? new Date(signedJWT.getJWTClaimsSet().getIssueTime()
                .toInstant().plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS).toEpochMilli())
                : signedJWT.getJWTClaimsSet().getExpirationTime();

        // Khởi tạo xác minh và check thời gian hợp lệ
        var verified = signedJWT.verify(verifier);
        if (!(verified && expriTime.after(new Date())))
            throw new RuntimeException("ErrorCode.UNAUTHENTICATED");
        if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID()))
            throw new RuntimeException("ErrorCode.UNAUTHENTICATED");

        return signedJWT;
    }

    private String buildScope(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        if (user.getRolePermission() != null) {
            String nameRole = user.getRolePermission().getRole().getNamerole();
            String PermissionRole = user.getRolePermission().getPermission().getName();
            stringJoiner.add(nameRole + "_" + PermissionRole);

            // if (role.getRolepermissions() != null &&
            // !role.getRolepermissions().isEmpty()) {
            // role.getRolepermissions()
            // .stream()
            // .forEach(permission ->
            // stringJoiner.add(permission.getPermission().getName()));
            // }
        }
        return stringJoiner.toString();
    }

}
