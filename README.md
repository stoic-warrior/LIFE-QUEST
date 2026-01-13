# LIFE QUEST

일상을 RPG처럼 게임화하는 모바일 앱 **LIFE QUEST**의 백엔드 프로젝트입니다.

## Prerequisites

- **Java 21 (LTS)**
- **Gradle** (wrapper 포함: `./gradlew`)
- **PostgreSQL 16**
- **Redis 7**
- **Docker & Docker Compose** (로컬 인프라 실행용)
- **Node.js 18+** + **npm/pnpm/yarn** (Vite 프론트엔드 실행 시)

## 환경 변수 / 설정

백엔드 설정은 `src/main/resources/application.yml`에 기본값이 있습니다.
로컬에서 값을 바꾸려면 다음 중 하나를 사용하세요.

### 1) `application.yml` 직접 수정

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/lifequest
    username: lifequest
    password: lifequest
  data:
    redis:
      host: localhost
      port: 6379

jwt:
  secret: "<BASE64 인코딩된 시크릿>"
  access-token-expiry-minutes: 15
  refresh-token-expiry-days: 7
```

### 2) 환경 변수로 오버라이드 (권장)

Spring Boot 표준 환경 변수를 사용합니다.

```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/lifequest
export SPRING_DATASOURCE_USERNAME=lifequest
export SPRING_DATASOURCE_PASSWORD=lifequest
export SPRING_DATA_REDIS_HOST=localhost
export SPRING_DATA_REDIS_PORT=6379
export JWT_SECRET="<BASE64 인코딩된 시크릿>"
export JWT_ACCESS_TOKEN_EXPIRY_MINUTES=15
export JWT_REFRESH_TOKEN_EXPIRY_DAYS=7
```

> 참고: 현재 `.env` 파일 로딩은 설정되어 있지 않습니다. 필요 시 `spring-boot-starter` 설정 또는 스크립트를 추가해 주세요.

## 로컬 인프라 실행 (PostgreSQL/Redis)

아래 예시 `docker-compose.yml`을 프로젝트 루트에 생성한 뒤 실행하세요.

```yaml
version: "3.9"
services:
  postgres:
    image: postgres:16
    container_name: lifequest-postgres
    ports:
      - "5432:5432"
    environment:
      POSTGRES_DB: lifequest
      POSTGRES_USER: lifequest
      POSTGRES_PASSWORD: lifequest
    volumes:
      - postgres_data:/var/lib/postgresql/data

  redis:
    image: redis:7
    container_name: lifequest-redis
    ports:
      - "6379:6379"

volumes:
  postgres_data:
```

실행:

```bash
docker compose up -d
```

## 백엔드 실행

```bash
./gradlew bootRun
```

기본 포트: `http://localhost:8080`

## 프론트엔드 실행 (Vite)

이 리포지토리에는 프론트엔드 코드가 포함되어 있지 않습니다.
별도 Vite 프로젝트가 있을 경우 아래처럼 실행하세요.

```bash
# 예시: frontend/ 디렉터리에 Vite 프로젝트가 있다고 가정
cd frontend
npm install
npm run dev
```

## 검증 방법

- **Swagger UI**: 현재 이 프로젝트에는 Swagger(Springdoc)가 설정되어 있지 않습니다.
  - 설정 후 예상 URL: `http://localhost:8080/swagger-ui/index.html`
- **Health Check**: Actuator가 설정되어 있지 않습니다.
  - 설정 후 예상 URL: `http://localhost:8080/actuator/health`

> 현재는 Postman/curl로 API를 호출하여 동작을 확인하는 방식을 권장합니다.

## Common Errors & Fixes

- **JWT 시크릿 에러 / InvalidKeyException**
  - `jwt.secret` 값이 Base64 인코딩 문자열인지 확인하세요.

- **DB 연결 실패 (Connection refused)**
  - PostgreSQL 컨테이너가 실행 중인지 확인하세요: `docker ps`
  - `SPRING_DATASOURCE_URL` 값이 올바른지 확인하세요.

- **Redis 연결 실패**
  - Redis 컨테이너가 실행 중인지 확인하세요.
  - `SPRING_DATA_REDIS_HOST`/`PORT` 값 확인.

- **포트 충돌**
  - 8080, 5432, 6379 포트가 사용 중인지 확인하고 다른 포트로 변경하세요.
