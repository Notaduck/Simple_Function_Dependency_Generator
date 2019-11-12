import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static java.sql.ResultSet.CONCUR_READ_ONLY;
import static java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE;

public class FD {

    private String user;
    private String pass;
    private String url;
    private HashMap<String, ArrayList<String>> tables = new HashMap<>();

    public FD(String host, String user, String password, String port, String db) {
        this.url = String.format("jdbc:postgresql://%s:%s/%s", host, port, db);
        this.user = user;
        this.pass = password;
    }

    public void initialize() {
        try {
            this.fdGenerator();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<String> getTables() throws SQLException {
        var tables = new ArrayList<String>();
        try (Connection conn = DriverManager.getConnection(url, "postgres", "postgres")) {
            var dbmd = conn.getMetaData();
            String[] types = {"TABLE"};
            var rs = dbmd.getTables(null, null, "%", types);
            while (rs.next()) {
                tables.add(rs.getString("TABLE_NAME"));
            }
        }

        return tables;
    }

    private ArrayList<String> getColumnNames(String table_name) throws SQLException {
       var columns = new ArrayList<String>();

        try (Connection conn = DriverManager.getConnection(url, "postgres", "postgres")) {
            var metadata = conn.getMetaData();
            var resultSet = metadata.getColumns(null, null, table_name, null);
            while (resultSet.next()) {
                var name = resultSet.getString("COLUMN_NAME");
                columns.add(name);
            }
        }

        return columns;
    }

    private void getTableAndColumnNames() throws SQLException {
       var tables = getTables();
        for (String table: tables) {
            var columns = getColumnNames(table);
            this.tables.put(table, columns);
        }
    }

    private int rowCounter(ResultSet rs) throws SQLException {

        var size = 0;
        if (rs != null)
        {
            rs.beforeFirst();
            rs.last();
            size = rs.getRow();
        }

        return size;
    }

    private void fdGenerator() throws SQLException {
        this.getTables();
        this.getTableAndColumnNames();

        var it = tables.entrySet().iterator();

        while (it.hasNext()) {
            var pair = (Map.Entry) it.next();
            ArrayList<String> cols = (ArrayList) pair.getValue();


            try (Connection conn = DriverManager.getConnection(url, "postgres", "postgres")) {

                var st = conn.createStatement(TYPE_SCROLL_INSENSITIVE, CONCUR_READ_ONLY);

                var rss = st.executeQuery("SELECT COUNT(*) FROM " + pair.getKey());
                rss.next();
                if (Integer.parseInt(rss.getString(1)) > 1) {
                    System.out.println("===========================");
                    System.out.println("FD's for: " + pair.getKey());
                    System.out.println("--------------------------");
                    for (String col1 : cols) {
                        for (String col2 : cols) {
                            if (col1 != col2) {
                                var rs = st.executeQuery("SELECT " + col1 +
                                        " FROM " + pair.getKey() + " GROUP BY " + col1 + " HAVING COUNT(DISTINCT " + col2 + ") > 1");
                                if (rowCounter(rs) == 0) {
                                    System.out.println(col1 + "-> " + col2);
                                }
                            }

                        }
                    }
                }
            }
        }
    }
}
