package com.ll.rsv.domain.member.member.controller;

import com.ll.rsv.global.rq.Rq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/member")
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Tag(name = "MemberController", description = "스웨거 로그인 용 로그인 폼, 카카오 로그인 기능 등 다양한 기능 제공")
public class MemberController {
    private final Rq rq;

    @GetMapping("/login")
    @ResponseBody
    @Operation(summary = "운영모드에서 스웨거 로그인시에 ADMIN 권한을 필요로 함, 그것을 위한 로그인 폼 보여줌")
    public String login() {
        return """
                <form method="POST">
                    <div>
                        <label>아이디</label>
                        <input type="text" name="username">
                    </div>
                    <div>
                        <label>비밀번호</label>
                        <input type="password" name="password">
                    </div>
                    <div>
                        <button type="submit">로그인</button>
                    </div>
                </form>
                """;
    }

    @GetMapping("/socialLogin/{providerTypeCode}")
    @Operation(summary = "프론트에서 소셜 로그인 후 원래 페이지로 돌아가기 위한 용도로 사용됨, redirectUrl 파라미터를 받아서 쿠키에 저장함")
    public String socialLogin(String redirectUrl, @PathVariable String providerTypeCode) {
        if (rq.isFrontUrl(redirectUrl)) {
            rq.setCookie("redirectUrlAfterSocialLogin", redirectUrl, 60 * 10);
        }

        return "redirect:/oauth2/authorization/" + providerTypeCode;
    }
}
