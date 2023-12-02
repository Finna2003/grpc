package org.example;
import dbservice.MyDatabaseServiceGrpc;
import dbservice.Main;
import io.grpc.Status;
import io.grpc.StatusException;
import io.grpc.stub.StreamObserver;
import com.google.protobuf.Empty;
import com.google.protobuf.Struct;
import com.google.protobuf.Value;

import java.util.*;
import java.util.stream.Collectors;



public class Implementation extends MyDatabaseServiceGrpc.MyDatabaseServiceImplBase{

        private static DataBase database = new DataBase();

    public void createDatabase(Main.CreateDatabaseRequest request,
                               StreamObserver<Main.Empty> responseObserver) {
            database = new DataBase();
            System.out.println("DB created");
        responseObserver.onNext(Main.Empty.newBuilder().build());
        responseObserver.onCompleted();

        }

    @Override
    public void addTable(Main.AddTableRequest request, StreamObserver<Main.Empty> responseObserver) {
        String tableName = request.getTableName();
        List<Map.Entry<String, String>> columnInfo = request.getColumnInfoList()
                .stream()
                .map(info -> new AbstractMap.SimpleEntry<>(info.getColumnName(), info.getColumnType()))
                .collect(Collectors.toList());

        try {
            database.addTable(tableName, columnInfo);
            System.out.println("Table: "+tableName+" created"+" " + columnInfo );
            responseObserver.onNext(Main.Empty.newBuilder().build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            System.out.println("Error" );
            responseObserver.onError(Status.NOT_FOUND.asRuntimeException());
        }
    }

    @Override
    public void removeTable(Main.RemoveTableRequest request, StreamObserver<Main.Empty> responseObserver) {

            String tableName = request.getTableName();
            database.removeTable(tableName);
        System.out.println("Table: "+tableName+" remove" );
            responseObserver.onNext(Main.Empty.newBuilder().build());
            responseObserver.onCompleted();
        }

    @Override
    public void addRow(Main.AddRowRequest request, StreamObserver<Main.Empty> responseObserver) {

            String tableName = request.getTableName();
            List<Value> values = request.getValuesList();
            if (database.getTables().containsKey(tableName)) {
                database.getTables().get(tableName).AddRow(values);
            } else {
                responseObserver.onError(new StatusException(Status.NOT_FOUND.withDescription("Table '" + tableName + "' does not exist")));
                return;
            }
        System.out.println("Row on table "+ tableName+" created" );
            responseObserver.onNext(Main.Empty.newBuilder().build());
            responseObserver.onCompleted();
        }


    @Override
    public void deleteRow(Main.DeleteRowRequest request, StreamObserver<Main.Empty> responseObserver) {

            String tableName = request.getTableName();
            int rowIndex = request.getRowIndex();

            if (database.getTables().containsKey(tableName)) {
                database.getTables().get(tableName).RemoveRow(rowIndex - 1);
            } else {
                System.out.println("Error" );
                responseObserver.onError(new StatusException(Status.NOT_FOUND.withDescription("Table '" + tableName + "' does not exist")));
                return;
            }
        System.out.println("Row on table"+ tableName+" remove" );
            responseObserver.onNext(Main.Empty.newBuilder().build());
            responseObserver.onCompleted();
        }


    @Override
    public void addColumn(Main.AddColumnRequest request, StreamObserver<Main.Empty> responseObserver) {

            String tableName = request.getTableName();
            Main.TableInfo columnInfo = request.getColumnInfo();

            if (database.getTables().containsKey(tableName)) {
                database.getTables().get(tableName).AddColumn(columnInfo.getColumnName(), columnInfo.getColumnType());
            } else {
                responseObserver.onError(new StatusException(Status.NOT_FOUND.withDescription("Table '" + tableName + "' does not exist")));
                return;
            }

            responseObserver.onNext(Main.Empty.newBuilder().build());
            responseObserver.onCompleted();
        }

    @Override
    public void deleteColumn(Main.DeleteColumnRequest request, StreamObserver<Main.Empty> responseObserver) {

            String tableName = request.getTableName();
            String columnName = request.getColumnName();

            if (database.getTables().containsKey(tableName)) {
                database.getTables().get(tableName).DeleteColumn(columnName);
            } else {
                System.out.println("Error" );
                responseObserver.onError(new StatusException(Status.NOT_FOUND.withDescription("Table '" + tableName + "' does not exist")));
                return;
            }
        System.out.println("Column "+columnName+" on table "+ tableName+" created" );
            responseObserver.onNext(Main.Empty.newBuilder().build());
            responseObserver.onCompleted();
        }

    @Override
    public void removeDuplicates(Main.RemoveDuplicatesRequest request, StreamObserver<Main.Empty> responseObserver) {

            String tableName = request.getTableName();

            if (database.getTables().containsKey(tableName)) {
                database.getTables().get(tableName).RemoveDuplicates();
            } else {
                System.out.println("Error" );
                responseObserver.onError(new StatusException(Status.NOT_FOUND.withDescription("Table '" + tableName + "' does not exist")));
                return;
            }
        System.out.println("Dublicate delete" );
            responseObserver.onNext(Main.Empty.newBuilder().build());
            responseObserver.onCompleted();
        }
@Override
    public void getColumnsInfo(Main.GetColumnsInfoRequest request, StreamObserver<Main.ColumnsInfoResponse> responseObserver) {
            String tableName = request.getTableName();

            if (database.getTables().containsKey(tableName)) {
                List<Main.TableInfo> columnsInfo = database.getTables().get(tableName).getColumnInfo()
                        .stream()
                        .map(entry ->  Main.TableInfo.newBuilder()
                                .setColumnName(entry.getKey())
                                .setColumnType(entry.getValue().toString())
                                .build())
                        .collect(Collectors.toList());
                System.out.println("Columns Info for Table '" + tableName + "':");
                for (Main.TableInfo columnInfo : columnsInfo) {
                    System.out.println("Column Name: " + columnInfo.getColumnName() + ", Column Type: " + columnInfo.getColumnType());
                }
                responseObserver.onNext(Main.ColumnsInfoResponse.newBuilder().addAllColumnsInfo(columnsInfo).build());
            } else {
                System.out.println("Error" );
                responseObserver.onError(new StatusException(Status.NOT_FOUND.withDescription("Table '" + tableName + "' does not exist")));
            }
            responseObserver.onCompleted();
        }

    @Override
    public void getTables(Main.Empty request, StreamObserver<Main.TablesResponse> responseObserver) {

            List<String> tables = new ArrayList<>(database.getTables().keySet());
        System.out.println("Tables: " + tables);
            responseObserver.onNext(Main.TablesResponse.newBuilder().addAllTables(tables).build());
            responseObserver.onCompleted();
        }

    @Override
    public void displayTable(Main.DisplayTableRequest request, StreamObserver<Main.DisplayTableResponse> responseObserver) {
        String tableName = request.getTableName();

        if (database.getTables().containsKey(tableName)) {
            List<Struct> rows = database.getTables().get(tableName).getRows()
                    .stream()
                    .map(row -> {
                        Map<String, Value> fields = row.entrySet()
                                .stream()
                                .collect(Collectors.toMap(Map.Entry::getKey, entry ->
                                        Value.newBuilder().setStringValue(entry.getValue().toString()).build()));
                        return Struct.newBuilder().putAllFields(fields).build();
                    })
                    .collect(Collectors.toList());

            Main.DisplayTableResponse.Builder responseBuilder = Main.DisplayTableResponse.newBuilder();
            for (Struct row : rows) {
                StringBuilder rowString = new StringBuilder("{\n");
                row.getFieldsMap().forEach((key, value) -> {
                    rowString.append("\t").append(key).append(": ").append(value.getStringValue().replaceAll("\"", "")).append("\n");
                });
                rowString.append("}");
                System.out.println(rowString.toString().trim());
            }
            responseObserver.onNext(responseBuilder.addAllRows(rows).build());
        } else {
            responseObserver.onError(new StatusException(Status.NOT_FOUND.withDescription("Table '" + tableName + "' does not exist")));
        }
        responseObserver.onCompleted();
    }





    @Override
    public void updateTableCell(Main.UpdateTableCellRequest request, StreamObserver<Main.UpdateTableCellResponse> responseObserver) {

            String tableName = request.getTableName();
            int row = request.getRow();
            String colName = request.getColName();
            String colType = request.getValue();

            responseObserver.onNext(Main.UpdateTableCellResponse.newBuilder().setSuccess(false).build());

            responseObserver.onCompleted();
        }


    private Object valueToJavaType(Value value) {
        switch (value.getKindCase()) {
            case STRING_VALUE:
                return value.getStringValue();
            case NUMBER_VALUE:
                return value.getNumberValue();
            case BOOL_VALUE:
                return value.getBoolValue();
            default:
                throw new IllegalArgumentException("Unsupported Value type");
        }
    }

    private Value convertToProtoValue(Object value) {
        if (value instanceof String) {
            return Value.newBuilder().setStringValue((String) value).build();
        } else if (value instanceof Double || value instanceof Float) {
            return Value.newBuilder().setNumberValue((Double) value).build();
        } else if (value instanceof Boolean) {
            return Value.newBuilder().setBoolValue((Boolean) value).build();
        } else {
            throw new IllegalArgumentException("Unsupported Java type");
        }
    }

    private Object convertToJavaType(String value, Class<?> targetType) {
        if (targetType == String.class) {
            return value;
        } else if (targetType == Double.class || targetType == double.class) {
            return Double.parseDouble(value);
        } else if (targetType == Float.class || targetType == float.class) {
            return Float.parseFloat(value);
        } else if (targetType == Boolean.class || targetType == boolean.class) {
            return Boolean.parseBoolean(value);
        } else {
            throw new IllegalArgumentException("Unsupported target type");
        }
    }
}
