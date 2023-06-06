package com.shadow.web.utils.ParamUtils;

import org.apache.commons.lang3.StringUtils;

public class SqlUtils {
	public static String wrapLike(String val) {
		return StringUtils.isEmpty(val) ? null : "%" + val + "%";
	}

}
