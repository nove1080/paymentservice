package com.ordertogether.paymentservice.payment.repository;

import com.ordertogether.paymentservice.payment.domain.PaymentEvent;
import com.ordertogether.paymentservice.payment.domain.PaymentOrderHistory;
import com.ordertogether.paymentservice.payment.domain.vo.OrderId;
import java.util.List;

public interface PaymentRepository {

    PaymentEvent selectPaymentEvent(OrderId orderId);

    void insertPaymentEvent(PaymentEvent paymentEvent);

    void insertPaymentHistory(PaymentOrderHistory history);

    List<PaymentOrderHistory> selectPaymentHistories(OrderId orderId);

    /**
     * 복구 가능한 결제 이벤트를 조회합니다.
     * @param failedCountThreshold 최대 허용 실패 횟수
     * @param afterSeconds 최종 업데이트 이후 경과 시간
     * @return 복구 가능한 결제 이벤트 목록
     */
    List<PaymentEvent> selectRecoverablePaymentEvents(Integer failedCountThreshold, Integer afterSeconds);
}
