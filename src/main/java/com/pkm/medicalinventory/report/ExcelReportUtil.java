package com.pkm.medicalinventory.report;

import static com.pkm.medicalinventory.util.EntityUtil.getDeclaredField;
import static com.pkm.medicalinventory.util.MapUtil.objectEquals;
import static org.apache.poi.ss.usermodel.BorderStyle.THIN;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pkm.medicalinventory.annotation.FormField;
import com.pkm.medicalinventory.constants.FieldType;
import com.pkm.medicalinventory.dto.model.BaseModel;
import com.pkm.medicalinventory.entity.setting.EntityElement;
import com.pkm.medicalinventory.entity.setting.EntityProperty;
import com.pkm.medicalinventory.util.DateUtil;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExcelReportUtil {
	private static final String DATE_PATTERN = "dd-MM-yyyy' 'hh:mm:ss";

	/**
	 * 
	 * @param row
	 * @param countOfColumns
	 * @param borderStyle         @Nullable
	 * @param horizontalAlignment @Nullable
	 */
	public static void autosizeColumn(XSSFRow row, int countOfColumns, BorderStyle borderStyle,
			HorizontalAlignment horizontalAlignment) {
		if (row == null) {
			return;
		}
		for (int i = 0; i < countOfColumns; i++) {
			XSSFCell cell = row.getCell(i);
			if (cell == null) {
				continue;
			}
			if (borderStyle != null)
				cell.getCellStyle().setBorderTop(borderStyle);
			if (horizontalAlignment != null)
				cell.getCellStyle().setAlignment(horizontalAlignment);

			cell.getCellStyle().setVerticalAlignment(VerticalAlignment.CENTER);
			row.getSheet().autoSizeColumn(i);
		}
	}

	public static void addMergedRegion(XSSFSheet sheet, CellRangeAddress... cellRangeAddresses) {
		for (int i = 0; i < cellRangeAddresses.length; i++) {
			sheet.addMergedRegion(cellRangeAddresses[i]);
		}
	}

	public static void setBorderTop(XSSFRow row, BorderStyle borderStyle) {
		short first = row.getFirstCellNum();
		short last = row.getLastCellNum();
		for (short i = first; i < last; i++) {
			XSSFCell cell = row.getCell(i);
			if (cell.getCellStyle() == null) {
				cell.setCellStyle(row.getSheet().getWorkbook().createCellStyle());
			}
			try {
				setBorder(cell.getCellStyle(), borderStyle, THIN, THIN, THIN);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	public static void validateCellRange(XSSFSheet sheet, CellRangeAddress cellRangeAddress) {
		int firstRow = cellRangeAddress.getFirstRow();
		int lastRow = cellRangeAddress.getLastRow();
		int firstColumn = cellRangeAddress.getFirstColumn();
		int lastColumn = cellRangeAddress.getLastColumn();
		for (int row = firstRow; row <= lastRow; row++) {
			XSSFRow xssfRow = createRow(sheet, row);
			for (int col = firstColumn; col <= lastColumn; col++) {
				XSSFCell cell = createCell(xssfRow, col);
				setAllBorder(cell.getCellStyle(), THIN);
			}
		}
	}

	public static interface RowCreatedCallback {
		public void callback(int i, int totalRow);
	}

	public static void createTable(XSSFSheet sheet, int columCount, int xOffset, int yOffset, Object... values) {
		createTable(sheet, columCount, xOffset, yOffset, null, values);

	}

	public static void createTable(XSSFSheet sheet, int columCount, int xOffset, int yOffset,
			RowCreatedCallback rowCallback, Object... values) {
		Map<Integer, List<Object>> tableContent = getTableContent(columCount, values);

		for (Integer integer : tableContent.keySet()) {
			log.info("row: {} of {}", integer, tableContent.keySet().size() - 1);

			List<Object> valueList = tableContent.get(integer);
//			System.out.println(integer+"."+valueList);
			Object[] rowValues = valueList.toArray();
			XSSFRow row = createRow(sheet, integer + yOffset, xOffset, rowValues);
			autosizeColumn(row, rowValues.length, BorderStyle.THIN, null);

			if (null != rowCallback) {
				rowCallback.callback(integer, tableContent.keySet().size());
			}

		}
	}

	private static Map<Integer, List<Object>> getTableContent(int columCount, Object... values) {
		Map<Integer, List<Object>> tableContent = new HashMap<>();
		int rowNum = 0;
		for (int i = 0; i < values.length; i++) {

			if (tableContent.get(rowNum) == null) {
				tableContent.put(rowNum, new ArrayList<>());
			}
			tableContent.get(rowNum).add(values[i]);
			if ((i + 1) % columCount == 0) {
				rowNum++;
			}
		}
		return tableContent;
	}

	public static XSSFCell createCell(XSSFRow row, int col) {

		XSSFCell cell = row.getCell(col);
		if (cell == null) {
			cell = row.createCell(col);
		}
		return cell;
	}

	public static Object[] getEntitiesTableValues(List<? extends BaseModel> entities, EntityProperty entityProperty) {

		List<EntityElement> entityElements = entityProperty.getElements();
		Object[] values = new Object[(entities.size() + 1) * (entityElements.size() + 1)];
		int seqNum = 0;

		/**
		 * column header
		 */
		values[seqNum] = "No";
		seqNum++;
		for (int i = 0; i < entityElements.size(); i++) {
			values[seqNum] = entityElements.get(i).getLabelName();
			seqNum++;
		}

		/**
		 * table content
		 */
		for (int e = 0; e < entities.size(); e++) {

			BaseModel entity = entities.get(e);
			values[seqNum] = e + 1; // numbering
			seqNum++;

			/**
			 * checking the value type
			 */
			for (int i = 0; i < entityElements.size(); i++) {

				Object value = mapEntityValue(entity, entityElements.get(i));
				values[seqNum] = value;
				seqNum++;

			}
		}

		return values;
	}

	private static <T extends BaseModel> Object mapEntityValue(T entity, EntityElement element) {
		final Field field = getDeclaredField(entity.getClass(), element.getId());

		Object value;

		try {
			final FieldType fieldType = field.getAnnotation(FormField.class).type();
			value = field.get(entity);

			if (null != value) {
				String optionItemName = element.getOptionItemName();
				if (optionItemName.trim().equals("") == false) {
					if (null != optionItemName && StringUtils.isEmpty(optionItemName) == false) {
						Field converterField = getDeclaredField(field.getType(), optionItemName);
						Object converterValue = converterField.get(value);
						value = converterValue;
					} else {
						value = value.toString();
					}
//				}else if(objectEquals(fieldType, FieldType.FIELD_TYPE_IMAGE)) {
//				
//					value = value.toString().split("~")[0];
//					values[seqNum] = ComponentBuilder.imageLabel(UrlConstants.URL_IMAGE+value, 100, 100);
//					continue elementLoop;

				} else if (field.getType().equals(Date.class)
						|| objectEquals(fieldType, FieldType.FIELD_TYPE_DATE, FieldType.FIELD_TYPE_DATETIME)) {
					value = DateUtil.formatDate((Date) value, DATE_PATTERN);
				} else if (objectEquals(fieldType, FieldType.FIELD_TYPE_NUMBER)) {
					value = Double.parseDouble(value.toString());
				}
			}

			return value;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public static XSSFRow createRow(XSSFSheet sheet, int row) {
		XSSFRow xssfRow = sheet.getRow(row);
		if (null == xssfRow) {
			xssfRow = sheet.createRow(row);
		}
		return xssfRow;
	}

	public static XSSFRow createRow(final XSSFSheet sheet, final int rownum, final int offsetIndex,
			final Object... values) {

		final XSSFRow existingRow = sheet.getRow(rownum);
		XSSFRow row = existingRow == null ? sheet.createRow(rownum) : existingRow;

		XSSFCellStyle style = sheet.getWorkbook().createCellStyle();
		setAllBorder(style, THIN);
		fillRows(row, offsetIndex, style, values);

		for (int i = 0; i < values.length; i++) {
			sheet.autoSizeColumn(i);
		}

		return row;
	}

	public static void setBorder(XSSFCellStyle cellStyle, BorderStyle top, BorderStyle bottom, BorderStyle right,
			BorderStyle left) {
		cellStyle.setBorderBottom(bottom);
		cellStyle.setBorderTop(top);
		cellStyle.setBorderRight(right);
		cellStyle.setBorderLeft(left);

	}

	public static void setAllBorder(XSSFCellStyle cellStyle, BorderStyle borderStyle) {
		setBorder(cellStyle, borderStyle, borderStyle, borderStyle, borderStyle);
	}

	public static void removeBorder(XSSFCellStyle cellStyle) {
		setAllBorder(cellStyle, BorderStyle.NONE);
	}

	public static CellStyle createCellStyle(XSSFWorkbook workbook) {
		return workbook.createCellStyle();
	}

	/**
	 * fill row with values
	 * 
	 * @param parentRow
	 * @param offsetIndex
	 * @param sourceStyle
	 * @param values
	 */
	public static void fillRows(XSSFRow parentRow, int offsetIndex, CellStyle sourceStyle, Object... values) {
		DataFormat fmt = parentRow.getSheet().getWorkbook().createDataFormat();
		XSSFCell[] cells = new XSSFCell[values.length];
		for (int i = 0; i < values.length; i++) {
			Object cellValue = values[i];
			if (cellValue == null) {
				cellValue = "";
			}
			XSSFCell cell = parentRow.createCell(offsetIndex + i);

			CellStyle cellStyle = createCellStyle(parentRow.getSheet().getWorkbook());

			if (sourceStyle != null) {
				cellStyle.cloneStyleFrom(sourceStyle);
				cell.setCellStyle(cellStyle);
			}

			setCellValue(cell, cellValue, fmt);

			cells[i] = cell;
		}
	}

	/**
	 * set value for cell
	 * 
	 * @param cell
	 * @param value
	 * @param fmt
	 */
	public static void setCellValue(XSSFCell cell, Object value, DataFormat fmt) {

		if (null == value) {
			return;
		}
//		if(value.getClass().getSuperclass().equals(CustomCell.class) && ((CustomCell<?>)value).getValue() != null) { 
//		 
//			if(value instanceof CurrencyCell) {
//			 	String stringValue = ((CurrencyCell)value).getValue().toString();
//				value = (Double.parseDouble(stringValue)); 
//				cell.setCellValue((Double) value);
//				
//				if(null != fmt)
//					cell.getCellStyle().setDataFormat( fmt.getFormat("#,##0") );
//			}else if(value instanceof NumericCell) {
//				value = (Double.parseDouble(((NumericCell)value).getValue().toString() )); 
//				cell.setCellValue((Double) value);
//			}
//			
//		}else 
		if (value instanceof Double) {
			cell.setCellValue((Double) value);

		} else if (value instanceof Date) {
			cell.setCellValue((Date) value);

		} else if (value instanceof Boolean) {
			cell.setCellValue((Boolean) value);

		} else if (value instanceof Calendar) {
			cell.setCellValue((Calendar) value);

		} else {
			try {
				cell.setCellValue(Double.parseDouble(value.toString()));

			} catch (Exception e) {
				cell.setCellValue(value.toString());
			}
		}

	}

	/**
	 * 
	 * Cell Formats General 0 0.00 #,##0 #,##0.00 "$"#,##0_);("$"#,##0)
	 * "$"#,##0_);[Red]("$"#,##0) "$"#,##0.00_);("$"#,##0.00)
	 * "$"#,##0.00_);[Red]("$"#,##0.00) 0% 0.00% 0.00E+00 # ?/? # ??/?? m/d/yy
	 * d-mmm-yy d-mmm mmm-yy h:mm AM/PM h:mm:ss AM/PM h:mm h:mm:ss m/d/yy h:mm
	 * reserved-0x17 reserved-0x18 reserved-0x19 reserved-0x1a reserved-0x1b
	 * reserved-0x1c reserved-0x1d reserved-0x1e reserved-0x1f reserved-0x20
	 * reserved-0x21 reserved-0x22 reserved-0x23 reserved-0x24 #,##0_);(#,##0)
	 * #,##0_);[Red](#,##0) #,##0.00_);(#,##0.00) #,##0.00_);[Red](#,##0.00) _("$"*
	 * #,##0_);_("$"* (#,##0);_("$"* "-"_);_(@_) _(* #,##0_);_(* (#,##0);_(*
	 * "-"_);_(@_) _("$"* #,##0.00_);_("$"* (#,##0.00);_("$"* "-"??_);_(@_) _(*
	 * #,##0.00_);_(* (#,##0.00);_(* "-"??_);_(@_) mm:ss [h]:mm:ss mm:ss.0
	 * ##0.0E+0 @
	 */

}
