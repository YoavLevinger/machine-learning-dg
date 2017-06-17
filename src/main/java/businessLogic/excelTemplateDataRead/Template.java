package businessLogic.excelTemplateDataRead;

import com.google.gson.reflect.TypeToken;
import commons.JsonObjectConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Levinger on 28/12/2016.
 */

/**
 * Template object holding the Templates name and information.
 */
public class Template {

    private String name;

    private List<ExcelRow> excelRows;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ExcelRow> getExcelRows() {
        return excelRows;
    }

    public void setExcelRows(List<ExcelRow> excelRows) {
        this.excelRows = excelRows;
    }

    public void setExcelRows(String excelRowsJson) {
        this.excelRows = JsonObjectConverter.jsonToObject(excelRowsJson, new TypeToken<ArrayList<ExcelRow>>(){}.getType());
    }


    @Override
    public String toString() {
        return "Template{" +
                "name='" + name + '\'' +
                ", \nexcelRows=\n " + excelRows +
                '}';
    }
}
