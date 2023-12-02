package org.example;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws IOException, InterruptedException {
        Server server= ServerBuilder.forPort(4156).addService(new Implementation()).build();
        server.start();
        System.out.println("Server Started");
        server.awaitTermination();
    }
}
