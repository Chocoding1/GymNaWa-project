# GymNaWa-project
Spring MVC , JPA 복습하며 만들어보는 사이드 프로젝트


## 헬스 트레이너 리뷰 서비스 - "짐나와"
### 프로젝트 개요
**<문제점>**
- 트레이너별로 가르치는 실력의 편차가 심해 어떤 트레이너에게 PT를 받아야할 지 고민이고, 혹시라도 나와 맞지 않는 트레이너라면 곤란
 
**<해결점>**
- PT를 받은 회원들의 리뷰를 확인하게 하자. (회원들이 리뷰를 쓰도록 만들어야 하는 게 중요)

**<효과>**
- 회원들은 자신에게 맞는 트레이너를 고를 확률이 높아지고, 헬스 업계에는 실력 있는 트레이너만 남게 되어 전반적인 PT의 질이 향상된다.

---
### 기능
**<주요 기능>**
- 내 주변 헬스장 표시
- 지도에 원하는 지역 주변 헬스장 표시
- 해당 지역 내에서 트레이너 필터링(별점 높은 순, 리뷰 많은 순 등)
- 이 달의 top 트레이너(평균 평점)

**<리뷰>**
- 로그인 해야 트레이너 리뷰 확인 가능
- 해당 트레이너에게 PT를 받은 기록이 있어야 리뷰 작성 가능
- 리뷰 작성하면 point 지급 (point는 PT 결제 시에 사용 가능)

**<회원>**
- 마이페이지
- 회원가입
- 로그인
- 정보 수정

**<기타>**
- PT 시작 시, 알림 설정

---
### ERD
<img width="90%" src="https://github.com/user-attachments/assets/5347967e-191e-409f-8839-fd19ffc99fec">

---
### 개발 진행 현황
#### 2024-12-17
- 도메인 완성 (도로명 주소 미완 - 도로명 주소 API 사용 예정)

#### 2024-12-18
- 회원 기능 개발 완료
  - **MemberRepository**
    - 회원 엔티티 명으로 인한 오류 발생 (해결)
      - User -> Member로 변경
      - User는 DB의 예약어이기 때문에 엔티티명으로 적합하지 않다.
  - **MemberService**
  - **MemberServiceTest**
 
#### 2024-12-19
- 트레이너 기능 개발 중
  - **TrainerRepository** (완료)
  - **TrainerService** (완료)
  - **TrainerServiceTest** (진행 중)
     - 헬스장 별 트레이너 검색 기능 -> 헬스장 리포지토리와 서비스가 개발되어야 진행 가능

#### 2024-12-20
- 헬스장 기능 개발 중
  - **GymRepository** (완료)
  - **GymService** (완료)
  - **GymServiceTest** (진행 중)
   
- 트레이너 기능 개발 완료
  - **TrainerServiceTest** (완료)
    - 체육관 별 트레이너 검색 시 오류 발생 (해결)
      - **org.hibernate.QueryException: could not resolve property**
      - JPQL 작성 시, 파라미터를 DB 기준 필드명으로 작성하는 것이 아니라 JPA에서 지정한 필드명으로 지정해야 한다.
      - select t from Trainer t where t.gymId = :gymId   ->   select t from Trainer t where t.gym = :gym
      - gymId를 gym으로 변경
 
   
      **+) 2025-01-03**
        - gymId로 검색 시 오류가 발생한 이유 : 객체 그래프 탐색 방식으로 쿼리를 작성하지 않았기 때문
        - t.gymId가 아니라 t.gym.id로 작성해야 한다.
        - 따라서 select t from Trainer t where t.gym.id = :gymId로 작성하면 오류가 발생하지 않는다.

#### 2024-12-23
- 도메인 수정
  - 양방향 연관관계 -> 단방향 연관관계로 변경
  - 수정 도메인 : Member, Trainer, Gym

- 회원 기능 추가
  - **MemberService**, **MemberServiceTest**
    - 로그인 아이디로 검색 기능 구현

- 헬스장 기능 개발 완료
  - **GymServiceTest**

#### 2024-12-26
- 카카오맵 API 사용하여 지도 표시

#### 2024-12-27
- 컨트롤러 개발 중
  - **HomeController**
    - 카카오앱, 회원가입 버튼, 로그인 버튼
  - **MemberController**
    - 회원가입
      - 회원가입 완료 시 로그인 url로 redirect

#### 2024-12-30
- 로그인 기능 개발 중
  - **MemberController**
    - 로그인 url 개발
      - @Get만 완료
  - 로그인 폼 생성
  - 로그인 화면 생성
  - @Post를 위해 로그인 검증을 해야하는데, JPQL에서 전체 조회 시 스트림 적용이 안 되는 것 같아서 좀 더 조사 후 진행 예정
    - JPQL에서 스트림 적용하여 로그인 아이디 비번 검증 코드 추가 완료
  - validation과 쿠키, 세션 사용하여 로그인 처리 예정
 
- 회원가입 기능 추가
  - Bean Validation 추가

#### 2025-01-02
- 로그인 기능 개발 완료
  - **MemberController**
    - 로그인 : @Post
      - Bean Validation 추가
      - 아이디 or 비밀번호 오류 시, 글로벌 오류 발생하여 로그인 화면으로 redirect
      - 로그인 시 세션 생성하여 로그인 정보 저장하여 홈 화면으로 redirect
    - 로그아웃 : @Post
      - 세션 만료 후 홈 화면으로 redirect
  - **HomeController**
    - 로그인 X : 홈 화면 랜더링
    - 로그인 O : 로그인 홈 화면 랜더링
      - 로그인 화면 따로 생성

#### 2025-01-03
- 도메인 수정
  - 등록일, 수정일 추가
  - BaseEntity 만들어서 상속

- 마이페이지 추가
- 테스트 데이터 초기화

(ToDo)
- 마이페이지에서 로그아웃 시 post로 보내기
- 정보 수정 기능 추가
- 

---
### To-Do-List
- 도메인 주소 저장 방법 조사 -> 도로명 주소 API를 가져와서 활용하면 된다.
- 회원가입 (중복 아이디 방지) (O)
- 로그인 (O)
- 카카오 지도 API 활용하여 헬스장 위치 표시
- 스프링 인터셉터 적용하여 로그인 안 된 사용자가 url로 마이페이지 들어가는 거 방지
- 마이페이지 구현
- 카카오맵 내 위치에서 시작
