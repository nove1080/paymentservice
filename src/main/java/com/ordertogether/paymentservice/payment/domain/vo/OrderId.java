package com.ordertogether.paymentservice.payment.domain.vo;

import jakarta.persistence.Embeddable;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * <h1>주문 식별자</h1>
 *
 * <p>
 * 주문한 결제를 식별하기 위한 고유 식별자입니다.
 * 각 주문마다 충분히 무작위한 값을 사용해야 하며,
 * 결제 데이터 관리를 위해 반드시 저장됩니다.
 * </p>
 * <br>
 *
 * <h2>제약 조건</h2>
 * <ul>
 *   <li><b>길이</b>: 6자 이상 64자 이하</li>
 *   <li><b>문자 구성</b>: 영문 대소문자(<code>A-Z</code>, <code>a-z</code>),
 *       숫자(<code>0-9</code>), 특수문자 <code>-</code>, <code>_</code>로만 구성</li>
 * </ul>
 *
 * @param value 주문 식별자 문자열
 */
@Embeddable
public record OrderId(
    String value
) {
    private static final int MIN_LENGTH = 6;
    private static final int MAX_LENGTH = 64;
    // 영문 대소문자, 숫자, -, _ 만 허용
    private static final Pattern FORMAT = Pattern.compile("^[A-Za-z0-9_-]+$");

    public OrderId {
        Objects.requireNonNull(value, "OrderId 는 null 일 수 없습니다.");
        validateLength(value);
        validateFormat(value);
    }

    public static OrderId from(String seed) {
        String value = UUID.nameUUIDFromBytes(seed.getBytes(StandardCharsets.UTF_8)).toString();
        return new OrderId(value);
    }

    private void validateLength(String value) {
        int length = value.length();
        if (length < MIN_LENGTH || length > MAX_LENGTH) {
            throw new IllegalArgumentException(
                "OrderId 는 %d자 이상 %d자 이하여야 합니다. [값: %s]".formatted(MIN_LENGTH, MAX_LENGTH, value));
        }
    }

    private void validateFormat(String value) {
        if (!FORMAT.matcher(value).matches()) {
            throw new IllegalArgumentException(
                "OrderId 는 영문 대소문자, 숫자, '-', '_' 문자로만 구성되어야 합니다. [값: %s]".formatted(value));
        }
    }

}
