package com.ordertogether.paymentservice.payment.infrastructure.toss;

import com.ordertogether.paymentservice.payment.domain.PaymentFailure;
import com.ordertogether.paymentservice.payment.infrastructure.toss.mapper.TossPaymentMethodMapper;
import com.ordertogether.paymentservice.payment.infrastructure.toss.request.TossPaymentConfirmRequest;
import com.ordertogether.paymentservice.payment.infrastructure.toss.response.TossPaymentConfirmResponse;
import com.ordertogether.paymentservice.payment.infrastructure.toss.response.TossPaymentFailureResponse;
import com.ordertogether.paymentservice.payment.service.PaymentGatewayClient;
import com.ordertogether.paymentservice.payment.service.command.PGConfirmCommand;
import com.ordertogether.paymentservice.payment.service.result.PGConfirmResult;
import com.ordertogether.paymentservice.payment.service.result.PGConfirmResult.SuccessExtraInfo;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@Slf4j
@RequiredArgsConstructor
public class TossPaymentWebClient implements PaymentGatewayClient {

    private final RestClient tossRestClient;

    private static final String CONFIRM_PAYMENT_URL = "/v1/payments/confirm";
    private static final String IDEMPOTENCY_KEY_HEADER = "Idempotency-Key";

    @Override
    public PGConfirmResult confirmPayment(PGConfirmCommand request) {
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
                    TossPaymentFailureResponse failureResponse = res.bodyTo(TossPaymentFailureResponse.class);
                    return PGConfirmResult.builder()
                        .paymentKey(request.paymentKey())
                        .orderId(request.orderId())
                        .status(TossPaymentErrorCode.from(failureResponse.code()).toPaymentStatus())
                        .failureExtraInfo(PaymentFailure.builder()
                            .code(failureResponse.code())
                            .message(failureResponse.message())
                            .build())
                        .build();
                } else {
                    TossPaymentConfirmResponse tossResponse = res.bodyTo(TossPaymentConfirmResponse.class);
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
}
