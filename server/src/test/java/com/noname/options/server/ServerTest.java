package com.noname.options.server;



import com.noname.options.client.Client;
import com.noname.server.*;
import org.junit.Assert;
import org.junit.Test;


import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ServerTest {


    @Test
    public void test() throws Exception {
        Map<String,String> params = new HashMap<>();
        params.put(Server.PORT, "8881");

        final Server server = new Server(params, Collections.singletonList(new RequestHandler() {

            @Override
            public Methods getMethod() {
                return Methods.POST;
            }

            @Override
            public String getUrl() {
                return "/h1";
            }

            @Override
            public Codec getCodec() {

                return new TestXmlServerCodec();
            }

            @Override
            public boolean process(HttpRequest request, HttpResponse response) {
                TestResponse res = new TestResponse();
                response.setSource(res);
                return true;
            }
        }));

        server.start();
        server.waitForRun();

        HttpRequest httpRequest = new HttpRequestImpl();
        httpRequest.setMethod(Methods.POST);
        httpRequest.setUri(new URI("http://localhost:8881/h1"));
        TestRequest payload = new TestRequest();
        payload.setRequestsType(RequestsTypes.CREATE_AGT);
        payload.getExtras().put(Extras.LOGIN, "123");
        payload.getExtras().put(Extras.PASSWORD, "pwd");
        httpRequest.setSource(payload);

        Client client = new Client(params, new XmlClientCodec());

        client.start();
        client.waitForRun();
        final HttpResponse httpResponse = client.send(httpRequest);
        final TestResponse responsePayload = (TestResponse) httpResponse.getSource();
        Assert.assertNotNull(responsePayload);
        Assert.assertEquals(responsePayload.getResultCode(), 0);
        client.stop();
        client.waitForStop();

        server.stop();
        server.waitForStop();

       // Thread.sleep(1000*20);

    }




}
