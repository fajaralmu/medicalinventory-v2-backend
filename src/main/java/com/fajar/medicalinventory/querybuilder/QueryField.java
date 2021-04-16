package com.fajar.medicalinventory.querybuilder;

import java.lang.reflect.Field;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryField {
	private Field modelField;
	private Field entityField;
	private String otherName;

}
