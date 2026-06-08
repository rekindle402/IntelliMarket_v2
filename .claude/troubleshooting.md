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
