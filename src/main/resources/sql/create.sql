CREATE TABLE `member` (
    `member_id` int(11) NOT NULL AUTO_INCREMENT,
    `login_id` varchar(45) NOT NULL,
    `password` varchar(255) NOT NULL,
    `nickname` varchar(45) DEFAULT NULL,
    `email` varchar(45) NOT NULL,
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `profile_image` varchar(255) DEFAULT 'avatar.png',
    `point` int(11) NOT NULL DEFAULT '100',
    `gender` char(1) DEFAULT 'U',
    PRIMARY KEY (`member_id`),
    UNIQUE KEY `login_id_UNIQUE` (`login_id`),
    UNIQUE KEY `email_UNIQUE` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8



CREATE TABLE todo (
    dno           INT AUTO_INCREMENT PRIMARY KEY, -- PK, 자동 증가
    title         VARCHAR(200)      NOT NULL,     -- 제목
    content       TEXT,                           -- 상세 내용
    location      VARCHAR(200),                   -- 위치 정보
    writer        VARCHAR(100),                   -- 작성자
    is_finished   TINYINT(1)      DEFAULT 0,      -- 완료 여부 (0:미완료, 1:완료)
    is_important  TINYINT(1)      DEFAULT 0,      -- 중요 여부 (0:일반, 1:중요)
    duedate       DATETIME,                       -- 마감일(날짜/시간)
    created_at    DATETIME         DEFAULT CURRENT_TIMESTAMP, -- 생성일시
    updated_at    DATETIME         DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 수정일시
    INDEX idx_writer (writer),                    -- 작성자 인덱스(검색용)
    INDEX idx_duedate (duedate)                   -- 마감일 인덱스(정렬/검색용)
);