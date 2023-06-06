package com.shadow.web.model.core;

import lombok.Data;

import java.util.List;

@Data
public class TableUniqueIndex {
	private String indexId;
	private String tableName;
	private String indexName;
	private List<UniqueIndexField> fieldList;
}
