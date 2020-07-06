# 스프링부트로 구현하는 간단한 검색 서비스

간단한 장소 검색 웹 애플리케이션 구현

## 애플리케이션 실행

애플리케이션을 실행하려면 카카오 오픈 API키가 **반드시** 필요합니다. 발급 방법은 [여기](https://developers.kakao.com/docs/latest/ko/getting-started/app)를 참고하세요.



### 직접빌드해서 실행

> src/main/resources/application.yml

위 경로 파일의 `{FIXME-INPUT-YOUR-KEY}` 부분을 발급받은 키로 교체 후 다음 명령어를 실행합니다.

```shell
$ ./gradlew bootRun
```



### 이미 빌드 된 jar 를 실행

[releases](https://github.com/iamkyu/place-search/releases) 에서 최신 릴리즈 파일을 다운로드 받습니다.

```shell
$ java -jar place-search-*.jar --spring.profiles.active=local --kakao.key={이부분을발급받은키입력}
```






## 기능 사용하기

성공적으로 애플리케이션이 실행되었다면 제공 되는 기능을 사용할 수 있습니다.



### 기본 회원 정보 (local 환경시 해당)

일부 기능은 로그인을 필요로 합니다. 제공되는 기본 로그인 정보는 다음과 같습니다. 

- ryan / foobar
- apeach / foobar1
- frodo / foobar2



### IntelliJ 를 통한 기능 실행

프로젝트 루트에 위치한 `rest.http`를 통해 각 API 를 호출 할 수 있습니다.



### curl 을 통한 기능 실행

먼저 JSON 포맷의 데이터를 다루는 커맨드라인 유틸리티 [jq](https://www.44bits.io/ko/post/cli_json_processor_jq_basic_syntax#%EB%93%A4%EC%96%B4%EA%B0%80%EB%A9%B0-%EC%BB%A4%EB%A7%A8%EB%93%9C%EB%9D%BC%EC%9D%B8-json-%ED%94%84%EB%A1%9C%EC%84%B8%EC%84%9C-jq)를 설치합니다.

```shell
$ brew install jq
```



#### 로그인

```shell
$ TOKEN_TEMP=$(curl -X "POST" "http://localhost:8080/login" \
     -H 'Content-Type: application/json; charset=utf-8' \
     -d $'{
  "id": "ryan",
  "password": "foobar"
}' | jq -r '.data.token')
```

로그인 성공시 `TOEKN_TEMP` 변수에 토큰이 저장됩니다.



#### 장소 검색

```shell
$ curl "http://localhost:8080/v1/search?page=1&size=10&keyword=kakaofriends" \
     -H 'Authorization: Bearer '"$TOKEN_TEMP"'' | jq
```



#### 인기 검색어

```shell
$ curl "http://localhost:8080/v1/trends" | jq
```





## 핵심문제해결전략
- 제어할 수 없는 외부 의존성 잘 다루기
  - 장소 검색에 대한 원본 데이터는 외부 API 에 의존적 (카카오, 네이버 등의 주소 벤더사).
  - 애플리케이션의 가장 취약 지점은 이러한 [통합 지점](https://github.com/iamkyu/TIL/blob/master/books/summary/release-it.md#%ED%86%B5%ED%95%A9%EC%A7%80%EC%A0%90).
  - 이 지점을 잘 다루기 위해 [시간 제한](https://github.com/iamkyu/TIL/blob/master/books/summary/release-it.md#%EC%A0%9C%ED%95%9C%EC%8B%9C%EA%B0%84), [회로차단기](https://github.com/iamkyu/TIL/blob/master/books/summary/release-it.md#%EC%B0%A8%EB%8B%A8%EA%B8%B0), [칸막이](https://github.com/iamkyu/TIL/blob/master/books/summary/release-it.md#%EC%B9%B8%EB%A7%89%EC%9D%B4) 패턴 등을 적용. 회로차단기는 간단하게 [직접 구현](https://github.com/iamkyu/place-search/blob/master/src/main/java/com/namkyujin/search/infrastructure/circuit/CircuitBreaker.java).
  - 또한, 사용자에게 더 나은 경험을 제공하기 위해 외부 의존성의 상황에 따라 [동적으로 비즈니스 로직의 전략을 변경](https://github.com/iamkyu/place-search/blob/master/src/main/java/com/namkyujin/search/search/application/DynamicSearchStrategyResolver.java)할 수 있는 설계.
    1. [외부 API 성공/실패 여부 무관하게 빠르게 응답하기](https://github.com/iamkyu/place-search/blob/master/src/test/java/com/namkyujin/search/search/application/strategy/FastFailSearchStrategyTest.java) 
    2. [지정된 횟수만큼 재시도하기](https://github.com/iamkyu/place-search/blob/master/src/test/java/com/namkyujin/search/search/application/strategy/RetrySearchStrategyTest.java)
  - 처음에는 A 벤더에서 실패시 B 벤더에서 재시도하는 시나리오를 생각함. 하지만 이 경우 사용자는 일관성 없는 읽기를 경험할 수 있음. 예컨대 A, B 벤더 모두 불안정한 상황이라면 1페이지 검색은 A 벤더, 2페이지 검색은 B 벤더, 다시 1페이지 검색하니 B 벤더.. 따라서 기본적으로 하나의 벤더를 통해서만 위 전략을 수행하도록 [구현](https://github.com/iamkyu/place-search/commit/76b127a0af624ac3c8f2462f27cdf6ba544f1745)함. 벤더 역시 런타임에 동적으로 변경할 수 있는 구현 필요함.
  - 테스트 환경에서는 [스텁](https://github.com/iamkyu/place-search/blob/master/src/test/java/com/namkyujin/search/search/api/SearchControllerIntegrationTest.java#L38)을 통해 테스트가 외부 의존성에 영향 받지 않도록 함. 
- 검색 랭킹 갱신
  - 동시성 문제가 발생할 수 있는 부분.
  - 원자적연산, 수평적확장, 분산환경 등을 고려했을 때 가장 적합한 솔루션이라고 판단되는 레디스를 사용.
  - 하지만 이는 또 다른 SPOF. 따라서 레디스에 문제를 감지한다면 RDS의 검색 기록을 바탕으로 랭킹을 집계.
  - 인덱스를 추가해두었지만 좋은 방법은 아니라고 생각. 차라리 집계 데이터를 별도의 통계 테이블에 저장하는 것도 방법.
  - 현재는 1일 단위 랭킹 제공을 고려, 캐시 Full 을 대비해 날짜 기반 KEY 설계. 다음날 이전일 랭킹을 제거함. 랭킹의 갱신 주기가 시간 단위 또는 슬라이딩 윈도우 형태로 변경 된다면 좀 더 고민이 필요할 것.
- API 인증
  - 검색은 인증 된 사용자만 가능해야 함.
  - API 인증을 구현하는 방법은 여러가지. API 키 기반, OAuth2 등.
  - 두가지 모두 요구사항 대비 복잡하고 서버에서 관리해야 하는 부담이 있음.
  - 따라서 간단하게 JWT 를 통해 구현. (로그아웃, 갱신 등은 고려X)