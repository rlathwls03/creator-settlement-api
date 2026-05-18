# Creator Settlement API
---

## 프로젝트 개요
크리에이터가 판매한 강의의 결제 내역과 환불 내역을 기반으로 월별 정산 금액을 계산하는 백엔드 API입니다.

플랫폼은 판매 금액에서 수수료를 제외한 금액을 크리에이터에게 정산하며, 운영자는 특정 기간 동안 전체 크리에이터의 정산 현황을 조회할 수 있습니다.

본 프로젝트는 판매 등록, 취소 등록, 월별 정산 조회, 운영자용 정산 집계 기능을 중심으로 구현합니다.


## 기술 스택
- Java 17
- Spring Boot
- Spring Data JPA
- MySQL
- Gradle
- JUnit5
- Lombok
- IntelliJ IDEA
- MySQL Workbench
- Git / GitHub

## 실행 방법


## 요구사항 해석 및 가정
판매 금액은 결제 완료 시점인 paidAt 기준으로 집계합니다.   
취소/환불 금액은 취소 발생 시점인 canceledAt 기준으로 집계합니다.   
월별 정산 기준은 KST(Asia/Seoul) 기준 해당 월 1일 00:00:00부터 말일 23:59:59까지로 정의합니다.   
플랫폼 수수료율은 고정값 20%로 계산합니다.   
실제 결제 시스템 연동은 구현하지 않고 API를 통한 직접 등록 방식으로 대체합니다.   
인증/인가 기능은 과제 범위에서 제외합니다.   
수강생(student)은 별도 엔티티로 분리하지 않고 ID 값만 관리합니다.   

## 설계 결정과 이유
정산 시스템의 핵심은 판매 내역과 환불 내역을 서로 다른 기준 날짜로 정확히 집계하는 것입니다.   
이를 위해 SaleRecord와 CancelRecord를 분리하여 원 결제와 환불 흐름을 독립적으로 관리했습니다.   
금액 계산에서 부동소수점 오차를 방지하기 위해 Long 타입으로 금액을 저장합니다.   
초기 구현 단계에서는 복잡한 JPA 연관관계보다 명확한 비즈니스 로직 구현이 중요하다고 판단하여 creatorId, courseId, saleRecordId 기반의 단순 참조 구조로 설계했습니다.   
수수료율은 현재 고정값이지만, 추후 변경 가능성을 고려하여 별도 상수 관리 방식으로 구현할 예정입니다.   

## 미구현 / 제약사항

## AI 활용 범위

## API 목록 및 예시

## 데이터 모델 설명
Creator
- id
- name
  
Course
- id
- creatorId
- title
  
SaleRecord
- id
- courseId
- studentId
- amount
- paidAt
  
CancelRecord
- id
- saleRecordId
- refundAmount
- canceledAt
  
관계
- Creator 1 : N Course
- Course 1 : N SaleRecord
- SaleRecord 1 : N CancelRecord

## 테스트 실행 방법
