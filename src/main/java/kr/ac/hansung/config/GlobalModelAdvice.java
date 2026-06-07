package kr.ac.hansung.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * 모든 컨트롤러의 Model 에 서버 시각(currentTime)을 자동으로 추가하는 전역 어드바이스.
 * 푸터 프래그먼트가 ${currentTime} 으로 초기 렌더링에 사용하며,
 * 이후 클라이언트 측 JavaScript 가 1초마다 실시간 갱신한다.
 * 형식 예: 2026년 06월 07일 (일) 오후 02:34:17
 */
@ControllerAdvice
public class GlobalModelAdvice {

    private static final DateTimeFormatter FMT =
        DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 (E) a hh:mm:ss", Locale.KOREAN);

    @ModelAttribute("currentTime")
    public String currentTime() {
        return LocalDateTime.now().format(FMT);
    }
}
