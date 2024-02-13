package com.shoppers.ekart.security;

import java.io.IOException;
import java.util.Optional;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.shoppers.ekart.entity.AccessToken;
import com.shoppers.ekart.exception.UserNotLoggedInException;
import com.shoppers.ekart.repository.AccessTokenRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@AllArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter{

	private AccessTokenRepository accessTokenRepo;
	private JwtService jwtService;
	private CustomUserDetailService userDetailService;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String at="", rt="";
		Cookie[] cookies = request.getCookies();
		
		for(Cookie cookie : cookies) {
			if(cookie.getName().equals("at")) at=cookie.getValue();
			if(cookie.getName().equals("rt")) rt=cookie.getValue();
		}
		
		String username="";
		if(at!=null && rt!=null) {
			
			Optional<AccessToken> accessTokenObj =accessTokenRepo.findByTokenAndIsBlocked(at,false);
		
			if(accessTokenObj == null) throw new UserNotLoggedInException("User not logged in");
		
			else {
				log.info("Authenticating the token..");
				username = jwtService.extractUsername(at);
				UserDetails userDetails = userDetailService.loadUserByUsername(username);
				
				UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, null, 
																userDetails.getAuthorities());
				
				token.setDetails(new WebAuthenticationDetails(request));
				SecurityContextHolder.getContext().setAuthentication(token);
				log.info("Token Authenticated Successfully !!");
			}
			
		}
		
	}

	
	
}
