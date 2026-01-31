package com.ordertogether.paymentservice.exception.handler;

import com.ordertogether.paymentservice.common.web.response.ApiResponse;
import com.ordertogether.paymentservice.exception.InvalidPaymentException;
import com.ordertogether.paymentservice.exception.InvalidPaymentStatusException;
import com.ordertogether.paymentservice.exception.PaymentAlreadyProceedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidPaymentStatusException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidPaymentStatusException(InvalidPaymentStatusException ex) {
        log.info("유효하지 않은 결제 상태 에러 발생 ", ex);
        return ResponseEntity.badRequest().body(ApiResponse.with(HttpStatus.BAD_REQUEST, ex.getMessage()));
    }

    @ExceptionHandler(PaymentAlreadyProceedException.class)
    public ResponseEntity<ApiResponse<Void>> handlePaymentAlreadyProceedException(PaymentAlreadyProceedException ex) {
        log.info("이미 처리된 결제 에러 발생 ", ex);
        return ResponseEntity.badRequest().body(ApiResponse.with(HttpStatus.BAD_REQUEST, ex.getMessage()));
    }

    @ExceptionHandler(InvalidPaymentException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidPaymentException(InvalidPaymentException ex) {
        log.info("결제 관련 에러 발생 ", ex);
        return ResponseEntity.badRequest().body(ApiResponse.with(HttpStatus.BAD_REQUEST, ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception ex) {
        log.error("에러 발생 ", ex);
        return ResponseEntity.internalServerError().body(ApiResponse.with(HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 오류가 발생했습니다."));
    }

}
