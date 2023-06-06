package com.shadow.web.utils.ParamUtils;

public final class Constants {

	// Spring profile for development, production
	public static final String SPRING_PROFILE_DEVELOPMENT = "dev";

	public static final String SPRING_PROFILE_PRODUCT = "prod";

	public static final String DEFAULT_SORTER_OPTION = "id ASC";

	public static final String SOFT_ASCEND = "ASC";
	public static final String SOFT_DESCEND = "DESC";

	public static final String ASCEND = "ascend";
	public static final String DESCEND = "descend";

	public static final String FLAG_ADD = "ADD";
	public static final String FLAG_UPDATE = "UPDATE";
	public static final String FLAG_DELETE = "DELETE";

	// =================================================== 返回消息 ====================================================
    // 通用
	public static final String MSG_DATABASE_OPERATION_ERROR = "数据库操作异常";
	public static final String MSG_DATABASE_OPERATION_ERROR_M1 = "数据库操作异常，返回值为-1";
	public static final String MSG_PARAM_ERROR = "输入参数错误";
	public static final String MSG_DATE_ERROR = "日期格式错误";
	public static final String MSG_QUERY_FAILED = "查询失败";
	public static final String MSG_DELETE_FAILED = "删除数据失败";
	public static final String MSG_INSERT_FAILED = "插入数据失败";
	public static final String MSG_EDIT_FAILED = "修改数据失败";
    // 用户
    public static final String MSG_CUSTOMER_NOT_EXIST = "用户不存在";
    // 水价
    public static final String MSG_WATER_PRICE_NOT_EXIST = "水价不存在";
    public static final String MSG_WATER_PRICE_NAME_EXIST = "水价名称已存在";
    // 水表
    public static final String MSG_WATER_METER_NOT_EXIST = "水表不存在";
    // 公司
	public static final String MSG_COMPANY_NOT_EXIST = "公司不存在";
	public static final String MSG_COMPANY_HAS_SUBSIDIARY = "公司有下属子公司";
	public static final String MSG_COMPANY_HAS_EMPLOYEE = "公司有下属员工";
	// 报警信息
	public static final String MSG_ALARM_NOT_EXIST = "报警信息不存在";
	public static final String MSG_ALARM_GROUP_ERROR = "报警类型错误";
	public static final String MSG_ALARM_CODE_ERROR = "报警类型码错误";
	// 员工
	public static final String MSG_USER_NOT_EXIST = "员工不存在";
	public static final String MSG_USER_LOGIN_NAME_EXIST = "登录名已存在";
	// 水表更换记录
	public static final String MSG_METER_CHANGE_NOT_EXIST = "无相关更换记录";

	// =================================================== 枚举类型 ====================================================
	public static final String ENUM_TYPE_METER_TYPE = "METER_STATUS";	// 水表状态

	// ================================================== 数据库字段 ===================================================
	public static final int DB_DELETED = 1;
	public static final int DB_NOT_DELETED = 0;
	// 公司表
	public static final int DB_COMPANY_LEVEL_HEAD_OFFICE = 0;		// 总公司
	public static final int DB_COMPANY_LEVEL_BRANCH_COMPANY = 1;	// 子公司
	public static final int DB_COMPANY_LEVEL_BUSINESS_HALL = 2;		// 水务大厅
	// 用水记录
	public static final String DB_WATER_RECORD_DAY = "DAY";		// 用水记录以天为单位
	public static final String DB_WATER_RECORD_MONTH = "MONTH"; // 用水记录一月为单位
	// 支付记录
	public static final String DB_PAY_REFUND_PAY_WAY_HALL = "HALL";		// 大厅缴费
	public static final String DB_PAY_REFUND_PAY_WAY_WEIXIN = "WEIXIN";	// 微信缴费
	// 微信小程序用水记录
	public static final String WATER_RECORD_DAY = "日";		// 用水记录以天为单位
	public static final String WATER_RECORD_MONTH = "月"; // 用水记录一月为单位

	// ================================================= 日志操作类型 ==================================================
	public static final String LOG_OP_INTERFACE = "INTERFACE"; // 接口操作
	public static final String LOG_OP_OPERATION = "OPERATION";	// 页面操作
}
