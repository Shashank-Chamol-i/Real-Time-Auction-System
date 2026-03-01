package com.example.realTimeAuctionSystem.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    @Value("${jwt.expiration}")
    private long jwtExpiration;


    public String generateToken(CustomUserDetails userDetails){
        return generateToken(new HashMap<>(),userDetails);
    }

    public String generateToken(HashMap<String,Object> extraClaims,CustomUserDetails userDetails){

        return buildToken(extraClaims,userDetails,jwtExpiration);
    }

    public String buildToken(HashMap<String , Object> extraClaims,CustomUserDetails userDetails,long jwtExpiration){
        return Jwts.builder()
                .setSubject(userDetails.getUserId())
                .claim("email",userDetails.getEmail())
                .claim("username",userDetails.getUsername())
                .claim("role",userDetails.getAuthorities().iterator().next().getAuthority())
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plusSeconds(jwtExpiration)))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token , CustomUserDetails userDetails){
    final String userId =extractUserId(token);
    return (userId.equals(userDetails.getUserId()) && !isTokenExpired(token));
    }

    public boolean isTokenExpired(String token){
    return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token){
    return extractClaims(token,Claims::getExpiration);
    }

    public String extractUserId(String token){
        return extractClaims(token, Claims::getSubject);
    }

    public <T> T extractClaims(String token , Function<Claims,T> claimsTFunction){
        final Claims claims = extractAllClaims(token);
        return claimsTFunction.apply(claims);
    }

    public Claims extractAllClaims(String token){
    return Jwts.parserBuilder()
            .setSigningKey(getSignInKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    private Key getSignInKey(){
    byte[] keyBytes = Decoders.BASE64URL.decode(SECRET_KEY);
    return Keys.hmacShaKeyFor(keyBytes);
    }



}
