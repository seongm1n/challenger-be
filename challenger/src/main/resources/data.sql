-- 사용자 데이터 삽입
INSERT INTO users (id, name) VALUES (1, 'Theo');
INSERT INTO users (id, name) VALUES (2, 'Yoshi');

-- 챌린지 1: 매일 30분 운동하기 (30일) - 진행 중, 중간 단계
INSERT INTO challenge (id, user_id, title, description, start_date, duration)
VALUES (1, 1, '매일 30분 운동하기',
        '하루 30분 이상 운동하여 체력 향상 및 건강한 습관을 기르는 도전입니다. 걷기, 달리기, 홈트레이닝 등 다양한 방법으로 운동할 수 있습니다.',
        DATEADD('DAY', -15, CURRENT_DATE()), 30);

-- 챌린지 2: 매일 명상하기 (21일) - 현재 진행 중, 초반 단계
INSERT INTO challenge (id, user_id, title, description, start_date, duration)
VALUES (2, 1, '아침 명상 습관화하기',
        '매일 아침 10분씩 명상을 하여 하루를 차분히 시작하고 집중력을 향상시키는 도전입니다. 명상 앱이나 유튜브 가이드를 활용해도 좋습니다.',
        DATEADD('DAY', -5, CURRENT_DATE()), 21);

-- 챌린지 3: 물 마시기 (14일) - 아직 시작 전
INSERT INTO challenge (id, user_id, title, description, start_date, duration)
VALUES (3, 2, '하루 2리터 물 마시기',
        '매일 2리터의 물을 마셔 수분 섭취량을 늘리고 건강한 신체 습관을 기르는 도전입니다. 물통을 항상 가지고 다니며 규칙적으로 마시는 것이 중요합니다.',
        DATEADD('DAY', 2, CURRENT_DATE()), 14);

-- 챌린지 4: 일찍 일어나기 (30일) - 중단된 상태
INSERT INTO challenge (id, user_id, title, description, start_date, duration)
VALUES (4, 2, '아침 6시 기상하기',
        '30일 동안 매일 아침 6시에 기상하여 아침 시간을 효율적으로 활용하고 규칙적인 수면 패턴을 형성하는 도전입니다.',
        DATEADD('DAY', -20, CURRENT_DATE()), 30);

-- 챌린지 5: 독서 (60일) - 거의 완료 상태
INSERT INTO challenge (id, user_id, title, description, start_date, duration)
VALUES (5, 1, '매일 30페이지 책 읽기',
        '매일 30페이지 이상의 책을 읽어 독서 습관을 기르고 지식을 확장하는 도전입니다. 다양한 장르의 책을 읽으면 더욱 효과적입니다.',
        DATEADD('DAY', -55, CURRENT_DATE()), 60);
