package com.pkm.medicalinventory.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.pkm.medicalinventory.constants.FormInputColumn;

@Retention(RetentionPolicy.RUNTIME)  
@Target(ElementType.TYPE)  
public @interface Dto {

	FormInputColumn formInputColumn() default FormInputColumn.TWO_COLUMN;
	
	String managementService() default "baseManagementService";
	String value() default "";
	
	boolean ignoreBaseField() default true;
	boolean editable() default true;
	boolean creatable() default true;
	boolean deletable() default true;
	boolean commonManagementPage() default true; 
	boolean withProgressWhenUpdated() default false; 
 
	 
}
