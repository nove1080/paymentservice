package com.ordertogether.paymentservice.payment.infrastructure.mock;

import com.ordertogether.paymentservice.payment.infrastructure.toss.mapper.TossPaymentMethodMapper;
import com.ordertogether.paymentservice.payment.service.PaymentGatewayClient;
import com.ordertogether.paymentservice.payment.service.command.PGConfirmCommand;
import com.ordertogether.paymentservice.payment.service.result.PGConfirmResult;
import com.ordertogether.paymentservice.payment.service.result.PGConfirmResult.SuccessExtraInfo;
import java.time.LocalDateTime;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
@Profile({"local", "test"})
public class MockTossPaymentClient implements PaymentGatewayClient {

    @Override
    public PGConfirmResult confirmPayment(PGConfirmCommand request) {
        log.info("mock toss payment client is called.");

        applyRandomDelay(100, 500);

        return PGConfirmResult.success(
            request.paymentKey(),
            request.orderId(),
            SuccessExtraInfo.builder()
                .paymentMethod(TossPaymentMethodMapper.mapToPaymentMethod("CARD"))
                .amount(request.amount())
                .approvedAt(LocalDateTime.now())
                .build()
        );
    }

    /**
     * 랜덤 지연을 적용합니다. 평균값을 기준으로 정규분포를 따르는 지연 시간을 생성합니다.
     * @param minMs
     * @param maxMs
     */
    private void applyRandomDelay(int minMs, int maxMs) {
        try {
            double mean = (minMs + maxMs) / 2.0;
            double standardDeviation = (maxMs - mean) / 3.0;

            Random random = new Random();
            double gaussianValue = (random.nextGaussian() * standardDeviation) + mean;

            long finalDelay = Math.round(Math.clamp(gaussianValue, minMs, maxMs));

            log.info("Generated Normal Distribution Delay: {}ms", finalDelay);
            Thread.sleep(finalDelay);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Mock delay interrupted", e);
        }
    }
}
