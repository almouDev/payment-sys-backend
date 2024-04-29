package com.almou.payment.utils;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
public class JwtUtilities {
    public static Algorithm getAlgorithm() throws UnsupportedEncodingException {
        return Algorithm.HMAC256("myscretisTolongSothinkjoucantsseug1234//AAmm");
    }
    public static JWTCreator.Builder JwtTokenCreation(String issuer, long expirationTime, Collection<String> authorities, String username) {
        String[] roles=new String[authorities.size()];
        authorities.toArray(roles);
        JWTCreator.Builder jwtAccessToken= JWT.create()
                .withSubject(username)
                .withExpiresAt(new Date(System.currentTimeMillis()+expirationTime))
                .withIssuer(issuer)
                .withArrayClaim("roles", roles);
        return jwtAccessToken;
    }
    public static DecodedJWT JwtTokenDecryption(String acces_token) throws Exception {
        String jwtToken=acces_token.substring(7);
        JWTVerifier jwtVerifier= JWT.require(getAlgorithm()).build();
        DecodedJWT decodedJWT=jwtVerifier.verify(jwtToken);
        return decodedJWT;
    }

    public static void SendTokens(String jwtAccessToken, String jwtRefreshToken, HttpServletResponse response) throws IOException {
        Map<String,String> jwtTokens=new HashMap<>();
        jwtTokens.put("access_token",jwtAccessToken);
        jwtTokens.put("refresh_token",jwtRefreshToken);
        response.setContentType("Application/json");
        new ObjectMapper().writeValue(response.getOutputStream(),jwtTokens);
    }
}
