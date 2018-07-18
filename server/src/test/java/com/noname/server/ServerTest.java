package com.noname.server;



import com.noname.client.Client;
import org.junit.Test;


import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ServerTest {
    void setUp() {

    }

    void tearDown() {
    }

    @Test
    public void test() throws Exception {
        int port = 8888;
        final Server server = new ServerBuilder().setPort(port).addHandler(new TestHandler()).createServer();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(server);

        URI uri = new URI("localhost:8888/h1");
        Client client = new Client(uri,  Methods.GET);
        client.start();

    }




}
