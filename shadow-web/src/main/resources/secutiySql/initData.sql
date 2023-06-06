-- =============================================【系统预置用户数据】============================================================================================
-- 超级管理员
INSERT INTO `cm_user`(id,username,password,name,state,deleted) VALUES ('1', 'admin', '$2a$10$ZEAaISuWX23UUjXeQkNYeOd70AHRncfynlETpIGXAiYFhuqInshZq', '管理员','1','0');
INSERT INTO `cm_role`(id,role_name,role_desc,is_system) VALUES ('1', 'am_admin_super', '后台超级管理员', 1);
INSERT INTO `cm_permission`(id,permission_name,permission_desc,is_system) VALUES ('1', 'am_admin_super', '后台超级管理员专属权限', 1);
INSERT INTO `cm_user_role`(id,user_id,role_id) values ('1','1','1');
INSERT INTO `cm_role_permission`(id,role_id,permission_id) VALUES ('1', '1', '1');

-- ==============================================【系统预置权限及权限结构化数据】===============================================================================
-- =====================================================权限树结构==========================================================
INSERT INTO `cm_function`(id,name,description,parent_id) VALUES ('10', '所有权限', '权限树', null);

INSERT INTO `cm_function`(id,name,description,parent_id) VALUES ('11', '权限设置', '权限树', '10');
INSERT INTO `cm_function`(id,name,description,parent_id) VALUES ('12', '产品中心', '权限树', '10' );

INSERT INTO `cm_function`(id,name,description,parent_id) VALUES ('110', '人员管理', '权限树', '11');
INSERT INTO `cm_function`(id,name,description,parent_id) VALUES ('111', '角色管理', '权限树', '11');
INSERT INTO `cm_function`(id,name,description,parent_id) VALUES ('120', '产品定义', '权限树', '12' );
INSERT INTO `cm_function`(id,name,description,parent_id) VALUES ('121', '物模型定义', '权限树', '12' );

-- =====================================================权限名==========================================================
-- =======人员管理
INSERT INTO `cm_function`(id,name,description,parent_id) VALUES ('11001', '人员管理_查看', '人员管理权限', '110');
INSERT INTO `cm_function`(id,name,description,parent_id) VALUES ('11002', '人员管理_注销', '人员管理权限', '110');
INSERT INTO `cm_function`(id,name,description,parent_id) VALUES ('11003', '人员管理_重置密码', '人员管理权限', '110');

INSERT INTO `cm_permission`(id,permission_name,permission_desc,is_system) VALUES ('11001', 'cm_user_view', '人员管理_查看', 1);
INSERT INTO `cm_permission`(id,permission_name,permission_desc,is_system) VALUES ('11002', 'cm_user_delete', '人员管理_注销', 1);
INSERT INTO `cm_permission`(id,permission_name,permission_desc,is_system) VALUES ('11003', 'cm_user_reset', '人员管理_重置密码', 1);

INSERT INTO `cm_permission_function`(id,function_id,permission_id) VALUES ('11001', '11001', '11001');
INSERT INTO `cm_permission_function`(id,function_id,permission_id) VALUES ('11002', '11002', '11002');
INSERT INTO `cm_permission_function`(id,function_id,permission_id) VALUES ('11003', '11003', '11003');

INSERT INTO `cm_role_permission`(id,role_id,permission_id) VALUES ('11001', '1', '11001');
INSERT INTO `cm_role_permission`(id,role_id,permission_id) VALUES ('11002', '1', '11002');
INSERT INTO `cm_role_permission`(id,role_id,permission_id) VALUES ('11003', '1', '11003');

