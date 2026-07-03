# 트러블슈팅 기록

---

## [TS-001] Hibernate 스키마 검증 실패 — 컬럼 타입 불일치

**발생일:** 2026-06-06

**증상**
```
Schema-validation: wrong column type encountered in column [is_default] in table [member_addresses];
found [tinyint (Types#TINYINT)], but expecting [bit (Types#BOOLEAN)]

Schema-validation: wrong column type encountered in column [birth_year] in table [members];
found [smallint (Types#SMALLINT)], but expecting [integer (Types#INTEGER)]
```

**원인**
- DB 테이블을 DDL 직접 실행으로 생성하면서 `TINYINT`, `SMALLINT` 타입으로 컬럼 생성
- Hibernate는 Java의 `Boolean` → `BIT(1)`, `Integer` → `INT` 타입을 기대
- Flyway 마이그레이션 파일 없이 DB를 먼저 생성한 것이 근본 원인

**해결**
- Flyway `V2__fix_column_types.sql` 마이그레이션 파일 추가
- `TINYINT` → `BIT(1)`, `SMALLINT` → `INT` 타입 변경

**교훈**
- DB 스키마는 Flyway 마이그레이션으로 관리해야 함
- DDL 직접 실행 시 Hibernate 기대 타입과 불일치 발생 가능
- 앞으로 엔티티 수정 → 마이그레이션 파일 추가 흐름 유지

---

## [TS-002] Swagger UI 403 Forbidden

**발생일:** 2026-06-06

**증상**
```
localhost에 대한 액세스가 거부됨
HTTP ERROR 403
```
`http://localhost:8080/swagger-ui.html` 접속 시 403 응답

**원인**
- Spring Security `SecurityConfig`에서 `anyRequest().authenticated()` 설정으로 인해 Swagger UI 경로도 인증 필요로 처리됨

**해결**
`SecurityConfig`에 Swagger 관련 경로 `permitAll()` 추가

```java
.requestMatchers("/swagger-ui/**", "/api-docs/**", "/swagger-ui.html").permitAll()
```

**교훈**
- Spring Security 적용 시 Swagger, actuator 등 개발 도구 경로도 명시적으로 허용해야 함

---

## [TS-003] 로그인 - 표준 인증 방식 적용 과정에서의 시행착오

**발생일:** 2026-06-08

**배경**
- 로그인을 직접 구현(이메일 조회 + `PasswordEncoder.matches()`)하는 방식에서, Spring Security 표준 방식(`UserDetails`/`UserDetailsService` + `AuthenticationManager` + `SecurityContext`)으로 전환

---

### 1) `AuthenticationSuccessHandler`가 호출되지 않음

**증상**
- 로그인 성공 시 `lastLoginAt`을 갱신하기 위해 `AuthenticationSuccessHandler`를 적용하려 했으나 동작하지 않음

**원인**
- `AuthenticationSuccessHandler`는 Spring Security의 **필터 체인 기반 로그인**(`formLogin()`, `UsernamePasswordAuthenticationFilter`)에서 인증 성공 시 프레임워크가 자동으로 호출해주는 콜백
- 이 프로젝트는 REST API 방식으로, Controller에서 직접 `authenticationManager.authenticate()`를 호출하는 구조라 필터 체인을 거치지 않음 → 핸들러가 호출될 지점 자체가 없음

**해결**
- `AuthenticationManager.authenticate()` 성공 시 Spring이 발행하는 `AuthenticationSuccessEvent`를 `@EventListener`로 구독하는 방식으로 전환
- `LoginSuccessListener`를 만들어 인증 흐름과 "로그인 성공 후 부가 작업"의 책임을 분리

**교훈**
- Spring Security의 확장 포인트(`SuccessHandler` 등)는 표준 필터 체인을 전제로 동작하므로, 인증 흐름을 직접 구성한 경우에는 적용 가능 여부를 먼저 확인해야 함
- 필터 체인과 무관하게 동작하는 이벤트 기반(`AuthenticationSuccessEvent`) 확장 포인트가 대안이 될 수 있음

---

### 2) 이벤트 리스너에서 엔티티를 수정해도 DB에 반영되지 않음

**증상**
- `LoginSuccessListener`에서 `CustomUserDetails`로부터 꺼낸 `Member`의 `updateLastLoginAt()`을 호출했지만 `last_login_at` 컬럼이 갱신되지 않음

