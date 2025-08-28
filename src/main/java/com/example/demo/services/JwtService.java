package com.example.demo.services;

import com.example.demo.models.UserApp;
import com.example.demo.repositories.UserAppRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

@Service
public class JwtService extends OncePerRequestFilter {
    @Value("${jwt.secret}")
    private static String SECRET;
    @Value("${jwt.secret}")
    public void setSecret(String secret) {
        SECRET = secret;
    }

    private static String COOKIE_NAME= "";
    @Value("${jwt.cookie_name}")
    public void setCookieName(String cookie_name) {
        COOKIE_NAME = cookie_name;
    }
    public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60 * 1000;

    @Autowired
    UserAppRepository userAppRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Si c'est une requête de déconnexion, ne pas traiter le JWT
        if (request.getRequestURI().equals("/logout") || request.getRequestURI().equals("/api/logout")) {
            filterChain.doFilter(request, response);
            return;
        }

        if (request.getCookies() != null) {
            Stream.of(request.getCookies())
                    .filter(cookie -> cookie.getName().equals(COOKIE_NAME))
                    .map(Cookie::getValue)
                    .filter(token -> token != null && !token.trim().isEmpty()) // Vérifier que le token n'est pas vide
                    .forEach(token -> {
                        try {
                            Claims claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();

                            Optional<UserApp> optUserApp = userAppRepository.findByUsername(claims.getSubject());
                            if(optUserApp.isEmpty()){
                                throw new UsernameNotFoundException(claims.getSubject());
                            }
                            UserApp userApp = optUserApp.get();

                            if (validateToken(token, userApp)) {
                                String role = claims.get("role", String.class);
                                Collection<SimpleGrantedAuthority> authorities = Collections.singletonList(
                                        new SimpleGrantedAuthority("ROLE_" + role)
                                );
                                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                        userApp, null, authorities);
                                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                                SecurityContextHolder.getContext().setAuthentication(authToken);
                            }
                        } catch (Exception e) {
                            // Token invalide ou expiré, supprimer le cookie
                            SecurityContextHolder.clearContext();
                            ResponseCookie expiredCookie = ResponseCookie.from(COOKIE_NAME, "")
                                    .httpOnly(true)
                                    .path("/")
                                    .maxAge(0)
                                    .build();
                            response.addHeader(HttpHeaders.SET_COOKIE, expiredCookie.toString());
                        }
                    });
        }

        filterChain.doFilter(request, response);
    }

    // Améliorer la validation du token
    public static Boolean validateToken(String token, UserApp userApp) {
        try {
            Claims claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();

            // Vérifier que le token n'est pas expiré
            if (claims.getExpiration().before(new Date())) {
                return false;
            }

            // Vérifier que l'utilisateur correspond
            if (!claims.getSubject().equals(userApp.getUsername())) {
                return false;
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String generateToken(UserApp userApp) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", userApp.getUsername());
        claims.put("role", userApp.getRole());
        return Jwts.builder().setClaims(claims).setSubject(userApp.getUsername()).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
                .signWith(SignatureAlgorithm.HS256, SECRET).compact();
    }

    public ResponseCookie createAuthenticationToken(UserApp userApp ) throws Exception {
        try {
            final String token = generateToken(userApp);
            return ResponseCookie.from(COOKIE_NAME, token).httpOnly(true)
                    .path("/").build();
        }catch(DisabledException e) {
            throw new Exception();
        }

    }
}
