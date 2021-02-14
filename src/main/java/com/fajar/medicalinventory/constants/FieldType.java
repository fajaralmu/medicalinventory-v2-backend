package com.fajar.medicalinventory.constants;

public enum FieldType {

	 FIELD_TYPE_TEXT ( "text"),
	 /**
	  * leave this field as null will remove value, leave as old value wont update field
	  */
	 FIELD_TYPE_IMAGE ( "img"),
	 FIELD_TYPE_CURRENCY ( "number"),
	 FIELD_TYPE_NUMBER ( "number"),
	 FIELD_TYPE_HIDDEN ( "hidden"),
	 FIELD_TYPE_COLOR ( "color"),
	 FIELD_TYPE_FIXED_LIST ("fixedlist"),
	 FIELD_TYPE_DYNAMIC_LIST ( "dynamiclist"),
	 FIELD_TYPE_TEXTAREA ( "textarea"),
	 FIELD_TYPE_PLAIN_LIST ( "plainlist"),
	 FIELD_TYPE_DATE ( "date"),
	 FIELD_TYPE_DATETIME ( "date"),
	 FIELD_TYPE_TEXTEDITOR ( "texteditor"),
	FIELD_TYPE_CHECKBOX ( "checkbox");
	public final String value;
	private FieldType(String val) {
		this.value = val;
	} 
}
