-- 사용자 데이터 초기화
INSERT INTO users (id, name) VALUES (1, 'Theo');
INSERT INTO users (id, name) VALUES (2, 'Yoshi');

ALTER TABLE users ALTER COLUMN id RESTART WITH 3;

-- 진행 중인 챌린지 데이터
INSERT INTO challenge (id, user_id, title, description, start_date, duration)
VALUES (1, 1, '매일 30분 운동하기',
        '하루 30분 이상 운동하여 체력 향상 및 건강한 습관을 기르는 도전입니다. 걷기, 달리기, 홈트레이닝 등 다양한 방법으로 운동할 수 있습니다.',
        DATEADD('DAY', -15, CURRENT_DATE()), 30);

INSERT INTO challenge (id, user_id, title, description, start_date, duration)
VALUES (2, 1, '아침 명상 습관화하기',
        '매일 아침 10분씩 명상을 하여 하루를 차분히 시작하고 집중력을 향상시키는 도전입니다. 명상 앱이나 유튜브 가이드를 활용해도 좋습니다.',
        DATEADD('DAY', -5, CURRENT_DATE()), 21);

INSERT INTO challenge (id, user_id, title, description, start_date, duration)
VALUES (3, 2, '하루 2리터 물 마시기',
        '매일 2리터의 물을 마셔 수분 섭취량을 늘리고 건강한 신체 습관을 기르는 도전입니다. 물통을 항상 가지고 다니며 규칙적으로 마시는 것이 중요합니다.',
        DATEADD('DAY', 2, CURRENT_DATE()), 14);

INSERT INTO challenge (id, user_id, title, description, start_date, duration)
VALUES (4, 2, '아침 6시 기상하기',
        '30일 동안 매일 아침 6시에 기상하여 아침 시간을 효율적으로 활용하고 규칙적인 수면 패턴을 형성하는 도전입니다.',
        DATEADD('DAY', -20, CURRENT_DATE()), 30);

INSERT INTO challenge (id, user_id, title, description, start_date, duration)
VALUES (5, 1, '매일 30페이지 책 읽기',
        '매일 30페이지 이상의 책을 읽어 독서 습관을 기르고 지식을 확장하는 도전입니다. 다양한 장르의 책을 읽으면 더욱 효과적입니다.',
        DATEADD('DAY', -55, CURRENT_DATE()), 60);

ALTER TABLE challenge ALTER COLUMN id RESTART WITH 6;

-- 종료된 챌린지 데이터 (진행 중인 챌린지와 중복되지 않는 내용)
INSERT INTO last_challenge (id, user_id, title, description, start_date, end_date, retrospection, assessment)
VALUES (1, 1, '디지털 디톡스',
        '하루 2시간 이상 스마트폰 사용을 자제하고 실제 생활에 집중하는 도전입니다.',
        DATEADD('DAY', -30, CURRENT_DATE()), DATEADD('DAY', 0, CURRENT_DATE()),
        '디지털 기기 사용 시간을 줄이니 삶에 여유가 생겼습니다.',
        '목표를 완벽하게 달성하고 디지털 사용 시간을 크게 줄이는 데 성공했습니다. 훌륭한 성과입니다.');

INSERT INTO last_challenge (id, user_id, title, description, start_date, end_date, retrospection, assessment)
VALUES (2, 1, '하루에 감사한 일 3가지 기록하기',
        '매일 감사한 일 3가지를 기록하여 긍정적인 마인드를 형성하는 도전입니다.',
        DATEADD('DAY', -21, CURRENT_DATE()), DATEADD('DAY', -1, CURRENT_DATE()),
        '사소한 것에도 감사하게 되었지만 매일 기록하기가 쉽지 않았습니다.',
        '일부 날짜에는 기록하지 못했지만, 전반적으로 긍정적인 태도 변화가 관찰되어 부분적인 성공으로 평가합니다.');

INSERT INTO last_challenge (id, user_id, title, description, start_date, end_date, retrospection, assessment)
VALUES (3, 2, '일주일에 3권 독서하기',
        '매주 3권의 책을 읽어 지식을 확장하고 독서 습관을 기르는 도전입니다.',
        DATEADD('DAY', -14, CURRENT_DATE()), DATEADD('DAY', -1, CURRENT_DATE()),
        '다양한 분야의 책을 접하게 되어 시야가 넓어졌습니다.',
        '목표한 책 수를 모두 읽고 다양한 분야의 지식을 습득하여 독서 습관 형성에 완전히 성공했습니다.');

ALTER TABLE last_challenge ALTER COLUMN id RESTART WITH 4;
