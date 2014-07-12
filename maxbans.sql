CREATE TABLE bans (
profile  varchar(40) NOT NULL,
banner  varchar(40) NOT NULL,
reason  varchar(200),
created  BIGINT NOT NULL,
expires  BIGINT NOT NULL DEFAULT 0,
PRIMARY KEY (profile)
);

-- ----------------------------
-- Records of bans
-- ----------------------------
INSERT INTO bans VALUES ('04978f72-2c79-4c70-98e6-a23f3ea9f9a1', '04978f72-2c79-4c70-98e6-a23f3ea9f9a1', 'Example UUID Ban Reason', 0, 1405160190000);

-- ----------------------------
-- Table structure for "ipbans"
-- ----------------------------
CREATE TABLE ipbans (
ip_range  varchar(30) NOT NULL,
banner  varchar(40) NOT NULL,
reason  varchar(200),
created  BIGINT NOT NULL,
expires  BIGINT NOT NULL DEFAULT 0,
PRIMARY KEY (ip_range)
);

-- ----------------------------
-- Records of ipbans
-- ----------------------------

-- ----------------------------
-- Table structure for "mutes"
-- ----------------------------
CREATE TABLE mutes (
profile  varchar(40) NOT NULL,
banner  varchar(40) NOT NULL,
reason  varchar(200),
created  BIGINT NOT NULL,
expires  BIGINT NOT NULL DEFAULT 0,
PRIMARY KEY (profile ASC)
);

-- ----------------------------
-- Records of mutes
-- ----------------------------
INSERT INTO mutes VALUES ('04978f72-2c79-4c70-98e6-a23f3ea9f9a1', '04978f72-2c79-4c70-98e6-a23f3ea9f9a1', 'Exampe Mute Reason', 0, 1405160190000);

-- ----------------------------
-- Table structure for "profiles"
-- ----------------------------
CREATE TABLE profiles (
uuid  varchar(40) NOT NULL,
user  varchar(30) NOT NULL,
lastIp  varchar(15),
lastSeen  BIGINT NOT NULL DEFAULT 0,
PRIMARY KEY (uuid ASC)
);

-- ----------------------------
-- Records of profiles
-- ----------------------------
INSERT INTO profiles VALUES ('04978f72-2c79-4c70-98e6-a23f3ea9f9a1', 'maxbans', '127.0.0.1', 0);
