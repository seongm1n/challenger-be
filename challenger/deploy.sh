#!/bin/bash

# Docker와 Docker Compose가 설치되어 있는지 확인
if ! [ -x "$(command -v docker)" ]; then
  echo 'Docker가 설치되어 있지 않습니다. 설치 중...'
  sudo apt-get update
  sudo apt-get install -y apt-transport-https ca-certificates curl software-properties-common
  curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
  sudo add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable"
  sudo apt-get update
  sudo apt-get install -y docker-ce
  sudo usermod -aG docker $USER
  echo 'Docker 설치 완료'
else
  echo 'Docker가 이미 설치되어 있습니다.'
fi

if ! [ -x "$(command -v docker-compose)" ]; then
  echo 'Docker Compose가 설치되어 있지 않습니다. 설치 중...'
  sudo curl -L "https://github.com/docker/compose/releases/download/v2.20.3/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
  sudo chmod +x /usr/local/bin/docker-compose
  echo 'Docker Compose 설치 완료'
else
  echo 'Docker Compose가 이미 설치되어 있습니다.'
fi

# EC2 인스턴스 시작 시 자동 실행을 위한 설정
sudo systemctl enable docker
sudo systemctl start docker

# .env 파일이 없으면 생성
if [ ! -f .env ]; then
  echo "환경 변수 설정 파일(.env)이 없습니다. 새로 생성합니다."
  cat > .env << EOL
# Application settings
APP_PORT=8080
SPRING_PROFILES_ACTIVE=prod

# MySQL settings
MYSQL_DATABASE=challenger
MYSQL_USER=challenger_user
MYSQL_PASSWORD=challenger_password
MYSQL_ROOT_PASSWORD=root_password
MYSQL_PORT=3306

# OpenAI API key - 실제 사용시 이 값을 변경해야 합니다
OPENAI_API_KEY=your_openai_api_key_here

# JWT settings
JWT_SECRET_KEY=ch@llengerSecretKey_ThisIsVeryLongSecureKeyForHS512SignatureAlgorithm_2024_ThisMustBeAtLeast256BitsLong
JWT_EXPIRATION_TIME=86400000
EOL
  echo ".env 파일이 생성되었습니다. 필요에 따라 값을 수정하세요."
else
  echo ".env 파일이 이미 존재합니다."
fi

# Docker Compose를 사용하여 애플리케이션 시작
echo "Docker Compose를 사용하여 애플리케이션을 시작합니다..."
docker-compose down
docker-compose build --no-cache
docker-compose up -d

echo "배포가 완료되었습니다. 애플리케이션이 실행 중입니다."
echo "애플리케이션 로그를 확인하려면: docker-compose logs -f app"
echo "MySQL 로그를 확인하려면: docker-compose logs -f mysql" 