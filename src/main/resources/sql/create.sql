CREATE TABLE `boardlike` (
                             `no` int NOT NULL AUTO_INCREMENT,
                             `who` varchar(8) DEFAULT NULL,
                             `boardNo` int DEFAULT NULL,
                             PRIMARY KEY (`no`),
                             KEY `fk_boardlike_boardNo_idx` (`boardNo`),
                             KEY `fk_boardlike_who_idx` (`who`),
                             CONSTRAINT `fk_boardlike_hboard` FOREIGN KEY (`boardNo`) REFERENCES `hboard` (`boardNo`),
                             CONSTRAINT `fk_boardlike_member` FOREIGN KEY (`who`) REFERENCES `member` (`memberId`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci

CREATE TABLE `boardreadlog` (
                                `boardReadLogNo` int NOT NULL AUTO_INCREMENT,
                                `readWho` varchar(130) NOT NULL,
                                `readWhen` datetime DEFAULT CURRENT_TIMESTAMP,
                                `boardNo` int NOT NULL,
                                PRIMARY KEY (`boardReadLogNo`),
                                KEY `fk_boardreadlog_boardno_idx` (`boardNo`),
                                CONSTRAINT `fk_boardreadlog_boardno` FOREIGN KEY (`boardNo`) REFERENCES `hboard` (`boardNo`)
) ENGINE=InnoDB AUTO_INCREMENT=112 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci


CREATE TABLE `boardupfiles` (
                                `fileNo` int NOT NULL AUTO_INCREMENT,
                                `originalFileName` varchar(100) NOT NULL,
                                `newFileName` varchar(150) NOT NULL,
                                `thumbFileName` varchar(150) DEFAULT NULL,
                                `isImage` tinyint DEFAULT '0',
                                `ext` varchar(20) DEFAULT NULL,
                                `size` bigint DEFAULT NULL,
                                `boardNo` int DEFAULT NULL,
                                `base64` longtext,
                                `filePath` varchar(200) DEFAULT NULL,
                                PRIMARY KEY (`fileNo`),
                                KEY `fk_upfiles_hboard_idx` (`boardNo`),
                                CONSTRAINT `fk_upfiles_hboard` FOREIGN KEY (`boardNo`) REFERENCES `hboard` (`boardNo`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=42 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='게시글에 업로드되는 파일정보'


CREATE TABLE `comment` (
                           `commentNo` int NOT NULL AUTO_INCREMENT,
                           `commenter` varchar(8) DEFAULT NULL,
                           `content` varchar(500) DEFAULT NULL,
                           `regDate` datetime DEFAULT CURRENT_TIMESTAMP,
                           `boardNo` int DEFAULT NULL,
                           PRIMARY KEY (`commentNo`),
                           KEY `fk_comm-mem_idx` (`commenter`),
                           KEY `fk_comm_hboard_idx` (`boardNo`),
                           CONSTRAINT `fk_comm-mem` FOREIGN KEY (`commenter`) REFERENCES `member` (`memberId`) ON DELETE CASCADE,
                           CONSTRAINT `fk_comm_hboard` FOREIGN KEY (`boardNo`) REFERENCES `hboard` (`boardNo`)
) ENGINE=InnoDB AUTO_INCREMENT=102 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci


CREATE TABLE `comment` (
                           `commentNo` int NOT NULL AUTO_INCREMENT,
                           `commenter` varchar(8) DEFAULT NULL,
                           `content` varchar(500) DEFAULT NULL,
                           `regDate` datetime DEFAULT CURRENT_TIMESTAMP,
                           `boardNo` int DEFAULT NULL,
                           PRIMARY KEY (`commentNo`),
                           KEY `fk_comm-mem_idx` (`commenter`),
                           KEY `fk_comm_hboard_idx` (`boardNo`),
                           CONSTRAINT `fk_comm-mem` FOREIGN KEY (`commenter`) REFERENCES `member` (`memberId`) ON DELETE CASCADE,
                           CONSTRAINT `fk_comm_hboard` FOREIGN KEY (`boardNo`) REFERENCES `hboard` (`boardNo`)
) ENGINE=InnoDB AUTO_INCREMENT=102 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci


CREATE TABLE `hboard` (
                          `boardNo` int NOT NULL AUTO_INCREMENT,
                          `title` varchar(20) NOT NULL,
                          `content` varchar(2000) DEFAULT NULL,
                          `writer` varchar(8) DEFAULT NULL,
                          `postDate` datetime DEFAULT CURRENT_TIMESTAMP,
                          `readCount` int DEFAULT '0',
                          `ref` int DEFAULT '0',
                          `step` int DEFAULT '0',
                          `refOrder` int DEFAULT '0',
                          `isDelete` char(1) DEFAULT 'N',
                          `boardType` varchar(10) DEFAULT NULL,
                          PRIMARY KEY (`boardNo`),
                          KEY `fk_hboard_member_idx` (`writer`),
                          CONSTRAINT `fk_hboard_member` FOREIGN KEY (`writer`) REFERENCES `member` (`memberId`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=1058 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='계층형 게시판'




CREATE TABLE `member` (
                          `memberId` varchar(8) NOT NULL,
                          `memberPwd` varchar(200) NOT NULL,
                          `memberName` varchar(12) DEFAULT NULL,
                          `mobile` varchar(13) DEFAULT NULL,
                          `email` varchar(50) DEFAULT NULL,
                          `registerDate` datetime DEFAULT CURRENT_TIMESTAMP,
                          `memberImg` varchar(100) NOT NULL DEFAULT 'avatar.png',
                          `memberPoint` int DEFAULT '100',
                          `gender` varchar(1) NOT NULL DEFAULT 'U',
                          `sesid` varchar(40) DEFAULT NULL,
                          `allimit` datetime DEFAULT NULL,
                          PRIMARY KEY (`memberId`),
                          UNIQUE KEY `mobile_UNIQUE` (`mobile`),
                          UNIQUE KEY `email_UNIQUE` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci



CREATE TABLE `pointdef` (
                            `pointWhy` enum('SIGNUP','LOGIN','WRITE','REPLY') NOT NULL,
                            `pointScore` int NOT NULL,
                            PRIMARY KEY (`pointWhy`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='멤버에게 적립할 포인트에 대한 정책'



CREATE TABLE `pointlog` (
                            `pointLogNo` int NOT NULL AUTO_INCREMENT,
                            `pointWho` varchar(8) NOT NULL,
                            `pointWhen` datetime DEFAULT CURRENT_TIMESTAMP,
                            `pointWhy` enum('SIGNUP','LOGIN','WRITE','REPLY') NOT NULL,
                            `pointScore` int DEFAULT NULL,
                            PRIMARY KEY (`pointLogNo`),
                            KEY `fk_pointlog_member_idx` (`pointWho`),
                            KEY `fk_pointlog_pointdef_idx` (`pointWhy`),
                            CONSTRAINT `fk_pointlog_member` FOREIGN KEY (`pointWho`) REFERENCES `member` (`memberId`),
                            CONSTRAINT `fk_pointlog_pointdef` FOREIGN KEY (`pointWhy`) REFERENCES `pointdef` (`pointWhy`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='멤버에게 지급된 포인트 기록 테이블'



CREATE TABLE `selfhboard` (
                              `boardNo` int NOT NULL AUTO_INCREMENT,
                              `title` varchar(50) NOT NULL,
                              `content` varchar(500) DEFAULT '내용없음',
                              `writer` varchar(20) NOT NULL,
                              `postDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              `readCount` int NOT NULL DEFAULT '0',
                              `ref` int NOT NULL DEFAULT '0',
                              `step` int NOT NULL DEFAULT '0',
                              `refOrder` int NOT NULL DEFAULT '0',
                              `status` varchar(1) DEFAULT 'A',
                              PRIMARY KEY (`boardNo`),
                              KEY `fk_writer_idx` (`writer`),
                              CONSTRAINT `fk_writer` FOREIGN KEY (`writer`) REFERENCES `selfmember` (`memberId`)
) ENGINE=InnoDB AUTO_INCREMENT=307 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci



CREATE TABLE `selfhboardlog` (
                                 `boardReadLogNo` int NOT NULL AUTO_INCREMENT,
                                 `readWho` varchar(130) NOT NULL,
                                 `readWhen` datetime DEFAULT CURRENT_TIMESTAMP,
                                 `boardNo` int NOT NULL,
                                 PRIMARY KEY (`boardReadLogNo`),
                                 KEY `fk_boardreadlog_boardno_idx` (`boardNo`),
                                 CONSTRAINT `fk_hboardlog_boardno` FOREIGN KEY (`boardNo`) REFERENCES `selfhboard` (`boardNo`)
) ENGINE=InnoDB AUTO_INCREMENT=89 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci



CREATE TABLE `selfmember` (
                              `memberId` varchar(20) NOT NULL,
                              `memberPwd` varchar(200) NOT NULL,
                              `memberName` varchar(100) NOT NULL,
                              `memberMobile` varchar(13) NOT NULL,
                              `memberEmail` varchar(100) NOT NULL,
                              `memberRegDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              `memberImg` varchar(200) NOT NULL DEFAULT 'avatar.png',
                              `memberPoint` int NOT NULL DEFAULT '1000',
                              `memberGender` varchar(1) NOT NULL DEFAULT 'U',
                              PRIMARY KEY (`memberId`),
                              UNIQUE KEY `memberMobile_UNIQUE` (`memberMobile`),
                              UNIQUE KEY `memberEmail_UNIQUE` (`memberEmail`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci