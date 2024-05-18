package cotato.csquiz.controller;

import cotato.csquiz.config.jwt.JwtTokenProvider;
import cotato.csquiz.controller.dto.auth.MemberInfoResponse;
import cotato.csquiz.controller.dto.member.CheckPasswordRequest;
import cotato.csquiz.controller.dto.member.MemberMyPageInfoResponse;
import cotato.csquiz.controller.dto.member.UpdatePasswordRequest;
import cotato.csquiz.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/member")
public class MemberController {

    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/info")
    public ResponseEntity<MemberInfoResponse> findMemberInfo(@RequestHeader("Authorization") String authorizationHeader) {
        String accessToken = jwtTokenProvider.getBearer(authorizationHeader);
        Long memberId = jwtTokenProvider.getMemberId(accessToken);
        return ResponseEntity.ok().body(memberService.findMemberInfo(memberId));
    }

    @PostMapping("/check/password")
    public ResponseEntity<Void> checkPassword(@RequestHeader("Authorization") String authorizationHeader,
                                              @RequestBody @Valid CheckPasswordRequest request) {
        String accessToken = jwtTokenProvider.getBearer(authorizationHeader);
        memberService.checkCorrectPassword(accessToken, request.password());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/update/password")
    public ResponseEntity<Void> updatePassword(@RequestHeader("Authorization") String authorizationHeader,
                                               @RequestBody @Valid UpdatePasswordRequest request) {
        String accessToken = jwtTokenProvider.getBearer(authorizationHeader);
        memberService.updatePassword(accessToken, request.password());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{memberId}/mypage")
    public ResponseEntity<MemberMyPageInfoResponse> findMyPageInfo(@PathVariable("memberId") Long memberId) {
        return ResponseEntity.ok().body(memberService.findMyPageInfo(memberId));
    }
}
