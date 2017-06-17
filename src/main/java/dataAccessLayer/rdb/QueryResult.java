package dataAccessLayer.rdb;

import java.sql.ResultSetMetaData;
import java.util.List;
import java.util.Map;

/**
 * Created by yoav.levinger.
 */
public class QueryResult {

    private Map<String, List<String>> data;
    private Map<String, List<String>> errors;
    private ResultSetMetaData resultSetMetaData;

    public Map<String, List<String>> getData() {
        return data;
    }

    public void setData(Map<String, List<String>> data) {
        this.data = data;
    }

    public Map<String, List<String>> getErrors() {
        return errors;
    }

    public void setErrors(Map<String, List<String>> errors) {
        this.errors = errors;
    }

    public ResultSetMetaData getResultSetMetaData() {
        return resultSetMetaData;
    }

    public void setResultSetMetaData(ResultSetMetaData resultSetMetaData) {
        this.resultSetMetaData = resultSetMetaData;
    }
}
