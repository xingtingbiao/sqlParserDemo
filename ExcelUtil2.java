//package com.huawei.spring.core.implement.SQLScan.util;
//
//import org.apache.poi.ss.usermodel.CellStyle;
//import org.apache.poi.ss.usermodel.IndexedColors;
//import org.apache.poi.ss.usermodel.Sheet;
//import org.apache.poi.xssf.streaming.SXSSFCell;
//import org.apache.poi.xssf.streaming.SXSSFRow;
//import org.apache.poi.xssf.streaming.SXSSFSheet;
//import org.apache.poi.xssf.streaming.SXSSFWorkbook;
//
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.io.OutputStream;
//import java.io.UnsupportedEncodingException;
//import java.lang.reflect.Field;
//import java.lang.reflect.InvocationTargetException;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.List;
//import java.util.Map;
//
//public class ExcelUtil2 {
//    //对应poi版本为3.9
//    public static SXSSFWorkbook getWorkBook(List<?> objects, Class c) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
//        SXSSFWorkbook wb = new SXSSFWorkbook(1000);
//        SXSSFSheet sheet = (SXSSFSheet) wb.createSheet("sheet1");
//        // setWidth(sheet);
//        SXSSFRow row = (SXSSFRow) sheet.createRow(0);
//        row.setHeightInPoints(40);
//        //CellStyle style = wb.createCellStyle();
//        CellStyle style = getCellStyle(wb);
//        style.setAlignment(CellStyle.ALIGN_CENTER);
//        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
//        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
//        style.setFillForegroundColor(IndexedColors.CORNFLOWER_BLUE.getIndex());
//        // Map<Integer, String> columnMap = resultColumnMap();
//        Map<Integer, String> columnMap = (Map<Integer, String>) c.getMethod("getFieldsAlias").invoke(c.newInstance());
//        SXSSFCell cell = null;
//        for (Map.Entry<Integer, String> entry : columnMap.entrySet()) {
//            cell = (SXSSFCell) row.createCell(entry.getKey());
//            cell.setCellValue(entry.getValue());
//            cell.setCellStyle(style);
//        }
//        for (int i = 0; i < objects.size(); i++) {
//            row = (SXSSFRow) sheet.createRow(i + 1);
//            useReflectToGetValue(row, objects.get(i), wb);
//        }
//        autoSizeColumns(sheet, columnMap.size());
//        return wb;
//    }
//
//
//    private static void useReflectToGetValue(SXSSFRow row, Object object, SXSSFWorkbook wb) throws IllegalAccessException {
//        Class<?> aClass = object.getClass();
//        Field[] fields = aClass.getDeclaredFields();
//        CellStyle style = getCellStyle(wb);
//        SXSSFCell cell;
//        for (int i = 0; i < fields.length; i++) {
//            fields[i].setAccessible(true);
//            cell = (SXSSFCell) row.createCell(i);
//            Object value = fields[i].get(object);
//            if (value instanceof Date) {
//                cell.setCellValue(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(value));
//            } else if (value == null) {
//                cell.setCellValue("");
//            } else {
//                cell.setCellValue(String.valueOf(value));
//            }
//            cell.setCellStyle(style);
//        }
//    }
//
//    private static void autoSizeColumns(Sheet sheet, int columnNumber) {
//        for (int i = 0; i < columnNumber; i++) {
//            sheet.autoSizeColumn(i, true);
//        }
//    }
//
//    private static CellStyle getCellStyle(SXSSFWorkbook wb) {
//        CellStyle style = wb.createCellStyle();
//        style.setBorderBottom(CellStyle.BORDER_THIN);
//        style.setBorderLeft(CellStyle.BORDER_THIN);
//        style.setBorderTop(CellStyle.BORDER_THIN);
//        style.setBorderRight(CellStyle.BORDER_THIN);
//        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
//        style.setAlignment(CellStyle.ALIGN_LEFT);
//        style.setWrapText(true);
//        return style;
//    }
//
//    private static int getHeight(String s) {
//        int h = 0;
//        if (null == s || "".equals(s)) {
//            h = 20;
//        } else {
//            String[] split = s.split("\n");
//            if (split.length * 15 >= 400) {
//                h = 400;
//            } else {
//                h = split.length * 20;
//            }
//        }
//        return h;
//    }
//
//    public static void export(HttpServletResponse response, SXSSFWorkbook workBook, String fileName) throws UnsupportedEncodingException {
//        String format = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(new Date());
//        String name = fileName + "_" + format + ".xlsx";
//        // response.setHeader("Accept-Ranges","bytes");
//        // response.setHeader("ETag", "");
//        response.setContentType("application/octet-stream");
//        response.setHeader("Content-Disposition", "attachment;filename=" + new String(name.getBytes("UTF-8"), "iso-8859-1"));
//        OutputStream os = null;
//        try {
//            os = response.getOutputStream();
//            workBook.write(os);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (null != os) {
//                try {
//                    os.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//}