-- =======角色管理
INSERT INTO `cm_function`(id,name,description,parent_id) VALUES ('11101', '角色管理_查看', '角色管理权限', '111');
INSERT INTO `cm_function`(id,name,description,parent_id) VALUES ('11102', '角色管理_新增', '角色管理权限', '111');
INSERT INTO `cm_function`(id,name,description,parent_id) VALUES ('11103', '角色管理_编辑', '角色管理权限', '111');
INSERT INTO `cm_function`(id,name,description,parent_id) VALUES ('11104', '角色管理_设置人员_查看', '角色管理权限', '111');
INSERT INTO `cm_function`(id,name,description,parent_id) VALUES ('11105', '角色管理_设置人员_编辑', '角色管理权限', '111');
INSERT INTO `cm_function`(id,name,description,parent_id) VALUES ('11106', '角色管理_权限设置_查看', '角色管理权限', '111');
INSERT INTO `cm_function`(id,name,description,parent_id) VALUES ('11107', '角色管理_权限设置_编辑', '角色管理权限', '111');

INSERT INTO `cm_permission`(id,permission_name,permission_desc,is_system) VALUES ('11101', 'cm_role_view', '角色管理_查看', 1);
INSERT INTO `cm_permission`(id,permission_name,permission_desc,is_system) VALUES ('11102', 'cm_role_create', '角色管理_新增', 1);
INSERT INTO `cm_permission`(id,permission_name,permission_desc,is_system) VALUES ('11103', 'cm_role_update', '角色管理_编辑', 1);
INSERT INTO `cm_permission`(id,permission_name,permission_desc,is_system) VALUES ('11104', 'cm_role_view_user', '角色管理_设置人员_查看', 1);
INSERT INTO `cm_permission`(id,permission_name,permission_desc,is_system) VALUES ('11105', 'cm_role_set_user', '角色管理_设置人员_编辑', 1);
INSERT INTO `cm_permission`(id,permission_name,permission_desc,is_system) VALUES ('11106', 'cm_role_view_auth', '角色管理_权限设置_查看', 1);
INSERT INTO `cm_permission`(id,permission_name,permission_desc,is_system) VALUES ('11107', 'cm_role_set_auth', '角色管理_权限设置_编辑', 1);

INSERT INTO `cm_permission_function`(id,function_id,permission_id) VALUES ('11101', '11101', '11101');
INSERT INTO `cm_permission_function`(id,function_id,permission_id) VALUES ('11102', '11102', '11102');
INSERT INTO `cm_permission_function`(id,function_id,permission_id) VALUES ('11103', '11103', '11103');
INSERT INTO `cm_permission_function`(id,function_id,permission_id) VALUES ('11104', '11104', '11104');
INSERT INTO `cm_permission_function`(id,function_id,permission_id) VALUES ('11105', '11105', '11105');
INSERT INTO `cm_permission_function`(id,function_id,permission_id) VALUES ('11106', '11106', '11106');
INSERT INTO `cm_permission_function`(id,function_id,permission_id) VALUES ('11107', '11107', '11107');

INSERT INTO `cm_role_permission`(id,role_id,permission_id) VALUES ('11101', '1', '11101');
INSERT INTO `cm_role_permission`(id,role_id,permission_id) VALUES ('11102', '1', '11102');
INSERT INTO `cm_role_permission`(id,role_id,permission_id) VALUES ('11103', '1', '11103');
INSERT INTO `cm_role_permission`(id,role_id,permission_id) VALUES ('11104', '1', '11104');
INSERT INTO `cm_role_permission`(id,role_id,permission_id) VALUES ('11105', '1', '11105');
INSERT INTO `cm_role_permission`(id,role_id,permission_id) VALUES ('11106', '1', '11106');
INSERT INTO `cm_role_permission`(id,role_id,permission_id) VALUES ('11107', '1', '11107');

-- =======产品定义
INSERT INTO `cm_function`(id,name,description,parent_id) VALUES ('12001', '产品定义_查看', '产品定义', '120');
INSERT INTO `cm_function`(id,name,description,parent_id) VALUES ('12002', '产品定义_编辑', '产品定义', '120');
INSERT INTO `cm_function`(id,name,description,parent_id) VALUES ('12003', '产品定义_打包', '产品定义', '120');
INSERT INTO `cm_function`(id,name,description,parent_id) VALUES ('12004', '产品定义_删除', '产品定义', '120');
INSERT INTO `cm_function`(id,name,description,parent_id) VALUES ('12005', '产品定义_新增', '产品定义', '120');

