package dataAccessLayer.rdb;

import org.apache.commons.dbcp.BasicDataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: levinger
 */
public class DatabaseQueries {

    private BasicDataSource dataSource;
    private DatabaseType databaseType;
    private Properties props = new Properties();

    private static Connection conn = null;
    private static PreparedStatement stmt = null;

    public DatabaseQueries() {}

//    DataSourceDetails
    /**
     * Configures the database source
     */
    public void setDataSource(DataSourceDetails dataSourceDetails) {
        setDataSource(dataSourceDetails.getDatabaseType(),
                dataSourceDetails.getUsername(),
                dataSourceDetails.getPassword(),
                dataSourceDetails.getHost(),
                dataSourceDetails.getPort(),
                dataSourceDetails.getSid(),
                dataSourceDetails.getSchema());
    }


    /**
     * Configures the database source
     *
     * @param databaseType -  database type
     * @param username -
     * @param password -
     * @param host     -
     * @param port     -
     * @param sid      - required only in Oracle
     * @param schema   - for mssql its actually database.
     */
    public void setDataSource(DatabaseType databaseType,
                                    String username,
                                    String password,
                                    String host,
                                    String port,
                                    String sid,
                                    String schema) {
        String postFix;
        this.databaseType = databaseType;
        final String EMPTY_STRING ="";

        switch (databaseType) {
            case ORACLE:
                postFix = ":" + sid.trim();
                break;
            case MYSQL:
                postFix = "/" + (schema != null ? schema.trim() : EMPTY_STRING) + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&autoReconnect=true&useSSL=false";
                // + "?autoReconnect=true&useSSL=false";
                break;
            case POSTGRES:
                postFix = "/" + (schema != null ? schema.trim() : EMPTY_STRING);
                props.setProperty("user", username);
                props.setProperty("password", password);
                props.setProperty("jdbc.drivers",DatabaseType.getDriverClassName(databaseType));
                break;
            case MSSQL:
                postFix = "/" + (schema != null ? schema.trim() : EMPTY_STRING) + ";sendStringParametersAsUnicode=true";
                break;
            default:
                postFix = "";
                break;
        }
        String url = DatabaseType.getDatabaseUrlPrefix(databaseType) + host + ":" + port + postFix;
        try {
            dataSource = new BasicDataSource();
            dataSource.setDriverClassName(DatabaseType.getDriverClassName(databaseType));
            dataSource.setUrl(url);
            dataSource.setUsername(username);
            dataSource.setPassword(password);
            dataSource.setMaxActive(100);
            dataSource.setMaxWait(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public QueryResult query(PreparedStatement preparedStatement, QueryType queryType, int timeout) {
        return query(preparedStatement, null, queryType,  timeout);
    }

    public QueryResult query(String query, QueryType queryType, int timeout) {
        return query(null, query, queryType,  timeout);
    }

    /**
     * Performs SQL Query
     * @param query - The Query String
     * @param queryType - The Query Type
     * @param timeout - Query Timeout
     * @return Query result object.
     */
    private QueryResult query(PreparedStatement preparedStatement, String query, QueryType queryType, int timeout) {

        QueryResult queryResult = new QueryResult();
        ResultSet rs;
        Map<String, List<String>> dataResult = new HashMap<>();
        Map<String, List<String>> errorResult = new HashMap<>();
        List<String> tempListOfDataValues = new ArrayList<>();
        List<String> tempListOfErrors = new ArrayList<>();
        int connectCounter =0;
        try {
            if (databaseType.equals(DatabaseType.NOT_SET)) {
                tempListOfErrors.add("Database type was not set correctly");
                errorResult.put("Input error: ", tempListOfErrors);
                queryResult.setErrors(errorResult);
                return queryResult;
            }
            do {
                if (databaseType.equals(DatabaseType.POSTGRES)) {
                    //method changed for postgres only as the postgres driver does not support "stmt.setQueryTimeout(timeout)" yet.
                    //http://jdbc.postgresql.org/documentation/head/connect.html for ref on all parameters
                    props.setProperty("loginTimeout", String.valueOf(timeout));
                    props.setProperty("socketTimeout", String.valueOf(timeout));
                    try {
                        Class.forName(props.getProperty("jdbc.drivers"));
                    } catch (ClassNotFoundException e) {
                        tempListOfErrors.add(e.getMessage());
                        errorResult.put("Loading postgres driver error message: ", tempListOfErrors);
                        queryResult.setErrors(errorResult);
                        return queryResult;
                    }
                    try { //retry open connection mechanism
                        conn = DriverManager.getConnection(dataSource.getUrl(), props);
                    } catch (Exception e) {
                        try {
                            Thread.sleep(500*(connectCounter+1)); //retry throttling
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                    }
                    if (preparedStatement != null && query == null) {
                        stmt = preparedStatement;
                    } else {
                        stmt = conn.prepareStatement(query);
                    }
                } else {

                    try { //retry open connection mechanism
                        conn = dataSource.getConnection();
                        if (preparedStatement != null && query == null) {
                            stmt = preparedStatement;
                        } else {
                            stmt = conn.prepareStatement(query);
                        }
                        stmt.setQueryTimeout(timeout);
                    } catch (Exception e) {
                        try {
                            Thread.sleep(500*(connectCounter+1)); //retry throttling
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
                connectCounter++;
            } while (connectCounter < 100 && conn.isClosed());

            switch (queryType) {
                case SELECT:
                    try {
                        conn.setReadOnly(true);
                        stmt.setMaxRows(0); //the new max rows limit; zero means there is no limit
                        rs = stmt.executeQuery();
                        ResultSetMetaData resultSetMetaData = rs.getMetaData();
                        queryResult.setResultSetMetaData(resultSetMetaData);
                        Map<String,String> fields = new HashMap<>(); //label , name
                        for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
                            fields.put(resultSetMetaData.getColumnLabel(i),resultSetMetaData.getColumnName(i));
                            dataResult.put(resultSetMetaData.getColumnName(i), new ArrayList<String>());
                        }
                        while (rs.next()) {
                            for (Map.Entry<String, String> entry : fields.entrySet()  ) {
                                tempListOfDataValues = dataResult.get(entry.getValue());
                                tempListOfDataValues.add(rs.getString(entry.getKey()));
                                dataResult.put(entry.getValue(), tempListOfDataValues);
                            }
                        }
                    } catch (Exception e) {
                        tempListOfErrors.add(e.toString());
                        errorResult.put("Query Error has occurred: ", tempListOfErrors);
                    }
                    break;

                case TRANSACTIONAL_MODIFY:
                    try {
                        conn.setReadOnly(false);
                        conn.setAutoCommit(false);
                        final int insertedNumber = stmt.executeUpdate();
                        conn.commit();

                        tempListOfDataValues.add("" + insertedNumber);
                        dataResult.put("Modify: ", tempListOfDataValues);
                    } catch (Exception e) {
                        conn.rollback();
                        tempListOfErrors.add("" + e.toString());
                        errorResult.put("Modify Error has occurred: ", tempListOfErrors);
                    }
                    break;

                case DDL:
                    try {
                        conn.setReadOnly(false);
                        final int insertedNumber = stmt.executeUpdate();
                        tempListOfDataValues.add("" + insertedNumber);
                        dataResult.put("DDL query results: ", tempListOfDataValues);
                    } catch (Exception e) {
                        tempListOfErrors.add("" + e.toString());
                        errorResult.put("DDL Error has occurred: ", tempListOfErrors);
                    }
                    break;
            }
        } catch (SQLException e) {
            tempListOfErrors.add(e.getMessage());
            errorResult.put("Error has occurred: ", tempListOfErrors);
        } finally {

            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }


            if (dataSource!=null) {
                try{
                    dataSource.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        queryResult.setData(dataResult);
        queryResult.setErrors(errorResult);
        return queryResult;
    }

}
