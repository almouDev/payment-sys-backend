package com.almou.payment.security;
import com.almou.payment.utils.JwtUtilities;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
public class JwtAuthorizationToken extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
            String acces_token = request.getHeader("Authorization");
            if (acces_token != null && acces_token.startsWith("Bearer ")) {
                try {
                    DecodedJWT decodedJWT = JwtUtilities.JwtTokenDecryption(acces_token);
                    String username = decodedJWT.getSubject();
                    Collection<GrantedAuthority> authorities = new ArrayList<>();
                    String[] roles = decodedJWT.getClaim("roles").asArray(String.class);
                    for (String role : roles) {
                        authorities.add(new SimpleGrantedAuthority(role));
                    }
                    UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(token);
                    filterChain.doFilter(request, response);
                } catch (Exception e) {
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                    response.getWriter().write("Your session has expired");
                }
            }else {
                filterChain.doFilter(request, response);
            }
    }

}
