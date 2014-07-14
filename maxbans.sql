CREATE TABLE bans (
profile  varchar(40) NOT NULL,
banner  varchar(40) NOT NULL,
reason  varchar(200),
created  BIGINT NOT NULL,
expires  BIGINT NOT NULL DEFAULT 0,
PRIMARY KEY (profile)
);

CREATE TABLE ipbans (
ip_range  varchar(30) NOT NULL,
banner  varchar(40) NOT NULL,
reason  varchar(200),
created  BIGINT NOT NULL,
expires  BIGINT NOT NULL DEFAULT 0,
PRIMARY KEY (ip_range)
);

CREATE TABLE mutes (
profile  varchar(40) NOT NULL,
banner  varchar(40) NOT NULL,
reason  varchar(200),
created  BIGINT NOT NULL,
expires  BIGINT NOT NULL DEFAULT 0,
PRIMARY KEY (profile ASC)
);

CREATE TABLE profiles (
uuid  varchar(40) NOT NULL,
user  varchar(30) NOT NULL,
lastIp  varchar(15),
lastSeen  BIGINT NOT NULL DEFAULT 0,
PRIMARY KEY (uuid ASC)
);

