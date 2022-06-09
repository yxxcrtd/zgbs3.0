package com.joyintech.tams.invest.batch.valbatchimport.common.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.RichTextString;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PoiCellUtil {

	private PoiCellUtil() {}
	// Sonar Fix modify by huwei 220609  SimpleDateFormat -> Non-thread-safe fields should not be static
	private static final ThreadLocal<DateFormat> threadLocalDateFmt =
			ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

	/**
	 * 
	 * @param cell POI cell
	 * @return string value of cell
	 */
	public static String getCellValue(Cell cell) {
		if(cell == null) return null;
		
		String contents = null;
		boolean succ = false;
		if(!succ) {
			try {
				contents = cell.getStringCellValue();
				succ = true;
			} catch(Exception e) { }
		}
		if(!succ) {
			try {
				double v = cell.getNumericCellValue();
//				contents = new BigDecimal(v).toString();
				contents =  String.valueOf(v);
				if(contents != null && contents.endsWith(".0")) {//2202.0这种特殊处理
					contents = contents.substring(0,contents.length()-1);
				}
				//contents = new BigDecimal(v).setScale(6, RoundingMode.HALF_EVEN).toString();
				succ = true;
			} catch(Exception e) { }
		}
		if(!succ) {
			try {
				boolean v = cell.getBooleanCellValue();
				contents = new Boolean(v).toString();
				succ = true;
			} catch(Exception e) { }
		}
		if(!succ) {
			try {
				Date v = cell.getDateCellValue();
				contents = threadLocalDateFmt.get().format(v);
				succ = true;
			} catch(Exception e) { }
		}
		if(!succ) {
			try {
				RichTextString v = cell.getRichStringCellValue();
				contents = v.getString();
				succ = true;
			} catch(Exception e) { }
		}
		return contents;
	}
	
	/**
	 * 
	 * @param cell
	 * @return column sequence, 'A' based, e.g.: A,B,...,Z,AA,AB,...,AZ,...
	 */
	public static String getColumnCode(Cell cell) {
		int colSeq = cell.getColumnIndex() + 1;
		return XlsUtil.convertColumnSeqToCode(colSeq);
	}
}
