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
- Jenkins (CI/CD)
- Nginx (무중단 배포)

## EC2 배포 방법

### 사전 준비

1. AWS EC2 인스턴스 생성 (Ubuntu 20.04 LTS 추천)
2. 보안 그룹 설정: 
   - SSH: 22
   - HTTP: 80
   - HTTPS: 443
   - Jenkins: 8080
   - 애플리케이션: 8081, 8082 (Blue-Green 배포용)

### 기본 배포 방법

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

## CI/CD 환경 구성 (무중단 배포)

### 1. 환경 설정 스크립트 실행

CI/CD 환경(Jenkins와 Nginx)을 구성하기 위해 다음 스크립트를 실행합니다:

```bash
chmod +x setup-cicd.sh
./setup-cicd.sh
```

이 스크립트는 다음 작업을 수행합니다:
- Docker 및 Docker Compose 설치
- Jenkins 설치 및 구성
- Nginx 설치 및 구성
- 방화벽 설정
- Blue-Green 배포를 위한 스크립트 설정

### 2. Jenkins 초기 설정

1. 브라우저에서 `http://your-ec2-public-ip:8080` 접속
2. 설정 스크립트가 출력한 초기 관리자 비밀번호를 입력
3. 권장 플러그인 설치 및 관리자 계정 생성
4. 다음 추가 플러그인 설치:
   - Docker Pipeline
   - Blue Ocean
   - Pipeline: AWS Steps
   - SSH Agent

### 3. Jenkins 파이프라인 설정

1. Jenkins 대시보드에서 "새로운 Item" 선택
2. "Pipeline" 선택 후 이름 입력 (예: challenger-pipeline)
3. "Pipeline" 섹션에서 "Pipeline script from SCM" 선택
4. SCM으로 "Git" 선택 후 저장소 URL 입력
5. 브랜치 지정 (예: */main 또는 */master)
6. Script Path로 "Jenkinsfile" 입력
7. 저장

### 4. Jenkins Credentials 설정

다음 자격 증명을 추가해야 합니다:
1. `docker-hub-credentials`: Docker Hub 접근용
2. `deploy-server-credentials`: 배포 서버 SSH 접근용

### 5. 무중단 배포 실행

Jenkins 파이프라인을 실행하면 다음 과정이 자동화됩니다:
1. 코드 체크아웃
2. Gradle 빌드
3. Docker 이미지 빌드 및 푸시
4. Blue-Green 배포 실행

Blue-Green 배포는 다음과 같이 동작합니다:
- 현재 실행 중인 애플리케이션 확인 (Blue 또는 Green)
- 새 버전을 대기 중인 환경에 배포
- 헬스 체크 후 Nginx 설정 변경으로 트래픽 전환
- 이전 환경 종료

## 로그 확인

애플리케이션 로그:
```bash
# Blue 환경 로그
docker logs -f challenger-blue

# Green 환경 로그
docker logs -f challenger-green
```

MySQL 로그:
```bash
docker logs -f challenger-mysql
```

Jenkins 로그:
```bash
sudo journalctl -u jenkins
```

Nginx 로그:
```bash
sudo tail -f /var/log/nginx/access.log
sudo tail -f /var/log/nginx/error.log
```

## 주의사항

- 실제 프로덕션 환경에서는 .env 파일의 비밀번호와 API 키를 반드시 변경하세요.
- HTTPS 설정을 위해 Let's Encrypt를 사용하는 것을 권장합니다.
- 데이터베이스는 Docker 볼륨을 통해 영구적으로 저장됩니다.
- Jenkins 초기 설정 후 보안을 위해 비밀번호를 변경하세요.
- Jenkins와 Docker의 권한 설정이 올바르게 되었는지 확인하세요.