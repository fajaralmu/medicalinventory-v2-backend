package com.fajar.medicalinventory.util;

import java.math.BigDecimal;
import java.util.Base64;
import java.util.Random;
 
public class StringUtil {

	static final Random rand = new Random();

	public static final String  ALPHABET = "abcdefghijklmnopqrstuvwxyz";
	public static final String[] GREEK_NUMBER = new String[] { "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX",
			"X" };

	public static String generateRandomNumber(int length) {

		String random = "";
		if (length < 1) {
			length = 1;
		}

		for (int i = 0; i < length; i++) {

			Integer n = rand.nextInt(9);
			random += n;
		}
		return random;
	}
 

	public static String addZeroBefore(Integer number) {
		return number < 10 ? "0" + number : number.toString();
	}

	public static String buildString(String... strings) {

		StringBuilder stringBuilder = new StringBuilder();

		for (String string : strings) {
			stringBuilder.append(" ").append(string);
		}

		return stringBuilder.toString();
	}

	public static String buildTableColumnDoubleQuoted(String tableName, String columnName) {
		return buildString(doubleQuoteMysql(tableName), ".", doubleQuoteMysql(columnName));
	}

	public static String doubleQuoteMysql(String str) {
		return " `".concat(str).concat("` ");
	}

	static boolean isUpperCase(char _char) {
//		StringBuilder str = new StringBuilder();
//		str.append(_char);
		String str = String.valueOf(_char);
		return str.equals(str.toUpperCase());
	}

	public static String extractCamelCase(String camelCased) {

		StringBuilder result = new StringBuilder();

		for (int i = 0; i < camelCased.length(); i++) {
			char _char = camelCased.charAt(i);
			if (isUpperCase(_char)) {
				result.append(" ");
			}
			if (0 == i) {
				result.append(("" + _char).toUpperCase());
			} else
				result.append(_char);
		}

		return result.toString();
	}

	public static String lowerCaseFirstChar(String string) {
		try {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(String.valueOf(string.charAt(0)).toLowerCase());
			stringBuilder.append(string.substring(1, string.length()));

			return stringBuilder.toString();
		} catch (Exception e) {

			return string;
		}
	}

	public static int base64StringFileSize  (String base64String)   {
		if (base64String.contains(",")) {
			base64String = base64String.split(",")[1];
		}
		int stringLength = base64String.length();

		double sizeInBytes = 4 * Math.ceil((stringLength / 3))*0.5624896334383812;
		return new BigDecimal(sizeInBytes).intValue();
	}
	
	public static String[] divideStringInto(String string, double partialSize) {
		int stringLength = string.length();
//		int partialSize = (stringLength / count);
		int arrayCOunt = (int) Math.ceil(stringLength / partialSize);
		String[] strings = new String[ arrayCOunt];
		int partialSizeCounter = 0;
		int order = 0;
		System.out.println("partialSize: "+partialSize+ " arrayCOunt: "+arrayCOunt);
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < stringLength; i++) {
			stringBuilder.append(string.charAt(i));
			partialSizeCounter++;
			 
			if (partialSizeCounter == partialSize) {
				 
				partialSizeCounter = 0;
				strings[order] = stringBuilder.toString();
				order++;
				
				stringBuilder = new StringBuilder();
			} else
			if (i == stringLength - 1) {
				 strings[order] = stringBuilder.toString();
			}
//			System.out.println("ORDER: "+order);
		}
		int dividedStringLengthSummary = 0;
		for (int i = 0; i < strings.length; i++) {
			if (strings[i] == null) continue;
			dividedStringLengthSummary += strings[i].length();
		}
		System.out.println("String length: "+ stringLength+", didived string length accumulation: "+ dividedStringLengthSummary);
		return strings ;
	}
	
	public static void main(String[] args) {
		byte[] b = new byte[] {1,0,0,0,0,0,0,0,0};
		System.out.println(toBase64(b));
	}


	public static String toBase64(byte[] src) {
		byte[] encoded = Base64.getEncoder().encode(src);
		return new String(encoded);
	}


	public static String twoDigits(int number) {
		if (number >= 10) {
			return String.valueOf(number);
		}
		return "0"+number;
	}
}
