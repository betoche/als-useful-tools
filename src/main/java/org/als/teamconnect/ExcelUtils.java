package org.als.teamconnect;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ExcelUtils {

    public static List<Object[]> getDataInSheet(InputStream is, int sheetNumber, boolean excludeFirstLine ) throws IOException {
        List<Object[]> fileContent = new ArrayList<>();
        XSSFWorkbook workbook = new XSSFWorkbook(is);
        XSSFSheet worksheet = workbook.getSheetAt(sheetNumber);

        for(int i=(excludeFirstLine?1:0);i<worksheet.getPhysicalNumberOfRows() ;i++) {
            XSSFRow row = worksheet.getRow(i);
            int columnCount = row.getPhysicalNumberOfCells();
            Object[] cells = new Object[columnCount];

            for( int j = 0 ; j < columnCount ; j++ ) {
                Cell cell = row.getCell(j);
                CellType cellType = cell.getCellType();


                cells[j] = switch (cellType) {
                    case NUMERIC -> cell.getNumericCellValue();
                    case null, default -> cell.getRichStringCellValue();
                };
            }

            fileContent.add(cells);
        }

        return fileContent;
    }
}
