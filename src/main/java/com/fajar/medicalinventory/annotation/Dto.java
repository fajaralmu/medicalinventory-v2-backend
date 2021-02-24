package com.fajar.medicalinventory.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.fajar.medicalinventory.constants.FormInputColumn;
import com.fajar.medicalinventory.entity.BaseEntity;

@Retention(RetentionPolicy.RUNTIME)  
@Target(ElementType.TYPE)  
public @interface Dto {

	FormInputColumn formInputColumn() default FormInputColumn.TWO_COLUMN;
	boolean ignoreBaseField() default true;
	boolean editable() default true;
	String value() default ""; 
	boolean creatable() default true;
	String updateService() default "commonUpdateService";
	public boolean commonManagementPage() default true; 
	public boolean withProgressWhenUpdated() default false; 
 
	 
}
