# GymNaWa


## 헬스 트레이너 리뷰 서비스 - "짐나와"
#### https://spectacular-taurus-e63.notion.site/Gymnawa-19530a2dedab802cacbbe1e4431007cd (노션 링크)
### 프로젝트 개요
**<문제점>**
- 트레이너의 수가 많아지면서, 트레이너마다 실력의 편차가 크고, 그에 따른 회원들의 불만도 증가하고 있다.
- 특히 일부 트레이너는 회원의 몸 상태나 체형의 불균형 등을 고려하지 않고 동일한 방식의 운동을 적용해, 부상이나 체형 불균형 악화와 같은 부정적인 결과를 초래한다.
- 이러한 문제는 소셜 미디어에서도 쉽게 확인할 수 있으며, 회원 입장에서는 어떤 트레이너에게 PT를 받아야 할지 판단하기 어려운 상황이다.
- 결과적으로, 자신과 맞지 않는 트레이너를 선택하게 될 경우 시간, 돈, 건강 모두에서 손해를 볼 가능성이 높다.
 
**<해결점>**
- 회원들이 PT를 받은 후, 트레이너에 대한 리뷰를 남길 수 있는 시스템을 도입해 간접적으로 트레이너의 스타일, 전문성, 소통 방식 등을 확인할 수 있도록 한다.

**<효과>**
- 회원들은 트레이너의 실제 스타일과 전문성을 사전에 파악할 수 있어, 자신에게 맞는 트레이너를 선택할 확률이 높아진다.
- 객관적이고 신뢰도 높은 평가 시스템을 통해 트레이너들 간의 건전한 경쟁이 유도되며, 자연스럽게 실력 있고 책임감 있는 트레이너들이 돋보이게 된다.
- 결과적으로 퍼스널 트레이닝 시장 전반의 서비스 품질이 향상되고, 회원들의 만족도와 재등록률도 함께 증가할 것으로 기대된다.
---

**+) 향후 계획**

**<문제점>**
- PT는 실제 대면하는 관계라서, 아무리 불만이 있어도 직접적으로 부정적인 리뷰를 남기기 어렵다는 심리적 장벽이 크다.
- 회원마다 운동 경험이나 기대치가 다르기 때문에, 리뷰가 감정적이거나 주관적일 수 있다.

**<해결점>**
- 단순한 별점이나 후기 대신, 체크리스트 형식의 객관적인 평가 항목(예: 체형 분석 여부, 운동 설명의 명확성 등)을 중심으로 구성하여, 개인 감정보다는 트레이너의 실질적인 지도 태도나 전문성을 파악할 수 있도록 설계한다.
- 리뷰는 익명으로 작성할 수 있게 하여 부정적인 피드백도 자유롭게 표현할 수 있도록 한다.

---
### 기술 스택
- Spring Boot, JPA, Spring Data JPA, Spring Security
- MySQL, Redis
- JWT

---
### ERD
<img width="90%" src="https://github.com/user-attachments/assets/4c0d8952-2f51-4c9b-ab87-82229fce9d9d">

---
### API 명세서
#### 회원 공통
|기능|URL|HTTP Method|인증 여부|
|---|---|---|---|
|로그인|/api/members/login|POST|X|
|로그아웃|/api/members/logout|POST|O|
|회원 탈퇴|/api/members/{id}|DELETE|O|

#### 일반 회원
|기능|URL|HTTP Method|인증 여부|
|---|---|---|---|
|회원가입|/api/normembers|POST|X|
|마이페이지|/api/normembers/{id}|GET|O|
|회원 정보 수정|/api/normembers/{id}|PATCH|O|
|비밀번호 변경|/api/normembers/{id}/password|PATCH|O|
|내가 쓴 리뷰 조회|/api/normembers/{id}/reviews|GET|O|
|진행 중인 PT 조회|/api/normembers/{id}/ptmemberships|GET|O|

