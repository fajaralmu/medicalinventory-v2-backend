package com.pkm.medicalinventory.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.pkm.medicalinventory.constants.FieldType;
import com.pkm.medicalinventory.constants.Filterable;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FormField {
 
	
	public FieldType type() default FieldType.FIELD_TYPE_TEXT; 
	
	public String labelName() default ""; 
	public String optionItemName() default "";  
	public String defaultValue() default ""; 
	public String[] availableValues() default {};
	public String[] detailFields() default {}; 
	public String[] multiply() default {};
	
	public boolean showDetail() default false;   
	public boolean required() default true;  
	public boolean emptyAble() default true;
	public boolean iconImage() default false;
	
	public boolean hasPreview() default false;
	public boolean editable() default true;
	public Filterable filterable() default Filterable.ENABLE_ALL; 
	public String entityField() default "";

}
