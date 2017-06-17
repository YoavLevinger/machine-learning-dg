package commons;

import businessLogic.excelTemplateDataRead.ExcelRow;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by Levinger on 26/12/2016.
 */
public class ExcelOperations {

    private enum CellType {
        CELL_TYPE_NUMERIC,
        CELL_TYPE_STRING
    }

    /**
     * Read Template from Excel file
     *
     * @param excelFilePath - The excel file path, including the file name & extension
     * @return - Excel Template object with the data
     */
    public static List<ExcelRow> getTemplateFromExcelFile(String excelFilePath) {
        List<ExcelRow> excelRows = new ArrayList<>();
        ExcelRow excelRow = new ExcelRow();

        Workbook workbook = null;
        try {
            workbook = getWorkbook(excelFilePath);

            int tempPercentage = 0;
            String tempRelation;
            //40 rows
            for (int i = 1; i <= 40; i++) {
                tempRelation = null;
                try {
                    tempPercentage = (int) Double.parseDouble(getCellValue(workbook, 0, i, 1, CellType.CELL_TYPE_NUMERIC));
                    if (tempPercentage > 0) {
                        tempRelation = getCellValue(workbook, 0, i, 11, CellType.CELL_TYPE_STRING);
                    }
                } catch (Exception e) {
                    System.out.println("Error reading excel ROW: [" + i + "]");
                }
                if (tempPercentage > 0 && tempRelation != null) {
                    excelRow.setPercentage((int) Double.parseDouble(getCellValue(workbook, 0, i, 1, CellType.CELL_TYPE_NUMERIC)));
                    if (getCellValue(workbook, 0, i, 2, CellType.CELL_TYPE_STRING).equalsIgnoreCase("random")) {
                        excelRow.setRandom(true);
                        excelRow.setRandomBits((int) Double.parseDouble(getCellValue(workbook, 0, i, 3, CellType.CELL_TYPE_NUMERIC)));
                        excelRow.setRelation(getCellValue(workbook, 0, i, 11, CellType.CELL_TYPE_STRING));
                    } else {
                        excelRow.setRelationPrefixName(getCellValue(workbook, 0, i, 2, CellType.CELL_TYPE_STRING).equalsIgnoreCase("v"));
                        excelRow.setxGender(getCellValue(workbook, 0, i, 3, CellType.CELL_TYPE_STRING).equalsIgnoreCase("v"));
                        excelRow.setxId(getCellValue(workbook, 0, i, 4, CellType.CELL_TYPE_STRING).equalsIgnoreCase("v"));
                        excelRow.setyGender(getCellValue(workbook, 0, i, 5, CellType.CELL_TYPE_STRING).equalsIgnoreCase("v"));
                        excelRow.setyId(getCellValue(workbook, 0, i, 6, CellType.CELL_TYPE_STRING).equalsIgnoreCase("v"));
                        excelRow.setzGender(getCellValue(workbook, 0, i, 7, CellType.CELL_TYPE_STRING).equalsIgnoreCase("v"));
                        excelRow.setzId(getCellValue(workbook, 0, i, 8, CellType.CELL_TYPE_STRING).equalsIgnoreCase("v"));
                        excelRow.settGender(getCellValue(workbook, 0, i, 9, CellType.CELL_TYPE_STRING).equalsIgnoreCase("v"));
                        excelRow.settId(getCellValue(workbook, 0, i, 10, CellType.CELL_TYPE_STRING).equalsIgnoreCase("v"));
                        excelRow.setRelation(getCellValue(workbook, 0, i, 11, CellType.CELL_TYPE_STRING));
                    }
                }
                tempPercentage = 0;
                excelRows.add(excelRow);
                excelRow = new ExcelRow();
            }

        } catch (IOException e) {
            System.out.println("Error loading excel file: [" + e.getMessage() + "]");
            excelRows = null;
        } finally {
            try {
                if (workbook != null) {
                    workbook.close();
                }
            } catch (IOException e) {
                    //do nothing as the workbook is closed.
            }
        }

        return excelRows;
    }

    /**
     * Get specific cell data from the excel file
     *
     * @param workbook - the workbook to use
     * @param sheet    - The sheet to use
     * @param row      - The row
     * @param cell     - The cell
     * @param cellType - The cell type (Handling only numeric & String)
     * @return - String with cell content
     */
    private static String getCellValue(Workbook workbook, int sheet, int row, int cell, CellType cellType) {
        String cellValue = null;

        try {

            switch (cellType) {
                case CELL_TYPE_NUMERIC:
                    cellValue = workbook.getSheetAt(sheet).getRow(row).getCell(cell).getNumericCellValue() + "";
                    break;

                case CELL_TYPE_STRING:
                    cellValue = workbook.getSheetAt(sheet).getRow(row).getCell(cell).getStringCellValue();
                    break;
            }
        } catch (Exception e) {
            System.out.println("Exception: " + Arrays.toString(e.getStackTrace()));
            System.out.println("Error reading excel SHEET number: " + sheet);
            System.out.println("Error reading excel ROW number: " + row);
            System.out.println("Error reading excel CELL number: " + cell);
            return "0";
        }

        return cellValue;
    }

    /**
     * Get the workbook and handle 2 types of available workbooks: XLS & XLSX
     *
     * @param excelFilePath - The Excel File Path to use
     * @return - Workbook object
     * @throws IOException - In case of read from file failure
     */
    private static Workbook getWorkbook(String excelFilePath)
            throws IOException {

        FileInputStream inputStream = new FileInputStream(new File(excelFilePath));

        Workbook workbook;

        if (excelFilePath.endsWith("xlsx")) {
            workbook = new XSSFWorkbook(inputStream);
        } else if (excelFilePath.endsWith("xls")) {
            workbook = new HSSFWorkbook(inputStream);
        } else {
            throw new IllegalArgumentException("The specified file is not Excel file");
        }
        inputStream.close();
        return workbook;
    }


}
