package org.example;

import com.google.protobuf.Value;

import java.util.*;
import java.util.stream.Collectors;

public class DynamicTable {
    public List<Map.Entry<String, String>> columnInfo;
    public List<Map<String, Object>> rows;

    public DynamicTable(List<Map.Entry<String, String>> columnInfo) {
        this.columnInfo = columnInfo;
        this.rows = new ArrayList<>();
    }

    public void AddColumn(String columnName, String columnType) {
        Map.Entry<String, String> newColumn = new AbstractMap.SimpleEntry<>(columnName, columnType);

        if (columnInfo.stream().anyMatch(col -> col.getKey().equals(columnName))) {
            throw new IllegalArgumentException("Column '" + columnName + "' already exists");
        }

        columnInfo.add(newColumn);

        for (Map<String, Object> row : rows) {
            row.put(columnName, null);
        }
    }
    public void DeleteColumn(String columnName) {
        if (!columnInfo.stream().anyMatch(col -> col.getKey().equals(columnName))) {
            throw new IllegalArgumentException("Column '" + columnName + "' does not exist");
        }

        int index = columnInfo.stream().filter(col -> col.getKey().equals(columnName)).findFirst().map(columnInfo::indexOf).orElse(-1);

        columnInfo.remove(index);

        for (Map<String, Object> row : rows) {
            row.remove(columnName);
        }
    }

    public void AddRow(List<Value> values) {
        if (values.size() != columnInfo.size()) {
            throw new IllegalArgumentException("Number of values must match the number of columns");
        }

        Map<String, Object> row = new HashMap<>();
        for (int i = 0; i < columnInfo.size(); i++) {
            Map.Entry<String, String> column = columnInfo.get(i);
            String columnName = column.getKey();
            String columnType = column.getValue();
            Object value = values.get(i);

            /*if (!columnType.equals(value.getClass().getSimpleName())) {
                throw new IllegalArgumentException("Invalid type for column '" + columnName + "'. Expected "
                        + columnType + ", got " + value.getClass().getSimpleName());
            }*/
            row.put(columnName, value);
        }

        rows.add(row);
    }

    public void AddNewRow() {
        Map<String, Object> newRow = new HashMap<>();
        for (Map.Entry<String, String> column : columnInfo) {
            newRow.put(column.getKey(), null);
        }

        rows.add(newRow);
    }

    public void RemoveRow(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= rows.size()) {
            throw new IllegalArgumentException("Invalid row index");
        }

        rows.remove(rowIndex);
    }

    public void RemoveDuplicates() {
        List<Map<String, Object>> uniqueRows = new ArrayList<>();
        HashSet<List<Object>> seenRows = new HashSet<>();

        for (Map<String, Object> row : rows) {
            List<Object> values = new ArrayList<>(row.values());

            if (seenRows.add(values)) {
                uniqueRows.add(row);
            }
        }

        rows = uniqueRows;
    }

    public List<Map.Entry<String, String>> getColumnInfo() {
        return columnInfo;
    }

    public List<Map<String, Object>> getRows() {
        return rows;
    }
}


