package com.shadow.web.model.params;

import com.shadow.web.utils.ParamUtils.ParamUtil;
import lombok.Data;

import java.util.Map;

/**
 * 查询列表的API的参数校验结果
 * 参数校验之后，把校验结果pageNum、pageSize、orderBy、查询条件对应的Example保存到这个里面
 * @author Administrator
 *
 */
@Data
public class ListParamValidResult<T> {
	public static Integer defaultPageNum = 1;
	public static Integer defaultPageSize = 10;
	
	private String errMsg;   //参数校验失败时的错误信息
	private Integer pageNum; //当前页码
	private Integer pageSize;//每页数量
	private String orderBy;  //orderBy
	private T example;  //查询列表用的Example或者其他东西
	
	public ListParamValidResult(String errMsg, Integer pageNum, Integer pageSize, String orderBy, T example) {
		this.errMsg = errMsg;
		this.pageNum = pageNum == null ? defaultPageNum : pageNum;
		this.pageSize = pageSize == null ? defaultPageSize : pageSize;
		this.orderBy = orderBy;
		this.example = example;
	}
	
	public ListParamValidResult() {
		this.pageNum = defaultPageNum;
		this.pageSize = defaultPageSize;
	}
	
	public ListParamValidResult(Map<String, Object> input) {
		if(input.containsKey("pageNum")) {
			this.pageNum = (Integer)input.get("pageNum");
		}else {
			this.pageNum = defaultPageNum;
		}
		if(input.containsKey("pageSize")) {
			this.pageSize = (Integer)input.get("pageSize");
		}else {
			this.pageSize = defaultPageSize;
		}
		if(input.containsKey("sorter")) {
			this.orderBy = ParamUtil.sortOpertion((String)input.get("sorter"));
		}
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public String getErrMsg() {

		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

	public boolean isSuccess() {
		return null == errMsg;
	}

	public T getExample() {
		return example;
	}

	public void setExample(T example) {
		this.example = example;
	}

	public Integer getPageNum() {
		return pageNum;
	}

	public void setPageNum(Integer pageNum) {
		this.pageNum = pageNum;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
}
