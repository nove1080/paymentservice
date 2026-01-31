package com.ordertogether.paymentservice.payment.infrastructure.toss.response;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

public record TossPaymentConfirmResponse(
    String mId,
    String lastTransactionKey,
    String paymentKey,
    String orderId,
    String orderName,
    Long taxExemptionAmount,
    String status,
    OffsetDateTime requestedAt,
    OffsetDateTime approvedAt,
    Boolean useEscrow,
    Boolean cultureExpense,

    Card card,
    VirtualAccount virtualAccount,
    Transfer transfer,
    MobilePhone mobilePhone,
    GiftCertificate giftCertificate,
    CashReceipt cashReceipt,
    List<CashReceiptHistory> cashReceipts,
    Discount discount,
    List<Cancel> cancels,
    String secret,

    String type,
    EasyPay easyPay,
    String country,
    Failure failure,
    Boolean isPartialCancelable,

    Receipt receipt,
    Checkout checkout,

    String currency,
    Long totalAmount,
    Long balanceAmount,
    Long suppliedAmount,
    Long vat,
    Long taxFreeAmount,

    Map<String, String> metadata,
    String method,
    String version
) {

    public record Card(
        String issuerCode,
        String acquirerCode,
        String number,
        Integer installmentPlanMonths,
        Boolean isInterestFree,
        String interestPayer,
        String approveNo,
        Boolean useCardPoint,
        String cardType,
        String ownerType,
        String acquireStatus,
        Long amount
    ) { }

    public record EasyPay(
        String provider,
        Long amount,
        Long discountAmount
    ) { }

    public record Receipt(
        String url
    ) { }

    public record Checkout(
        String url
    ) { }

    public record Failure(
        String code,
        String message
    ) { }

    public record Cancel(
        Long cancelAmount,
        String cancelReason,
        Long taxFreeAmount,
        Long taxExemptionAmount,
        Long refundableAmount,
        Long cardDiscountAmount,
        Long transferDiscountAmount,
        Long easyPayDiscountAmount,
        OffsetDateTime canceledAt,
        String transactionKey,
        String receiptKey,
        String cancelStatus,
        String cancelRequestId
    ) { }

    public record VirtualAccount(
        String accountType,
        String accountNumber,
        String bankCode,
        String customerName,
        String depositorName,
        OffsetDateTime dueDate,
        String refundStatus,
        Boolean expired,
        String settlementStatus,
        RefundReceiveAccount refundReceiveAccount
    ) { }

    public record RefundReceiveAccount(
        String bankCode,
        String accountNumber,
        String holderName
    ) { }

    public record Transfer(
        String bankCode,
        String settlementStatus
    ) { }

    public record MobilePhone(
        String customerMobilePhone,
        String settlementStatus,
        String receiptUrl
    ) { }

    public record GiftCertificate(
        String approveNo,
        String settlementStatus
    ) { }

    public record CashReceipt(
        String type,
        String receiptKey,
        String issueNumber,
        String receiptUrl,
        Long amount,
        Long taxFreeAmount
    ) { }

    public record CashReceiptHistory(
        String receiptKey,
        String orderId,
        String orderName,
        String type,
        String issueNumber,
        String receiptUrl,
        String businessNumber,
        String transactionType,
        Long amount,
        Long taxFreeAmount,
        String issueStatus,
        Failure failure,
        String customerIdentityNumber,
        OffsetDateTime requestedAt
    ) { }

    /** 문서에 존재하지만 예시 JSON에는 null로만 등장하는 경우가 많아 최소 형태로 둠 */
    public record Discount(
        Long amount
    ) { }
}
