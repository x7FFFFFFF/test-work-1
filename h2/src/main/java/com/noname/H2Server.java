package com.noname;

import org.h2.tools.Server;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.UUID;

public class H2Server {


    public static void main(String[] args) throws SQLException {

        final String tempDir = System.getProperty("java.io.tmpdir");
        final UUID uuid = UUID.randomUUID();
        final Path path = Paths.get(tempDir, uuid.toString());
        // start the TCP Server
        Server server = Server.createTcpServer("-tcpPort", "9123", "-tcpAllowOthers", " -baseDir",  path.toString()).start();

// stop the TCP Server
        server.stop();

    }
}
