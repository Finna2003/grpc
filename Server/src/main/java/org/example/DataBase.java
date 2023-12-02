package org.example;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class DataBase {
        private Map<String, DynamicTable> tables;

        public DataBase() {
            tables = new HashMap<>();
        }

        public void addTable(String tableName, List<Map.Entry<String, String>> columnInfo) {
            if (tables.containsKey(tableName)) {
                throw new IllegalArgumentException("Table '" + tableName + "' already exists");
            }

            tables.put(tableName, new DynamicTable(columnInfo));
        }

        public void removeTable(String tableName) {
            if (!tables.containsKey(tableName)) {
                throw new IllegalArgumentException("Table '" + tableName + "' does not exist");
            }

            tables.remove(tableName);
        }

    public Map<String, DynamicTable> getTables() {
        return tables;
    }
}
