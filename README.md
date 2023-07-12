# README

# 프로젝트명

> 걸음 수 기반 경쟁 채팅 플랫폼 walk-talk
> 

이 앱은 유저들의 걸음 수를 기록하고, 이를 기반으로 랭킹을 제공합니다. 이 랭킹은 건강한 라이프스타일을 촉진하기 위한 것으로, 유저들이 건강한 삶의 습관을 형성하고 유지할 수 있도록 도와줍니다.

또한, 건강한 라이프스타일을 추구하는 유저들 간의 채팅을 통해, 건강의 선순환을 유발하는 것에 더해 건강에 관심이 많은 사용자들간의 경쟁의식을 유발하여, 유저들 간의 건강 경쟁을 통해 더욱 건강한 삶의 습관을 형성하도록 지원합니다.

# Demo Video

[[walk-talk] 걸음 수 기반 채팅 어플 데모 영상](https://codenme.tistory.com/72)

# API SPEC

## Swagger-UI

[http://3.37.137.127:21903/swagger-ui/index.html#](http://3.37.137.127:21903/swagger-ui/index.html#)

위의 Swagger-UI를 활용하여  API설명을 확인하고 실제 API 테스트를 수행하실 수 도 있습니다.

# 프로젝트 설명

[walk-talk 설명 문서](https://www.notion.so/walk-talk-3e9dffa20ab94015a1d524524e0418e8?pvs=21) 

# 코딩 컨벤션

[walk-talk 코딩 컨벤션](https://shyswy.notion.site/code-convention-402842f4a27e4ca88f753b2b209dd7d1?pvs=4)

# ⚒️ 기술스택

---

## Language

- Java
- YML

## **Back-End**

- Spring Boot
- Spring Security, JWT
- Redis

## Collaboration & Tools

- IntelliJ, Vim
- Git,  Git Flow, Notion, Jira, Confluence, Slack

## DBMS

- MariaDB
- Spring JPA, Spring Data JPA, Querydsl

## Test

- JUnit
- Mockito

## DevOps

- AWS
- Docker

## 설치 방법

해당 프로젝트는 로컬환경에서도 redis를 통해 구동하기에, redis를 설치해야합니다.

**OS X & 리눅스:**

```bash
brew install redis
```

위의 명령어로 redis를 설치합니다.

```bash
brew services start redis 

brew services stop redis

brew services restart redis
```

brew services start 명령어를 통해 Redis를 실행합니다.

```bash
redis-cli
```

이제 위의 명령어로 redis에 접속할 수 있습니다.

**Windows:**

[https://github.com/microsoftarchive/redis](https://github.com/microsoftarchive/redis)

위의 링크를 통해 redis를 설치하세요. 

## 정보

이름: 윤상현- shyswy@naver.com