INSERT INTO `cm_permission`(id,permission_name,permission_desc,is_system) VALUES ('12001', 'am_product_view', '产品定义_查看', 1);
INSERT INTO `cm_permission`(id,permission_name,permission_desc,is_system) VALUES ('12002', 'am_product_update', '产品定义_编辑', 1);
INSERT INTO `cm_permission`(id,permission_name,permission_desc,is_system) VALUES ('12003', 'am_product_download', '产品定义_打包', 1);
INSERT INTO `cm_permission`(id,permission_name,permission_desc,is_system) VALUES ('12004', 'am_product_delete', '产品定义_删除', 1);
INSERT INTO `cm_permission`(id,permission_name,permission_desc,is_system) VALUES ('12005', 'am_product_create', '产品定义_新增', 1);

INSERT INTO `cm_permission_function`(id,function_id,permission_id) VALUES ('12001', '12001', '12001');
INSERT INTO `cm_permission_function`(id,function_id,permission_id) VALUES ('12002', '12002', '12002');
INSERT INTO `cm_permission_function`(id,function_id,permission_id) VALUES ('12003', '12003', '12003');
INSERT INTO `cm_permission_function`(id,function_id,permission_id) VALUES ('12004', '12004', '12004');
INSERT INTO `cm_permission_function`(id,function_id,permission_id) VALUES ('12005', '12005', '12005');

INSERT INTO `cm_role_permission`(id,role_id,permission_id) VALUES ('12001', '1', '12001');
INSERT INTO `cm_role_permission`(id,role_id,permission_id) VALUES ('12002', '1', '12002');
INSERT INTO `cm_role_permission`(id,role_id,permission_id) VALUES ('12003', '1', '12003');
INSERT INTO `cm_role_permission`(id,role_id,permission_id) VALUES ('12004', '1', '12004');
INSERT INTO `cm_role_permission`(id,role_id,permission_id) VALUES ('12005', '1', '12005');

-- =======物模型定义
INSERT INTO `cm_function`(id,name,description,parent_id) VALUES ('12101', '物模型定义_新增', '物模型定义', '121' );
INSERT INTO `cm_function`(id,name,description,parent_id) VALUES ('12102', '物模型定义_查看', '物模型定义', '121' );
INSERT INTO `cm_function`(id,name,description,parent_id) VALUES ('12103', '物模型定义_删除', '物模型定义', '121' );

INSERT INTO `cm_permission`(id,permission_name,permission_desc,is_system) VALUES ('12101', 'am_device_create', '物模型定义_新增', 1);
INSERT INTO `cm_permission`(id,permission_name,permission_desc,is_system) VALUES ('12102', 'am_device_view', '物模型定义_查看', 1);
INSERT INTO `cm_permission`(id,permission_name,permission_desc,is_system) VALUES ('12103', 'am_device_delete', '物模型定义_删除', 1);

INSERT INTO `cm_permission_function`(id,function_id,permission_id) VALUES ('12101', '12101', '12101');
INSERT INTO `cm_permission_function`(id,function_id,permission_id) VALUES ('12102', '12102', '12102');
INSERT INTO `cm_permission_function`(id,function_id,permission_id) VALUES ('12103', '12103', '12103');

INSERT INTO `cm_role_permission`(id,role_id,permission_id) VALUES ('12101', '1', '12101');
INSERT INTO `cm_role_permission`(id,role_id,permission_id) VALUES ('12102', '1', '12102');
INSERT INTO `cm_role_permission`(id,role_id,permission_id) VALUES ('12103', '1', '12103');