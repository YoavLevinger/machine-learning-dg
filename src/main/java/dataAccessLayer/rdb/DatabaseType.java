package dataAccessLayer.rdb;

/**
 * Created with IntelliJ IDEA.
 * User: levinger
 */

public enum DatabaseType {

    MSSQL, ORACLE, MYSQL, POSTGRES, NOT_SET;

    public static String[] names() {
        DatabaseType[] databases = values();
        String[] names = new String[databases.length];

        for (int i = 0; i < databases.length; i++) {
            names[i] = databases[i].name();
        }
        return names;
    }

    public static synchronized String getDatabaseUrlPrefix(DatabaseType databaseType) {
        String databaseUrlPrefix = "jdbc:";
        switch (databaseType) {
            case MSSQL:
                databaseUrlPrefix += "jtds:sqlserver://";
                break;
            case ORACLE: //oracle driver is no longer available without registration - don't use it.
                databaseUrlPrefix += "oracle:thin:@";
                break;
            case MYSQL:
                databaseUrlPrefix += "mysql://";
                break;
            case POSTGRES:
                databaseUrlPrefix += "postgresql://";
                break;
        }
        return databaseUrlPrefix;
    }

	public static synchronized String getDatabaseUrlPostfix(DatabaseType databaseType, String schema) {
		String postFix;
		final String EMPTY_STRING ="";
		switch (databaseType) {
			case ORACLE:
				postFix = ":" + schema.trim(); //in this case schema==sid
				break;
			case MYSQL:
				postFix = "/" + (schema != null ? schema.trim() : EMPTY_STRING) + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
				break;
			case POSTGRES:
				postFix = "/" + (schema != null ? schema.trim() : EMPTY_STRING);
				break;
			case MSSQL:
				postFix = "/" + (schema != null ? schema.trim() : EMPTY_STRING) + ";sendStringParametersAsUnicode=true;useUnicode=true;characterEncoding=UTF-8";
				break;
			default:
				postFix = "";
				break;
		}
		return postFix;
	}

    public static synchronized String getDriverClassName(DatabaseType databaseType) {
        String driverClassName = "[Error----driverClassName NotFound]";
        switch (databaseType) {
            case MSSQL:
                driverClassName = "net.sourceforge.jtds.jdbc.Driver";
                break;
            case ORACLE:
                driverClassName = "oracle.jdbc.OracleDriver";
                break;
            case MYSQL:
                driverClassName =
                        "com.mysql.cj.jdbc.Driver"; //version 5.7+
//                        "com.mysql.jdbc.Driver"; //Version 5.1.xxx
                break;
            case POSTGRES:
                driverClassName = "org.postgresql.Driver";
                break;
        }
        return driverClassName;
    }

}
