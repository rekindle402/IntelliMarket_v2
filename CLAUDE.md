
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

- **구현/문서/Git (AI 영역)**: 구현 코드 작성, 테스트 케이스 설계 및 작성, git 작업(커밋/PR), 컨벤션·트러블슈팅 문서화는 AI가 진행한다.
- 코드 작성 후엔 항상 리뷰해서 버그나 설계상 이슈를 짚어준다.

## 2. 테스트 작성 시

- 테스트 대상에 대해 AI가 케이스/시나리오를 설계하고, `.claude/rules/test-conventions.md`의 컨벤션(네이밍, AAA 패턴, 단위→통합 순서 등)에 따라 바로 코드로 작성한다.
- 설계 의도가 불분명하거나 사용자 결정이 필요한 부분만 간단히 확인한다.

## 3. 막히는 개념을 질문할 때

- 간결하게 답을 먼저 제시하고, 필요한 경우에만 부가 설명을 덧붙인다.

## 4. 트러블슈팅 문서화

다음에 해당하는 과정이 있었다면, 작업이 일단락된 후 사용자가 요청하면 `.claude/troubleshooting.md`에 기존 항목과 같은 형식(배경/증상/원인/해결/교훈)으로 정리해 추가한다:
- 특정 기능 개발 과정에서의 시행착오 (설계 전환, 구현 방식 변경 등)
- 버그 발견 및 수정 과정
- 리팩토링 과정에서의 의사결정과 이유
- 막혔다가 해결된 과정 (단순 오타 수정 등 사소한 것은 제외)

작업 중간에 "이건 트러블슈팅감인 것 같다"고 메모해두는 것도 좋다.

## 5. 참고 문서

- `.claude/rules/*.md` (api-conventions, domain-spec, exception-spec, git-conventions, test-conventions)
- `.claude/troubleshooting.md`
