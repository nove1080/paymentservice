package com.ordertogether.paymentservice.payment.infrastructure.toss;

import com.ordertogether.paymentservice.exception.PaymentException;
import com.ordertogether.paymentservice.exception.PaymentGatewayConfirmationException;
import com.ordertogether.paymentservice.payment.domain.PaymentStatus;
import com.ordertogether.paymentservice.payment.infrastructure.toss.error.TossPaymentErrorCode;
import com.ordertogether.paymentservice.payment.infrastructure.toss.mapper.TossPaymentMethodMapper;
import com.ordertogether.paymentservice.payment.infrastructure.toss.request.TossPaymentConfirmRequest;
import com.ordertogether.paymentservice.payment.infrastructure.toss.response.TossPaymentConfirmResponse;
import com.ordertogether.paymentservice.payment.infrastructure.toss.response.TossPaymentFailureResponse;
import com.ordertogether.paymentservice.payment.service.PaymentGatewayClient;
import com.ordertogether.paymentservice.payment.service.command.PGConfirmCommand;
import com.ordertogether.paymentservice.payment.service.result.PGConfirmResult;
import com.ordertogether.paymentservice.payment.service.result.PGConfirmResult.SuccessExtraInfo;
import java.time.ZoneId;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.retry.RetryException;
import org.springframework.core.retry.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@Slf4j
@RequiredArgsConstructor
public class TossPaymentClient implements PaymentGatewayClient {

    private static final String CONFIRM_PAYMENT_URL = "/v1/payments/confirm";
    private static final String IDEMPOTENCY_KEY_HEADER = "Idempotency-Key";

    private final RestClient tossRestClient;
    private final RetryTemplate paymentGatewayRetryTemplate;

    @Override
    public PGConfirmResult confirmPayment(PGConfirmCommand request) {
        try {
            return paymentGatewayRetryTemplate.execute(() -> executeConfirmPayment(request));
        } catch (RetryException e) {
            Throwable lastException = e.getLastException();
            if (lastException instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }
            throw new PaymentException("결제 승인 과정 중 알 수 없는 오류가 발생했습니다. %s".formatted(e.getMessage()), e);
        }
    }

    private PGConfirmResult executeConfirmPayment(PGConfirmCommand request) throws PaymentGatewayConfirmationException {
        return tossRestClient.post()
            .uri(CONFIRM_PAYMENT_URL)
            .header(IDEMPOTENCY_KEY_HEADER, request.orderId().value())
            .body(TossPaymentConfirmRequest.builder()
                .orderId(request.orderId().value())
                .paymentKey(request.paymentKey())
                .amount(request.amount())
                .build())
            .exchange((req, res) -> {
                if (res.getStatusCode().isError()) {
                    TossPaymentFailureResponse failureResponse = Optional.ofNullable(res.bodyTo(TossPaymentFailureResponse.class))
                        .orElseThrow(() -> unknownConfirmationException(request));
                    TossPaymentErrorCode errorCode = TossPaymentErrorCode.from(failureResponse.code());

                    log.info("토스페이먼츠 결제 승인 실패. response: {}, errorCode: {}", failureResponse, errorCode);

                    throw new PaymentGatewayConfirmationException(
                        request.paymentKey(),
                        request.orderId(),
                        errorCode.toPaymentStatus(),
                        errorCode.name(),
                        errorCode.getMessage()
                    );
                } else {
                    TossPaymentConfirmResponse tossResponse = Optional.ofNullable(res.bodyTo(TossPaymentConfirmResponse.class))
                        .orElseThrow(() -> unknownConfirmationException(request));

                    log.info("토스페이먼츠 결제 승인 성공. response: {}", tossResponse);

                    return PGConfirmResult.success(
                        request.paymentKey(),
                        request.orderId(),
                        SuccessExtraInfo.builder()
                            .paymentMethod(TossPaymentMethodMapper.mapToPaymentMethod(tossResponse.method()))
                            .amount(tossResponse.totalAmount())
                            .approvedAt(tossResponse.approvedAt()
                                .atZoneSameInstant(ZoneId.of("UTC"))
                                .toLocalDateTime())
                            .build()
                    );
                }
            });
    }

    private static PaymentGatewayConfirmationException unknownConfirmationException(PGConfirmCommand request) {
        return new PaymentGatewayConfirmationException(
            request.paymentKey(),
            request.orderId(),
            PaymentStatus.UNKNOWN,
            TossPaymentErrorCode.UNKNOWN.name(),
            TossPaymentErrorCode.UNKNOWN.getMessage()
        );
    }
}
