package com.ordertogether.paymentservice.payment.service;

import com.ordertogether.paymentservice.exception.PaymentRetryExhaustedException;
import com.ordertogether.paymentservice.payment.service.command.PGConfirmCommand;
import com.ordertogether.paymentservice.payment.service.result.PGConfirmResult;

/**
 * 결제 게이트웨이 클라이언트 인터페이스
 */
public interface PaymentGatewayClient {

    /**
     * <h2>결제 승인 요청을 수행합니다</h2>
     * - PG사의 결제 승인 API를 호출하여 결제를 승인합니다.<br>
     * - 승인 결과에 따라 재시도를 수행할 수 있습니다.
     * @param request 결제 승인 요청 정보
     * @return 결제 승인 결과
     * @throws PaymentRetryExhaustedException 재시도 한도를 초과한 경우 발생합니다.
     */
    PGConfirmResult confirmPayment(PGConfirmCommand request) throws PaymentRetryExhaustedException;

}
