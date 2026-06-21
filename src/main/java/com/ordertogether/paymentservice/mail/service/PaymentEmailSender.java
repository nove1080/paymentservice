package com.ordertogether.paymentservice.mail.service;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class PaymentEmailSender {

    private final JavaMailSender mailSender;

    @Value("${app.admin-email}")
    private String adminEmail;

    @Async
    public void sendDltAlertEmail(String originalTopic, String rawMessage, String exceptionMessage) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(adminEmail);
            message.setSubject("[DLT 에러] 결제 메시지 처리 최종 실패");

            String text = """
                    결제 후속 처리 중 에러가 발생하여 메시지가 DLT로 이동되었습니다.
                    발생 시간: %s
                    원본 토픽: %s
                    에러 원인: %s
                    원본 데이터: %s
                    확인 후 수동 재처리 바랍니다.
                """.formatted(LocalDateTime.now(), originalTopic, exceptionMessage, rawMessage);

            message.setText(text);
            mailSender.send(message);
            log.info("DLT Alert email 전송완료 수신자 {}", adminEmail);

        } catch (Exception e) {
            log.warn("DLT alert email 전송 실패", e);
        }
    }
}
