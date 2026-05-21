# Creator Settlement API

---

## 프로젝트 개요

크리에이터가 판매한 강의의 결제 내역과 환불 내역을 기반으로 월별 정산 금액을 계산하는 백엔드 API입니다.

플랫폼은 판매 금액에서 수수료를 제외한 금액을 크리에이터에게 정산하며, 운영자는 특정 기간 동안 전체 크리에이터의 정산 현황을 조회할 수 있습니다.

본 프로젝트는 판매 등록, 취소 등록, 월별 정산 조회, 운영자용 정산 집계 기능을 중심으로 구현하였으며, 추가 기능으로 정산 상태 관리, 중복 정산 방지, CSV 다운로드, 수수료 정책 관리 기능을 구현했습니다.

---

## 기술 스택

- Java 17
- Spring Boot
- Spring Data JPA
- MySQL 8.0
- Gradle
- JUnit5
- Lombok

## 개발 환경

- IntelliJ IDEA
- MySQL Workbench
- Git / GitHub

---

## 실행 방법

### 1. MySQL 데이터베이스 준비

MySQL 서버를 실행하고 데이터베이스를 생성합니다.

```sql
CREATE DATABASE creator_settlement CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

`application.properties`의 접속 정보를 환경에 맞게 수정합니다.

```properties
spring.datasource.url=jdbc:mysql://127.0.0.1:3307/creator_settlement?serverTimezone=Asia/Seoul&characterEncoding=UTF-8
spring.datasource.username=root
spring.datasource.password=root
```

### 2. 프로젝트 실행

```bash
./gradlew bootRun
```

또는 IntelliJ에서 `CreatorSettlementApplication` 실행

---

### 3. 접속 정보

```
http://localhost:8080
```

---

### 참고 사항

애플리케이션 실행 시 `spring.jpa.hibernate.ddl-auto=create` 설정으로 테이블이 자동 생성되며,
샘플 데이터는 `data.sql` 기준으로 자동 로드됩니다.

애플리케이션을 재실행하면 테이블이 재생성되고 샘플 데이터가 다시 로드됩니다.

---

## 요구사항 해석 및 가정

- 판매 금액은 결제 완료 시점인 `paidAt` 기준으로 집계합니다.
- 취소/환불 금액은 취소 발생 시점인 `canceledAt` 기준으로 집계합니다.
- 월별 정산 기준은 KST(Asia/Seoul) 기준 해당 월 1일 00:00:00 ~ 말일 23:59:59로 정의합니다.
- 실제 결제 시스템 연동은 구현하지 않고 API 직접 등록 방식으로 처리했습니다.
- 인증/인가 기능은 과제 범위에서 제외했습니다.
- 수강생(Student)은 별도 엔티티로 분리하지 않고 ID만 관리했습니다.
- 금액 계산은 정수(Long) 기반으로 처리하여 오차를 방지했습니다.

---

## 설계 결정과 이유

### 판매/취소 내역 분리

판매와 환불은 집계 기준 시점이 다르므로 `SaleRecord`, `CancelRecord`를 분리하여 관리했습니다.

---

### 정산 결과 저장 구조

정산 조회 시 계산 결과를 `Settlement` 엔티티로 저장하여 이후 정산 확정 및 지급 상태를 관리할 수 있도록 설계했습니다.

```
PENDING
↓

CONFIRMED

↓

PAID
```

---

### 수수료 정책 분리

초기에는 고정 수수료(20%)로 구현했으나, 추후 정책 변경을 고려하여 `FeePolicy` 테이블 기반 구조로 확장했습니다.

예시

| 기간 | 수수료 |
|------|------|
| 2025-01 ~ 2025-03 | 20% |
| 2025-04 ~ | 15% |

---

## 구현 기능

### 필수 구현

### 1. 판매 내역 관리

- 판매 등록 API
- 취소 내역 등록 API
- 판매 내역 조회 API (기간 필터)

---

### 2. 월별 정산 계산

응답 항목

- 총 판매 금액
- 총 환불 금액
- 순 판매 금액
- 플랫폼 수수료
- 정산 예정 금액
- 판매 건수
- 취소 건수

---

### 3. 운영자 정산 집계

- 기간별 전체 크리에이터 정산 조회
- 전체 정산 합계 제공

---

## 추가 구현 (가점)

### 1. 정산 상태 관리

정산 계산 결과를 저장하고 상태 변경 기능 구현

```
PENDING
↓

CONFIRMED

↓

