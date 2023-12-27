package com.bt1.qltv1.filter;

import com.bt1.qltv1.exception.TokenException;
import com.bt1.qltv1.service.impl.UserDetailsServiceImpl;
import com.bt1.qltv1.service.SessionService;
import com.bt1.qltv1.util.JwtUtil;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
@Log4j
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;

    private final SessionService sessionService;

    private final UserDetailsServiceImpl userDetailsService;

    //set cookie
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");

        String jwt = null;
        String email = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                email = jwtUtil.extractUsername(jwt);
            }catch (JwtException jwtException){
                throw new TokenException(jwtException.getMessage(),"user.token.invalid");

            }
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            try {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(email);

                //check token is belongto user
                //check token is expired
                jwtUtil.validateToken(jwt,userDetails);

                //find session from refresh token
                String jti = jwtUtil.extractJTi(jwt);
                if (sessionService.checkIsBlockSession(jti)) {
                    throw new TokenException("Your token can not use any more.","user.session.block");
                }

                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }catch (JwtException jwtException){
                throw new TokenException(jwtException.getMessage(),"user.token.invalid");

            }
        }
        filterChain.doFilter(request, response);
    }

}
