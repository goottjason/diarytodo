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