PAID
```

API

```
POST /api/settlements/confirm
POST /api/settlements/pay
```

---

### 2. 동일 기간 중복 정산 방지

동일 `creatorId + month` 조합의 정산이 이미 존재할 경우 재계산 없이 저장된 정산 결과를 반환합니다.

첫 조회 시 정산을 계산하여 `PENDING` 상태로 저장하고, 이후 같은 조건으로 요청하면 저장된 값을 그대로 반환합니다. 이를 통해 중복 계산과 데이터 불일치를 방지합니다.

---

### 3. 정산 내역 CSV 다운로드

운영자가 기간별 정산 결과를 CSV 파일로 다운로드 가능

예시

```csv
creatorId,payoutAmount
creator-1,120000
creator-2,48000
creator-3,0
```

API

```
GET /api/admin/settlements/export
```

---

### 4. 수수료율 변경 이력 관리

수수료를 고정값이 아닌 정책 테이블에서 조회

```
2025-03 → 20%
2025-04 → 15%
```

과거 정산은 당시 정책 기준으로 계산

---

## API 목록 및 예시

### 판매 등록

```http
POST /api/sales
```

```json
{
  "id":"sale-1",
  "courseId":"course-1",
  "studentId":"student-1",
  "amount":50000,
  "paidAt":"2025-03-05T10:00:00+09:00"
}
```

---

### 취소 등록

```http
POST /api/cancellations
```

```json
{
  "id": "cancel-1",
  "saleRecordId": "sale-3",
  "refundAmount": 80000,
  "canceledAt": "2025-03-25T10:00:00+09:00"
}
```

---

### 월별 정산 조회

```http
GET /api/settlements/monthly?creatorId=creator-1&month=2025-03
```

응답

```json
{
  "creatorId":"creator-1",
  "month":"2025-03",
  "totalSalesAmount":260000,
  "totalRefundAmount":110000,
  "netSalesAmount":150000,
  "platformFeeAmount":30000,
  "payoutAmount":120000
}
```

---

### 운영자 정산 집계

```http
GET /api/admin/settlements
```
예시

```http
GET /api/admin/settlements?startDate=2025-03-01T00:00:00+09:00&endDate=2025-03-31T23:59:59+09:00
```

---

### 정산 확정

```http
POST /api/settlements/confirm?creatorId=creator-1&month=2025-03
```
---

### 지급 완료

```http
POST /api/settlements/pay?creatorId=creator-1&month=2025-03
```
---
### CSV 다운로드

```http
GET /api/admin/settlements/export?startDate=2025-03-01T00:00:00+09:00&endDate=2025-03-31T23:59:59+09:00
```
---

## 데이터 모델 설명

### Creator

- id
- name

---

### Course

- id
- creatorId
- title

---

### SaleRecord

- id
- courseId
- studentId
- amount
- paidAt

---

### CancelRecord

- id
- saleRecordId
- refundAmount
- canceledAt

---

### Settlement

- creatorId
- month
- totalSalesAmount
- totalRefundAmount
- payoutAmount
- status

---

### FeePolicy

- feeRate
- effectiveFrom
- effectiveTo

---

관계

```
Creator
  ↓
Course
  ↓
SaleRecord
  ↓
CancelRecord

Settlement
↑
FeePolicy
```

---

## 테스트 실행 방법

### 정상 정산

```
creator-1
2025-03

예상 결과
총 판매: 260000
환불: 110000
순 판매: 150000
수수료: 30000
정산 예정: 120000
```

### 부분 환불

```
sale-4
refund 30000
```

### 빈 월 조회

```
creator-3
2025-03
→ 0원
```

### 중복 정산 방지

동일 요청 2회

→ 기존 정산 결과 반환 (재계산 없음)

### 수수료 변경 검증

```
2025-03 → 20%
2025-04 → 15%
```

### 월 경계 테스트

```
sale-5
결제: 2025-01-31 23:30

cancel-3
취소: 2025-02

