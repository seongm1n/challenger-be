#!/bin/bash

# 필요한 환경 변수 설정
DOCKER_IMAGE_NAME=${DOCKER_IMAGE_NAME:-yourusername/challenger-app}
DOCKER_IMAGE_TAG=${DOCKER_IMAGE_TAG:-latest}
BLUE_PORT=8081
GREEN_PORT=8082
NGINX_PROXY_PORT=8080
DOCKER_NETWORK=challenger-network

# 환경 변수 파일 로드
if [ -f .env ]; then
    export $(cat .env | grep -v '^#' | xargs)
fi

# Docker 이미지 풀
echo "📥 최신 Docker 이미지 다운로드 중..."
docker pull ${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG}

# Docker 네트워크 생성 (존재하지 않는 경우)
if ! docker network ls | grep -q ${DOCKER_NETWORK}; then
    echo "🌐 Docker 네트워크 생성 중: ${DOCKER_NETWORK}"
    docker network create ${DOCKER_NETWORK}
fi

# 현재 실행 중인 애플리케이션 확인 (blue 또는 green)
CURRENT_DEPLOYMENT=""
IDLE_DEPLOYMENT=""
IDLE_PORT=""

if docker ps | grep -q "challenger-blue"; then
    CURRENT_DEPLOYMENT="blue"
    IDLE_DEPLOYMENT="green"
    IDLE_PORT=${GREEN_PORT}
    echo "🔵 현재 실행 중인 배포: BLUE"
elif docker ps | grep -q "challenger-green"; then
    CURRENT_DEPLOYMENT="green"
    IDLE_DEPLOYMENT="blue"
    IDLE_PORT=${BLUE_PORT}
    echo "🟢 현재 실행 중인 배포: GREEN"
else
    CURRENT_DEPLOYMENT=""
    IDLE_DEPLOYMENT="blue"  # 최초 배포 시 Blue로 시작
    IDLE_PORT=${BLUE_PORT}
    echo "⚪ 실행 중인 배포가 없습니다. BLUE로 시작합니다."
fi

# 대기 중인 배포 컨테이너 실행
echo "🚀 ${IDLE_DEPLOYMENT} 배포 시작 중..."
docker rm -f challenger-${IDLE_DEPLOYMENT} 2>/dev/null || true

# MySQL 및 Redis 컨테이너가 실행 중인지 확인하고, 필요한 경우 시작
if ! docker ps | grep -q "challenger-mysql"; then
    echo "🔄 MySQL 컨테이너 시작 중..."
    docker run -d --name challenger-mysql \
        --network ${DOCKER_NETWORK} \
        -e MYSQL_DATABASE=${MYSQL_DATABASE:-challenger} \
        -e MYSQL_USER=${MYSQL_USER:-challenger_user} \
        -e MYSQL_PASSWORD=${MYSQL_PASSWORD:-challenger_password} \
        -e MYSQL_ROOT_PASSWORD=${MYSQL_ROOT_PASSWORD:-root_password} \
        -v mysql-data:/var/lib/mysql \
        mysql:8.0 \
        --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci
fi

if ! docker ps | grep -q "challenger-redis"; then
    echo "🔄 Redis 컨테이너 시작 중..."
    docker run -d --name challenger-redis \
        --network ${DOCKER_NETWORK} \
        -v redis-data:/data \
        redis:latest \
        redis-server --appendonly yes
fi

echo "🔄 ${IDLE_DEPLOYMENT} 컨테이너 실행 중 (포트: ${IDLE_PORT})..."
docker run -d --name challenger-${IDLE_DEPLOYMENT} \
    --network ${DOCKER_NETWORK} \
    -p ${IDLE_PORT}:8080 \
    -e SPRING_PROFILES_ACTIVE=prod \
    -e MYSQL_HOST=challenger-mysql \
    -e MYSQL_PORT=3306 \
    -e MYSQL_DATABASE=${MYSQL_DATABASE:-challenger} \
    -e MYSQL_USER=${MYSQL_USER:-challenger_user} \
    -e MYSQL_PASSWORD=${MYSQL_PASSWORD:-challenger_password} \
    -e OPENAI_API_KEY=${OPENAI_API_KEY} \
    -e JWT_SECRET_KEY=${JWT_SECRET_KEY} \
    -e JWT_EXPIRATION_TIME=${JWT_EXPIRATION_TIME:-86400000} \
    -e JWT_REFRESH_EXPIRATION=${JWT_REFRESH_EXPIRATION:-604800000} \
    -e REDIS_HOST=challenger-redis \
    -e REDIS_PORT=6379 \
    ${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAG}

# 새 컨테이너가 정상적으로 시작될 때까지 대기
echo "⏳ 애플리케이션 시작 대기 중..."
sleep 20

# 애플리케이션 헬스 체크
echo "🩺 ${IDLE_DEPLOYMENT} 배포 헬스 체크 중..."
for i in {1..10}; do
    RESPONSE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:${IDLE_PORT}/actuator/health)
    
    if [ "$RESPONSE" = "200" ]; then
        echo "✅ ${IDLE_DEPLOYMENT} 배포 준비 완료!"
        break
    fi
    
    if [ $i -eq 10 ]; then
        echo "❌ ${IDLE_DEPLOYMENT} 배포 헬스 체크 실패. 이전 배포 유지."
        docker rm -f challenger-${IDLE_DEPLOYMENT} 2>/dev/null || true
        exit 1
    fi
    
    echo "⏳ 재시도 중... ($i/10)"
    sleep 5
done

# Nginx 설정 업데이트
echo "🔄 Nginx 설정 업데이트 중..."
sudo bash -c "cat > /etc/nginx/conf.d/challenger.conf << EOL
server {
    listen 80;
    server_name yourdomain.com;
    
    location / {
        proxy_pass http://localhost:${IDLE_PORT};
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
    }
}
EOL"

# Nginx 설정 테스트 및 재시작
echo "🔄 Nginx 설정 테스트 및 재시작 중..."
sudo nginx -t && sudo systemctl reload nginx

# 트래픽 전환 성공 후 이전 배포 종료
if [ ! -z "$CURRENT_DEPLOYMENT" ]; then
    echo "🛑 이전 ${CURRENT_DEPLOYMENT} 배포 종료 중..."
    docker rm -f challenger-${CURRENT_DEPLOYMENT} 2>/dev/null || true
fi

echo "✅ 무중단 배포가 성공적으로 완료되었습니다!"
echo "🌐 현재 활성 배포: ${IDLE_DEPLOYMENT} (포트: ${IDLE_PORT})" 