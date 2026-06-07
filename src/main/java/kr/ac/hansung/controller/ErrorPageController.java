package kr.ac.hansung.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class ErrorPageController {

    /**
     * 권한이 없는 사용자가 ADMIN 전용 URL(예: /products/{id}/edit)에 접근하면
     * Spring Security 의 accessDeniedPage 설정에 의해 이 핸들러로 포워딩되어
     * HTTP 403(Forbidden) 상태와 함께 안내 페이지를 보여준다.
     */
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @GetMapping("/access-denied")
    public String accessDenied() {
        return "error/access-denied";
    }
}
