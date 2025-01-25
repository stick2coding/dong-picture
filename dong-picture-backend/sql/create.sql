-- 用户表
create table if not exists user
(
    id           bigint auto_increment comment 'id' primary key,
    userAccount  varchar(256)                           not null comment '账号',
    userPassword varchar(512)                           not null comment '密码',
    userName     varchar(256)                           null comment '用户昵称',
    userAvatar   varchar(1024)                          null comment '用户头像',
    userProfile  varchar(512)                           null comment '用户简介',
    userRole     varchar(256) default 'user'            not null comment '用户角色：user/admin',
    editTime     datetime     default CURRENT_TIMESTAMP not null comment '编辑时间',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      default 0                 not null comment '是否删除',
    UNIQUE KEY uk_userAccount (userAccount),
    INDEX idx_userName (userName)
    ) comment '用户' collate = utf8mb4_unicode_ci;

-- 编辑时间：编辑时间是指用户更新这条数据的时候的时间，由业务代码进行更新
-- 更新时间：更新时间是用户这条记录任何字段发生变化的时候，由数据库自动更新
-- 账号字段天然唯一，可以添加唯一索引
-- 常用字段添加索引
-- 扩展功能1：会员功能（role可以增加vip，然后新增几个字段表示会员的有效期等信息）
-- 扩展功能2：用户邀请功能（新增两个字段，一个是当前用户的邀请码，一个是当前用户是被谁邀请的）
