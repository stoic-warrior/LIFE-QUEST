# LIFE QUEST

일상을 RPG처럼 게임화하는 모바일 앱의 백엔드 프로젝트입니다.

## 기술 스택

- Java 21 (LTS)
- Spring Boot 3.2.x
- Spring Data JPA + QueryDSL
- Spring Security + JWT
- PostgreSQL, Redis, Kafka

## 주요 기능 (MVP)

- 회원가입/로그인 및 JWT 발급
- 사용자 프로필/스탯 조회
- 퀘스트 생성/수락/완료/포기
- XP/골드/스탯 포인트 계산 및 레벨업

## 프로젝트 구조

```
com.lifequest
├── api/
│   ├── controller/
│   ├── dto/
│   └── advice/
├── domain/
│   ├── user/
│   ├── quest/
│   ├── guild/
│   └── item/
├── application/
│   └── service/
├── infrastructure/
│   └── security/
└── config/
```

## 실행 방법

```bash
./gradlew bootRun
```

## 테스트

```bash
./gradlew test
```
