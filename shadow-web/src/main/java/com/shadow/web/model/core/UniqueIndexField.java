package com.shadow.web.model.core;

import lombok.Data;

@Data
public class UniqueIndexField {
	private Integer id;
	private Integer indexId;
	private String fieldName;
	private String fieldType;
}
