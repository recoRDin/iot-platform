package com.iot.server.auth.token;

import com.iot.core.log.exception.ServiceException;
import com.iot.server.auth.model.AuthenticatedUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class JwtTokenService {

    private final TokenProperties properties;
    private final SecretKey signingKey;

    public JwtTokenService(TokenProperties properties) {
        this.properties = properties;
        this.signingKey = createSigningKey(properties.getSecret());

        if (properties.getIssuer() == null || properties.getIssuer().isBlank()) {
            throw new IllegalStateException("JWT issuer 不能为空");
        }
        if (properties.getAccessTokenSeconds() <= 0) {
            throw new IllegalStateException("JWT 有效期必须大于0");
        }
    }

    public TokenResult createAccessToken(AuthenticatedUser user) {
        //设置过期时间
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plusSeconds(properties.getAccessTokenSeconds());

        //创建token
        String token = Jwts.builder()
                .issuer(properties.getIssuer())
                .subject(user.getUserId())
                .id(UUID.randomUUID().toString())
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiresAt))
                .claim("token_type", "access")
                .claim("tenant_id", user.getTenantId())
                .claim("account", user.getAccount())
                .claim("roles", user.getRoleCodes())
                .signWith(signingKey)
                .compact();

        return new TokenResult(token, properties.getAccessTokenSeconds());
    }

    public TokenPayload parseAccessToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(signingKey)
                    .requireIssuer(properties.getIssuer())
                    .require("token_type", "access")
                    .build()
                    .parseSignedClaims(removeBearerPrefix(token))
                    .getPayload();

            return new TokenPayload(
                    claims.getSubject(),
                    claims.get("tenant_id", String.class),
                    claims.get("account", String.class),
                    readRoleCodes(claims.get("roles")),
                    claims.getIssuedAt().toInstant().getEpochSecond(),
                    claims.getExpiration().toInstant().getEpochSecond());
        } catch (ExpiredJwtException exception) {
            throw new ServiceException("Token已过期");
        } catch (JwtException | IllegalArgumentException exception) {
            throw new ServiceException("Token无效");
        }
    }

    private SecretKey createSigningKey(String secret) {
        if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException("IOT_TOKEN_SECRET 至少需要32字节");
        }
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    private String removeBearerPrefix(String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Token不能为空");
        }
        String value = token.strip();
        return value.regionMatches(true, 0, "Bearer ", 0, 7)
                ? value.substring(7).strip()
                : value;
    }

    private List<String> readRoleCodes(Object roles) {
        if (!(roles instanceof List<?> values)) {
            return List.of();
        }
        return values.stream()
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .toList();
    }
}
