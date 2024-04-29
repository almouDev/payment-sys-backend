package com.almou.payment.security;

import com.almou.payment.beans.Client;
import com.almou.payment.metier.ServiceMetier;
import com.almou.payment.utils.JwtUtilities;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private AuthenticationManager authenticationManager;
    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String pw=request.getParameter("password");
        String username=request.getParameter("username");
        UsernamePasswordAuthenticationToken token=new UsernamePasswordAuthenticationToken(username,pw);
        return authenticationManager.authenticate(token);
    }
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        User user= (User) authResult.getPrincipal();
        Collection<String> authorities=user.getAuthorities().stream().map(ga->ga.getAuthority()).collect(Collectors.toList());
        String jwtAccessToken= JwtUtilities
                .JwtTokenCreation(request.getRequestURL().toString(), 15*60*1000,authorities,user.getUsername())
                .sign(JwtUtilities.getAlgorithm());
        ObjectMapper mapper=new ObjectMapper();
        Map<String,String> jwtToken=new HashMap<>();
        jwtToken.put("access_token",jwtAccessToken);
        response.setContentType("Application/json");
        mapper.writeValue(response.getOutputStream(),jwtToken);
    }
}