**원인**
- 해당 `Member`는 `CustomUserDetailsService.loadUserByUsername()`이 조회한 시점의 영속성 컨텍스트(트랜잭션)에 속해 있어, 리스너의 새로운 트랜잭션에서는 **준영속(detached) 상태**
- 준영속 엔티티의 필드를 변경해도 JPA 변경 감지(dirty checking) 대상이 아니므로 DB에 반영되지 않음

**해결**
- 리스너의 트랜잭션 내에서 `memberRepository.findById()`로 엔티티를 다시 조회 (영속 상태로 만듦)
- 영속 상태의 엔티티를 수정하여 변경 감지로 자동 반영되도록 처리

**교훈**
- 트랜잭션 경계를 넘어 전달된 엔티티는 준영속 상태일 수 있으므로, 다른 트랜잭션에서 변경이 필요하면 해당 트랜잭션 내에서 다시 조회해야 함

---

## [TS-004] DTO 검증 실패(`MethodArgumentNotValidException`) 처리 시행착오

**발생일:** 2026-06-08

**배경**
- 회원가입 요청에서 비밀번호 형식이 잘못된 경우(`@Pattern` 검증 실패) 응답이 우리 `ErrorResponse` 형식으로 내려오지 않는 문제를 발견
- `exception-spec.md`에는 `MethodArgumentNotValidException`을 `GlobalExceptionHandler`가 처리하도록 정의되어 있었으나 실제 구현이 누락된 상태였음

**증상 및 시행착오**

1. **`@ExceptionHandler` 어노테이션 값 오기재**
   - `MethodArgumentNotValidException`을 처리하는 메서드에 `@ExceptionHandler(BusinessException.class)`를 그대로 복붙하여 어노테이션 값과 파라미터 타입이 불일치
   - `BusinessException.class`에 두 개의 핸들러 메서드가 매핑되어 `Ambiguous @ExceptionHandler method` 예외로 서버가 정상 기동되지 않음 → 그 사이 클라이언트가 받은 `403` 응답은 수정 전(또는 기동 실패 상태의) 서버 인스턴스의 응답이었음
   - `@ExceptionHandler(MethodArgumentNotValidException.class)`로 수정하여 해결

2. **에러 코드를 `AuthErrorCode`에 두려고 했던 점**
   - 처음에는 "회원가입 중 발생했으니" `AuthErrorCode`에 추가하려 했으나, `MethodArgumentNotValidException`은 `@Valid`가 적용된 모든 도메인의 요청 DTO에서 공통으로 발생하는 예외임을 인지
   - 도메인에 종속되지 않는 별도의 `CommonErrorCode`(접두사 `COM`)를 만들어 `INVALID_INPUT`으로 분류

3. **고정 메시지 vs 동적 메시지 혼동**
   - `ErrorCode`의 `message`는 "검증 실패"라는 분류상 고정 메시지이지만, 실제로 클라이언트에게 보여줘야 할 메시지는 `@Pattern(message = "...")` 등 필드별로 다르게 정의된 동적 메시지
   - `ErrorResponse.of(ErrorCode)`(고정 메시지) 대신, `ErrorResponse.of(ErrorCode, String message)` 오버로드를 추가하여 "분류 정보(`error`/`code`/`status`)는 `ErrorCode`에서, 실제 메시지는 `BindingResult.getFieldErrors().get(0).getDefaultMessage()`로 동적 추출"하는 방식으로 해결

4. **`HttpStatus` 오기재**
   - `CommonErrorCode.INVALID_INPUT`을 `EMAIL_ALREADY_EXISTS`(`HttpStatus.CONFLICT`, 409)를 참고해 작성하다가 상태 코드를 잘못 그대로 사용 → 검증 실패는 `400 Bad Request`이므로 `HttpStatus.BAD_REQUEST`로 수정

**교훈**
- `@ExceptionHandler` 어노테이션 값과 메서드 파라미터 타입은 반드시 일치해야 하며, 불일치 시 컴파일 오류가 아닌 **런타임(애플리케이션 기동 시점)** 오류로 나타나 원인 파악이 어려울 수 있음
- 프레임워크가 던지는 공통 예외(`MethodArgumentNotValidException` 등)에 대한 `ErrorCode`는 특정 도메인이 아닌 공통(`Common`) 영역으로 분리해야 함
- "예외의 분류(고정)"와 "사용자에게 보여줄 메시지(동적)"를 분리해서 설계하면, `ErrorCode`의 일관성을 유지하면서도 상황별 메시지를 유연하게 응답할 수 있음
