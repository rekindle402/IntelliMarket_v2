# IntelliMarket — 프로젝트 명세 (CLAUDE.md 프로젝트 섹션)

---

## 프로젝트 개요

| 항목 | 내용 |
|------|------|
| 프로젝트명 | IntelliMarket |
| 유형 | 개인 포트폴리오 프로젝트 |
| 목적 | 다수 판매자 기반 오픈마켓 백엔드 설계 및 구현 |
| 핵심 메시지 | 기능량이 아닌 도메인 설계 품질과 API 완성도 |

---

## 기술 스택

| 구분 | 기술 |
|------|------|
| Backend | Spring Boot, Spring Data JPA, Spring Security |
| Database | MySQL |
| Cache | Redis (2차 고도화 시 도입) |
| Frontend | Vue |
| Infra | Docker Compose, Nginx, Mini PC, DDNS |
| CI/CD | GitHub Actions (필요 시 도입) |
| 버전 관리 | Git / GitHub, Git-flow 전략 |

---

## 도메인 구조

```
회원(Member) ─── 판매자(Seller) ─── 스토어(Store) ─── 상품(Product)
                                                          │
회원(Member) ─── 주문(Order) ──────────────────── 주문상품(OrderItem)
                    │
               결제(Payment)
```

### 권한 구분
- `ROLE_USER` : 일반 구매 회원
- `ROLE_SELLER` : 판매자 (관리자 승인 후 활성화)
- `ROLE_ADMIN` : 플랫폼 관리자

---

## 요구사항 명세

> 세부 구현 방식은 작업 시점에 결정한다.
> 이 명세는 도메인별 엔드포인트, HTTP 메서드, 기능 단위만 정의한다.

---

### AUTH — 인증/인가

| Method | Endpoint | 기능 | 권한 |
|--------|----------|------|------|
| POST | /api/auth/signup | 회원가입 | 비로그인 |
| POST | /api/auth/login | 로그인 (세션 발급) | 비로그인 |
| POST | /api/auth/logout | 로그아웃 | 로그인 |
| GET | /api/auth/me | 내 정보 조회 | 로그인 |

---

### MEMBER — 회원

| Method | Endpoint | 기능 | 권한 |
|--------|----------|------|------|
| GET | /api/members/me | 내 프로필 조회 | USER |
| PUT | /api/members/me | 내 프로필 수정 | USER |
| DELETE | /api/members/me | 회원 탈퇴 | USER |
| GET | /api/admin/members | 전체 회원 목록 | ADMIN |
| GET | /api/admin/members/{memberId} | 회원 상세 조회 | ADMIN |
| PATCH | /api/admin/members/{memberId}/status | 회원 상태 변경 (정지 등) | ADMIN |

---

### SELLER — 판매자 신청/승인

| Method | Endpoint | 기능 | 권한 |
|--------|----------|------|------|
| POST | /api/sellers/apply | 판매자 신청 | USER |
| GET | /api/sellers/me | 내 판매자 정보 조회 | SELLER |
| GET | /api/admin/sellers | 판매자 신청 목록 | ADMIN |
| GET | /api/admin/sellers/{sellerId} | 판매자 신청 상세 | ADMIN |
| PATCH | /api/admin/sellers/{sellerId}/approve | 판매자 승인 | ADMIN |
| PATCH | /api/admin/sellers/{sellerId}/reject | 판매자 거절 | ADMIN |

---

### STORE — 스토어

| Method | Endpoint | 기능 | 권한 |
|--------|----------|------|------|
| POST | /api/stores | 스토어 생성 | SELLER |
| GET | /api/stores/me | 내 스토어 조회 | SELLER |
| PUT | /api/stores/me | 내 스토어 수정 | SELLER |
| GET | /api/stores | 스토어 목록 조회 (공개) | 비로그인 |
| GET | /api/stores/{storeId} | 스토어 상세 조회 (공개) | 비로그인 |
| PATCH | /api/admin/stores/{storeId}/status | 스토어 상태 변경 | ADMIN |

---

### PRODUCT — 상품

| Method | Endpoint | 기능 | 권한 |
|--------|----------|------|------|
| POST | /api/products | 상품 등록 | SELLER |
| GET | /api/products/me | 내 상품 목록 조회 | SELLER |
| PUT | /api/products/{productId} | 상품 수정 | SELLER (본인) |
| DELETE | /api/products/{productId} | 상품 삭제 | SELLER (본인) |
| GET | /api/products | 상품 목록 조회 (공개, 검색/필터) | 비로그인 |
| GET | /api/products/{productId} | 상품 상세 조회 (공개) | 비로그인 |
| GET | /api/stores/{storeId}/products | 특정 스토어 상품 목록 | 비로그인 |

---

### ORDER — 주문

| Method | Endpoint | 기능 | 권한 |
|--------|----------|------|------|
| POST | /api/orders | 주문 생성 | USER |
| GET | /api/orders | 내 주문 목록 조회 | USER |
| GET | /api/orders/{orderId} | 주문 상세 조회 | USER (본인) |
| PATCH | /api/orders/{orderId}/cancel | 주문 취소 | USER (본인) |
| GET | /api/sellers/orders | 내 스토어 주문 목록 | SELLER |
| PATCH | /api/sellers/orders/{orderId}/status | 주문 상태 변경 (배송 등) | SELLER |
| GET | /api/admin/orders | 전체 주문 목록 | ADMIN |

---

### PAYMENT — 결제

| Method | Endpoint | 기능 | 권한 |
|--------|----------|------|------|
| POST | /api/payments/request | 결제 요청 | USER |
| POST | /api/payments/success | 결제 성공 콜백 처리 | USER |
| POST | /api/payments/fail | 결제 실패 콜백 처리 | USER |
| POST | /api/payments/{paymentId}/cancel | 결제 취소 요청 | USER (본인) |
| GET | /api/payments/{paymentId} | 결제 내역 조회 | USER (본인) |

> 결제 API 연동 전 주문/결제 상태 흐름을 먼저 설계할 것.
> 중복 요청 방지, 주문-결제 상태 정합성 검증 필수.

---

### STATISTICS — 통계 (2차)

| Method | Endpoint | 기능 | 권한 |
|--------|----------|------|------|
| GET | /api/sellers/stats/sales | 판매자 매출 통계 | SELLER |
| GET | /api/sellers/stats/products | 판매자 상품별 판매량 | SELLER |
| GET | /api/admin/stats/overview | 플랫폼 전체 통계 | ADMIN |

---

## 구현 우선순위

### 1차 MVP
1. 인증/인가 (회원가입, 로그인, 권한)
2. 판매자 신청 및 관리자 승인
3. 스토어 생성 및 관리
4. 상품 CRUD
5. 사용자/관리자 기본 화면
6. Docker + 배포 환경

### 2차 고도화
1. 주문 생성 및 상태 관리
2. 결제 API 연동 (성공/실패/취소/정합성)
3. 통계 기능
4. 테스트 코드 (단위 5~8개, 통합 3~5개)
5. README / API 문서 정리

---

## 설계 원칙 (AI에게 위임하지 않는 영역)

- ERD 설계 및 도메인 관계
- 권한 구조 및 접근 제어 기준
- 주문/결제 상태 흐름 및 트랜잭션 경계
- 예외 처리 기준 및 에러 응답 구조
- 테스트 대상 선정 및 테스트 의도

---
