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
  - 내 좌표 기준 반경 5키로 내의 헬스장 표시(좌표값으로 거리 계산하면 될 듯?)
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
  - 마이페이지에서 로그아웃 시, 요청을 post로 변경
- 테스트 데이터 초기화

#### 2025-01-06
- 로그인 체크 인터셉터 추가(미로그인 사용자가 url로 마이페이지 들어가는 거 방지)
- 회원 정보 수정 기능 개발
  - MemberSaveForm -> MemberForm으로 변경하여 edit 시에도 사용
  - **Member** : 도메인에 업데이트 메서드 추가
  - **MemberService** : 업데이트 메서드 추가
  - **MemberController** : 회원 정보 추가(@Get, @Post)
    - **로그인 아이디 수정 시, 중복 체크 기능 필요**

#### 2025-01-07
- 카카오 api 키값 노출시키지 않기
  - application.yml 파일에 넣고, application.yml 파일 .gitignore 파일에 등록

- application.yml에 등록한 키값을 코드로 불러오는 과정에서 오류 발생(아예 실행조차 되지 않음)
  - MapService를 새로 만들어 해당 클래스에 필드로 @Value 어노테이션을 적용하여 키값을 가져오려고 했으나, 오류 발생
  - **Error : Parameter 0 of constructor in ~ required a bean of type 'java.lang.String' that could not be found.**
  - 트러블슈팅 실패...(내일 다시 도전..!)

#### 2025-01-08
- 트러블슈팅 재시도
  - 위 에러는 의존성 주입 시, String 타입의 빈이 등록되어 있지 않아서 생기는 것이었다.
  - MemberService가 빈으로 등록될 때, 생성자에 String 타입의 @value 변수를 파라미터로 넣었는데, 이 String 타입의 변수는 빈으로 등록되어 있지 않기 때문에 생긴 에러이다.
  - 즉, **의존성 주입 시 주입되는 특정 클래스가 Bean으로 관리가 되지 않아서 발생하는 문제**이다.
  - 스프링에서 모든 객체를 빈으로 등록하고, 의존성 주입을 시작할 때, 의존성 주입을 위해 생성자에 파라미터로 존재하는 객체들 역시 빈으로 등록되어 있어야 한다.
  - 그러나 MemberService에 의존성 주입을 할 때, 파라미터로 존재하는 String 타입의 키값은 빈으로 등록되어 있지 않기 때문에 에러가 발생한 것이다.
  - 따라서 String 타입의 @value 변수를 필드로 가지는 ApiKey라는 클래스를 새로 만들고, ApiConfig라는 @Configuration 클래스를 새로 만들어서 ApiKey 클래스를 빈으로 등록했다.
  - 그 후, HomeController에서 이 ApiKey 객체를 가져와 그 안에 있는 String 변수를 꺼내 사용하여 해결하였다.
  - 그러나..! 그냥 HomeController에서 @Value 변수를 선언하여 사용하면 되는 것이었다...!
  - 정말 많은 시간을 이 키값을 가져오는데 사용해서 아깝지만, 이번 기회에 스프링의 의존성 주입이 어떻게 진행되는지 다시 복습할 수 있어 나쁘지 않은 시간이었다고 생각한다.
 
- 카카오맵 API
  - 사이트 접속 시, 현 위치(접속 위치)에서 시작하도록 변경
- 다음 주소 API
  - 회원가입 시, 주소를 입력받도록 다음 주소 API 적용
  - Address 임베디드 값 타입 생성(Member뿐 아니라, Gym 객체에도 사용할 예정)
  - 우선 Member에만 적용
    - Member
    - MemberForm
    - MemberController
    - createMemberForm.html
    - MemberService
  - 그러나 현재 화면에서 입력받은 임베디드 타입 필드들이 컨트롤러로 넘어올 때 null로 넘어온다.
  - 원인 파익 필요

#### 2025-01-10
- 다음 주소 API를 사용하여 입력받은 주소 DB에 저장 성공
  - 그러나 내가 원하는대로 @ModelAttribute를 이용하여 자동으로 임베디드 타입 값이 바인딩시키지는 못 했다.
  - 차선책으로 요청 파라미터에서 직접 데이터를 꺼내(request.getParameter) Address 필드를 직접 생성했다.
  - 따라서 MemberSaveDto에 Address 임베디드 필드를 제거했다.
- 회원가입 시, MemberForm에 Address 임베디드 타입을 필드로 넣었으나 현재 해당 필드는 자동 바인딩을 사용하지 않고 컨트롤러에서 직접 Address 객체를 생성하기 때문에 필요 X
  - 그러나 회원 정보 수정 시에는 Address 필드가 필요
  - 따라서 회원 DTO 분리 결정
  - MemberForm -> MemberSaveDto, MemberEditDto로 분리
  - 하는 김에 기존 LoginForm도 명칭 통일
  - LoginForm -> MemberLoginDto

- 트러블 슈팅
  - Caused by: org.springframework.beans.NotReadablePropertyException: Invalid property 'address' of bean class [project.gymnawa.domain.dto.member.MemberSaveDto]: Bean property 'address' is not readable or has an invalid getter method: Does the return type of the getter match the parameter type of the setter?
  - 회원가입 시, 주소를 꼭 입력하게 하기 위해 컨트롤러에서 MemberDto 앞에 @Validated를 붙이며 검증 실시
  - html 파일에서는 address 부분에 오류 메시지를 출력하도록 th:errorclass를 추가
  - address 필드는 MemberDto의 필드가 아닌데, th:errorclass를 붙이며 검증을 시도하려고 했기 때문에 읽을 수 없다고 오류 발생한 것
  - 객체를 검증하기로 했으면 해당 객체의 필드만 검사할 수 있도록 하자

#### 2025-01-11
- 회원 정보 수정 추가
  - 주소 수정

#### 2025-01-12
- 회원가입 시, 일반 회원, 트레이너 회원 선택 페이지 추가

---
### To-Do-List
- 도로명 주소 저장 방법 조사 -> 도로명 주소 API를 가져와서 활용하면 된다. (O)
- 회원가입
  - 중복 아이디 검증 (O)
  - 주소 저장 (O)
  - 이메일 인증
- 로그인 (O)
- 카카오 지도 API 활용하여 헬스장 위치 표시
  - 접속 위치 or 검색한 위치 반경 2km 내의 헬스장 지도에 표시
- 스프링 인터셉터 적용하여 로그인 안 된 사용자가 url로 마이페이지 들어가는 거 방지 (O)
- 마이페이지 구현
  - 화면 구현
    - 회원 주소 추가
  - 회원 정보 수정 시, 로그인 중복 체크
- 카카오맵 내 위치에서 시작 (O)
- 트레이너 회원 가입
- 헬스장 정보 저장(본인 헬스장 직접 등록 vs 공공데이터로 등록)
