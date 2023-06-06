package com.shadow.web.model.core;

import lombok.Data;

import java.util.List;

/**
 * 
 * @author Administrator
 *
 */
@Data
public class UniqueIndexInfo {
	
	private Integer id;
	private String tableName;
	private String indexName;
	private List<IndexFieldInfo> fieldInfos;
	
	@Data
	public static class IndexFieldInfo {
		private String id;
		private String fieldType;
		private String fieldName;
		private Object fieldValue;
	}
}
