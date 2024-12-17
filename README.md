# GymNaWa-project
Spring MVC , JPA 복습하며 만들어보는 사이드 프로젝트


## 헬스 트레이너 리뷰 서비스 - "짐나와"
### 프로젝트 개요
**<문제점>**
- 트레이너별로 가르치는 실력의 편차가 심해 어떤 트레이너에게 받아야할 지 고민이고, 혹시라도 나와 맞지 않는 트레이너라면 곤란
 
**<해결점>**
- PT를 받은 회원들의 리뷰를 확인하게 하자. (회원들이 리뷰를 쓰도록 만들어야 하는 게 중요)

**<효과>**
- 회원들은 자신에게 맞는 트레이너를 고를 확률이 높아지고, 헬스 업계에는 실력 있는 트레이너만 남아있어 전반적인 PT의 질이 향상된다.

---
### 기능
**<주요 기능>**
- 지도에 원하는 지역 주변 헬스장 표시
- 해당 지역 네에서 트레이너 필터링(별점 높은 순, 리뷰 많은 순 등)
- 이 달의 top 트레이너(평균 평점)

**<리뷰>**
- 로그인 해야 트레이너 리뷰 확인 가능
- 해당 트레이너에게 PT를 받은 기록이 있어야 리뷰 작성 가능
- 리뷰 작성하면 point 지급 (point는 PT 결제 시에 사용 가능)

---
### 개발 진행 현황
#### 2024-12-17
- 도메인 완성
