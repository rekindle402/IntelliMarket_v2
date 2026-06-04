# 예외 처리 전략

## 에러 응답 필드 구조

| 필드 | 타입 | 설명 |
|------|------|------|
| error | String | 에러명 |
| code | String | 도메인 식별 코드 (예: `MEM001`) |
| message | String | 사용자 메시지 |
| status | int | HTTP 상태코드 |

---

## 예외 클래스 계층

```
ErrorCode (interface)        — getStatus(), getCode(), getMessage()
    ↑ implements
MemberErrorCode (enum)
SellerErrorCode (enum)
StoreErrorCode  (enum)
...

BusinessException            — ErrorCode 필드 하나만 보유, 도메인별 Exception 클래스 없음

GlobalExceptionHandler       — @ExceptionHandler(BusinessException.class) 단일 핸들러로 처리
```

---

## 에러 코드 네이밍 컨벤션

- **접두사 규칙:** 도메인명 앞 3자리 대문자. 충돌 시 의미 있는 약어로 수동 지정
- **형식:** 접두사 + 3자리 숫자 (예: `MEM001`)
- **enum 상수명:** 대문자 스네이크 (예: `MEMBER_NOT_FOUND`)
- **enum 클래스명:** 도메인 + ErrorCode (예: `MemberErrorCode`)

---

## 현재 등록된 접두사 목록

| 도메인 | 접두사 |
|--------|--------|
| Member | `MEM` |
| Seller | `SEL` |
| Store | `STO` |
| Product | `PRD` |
| Order | `ORD` |
| Payment | `PAY` |

> 새 도메인 추가 시 이 테이블에 먼저 등록 후 enum 생성

---

## GlobalExceptionHandler 처리 대상

| 예외 | 상태코드 | 처리 방식 |
|------|----------|-----------|
| `BusinessException` | 도메인별 상이 | ErrorCode에서 status, code, message 추출 |
| `MethodArgumentNotValidException` | 400 | 첫 번째 에러 메시지만 message에 담아 반환 |
| `Exception` | 500 | 고정 메시지 반환 + log.error() 로깅 |

---

## 패키지 위치

```
com.intellimarket.common.exception
```

---

## 에러 로깅 전략

- 500 예외 발생 시 `log.error()`로 서버 로그에 기록
- 클라이언트 응답 메시지: `"예상치 못한 에러가 발생했습니다. 관리자에게 문의해주세요"`
- 내부 에러 상세 정보는 클라이언트에 노출하지 않음
