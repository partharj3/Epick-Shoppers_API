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
import com.shoppers.ekart.entity.RefreshToken;
import com.shoppers.ekart.exception.UserNotLoggedInException;
import com.shoppers.ekart.repository.AccessTokenRepository;
import com.shoppers.ekart.repository.RefreshTokenRepository;

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
		String at = null,rt = null;
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("at"))
					at = cookie.getValue();
				if (cookie.getName().equals("rt"))
					rt = cookie.getValue();
			}
		
			String username=null;
			System.out.println("from jwt Filter 1: "+at+" , "+rt);
			if(at!=null && rt!=null) {
				
				Optional<AccessToken> accessTokenObj =accessTokenRepo.findByTokenAndIsBlocked(at,false);	
				System.out.println("from jwt Filter 2: "+at+" , "+rt);
				if(accessTokenObj == null) throw new UserNotLoggedInException("User not logged in");
			
				else {
					log.info("Authenticating the token..");
					System.out.println("-1-");
					username = jwtService.extractUsername(at);
					System.out.println("-2-");
					UserDetails userDetails = userDetailService.loadUserByUsername(username);
					System.out.println("-3-");
					UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, null, 
																	userDetails.getAuthorities());
					System.out.println("-4-");
					token.setDetails(new WebAuthenticationDetails(request));
					System.out.println("-5-");
					SecurityContextHolder.getContext().setAuthentication(token);
					log.info("Token Authenticated Successfully !!");
				}
			}
		}
		filterChain.doFilter(request, response);
	}
}
