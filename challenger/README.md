# Challenger 백엔드

Spring Boot 기반의 Challenger 애플리케이션 백엔드입니다.

## 기술 스택

- Java 17
- Spring Boot 3.3.1
- Spring Security
- Spring Data JPA
- MySQL 8.0
- Docker / Docker Compose
- JWT 인증

## EC2 배포 방법

### 사전 준비

1. AWS EC2 인스턴스 생성 (Ubuntu 20.04 LTS 추천)
2. 보안 그룹 설정: 
   - SSH: 22
   - HTTP: 80
   - HTTPS: 443
   - 애플리케이션: 8080 (또는 .env 파일에서 설정한 포트)

### 배포 단계

1. EC2 인스턴스에 SSH로 접속

```bash
ssh -i your-key.pem ubuntu@your-ec2-public-ip
```

2. 프로젝트 클론

```bash
git clone https://github.com/your-username/challenger-be.git
cd challenger-be
```

3. 배포 스크립트 실행 권한 부여 및 실행

```bash
chmod +x deploy.sh
./deploy.sh
```

4. 환경 변수 설정 (.env 파일)

배포 스크립트가 자동으로 .env 파일을 생성합니다. 필요에 따라 수정하세요:

```bash
vi .env
```

다음과 같은 환경변수를 수정해야 합니다:
- OPENAI_API_KEY: OpenAI API 키
- MYSQL_USER: MySQL 사용자 이름
- MYSQL_PASSWORD: MySQL 비밀번호
- MYSQL_ROOT_PASSWORD: MySQL 루트 비밀번호 
- JWT_SECRET_KEY: JWT 암호화 키

5. 애플리케이션 재시작

```bash
docker-compose down
docker-compose up -d
```

6. 배포 확인

```bash
docker-compose ps
```

API 테스트:
```bash
curl http://localhost:8080/actuator/health
```

## 로그 확인

애플리케이션 로그:
```bash
docker-compose logs -f app
```

MySQL 로그:
```bash
docker-compose logs -f mysql
```

## 주의사항

- 실제 프로덕션 환경에서는 .env 파일의 비밀번호와 API 키를 반드시 변경하세요.
- HTTPS 설정을 위해 Nginx와 Let's Encrypt를 사용하는 것을 권장합니다.
- 데이터베이스는 Docker 볼륨을 통해 영구적으로 저장됩니다.