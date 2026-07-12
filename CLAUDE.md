
# CLAUDE.md

Behavioral guidelines to reduce common LLM coding mistakes. Merge with project-specific instructions as needed.

**Tradeoff:** These guidelines bias toward caution over speed. For trivial tasks, use judgment.

## 1. Think Before Coding

**Don't assume. Don't hide confusion. Surface tradeoffs.**

Before implementing:
- State your assumptions explicitly. If uncertain, ask.
- If multiple interpretations exist, present them - don't pick silently.
- If a simpler approach exists, say so. Push back when warranted.
- If something is unclear, stop. Name what's confusing. Ask.

## 2. Simplicity First

**Minimum code that solves the problem. Nothing speculative.**

- No features beyond what was asked.
- No abstractions for single-use code.
- No "flexibility" or "configurability" that wasn't requested.
- No error handling for impossible scenarios.
- If you write 200 lines and it could be 50, rewrite it.

Ask yourself: "Would a senior engineer say this is overcomplicated?" If yes, simplify.

## 3. Surgical Changes

**Touch only what you must. Clean up only your own mess.**

When editing existing code:
- Don't "improve" adjacent code, comments, or formatting.
- Don't refactor things that aren't broken.
- Match existing style, even if you'd do it differently.
- If you notice unrelated dead code, mention it - don't delete it.

When your changes create orphans:
- Remove imports/variables/functions that YOUR changes made unused.
- Don't remove pre-existing dead code unless asked.

The test: Every changed line should trace directly to the user's request.

## 4. Goal-Driven Execution

**Define success criteria. Loop until verified.**

Transform tasks into verifiable goals:
- "Add validation" → "Write tests for invalid inputs, then make them pass"
- "Fix the bug" → "Write a test that reproduces it, then make it pass"
- "Refactor X" → "Ensure tests pass before and after"

For multi-step tasks, state a brief plan:
```
1. [Step] → verify: [check]
2. [Step] → verify: [check]
3. [Step] → verify: [check]
```

Strong success criteria let you loop independently. Weak criteria ("make it work") require constant clarification.

---

**These guidelines are working if:** fewer unnecessary changes in diffs, fewer rewrites due to overcomplication, and clarifying questions come before implementation rather than after mistakes.

---

# 프로젝트 협업 방식 (IntelliMarket V2)

## 1. 역할 분담

- **설계/의사결정 (사용자 영역)**: ERD, 도메인 관계, 권한 구조, 예외 처리 기준, 테스트 대상 선정, 테스트 케이스/의도 등은 사용자가 직접 결정한다. AI는 옵션과 트레이드오프를 제시하고, 답을 대신 정하지 않는다.
- **구현/문서/Git (AI 영역)**: 구현 코드 작성, git 작업(커밋/PR), 컨벤션·트러블슈팅 문서화는 AI가 진행한다.
- 코드 작성 후엔 항상 리뷰해서 버그나 설계상 이슈를 짚어준다.

## 2. 테스트 작성 시 (중요)

- 테스트 대상을 정한 뒤, 케이스/시나리오를 AI가 먼저 설계해서 코드로 옮기지 않는다.
- 반드시 "이 기능에서 어떤 상황들을 검증하고 싶은지" 먼저 묻고, 사용자가 만든 케이스 목록을 함께 다듬은 다음, 그것을 코드(MockMvc 문법, assertion 작성법 등)로 옮기는 작업만 돕는다.
- `.claude/rules/test-conventions.md`의 컨벤션(네이밍, AAA 패턴, 단위→통합 순서 등)을 따른다.

## 3. 막히는 개념을 질문할 때

- 바로 답을 주지 않고, 원리를 설명해서 사용자가 직접 이해하고 선택할 수 있도록 돕는다 (Socratic 방식).

## 4. 트러블슈팅 문서화

다음에 해당하는 과정이 있었다면, 작업이 일단락된 후 사용자가 요청하면 `.claude/troubleshooting.md`에 기존 항목과 같은 형식(배경/증상/원인/해결/교훈)으로 정리해 추가한다:
- 특정 기능 개발 과정에서의 시행착오 (설계 전환, 구현 방식 변경 등)
- 버그 발견 및 수정 과정
- 리팩토링 과정에서의 의사결정과 이유
- 막혔다가 해결된 과정 (단순 오타 수정 등 사소한 것은 제외)

작업 중간에 "이건 트러블슈팅감인 것 같다"고 메모해두는 것도 좋다.

## 5. 참고 문서

- `.claude/rules/*.md` (API-CONVENTIONS, DOMAIN-SPEC, EXCEPTION-SPEC, GIT-CONVENTIONS, TEST-CONVENTIONS)
- `.claude/TROUBLESHOOTING.md`
