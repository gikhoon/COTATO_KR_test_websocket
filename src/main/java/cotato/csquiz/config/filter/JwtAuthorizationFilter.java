package cotato.csquiz.config.filter;

import cotato.csquiz.config.jwt.JwtTokenProvider;
import cotato.csquiz.domain.entity.Member;
import cotato.csquiz.exception.FilterAuthenticationException;
import cotato.csquiz.repository.MemberRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private static final String AUTH_PATH = "/v1/api/auth";
    private static final String LOGIN_PATH = "/login";
    private static final String SWAGGER_PATH = "/swagger-ui";
    private static final String SWAGGER_PATH_3 = "/v3/api-docs";
    private static final String WS = "/websocket/csquiz";

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");
        String accessToken = jwtTokenProvider.resolveAccessToken(authorizationHeader);
        jwtTokenProvider.validateAccessToken(accessToken);

        setAuthentication(accessToken);
        filterChain.doFilter(request, response);
    }

    private void setAuthentication(String accessToken) {
        Long memberId = jwtTokenProvider.getMemberId(accessToken);
        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new FilterAuthenticationException("해당 회원이 존재하지 않습니다."));
        String role = jwtTokenProvider.getRole(accessToken);
        log.info("[인증 필터 인증 진행, {}]", memberId);
        log.info("Member Role: {}", role);
        jwtTokenProvider.checkMemberExist(memberId);

        Authentication authenticationToken = new UsernamePasswordAuthenticationToken(findMember.getEmail(), "",
                List.of(new SimpleGrantedAuthority(role)));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        log.info("요청 경로: {}", path);
        log.info("요청 메서드: {}", request.getMethod());
        return path.startsWith(AUTH_PATH) || path.startsWith(LOGIN_PATH)
                || path.startsWith(SWAGGER_PATH) || path.startsWith(SWAGGER_PATH_3) || path.startsWith(WS);
    }
}