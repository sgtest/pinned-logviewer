DROP TABLE IF EXISTS "users";
CREATE TABLE "users" (
                         "id_user" TEXT(50) NOT NULL,
                         "name_user" TEXT(50),
                         "password" TEXT(64) NOT NULL,
                         "create_time" text(32),
                         "email" TEXT(64),
                         "organization" TEXT(64),
                         "cd_phone" TEXT(32),
                         "expire_time" TEXT(32),
                         "user_flag" TEXT(1),
                         "permission" TEXT,
                         PRIMARY KEY ("id_user")
);

INSERT INTO "users" VALUES ('admin', NULL, 'DC1FD00E3EEEB940FF46F457BF97D66BA7FCC36E0B20802383DE142860E76AE6', NULL, NULL, NULL, NULL, NULL, NULL, 'ALL');
INSERT INTO "users" VALUES ('test', NULL, '55E12E91650D2FEC56EC74E1D3E4DDBFCE2EF3A65890C2A19ECF88A307E76A23', NULL, NULL, NULL, NULL, NULL, NULL, 'ADD,');
INSERT INTO "users" VALUES ('developer', NULL, '82BE8D25346156C92E408CF511675D9075EEB955D9C5FD9A57505D9CDFBBAAC9', NULL, NULL, NULL, NULL, NULL, NULL, 'ADD,');


DROP TABLE IF EXISTS "common_project_mgmt";
CREATE TABLE "common_project_mgmt" (
                                       "id_host" TEXT NOT NULL,
                                       "id_project" TEXT NOT NULL,
                                       "name_project" TEXT NOT NULL,
                                       "cd_path" TEXT NOT NULL,
                                       "cmd_start" TEXT,
                                       "cmd_stop" TEXT,
                                       "cmd_restart" TEXT,
                                       "cmd_refresh" TEXT,
                                       "cmd_status" TEXT,
                                       "cmd_status_success_key" TEXT,
                                       "cd_description" TEXT,
                                       "cd_tag" TEXT,
                                       PRIMARY KEY ("id_host", "id_project")
);
INSERT INTO "common_project_mgmt" ("id_host", "id_project", "name_project", "cd_path", "cmd_start", "cmd_stop", "cmd_restart", "cmd_refresh", "cmd_status", "cmd_status_success_key", "cd_description", "cd_tag") VALUES ('192.168.190.160', 'nginx', 'nginx', '/usr/local/nginx/sbin', './nginx ', './nginx -s stop', '', './nginx -s reload', 'ps -ef | grep nginx', 'nginx: master', 'nginx配置示例', '');

-- ----------------------------
-- Table structure for connection_info
-- ----------------------------
DROP TABLE IF EXISTS "connection_info";
CREATE TABLE "connection_info" (
                                   "id_host" TEXT NOT NULL,
                                   "cd_port" TEXT NOT NULL,
                                   "id_user" TEXT,
                                   "cd_password" TEXT,
                                   "cd_key_path" TEXT,
                                   "cd_logpath" TEXT,
                                   PRIMARY KEY ("id_host", "cd_port")
);

-- ----------------------------
-- Records of connection_info
-- ----------------------------
INSERT INTO "connection_info" VALUES ('192.168.190.100', '22', 'root', 'test', NULL, NULL);

-- ----------------------------
-- Table structure for log_path
-- ----------------------------
DROP TABLE IF EXISTS "log_path";
CREATE TABLE "log_path" (
                            "id_loghost" TEXT(32) NOT NULL,
                            "id_log_path" TEXT(64) NOT NULL,
                            "name_log" TEXT(32),
                            PRIMARY KEY ("id_loghost", "id_log_path")
);

DROP TABLE IF EXISTS "projects";
CREATE TABLE "projects" (
                            "id_host" TEXT(32) NOT NULL,
                            "id_project" TEXT(64) NOT NULL,
                            "name_project" TEXT(32),
                            "cd_parent_path" TEXT(128) NOT NULL,
                            "cd_tag" TEXT(32),
                            "cd_command" TEXT(128),
                            "jvm_param" TEXT(128),
                            "jar_param" TEXT(64),
                            "jar_name" TEXT(128),
                            "cd_description" TEXT(128),
                            PRIMARY KEY ("id_host", "id_project")
);


-- ----------------------------
-- Table structure for tomcat_info
-- ----------------------------
DROP TABLE IF EXISTS "tomcat_info";
CREATE TABLE "tomcat_info" (
                               "id_host" TEXT NOT NULL,
                               "tomcat_id" text(50) NOT NULL,
                               "name_tomcat" TEXT(64),
                               "tomcat_path" text(100),
                               "webapp_path" text(100),
                               "tag" TEXT(32),
                               "cd_description" TEXT(150),
                               PRIMARY KEY ("id_host", "tomcat_id")
);
