package org.example;

import com.google.protobuf.Struct;
import com.google.protobuf.Value;
import dbservice.Main;
import dbservice.MyDatabaseServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;


public class Client {
    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:4156").usePlaintext().build();
        MyDatabaseServiceGrpc.MyDatabaseServiceBlockingStub stub = MyDatabaseServiceGrpc.newBlockingStub(channel);
        Main.CreateDatabaseRequest CreateDatabaseRequest = Main.CreateDatabaseRequest.newBuilder()
                .build();
        Main.AddTableRequest AddTableRequest = Main.AddTableRequest.newBuilder()
                .setTableName("NewTable")
                .addColumnInfo(Main.TableInfo.newBuilder().setColumnName("ID").setColumnType("INT").build())
                .addColumnInfo(Main.TableInfo.newBuilder().setColumnName("Name").setColumnType("STRING").build())
                .build();
        stub.addTable(AddTableRequest);

        Main.AddRowRequest AddRowRequest = Main.AddRowRequest.newBuilder().setTableName("NewTable")
                .addValues(Value.newBuilder().setNumberValue(1).build())
                        .addValues(Value.newBuilder().setStringValue("Bob").build()).build();
        stub.addRow(AddRowRequest);
        /////
        Main.DisplayTableRequest displayTableRequest = Main.DisplayTableRequest.newBuilder()
                .setTableName("NewTable") // Назва таблиці, яку ви хочете відобразити
                .build();

        stub.displayTable(displayTableRequest);
        /////
        Main.AddRowRequest AddRowRequest2 = Main.AddRowRequest.newBuilder().setTableName("NewTable")
                .addValues(Value.newBuilder().setNumberValue(2).build())
                .addValues(Value.newBuilder().setStringValue("Bohdan").build()).build();
        stub.addRow(AddRowRequest2);
        ////
        stub.displayTable(displayTableRequest);
        ////
        Main.AddRowRequest AddRowRequest3 = Main.AddRowRequest.newBuilder().setTableName("NewTable")
                .addValues(Value.newBuilder().setNumberValue(3).build())
                .addValues(Value.newBuilder().setStringValue("Bohdan").build()).build();
        stub.addRow(AddRowRequest3);
        ////
        Main.AddRowRequest AddRowRequest4 = Main.AddRowRequest.newBuilder().setTableName("NewTable")
                .addValues(Value.newBuilder().setNumberValue(4).build())
                .addValues(Value.newBuilder().setStringValue("Inna").build()).build();
        stub.addRow(AddRowRequest4);
        ////
        stub.displayTable(displayTableRequest);
        ////

        Main.DeleteRowRequest deleteRowRequest = Main.DeleteRowRequest.newBuilder().setTableName("NewTable").setRowIndex(1).build();
        stub.deleteRow(deleteRowRequest);
        ////
        stub.displayTable(displayTableRequest);
        ////
        Main.DeleteColumnRequest deleteColumnRequest = Main.DeleteColumnRequest.newBuilder().setTableName("NewTable").setColumnName("ID").build();
        stub.deleteColumn(deleteColumnRequest);
        ////
        stub.displayTable(displayTableRequest);
        ////
        Main.RemoveDuplicatesRequest removeDuplicatesRequest = Main.RemoveDuplicatesRequest.newBuilder().setTableName("NewTable").build();
        stub.removeDuplicates(removeDuplicatesRequest);
        stub.displayTable(displayTableRequest);
        ////
        Main.AddTableRequest AddTableRequest2 = Main.AddTableRequest.newBuilder()
                .setTableName("NewTable2")
                .addColumnInfo(Main.TableInfo.newBuilder().setColumnName("ID").setColumnType("INT").build())
                .addColumnInfo(Main.TableInfo.newBuilder().setColumnName("Work").setColumnType("STRING").build())
                .build();
        stub.addTable(AddTableRequest2);
        ///
        stub.getTables(Main.Empty.newBuilder().build());
        ////
        Main.RemoveTableRequest RemoveTableRequest = Main.RemoveTableRequest.newBuilder()
                .setTableName("NewTable2")
                .build();
        stub.removeTable(RemoveTableRequest);
        ///
        stub.getTables(Main.Empty.newBuilder().build());

        channel.shutdownNow();
    }
}
