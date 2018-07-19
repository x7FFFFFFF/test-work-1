package com.noname.server;



import com.noname.client.Client;
import org.junit.Test;


import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
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
        Map<String,String> params = new HashMap<>();
        params.put(IServer.PORT, "8881");
        final Server server = new Server();
        server.init(params, Collections.singletonList(new IRequestHandler() {

            @Override
            public Methods getMethod() {
                return Methods.POST;
            }

            @Override
            public String getUrl() {
                return "/h1";
            }

            @Override
            public ICodec getCodec() {
                return new TestXmlCodec();
            }

            @Override
            public boolean process(IRequest request, IResponse response) {
                TestResponse res = new TestResponse();
                response.setSource(res);
                return true;
            }
        }));

        server.start();
        server.waitForRun();
        System.out.println("params = " + server.getStatus());
        //server.stop();
        //server.waitForStop();
        //System.out.println("params = " + server.getStatus());



        URI uri =new URI("http://localhost:8881/h1");
        Client client = new Client(uri,  Methods.POST, "<?xml version=\"1.0\" encoding=\"utf-8\" ?>   <request><request-type>CREATE-AGT</request-type><extra name=\"login\">123</extra><extra name=\"password\">pwd</extra></request>");
        client.start();

    }




}
