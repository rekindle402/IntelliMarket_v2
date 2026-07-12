## Git 전략

간소화 Git-flow 방식 사용

- `main` : 배포 가능한 안정 버전
- `develop` : 통합 브랜치
- `feature/xxx`, `chore/xxx` 등 : 기능 단위 브랜치

작업 흐름: 기능 브랜치 → PR → develop → PR → main

---

## 브랜치 네이밍

| 유형 | 형식 | 예시 |
|------|------|------|
| 기능 추가 | `feature/기능명` | `feature/member-auth` |
| 설정/환경 | `chore/작업명` | `chore/base-config` |
| 버그 수정 | `fix/수정내용` | `fix/order-status-null` |
| 리팩토링 | `refactor/대상` | `refactor/member-service` |

---

## 커밋 메시지

한글로 작성. `타입: 설명` 형식.

| 타입 | 용도 |
|------|------|
| `feat` | 새 기능 |
| `fix` | 버그 수정 |
| `chore` | 설정, 환경, 빌드 |
| `refactor` | 기능 변경 없는 코드 개선 |
| `test` | 테스트 코드 |
| `docs` | 문서 |

예시:
```
feat: 멤버 로그인 API 추가
fix: 주문 상태 null 참조 오류 수정
chore: 기본 애플리케이션 설정 추가
```

---

## PR 규칙

- 제목: `타입: 한글 요약` (예: `chore: 기본 애플리케이션 설정 추가`)
- 본문: 한글 작성
- 머지 대상: 항상 `develop`
- PR 머지 후 브랜치 삭제

## PR 템플릿

```markdown
## 작업 내용

## 작업 이유

## 주요 변경사항

## 기술적 고민 / 의사결정

## 참고사항
```