#### 트레이너
|기능|URL|HTTP Method|인증 여부|
|---|---|---|---|
|회원가입|/api/trainers|POST|X|
|마이페이지|/api/trainers/{id}|GET|O|
|회원 정보 수정|/api/trainers/{id}|PATCH|O|
|비밀번호 변경|/api/trainers/{id}/password|PATCH|O|
|내게 달린 리뷰 조회|/api/trainers/{id}/reviews|GET|O|
|진행 중인 PT 조회|/api/trainers/{id}/ptmemberships|GET|O|

#### 헬스장 소속 트레이너
|기능|URL|HTTP Method|인증 여부|
|---|---|---|---|
|헬스장 별 트레이너 조회|/api/gymtrainers/{gymId}/trainers|GET|X|

#### 리뷰
|기능|URL|HTTP Method|인증 여부|
|---|---|---|---|
|리뷰 작성|/api/reviews|POST|O|
|리뷰 수정|/api/reviews/{id}|PATCH|O|
|리뷰 삭제|/api/reviews/{id}|DELETE|O|
|트레이너 별 리뷰 조회|/api/reviews/{trainerId}|GET|X|

---
### 프로젝트 구조
```
src
    ├───main
    │   ├───java
    │   │   └───project
    │   │       └───gymnawa
    │   │           │   GymnawaApplication.java
    │   │           │   
    │   │           ├───auth
    │   │           │   ├───cookie
    │   │           │   │   └───util
    │   │           │   │           CookieUtil.java
    │   │           │   │           
    │   │           │   ├───domain
    │   │           │   │       SecurityWhiteListProperties.java
    │   │           │   │       
    │   │           │   ├───filter
    │   │           │   │       CustomLoginFilter.java
    │   │           │   │       CustomLogoutFilter.java
    │   │           │   │       JwtAuthenticationFilter.java
    │   │           │   │       JwtExceptionHandleFilter.java
    │   │           │   │       
    │   │           │   ├───jwt
    │   │           │   │   ├───controller
    │   │           │   │   │       ReissueController.java
    │   │           │   │   │       
    │   │           │   │   ├───dto
    │   │           │   │   │       JwtInfoDto.java
    │   │           │   │   │       
    │   │           │   │   ├───error
    │   │           │   │   │       CustomAuthException.java
    │   │           │   │   │       
    │   │           │   │   ├───service
    │   │           │   │   │       ReissueService.java
    │   │           │   │   │       ReissueServiceImpl.java
    │   │           │   │   │       
    │   │           │   │   └───util
    │   │           │   │           JwtUtil.java
    │   │           │   │           
    │   │           │   └───oauth
    │   │           │       ├───domain
    │   │           │       │       CustomOAuth2UserDetails.java
    │   │           │       │       GoogleUserInfo.java
    │   │           │       │       KakaoUserInfo.java
    │   │           │       │       OAuth2UserInfo.java
    │   │           │       │       
    │   │           │       ├───handler
    │   │           │       │       CustomSuccessHandler.java
    │   │           │       │       
    │   │           │       └───service
    │   │           │               CustomOauth2UserService.java
    │   │           │               CustomUserDetailsService.java
    │   │           │               
    │   │           ├───config
    │   │           │       EmailConfig.java
    │   │           │       EncodeConfig.java
    │   │           │       JpaAuditingConfig.java
    │   │           │       RedisConfig.java
    │   │           │       RestTemplateConfig.java
    │   │           │       SecurityConfig.java
    │   │           │       
    │   │           ├───domain
    │   │           │   ├───common
    │   │           │   │   ├───api
    │   │           │   │   │       ApiResponse.java
    │   │           │   │   │       
    │   │           │   │   ├───error
    │   │           │   │   │   ├───dto
    │   │           │   │   │   │       ErrorCode.java
    │   │           │   │   │   │       ErrorResponse.java
    │   │           │   │   │   │       
    │   │           │   │   │   ├───exception
    │   │           │   │   │   │       CustomException.java
    │   │           │   │   │   │       
    │   │           │   │   │   └───handler
    │   │           │   │   │           GlobalExceptionHandler.java
    │   │           │   │   │           
    │   │           │   │   └───etcfield
    │   │           │   │           Address.java
    │   │           │   │           BaseTime.java
    │   │           │   │           ContractStatus.java
    │   │           │   │           
    │   │           │   ├───email
    │   │           │   │   ├───controller
    │   │           │   │   │       EmailApiController.java
    │   │           │   │   │       
    │   │           │   │   ├───dto
    │   │           │   │   │       EmailDto.java
    │   │           │   │   │       
    │   │           │   │   └───service
    │   │           │   │           EmailService.java
    │   │           │   │           RedisService.java
    │   │           │   │           
    │   │           │   ├───gym
    │   │           │   │   ├───controller
    │   │           │   │   │       GymApiController.java
    │   │           │   │   │       
    │   │           │   │   ├───dto
    │   │           │   │   │       GymDto.java
    │   │           │   │   │       
    │   │           │   │   ├───entity
    │   │           │   │   │       Gym.java
    │   │           │   │   │       
    │   │           │   │   ├───repository
    │   │           │   │   │       GymRepository.java
    │   │           │   │   │       
    │   │           │   │   └───service
    │   │           │   │           GymService.java
    │   │           │   │           
    │   │           │   ├───gymtrainer
    │   │           │   │   ├───controller
    │   │           │   │   │       GymTrainerApiController.java
    │   │           │   │   │       
    │   │           │   │   ├───dto
    │   │           │   │   │       GymTrainerRequestDto.java
    │   │           │   │   │       GymTrainerResponseDto.java
    │   │           │   │   │       GymTrainerViewDto.java
    │   │           │   │   │       
    │   │           │   │   ├───entity
    │   │           │   │   │       GymTrainer.java
    │   │           │   │   │       
    │   │           │   │   ├───repository
    │   │           │   │   │       GymTrainerRepository.java
    │   │           │   │   │       
    │   │           │   │   └───service
    │   │           │   │           GymTrainerService.java
    │   │           │   │           
    │   │           │   ├───kakao
    │   │           │   │   ├───dto
    │   │           │   │   │       KakaoApiResponse.java
    │   │           │   │   │       MetaData.java
    │   │           │   │   │       
    │   │           │   │   └───service
    │   │           │   │           KakaoService.java
    │   │           │   │           
    │   │           │   ├───member
    │   │           │   │   ├───controller
    │   │           │   │   │       MemberApiController.java
    │   │           │   │   │       
    │   │           │   │   ├───dto
    │   │           │   │   │       MemberHomeInfoDto.java
    │   │           │   │   │       MemberLoginDto.java
    │   │           │   │   │       MemberOauthInfoDto.java
    │   │           │   │   │       MemberSessionDto.java
    │   │           │   │   │       PasswordDto.java
    │   │           │   │   │       UpdatePasswordDto.java
    │   │           │   │   │       
    │   │           │   │   ├───entity
    │   │           │   │   │   │   Member.java
    │   │           │   │   │   │   
    │   │           │   │   │   └───etcfield
    │   │           │   │   │           Gender.java
    │   │           │   │   │           Role.java
    │   │           │   │   │           
    │   │           │   │   ├───repository
    │   │           │   │   │       MemberRepository.java
    │   │           │   │   │       
    │   │           │   │   └───service
    │   │           │   │           MemberService.java
    │   │           │   │           
    │   │           │   ├───normember
    │   │           │   │   ├───controller
    │   │           │   │   │       NorMemberApiController.java
    │   │           │   │   │       
    │   │           │   │   ├───dto
    │   │           │   │   │       MemberEditDto.java
    │   │           │   │   │       MemberSaveDto.java
    │   │           │   │   │       MemberViewDto.java
    │   │           │   │   │       
    │   │           │   │   ├───entity
    │   │           │   │   │       NorMember.java
    │   │           │   │   │       
    │   │           │   │   ├───repository
    │   │           │   │   │       NorMemberRepository.java
    │   │           │   │   │       
    │   │           │   │   └───service
    │   │           │   │           NorMemberService.java
    │   │           │   │           
    │   │           │   ├───ptmembership
    │   │           │   │   ├───controller
    │   │           │   │   │       PtMembershipApiController.java
    │   │           │   │   │       
    │   │           │   │   ├───dto
    │   │           │   │   │       PtMembershipSaveDto.java
    │   │           │   │   │       PtMembershipViewDto.java
    │   │           │   │   │       
    │   │           │   │   ├───entity
    │   │           │   │   │       PtMembership.java
    │   │           │   │   │       
    │   │           │   │   ├───repository
    │   │           │   │   │       PtMembershipRepository.java
    │   │           │   │   │       
    │   │           │   │   └───service
    │   │           │   │           PtMembershipService.java
    │   │           │   │           
    │   │           │   ├───review
    │   │           │   │   ├───controller
    │   │           │   │   │       ReviewApiController.java
    │   │           │   │   │       
    │   │           │   │   ├───dto
    │   │           │   │   │       ReviewEditDto.java
    │   │           │   │   │       ReviewSaveDto.java
    │   │           │   │   │       ReviewViewDto.java
    │   │           │   │   │       
    │   │           │   │   ├───entity
    │   │           │   │   │       Review.java
    │   │           │   │   │       
    │   │           │   │   ├───repository
    │   │           │   │   │       ReviewRepository.java
    │   │           │   │   │       
    │   │           │   │   └───service
    │   │           │   │           ReviewService.java
    │   │           │   │           
    │   │           │   └───trainer
    │   │           │       ├───controller
    │   │           │       │       TrainerApiController.java
    │   │           │       │       
    │   │           │       ├───dto
    │   │           │       │       TrainerEditDto.java
    │   │           │       │       TrainerSaveDto.java
    │   │           │       │       TrainerViewDto.java
    │   │           │       │       
    │   │           │       ├───entity
    │   │           │       │       Trainer.java
    │   │           │       │       
    │   │           │       ├───repository
    │   │           │       │       TrainerRepository.java
    │   │           │       │       
    │   │           │       └───service
    │   │           │               TrainerService.java
    │   │           │               
    │   │           └───testdata
    │   │                   TestData.java
    │   │                   
    │   └───resources
    │       │   application.yml
    │       │   
    │       ├───static
    │       └───templates
    └───test
        ├───java
        │   └───project
        │       └───gymnawa
        │           ├───auth
        │           │   ├───filter
        │           │   │       CustomLoginFilterTest.java
        │           │   │       CustomLogoutFilterTest.java
        │           │   │       JwtAuthenticationFilterTest.java
        │           │   │       
        │           │   ├───jwt
        │           │   │   └───service
        │           │   │           ReissueServiceImplTest.java
        │           │   │           
        │           │   └───oauth
        │           │       └───service
        │           │               CustomUserDetailsServiceTest.java
        │           │               
        │           ├───config
        │           │       SecurityTestConfig.java
        │           │       
        │           └───domain
        │               ├───email
        │               │   └───service
        │               │           EmailServiceTest.java
        │               │           
        │               ├───gymtrainer
        │               │   └───service
        │               │           GymTrainerServiceTest.java
        │               │           
        │               ├───member
        │               │   ├───controller
        │               │   │       MemberApiControllerTest.java
        │               │   │       
        │               │   └───service
        │               │           MemberServiceTest.java
        │               │           
        │               ├───normember
        │               │   ├───controller
        │               │   │       NorMemberApiControllerTest.java
        │               │   │       
        │               │   └───service
        │               │           NorMemberServiceTest.java
        │               │           
        │               ├───review
        │               │   ├───controller
        │               │   │       ReviewApiControllerTest.java
        │               │   │       
        │               │   └───service
        │               │           ReviewServiceTest.java
        │               │           
        │               └───trainer
        │                   ├───controller
        │                   │       TrainerApiControllerTest.java
        │                   │       
        │                   ├───repository
        │                   │       TrainerRepositoryTest.java
        │                   │       
        │                   └───service
        │                           TrainerServiceTest.java
        │                           
        └───resources
                application.yml
```
