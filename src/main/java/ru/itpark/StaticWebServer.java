package ru.itpark;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class StaticWebServer {
    public static void main(String[] args) {
        try (
                ServerSocket staticServer = new ServerSocket(9888);
        ) {

            while (true) {
                System.out.println("Server start");
                try (Socket socket = staticServer.accept();) {
                    BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    OutputStream out = socket.getOutputStream();
                    String linePath = input.readLine();
                    String[] partsPath = linePath.split(" ");
                    String path = partsPath[1];

                    try {

                        Path requestPath = Paths.get("filesCatalog", path);
                        if (!Files.exists(requestPath) || !Files.isRegularFile(requestPath)) {
                            throw new NoSuchFieldException(path);
                        }

                        long sizeFile = Files.size(requestPath);
                        out.write(("HTTP/1.1 200 OK\r\n" +
                                "Content-lehgth: " + sizeFile + "\r\n" +
                                "Content-Type: " + Files.probeContentType(requestPath) + "\r\n" +
                                "Connection: close\r\n\r\n").getBytes());

                        Files.copy(requestPath, out);

//                        try(Stream<Path> entries = Files.list(requestPath); ){
//                            System.out.println("Всего файлов в каталоге: "+entries.count());
//
//                        }

                    } catch (NoSuchFieldException e) {
                        Path requestPath = Paths.get("public", "404.html");
                        long sizeFile = Files.size(requestPath);
                        out.write(("HTTP/1.1 404\r\n" +
                                "Content-lehgth: " + sizeFile + "\r\n" +
                                "Content-Type: " + Files.probeContentType(requestPath) + "\r\n" +
                                "Connection: close\r\n\r\n").getBytes());
                        Files.copy(requestPath, out);


                    }

                }
            }

        } catch (IOException e) {
            e.printStackTrace();

        }

    }
}
