package com.clab.auth.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clab.auth.dto.LoginDto;
import com.clab.common.exception.CustomException;
import com.clab.common.exception.ErrorCode;
import com.clab.common.security.CustomUserDetails;
import com.clab.common.security.jwt.JWTUtil;
import com.clab.member.dao.MemberMapper;
import com.clab.member.dto.MemberDto;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

	private final MemberMapper mapper;
	private final JWTUtil jwtUtil;
	private final AuthenticationManager authenticationManager;

	@Override
	@Transactional
	public Map<String, String> login(LoginDto dto) {
		Authentication authentication;
		try {
			authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword()));
		} catch (BadCredentialsException e) {
			throw new CustomException(ErrorCode.INVALID_CREDENTIALS);
		}
		CustomUserDetails details = (CustomUserDetails) authentication.getPrincipal();
		MemberDto member = details.getMember();
		String accessToken = jwtUtil.createAccessToken(member);
		String refreshToken = jwtUtil.createRefreshToken(member);
		updateRefreshToken(member.getId(), refreshToken);
		Map<String, String> tokens = new HashMap<>();
		tokens.put("accessToken", accessToken);
		tokens.put("refreshToken", refreshToken);
		return tokens;
	}

	@Override
	public String refresh(String refreshToken) {
		Claims claims = jwtUtil.getClaims(refreshToken);
		String email = claims.get("email", String.class);

		MemberDto member = mapper.findByEmail(email);
		if (member == null) {
			throw new UsernameNotFoundException("존재하지 않는 사용자입니다.");
		}

		String storedRefreshToken = mapper.findRefreshTokenByEmail(email);
		if (!refreshToken.equals(storedRefreshToken)) {
			throw new BadCredentialsException("유효하지 않은 Refresh Token입니다.");
		}

		return jwtUtil.createAccessToken(member);
	}

	@Override
	public void updateRefreshToken(int id, String refreshToken) {
		mapper.updateRefreshToken(id, refreshToken);
	}

	@Override
	@Transactional
	public void logout(String refreshToken) {
		Claims claims = jwtUtil.getClaims(refreshToken);
		String email = claims.get("email", String.class);

		MemberDto member = mapper.findByEmail(email);
		if (member == null) {
			throw new UsernameNotFoundException("존재하지 않는 사용자입니다.");
		}

		String storedRefreshToken = mapper.findRefreshTokenByEmail(email);
		if (!refreshToken.equals(storedRefreshToken)) {
			throw new BadCredentialsException("유효하지 않은 Refresh Token입니다.");
		}

		mapper.updateRefreshToken(member.getId(), null);
	}

}
