package dataAccessLayer.rdb;

/**
 * Created by yoav.levinger.
 */
public class DataSourceDetails {

    private DatabaseType databaseType;
    private String username;
    private String password;
    private String host;
    private String port;
    private String sid;
    private String schema;

    public DataSourceDetails(DatabaseType databaseType, String username, String password, String host, String port, String sid, String schema) {
        this.databaseType = databaseType;
        this.username = username;
        this.password = password;
        this.host = host;
        this.port = port;
        this.sid = sid;
        this.schema = schema;
    }

    public DatabaseType getDatabaseType() {
        return databaseType;
    }

    public void setDatabaseType(DatabaseType databaseType) {
        this.databaseType = databaseType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    @Override
    public String toString() {
        return "DataSourceDetails{" +
                "databaseType=" + databaseType +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", host='" + host + '\'' +
                ", port='" + port + '\'' +
                ", sid='" + sid + '\'' +
                ", schema='" + schema + '\'' +
                '}';
    }
}
