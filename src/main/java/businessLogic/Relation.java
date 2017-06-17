package businessLogic;

import java.util.Arrays;

/**
 * Created by user on 17/12/2016.
 */

/**
 * Relation POJO
 */
public class Relation {
    private String name;
    private String prefixName;
    private String query;
    private String description;
    private String[] viewHeaders; //The query result headers, from which individuals order is derived for the relation file

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrefixName() {
        return prefixName;
    }

    public void setPrefixName(String prefixName) {
        this.prefixName = prefixName;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String[] getViewHeaders() {
        return viewHeaders;
    }

    public String getViewHeadersString() {
        StringBuilder viewHeadersString = new StringBuilder();
        for (int i = 0; i < viewHeaders.length; i++) {
            viewHeadersString.append(viewHeaders[i]);
            if (i<viewHeaders.length-1) viewHeadersString.append(",");
        }
        return viewHeadersString.toString();
    }

    public void setViewHeaders(String[] viewHeaders) {
        this.viewHeaders = viewHeaders;
    }

    public void setViewHeaders(String viewHeaders) {
        this.viewHeaders = viewHeaders.split(",");
    }

    @Override
    public String toString() {
        return "Relation{" +
                "name='" + name + '\'' +
                ", prefixName='" + prefixName + '\'' +
                ", query='" + query + '\'' +
                ", description='" + description + '\'' +
                ", viewHeaders=" + Arrays.toString(viewHeaders) +
                '}';
    }
    public String toStringFileDisplay() {
        return "Relation{" +
                "\n name='" + name + '\'' +
                ",\n prefixName='" + prefixName + '\'' +
                ",\n query='" + query + '\'' +
                ",\n description='" + description + '\'' +
                ",\n viewHeaders=" + Arrays.toString(viewHeaders) +
                "\n}";
    }

    public String toStringConsoleDisplay() {
        return "Relation{" +
                "\n name='" + name + '\'' +
                ",\n prefixName='" + prefixName + '\'' +
                ",\n description='" + description + '\'' +
                ",\n viewHeaders=" + Arrays.toString(viewHeaders) +
                "\n}";
    }
}