→ 판매와 취소가 각각 다른 월 정산에 반영되는지 확인
```

---

## 추가한 테스트 케이스와 이유

과제에서 제시한 기본 시나리오 외에 아래 케이스를 직접 추가하여 검증했습니다.

### 1. 음수 정산 발생 케이스 (`creator2_2월_월경계취소_반영`)

**내용**: `creator-2`의 2025년 2월 정산 조회

- 2월 판매: 0원 (해당 월 판매 없음)
- 2월 취소: 60,000원 (`sale-5`에 대한 `cancel-3`이 2월에 발생)
- 예상 결과: 순 판매 -60,000원 / 수수료 -12,000원 / 정산 예정 -48,000원

**추가 이유**: 월 경계 취소가 실제로 해당 월 정산에 올바르게 반영되는지 검증하기 위해 추가했습니다. 이 케이스는 취소 조회를 "이 달 판매의 취소"가 아닌 "이 크리에이터의 전체 판매 중 이 달에 취소된 것"으로 처리해야 한다는 점을 검증하며, 음수 정산이라는 경계값도 함께 확인합니다.

---

### 2. 빈 월 조회 케이스 (`creator3_빈월조회`)

**내용**: `creator-3`의 2025년 3월 정산 조회

- `creator-3`는 2월에만 판매 기록이 있고 3월 판매 없음
- 예상 결과: 총 판매 0원 / 정산 예정 0원

**추가 이유**: 판매 내역이 없는 월을 조회했을 때 서버 오류 없이 0원으로 정상 응답하는지 검증하기 위해 추가했습니다. 빈 리스트를 SQL `IN ()` 조건으로 전달할 때 오류가 발생할 수 있는 엣지 케이스를 방어하는 코드가 올바르게 동작하는지 확인합니다.

---

### 3. 잘못된 상태 전이 예외 케이스 (`PENDING_상태에서_지급_시도시_예외`)

**내용**: `PENDING` 상태의 정산에 대해 `confirm()` 없이 `pay()`를 직접 호출

- 예상 결과: `ResponseStatusException(400 Bad Request)` 발생

**추가 이유**: 정산 상태 전이가 `PENDING → CONFIRMED → PAID` 순서를 반드시 따르도록 강제되는지 검증하기 위해 추가했습니다. 상태 검증 로직(`IllegalStateException`)이 컨트롤러까지 올바르게 전파되는지 확인합니다.

---

### 4. 정산 상태 전이 전체 흐름 검증 (`정산_확정_후_지급_상태전이`)

**내용**: 정산 생성 → confirm → pay 순서로 호출 후 DB 상태 확인

- 예상 결과: `status = PAID`, `confirmedAt != null`, `paidAt != null`

**추가 이유**: 상태 전이가 완료된 후 실제로 DB에 상태와 타임스탬프가 올바르게 저장되는지 확인하기 위해 추가했습니다.

---

## 미구현 / 제약사항

- 인증/인가 기능은 과제 범위에 따라 구현하지 않았습니다.
- 실제 결제 시스템 연동 없이 API를 통한 직접 데이터 등록 방식으로 구현했습니다.
- 동시성 제어(락 기반 처리)는 적용하지 않았습니다. 동시에 동일 조건 요청이 오더라도 Settlement ID(`creatorId + "-" + month`)가 PK로 고정되어 있어 DB 레벨에서 중복 저장이 차단됩니다.
- CSV 다운로드만 구현하였으며 Excel(.xlsx) 다운로드는 제공하지 않습니다.
- 테스트 코드는 주요 정산 시나리오와 상태 전이 검증 중심으로 수행했습니다.

### 음수 정산 처리 방침

월 경계 취소가 발생하면 해당 월의 순 판매 금액이 음수가 될 수 있습니다.

예시: `creator-2` 2025년 2월 정산
- 2월 판매: 0원 (2월에 판매 없음)
- 2월 취소: 60,000원 (1월 판매 `sale-5`에 대한 취소가 2월에 발생)
- 순 판매: -60,000원 / 수수료: -12,000원 / 정산 예정: -48,000원

이 경우 음수 정산을 그대로 반영하도록 설계했습니다. 실무에서는 다음 달 정산에서 차감하는 방식도 고려할 수 있으나, 과제 명세에 "취소는 취소 일시 기준으로 해당 월 정산에 반영"이라고 명시되어 있어 이 방식을 선택했습니다.

### Admin API 수수료율 기준일 제약

운영자 정산 집계 API(`GET /api/admin/settlements`)는 `startDate` 기준의 수수료율을 조회 기간 전체에 적용합니다.

조회 기간이 수수료율 변경 시점을 포함하는 경우(예: 2025-03-01 ~ 2025-05-31), 3월 기준 수수료율(20%)이 4~5월 판매에도 동일하게 적용됩니다. 크리에이터별 월별 정산 API는 월 단위로 조회하므로 이 문제가 발생하지 않습니다.

---
## AI 활용 범위

- 설계 방향 검토
- API 구조 리뷰
- 테스트 시나리오 설계
- README 문서화 보조

AI를 보조 도구로 활용하였으며, 제안된 내용을 직접 검토·수정하고 최종 설계·구현·테스트 및 검증은 직접 수행했습니다.   
생성된 결과는 실제 실행 및 테스트를 통해 검증 후 반영했습니다.