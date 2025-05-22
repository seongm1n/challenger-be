#!/bin/bash

# 색상 변수 설정
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 로그 함수
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 패키지 업데이트
log_info "시스템 패키지 업데이트 중..."
sudo apt-get update
if [ $? -ne 0 ]; then
    log_error "패키지 업데이트 실패"
    exit 1
fi

# 필요한 패키지 설치
log_info "필수 패키지 설치 중..."
sudo apt-get install -y curl wget git apt-transport-https ca-certificates gnupg lsb-release
if [ $? -ne 0 ]; then
    log_error "필수 패키지 설치 실패"
    exit 1
fi

# Docker 설치
install_docker() {
    if ! command -v docker &> /dev/null; then
        log_info "Docker 설치 중..."
        
        # Docker GPG 키 추가
        curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg
        
        # Docker 저장소 추가
        echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
        
        # Docker 설치
        sudo apt-get update
        sudo apt-get install -y docker-ce docker-ce-cli containerd.io
        
        # 현재 사용자를 Docker 그룹에 추가
        sudo usermod -aG docker $USER
        
        # Docker 서비스 시작
        sudo systemctl enable docker
        sudo systemctl start docker
        
        log_success "Docker 설치 완료"
    else
        log_info "Docker가 이미 설치되어 있습니다."
    fi
}

# Docker Compose 설치
install_docker_compose() {
    if ! command -v docker-compose &> /dev/null; then
        log_info "Docker Compose 설치 중..."
        
        # 최신 버전 다운로드
        COMPOSE_VERSION=$(curl -s https://api.github.com/repos/docker/compose/releases/latest | grep 'tag_name' | cut -d\" -f4)
        sudo curl -L "https://github.com/docker/compose/releases/download/${COMPOSE_VERSION}/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
        
        # 실행 권한 부여
        sudo chmod +x /usr/local/bin/docker-compose
        
        log_success "Docker Compose 설치 완료"
    else
        log_info "Docker Compose가 이미 설치되어 있습니다."
    fi
}

# Jenkins 설치
install_jenkins() {
    if ! command -v jenkins &> /dev/null && ! systemctl list-units --full -all | grep -q "jenkins.service"; then
        log_info "Jenkins 설치 중..."
        
        # Jenkins 저장소 키 추가
        wget -q -O - https://pkg.jenkins.io/debian-stable/jenkins.io.key | sudo apt-key add -
        
        # Jenkins 저장소 추가
        sudo sh -c 'echo deb https://pkg.jenkins.io/debian-stable binary/ > /etc/apt/sources.list.d/jenkins.list'
        
        # 패키지 업데이트 및 Jenkins 설치
        sudo apt-get update
        sudo apt-get install -y jenkins
        
        # Jenkins 서비스 설정
        sudo systemctl enable jenkins
        sudo systemctl start jenkins
        
        # 초기 관리자 비밀번호 가져오기
        sleep 10
        JENKINS_PASSWORD=$(sudo cat /var/lib/jenkins/secrets/initialAdminPassword)
        
        log_success "Jenkins 설치 완료"
        log_info "Jenkins 초기 관리자 비밀번호: ${JENKINS_PASSWORD}"
        log_info "Jenkins 접속 URL: http://localhost:8080"
    else
        log_info "Jenkins가 이미 설치되어 있습니다."
    fi
}

# Nginx 설치
install_nginx() {
    if ! command -v nginx &> /dev/null; then
        log_info "Nginx 설치 중..."
        
        # Nginx 설치
        sudo apt-get install -y nginx
        
        # Nginx 서비스 설정
        sudo systemctl enable nginx
        sudo systemctl start nginx
        
        log_success "Nginx 설치 완료"
    else
        log_info "Nginx가 이미 설치되어 있습니다."
    fi
}

# Nginx 설정 적용
configure_nginx() {
    log_info "Nginx 설정 적용 중..."
    
    # Nginx 설정 디렉토리 생성
    sudo mkdir -p /etc/nginx/conf.d
    
    # 기존 설정 파일 백업
    if [ -f /etc/nginx/nginx.conf ]; then
        sudo cp /etc/nginx/nginx.conf /etc/nginx/nginx.conf.bak
    fi
    
    # 새 설정 파일 적용
    sudo cp nginx.conf /etc/nginx/nginx.conf
    
    # Challenger 애플리케이션 설정 파일 생성
    sudo bash -c "cat > /etc/nginx/conf.d/challenger.conf << EOL
server {
    listen 80;
    server_name yourdomain.com;
    
    location / {
        proxy_pass http://localhost:8081;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
    }
}
EOL"
    
    # Nginx 설정 테스트
    sudo nginx -t
    if [ $? -ne 0 ]; then
        log_error "Nginx 설정 테스트 실패"
        exit 1
    fi
    
    # Nginx 서비스 재시작
    sudo systemctl reload nginx
    
    log_success "Nginx 설정 적용 완료"
}

# Jenkins 설정
configure_jenkins() {
    log_info "Jenkins 설정 중..."
    
    # Jenkins가 Docker 명령을 실행할 수 있도록 권한 설정
    sudo usermod -aG docker jenkins
    
    # Jenkins 서비스 재시작
    sudo systemctl restart jenkins
    
    log_success "Jenkins 설정 완료"
    log_info "다음 Jenkins 플러그인을 설치하세요:"
    log_info "1. Docker Pipeline"
    log_info "2. Blue Ocean"
    log_info "3. Pipeline: AWS Steps"
    log_info "4. SSH Agent"
}

# 방화벽 설정
configure_firewall() {
    log_info "방화벽 설정 중..."
    
    # ufw 활성화
    sudo ufw enable
    
    # SSH, HTTP, HTTPS 포트 개방
    sudo ufw allow ssh
    sudo ufw allow http
    sudo ufw allow https
    
    # Jenkins 포트 개방
    sudo ufw allow 8080
    
    # 애플리케이션 포트 개방
    sudo ufw allow 8081
    sudo ufw allow 8082
    
    log_success "방화벽 설정 완료"
}

# 실행 스크립트에 실행 권한 부여
make_scripts_executable() {
    log_info "스크립트에 실행 권한 부여 중..."
    
    chmod +x deploy-blue-green.sh
    
    log_success "실행 권한 부여 완료"
}

# 메인 실행 함수
main() {
    log_info "CI/CD 환경 설정을 시작합니다..."
    
    install_docker
    install_docker_compose
    install_jenkins
    install_nginx
    configure_nginx
    configure_jenkins
    configure_firewall
    make_scripts_executable
    
    log_success "CI/CD 환경 설정이 완료되었습니다!"
    log_info "Jenkins와 Nginx가 설치되었으며, 무중단 배포를 위한 환경이 구성되었습니다."
    log_info "Jenkins 초기 설정을 완료하고, 필요한 플러그인을 설치한 후 파이프라인을 구성하세요."
    log_warning "참고: 현재 사용자 세션에서 Docker 명령을 실행하려면 로그아웃 후 다시 로그인하거나 'newgrp docker' 명령을 실행하세요."
}

# 스크립트 실행
main 