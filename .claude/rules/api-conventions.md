# API 컨벤션

## 공통 응답 래퍼

모든 API 응답은 `ApiResponse<T>`로 감싸서 반환한다.

```json
{
  "success": true,
  "data": { ... }
}
```

| 필드 | 타입 | 설명 |
|------|------|------|
| `success` | boolean | 요청 성공 여부 |
| `data` | T | 응답 데이터 (실패 시 null) |

> HTTP 상태코드는 응답 헤더로 전달되므로 래퍼에 중복 포함하지 않는다.
> 에러 응답은 `ApiResponse` 대신 `ErrorResponse`를 사용한다.

---

## 페이징 응답 구조

목록 조회 API의 페이징 응답은 아래 구조를 따른다.

```json
{
  "success": true,
  "data": {
    "content": [ ... ],
    "page": 0,
    "size": 20,
    "totalPages": 5,
    "hasNext": true
  }
}
```

| 필드 | 타입 | 설명 |
|------|------|------|
| `content` | List | 실제 데이터 리스트 |
| `page` | int | 현재 페이지 번호 (0부터 시작) |
| `size` | int | 페이지 크기 |
| `totalPages` | int | 전체 페이지 수 |
| `hasNext` | boolean | 다음 페이지 존재 여부 |

---

## 날짜/시간 포맷

모든 날짜/시간 값은 **ISO 8601** 형식을 사용한다.

```
2026-06-03T14:30:00
```

> 커스텀 포맷 사용 시 프론트 파싱 오류 발생 가능성이 높으므로 표준 형식을 준수한다.
