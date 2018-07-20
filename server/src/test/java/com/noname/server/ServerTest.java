package com.noname.server;



import com.noname.client.Client;
import org.junit.Assert;
import org.junit.Test;


import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ServerTest {
    void setUp() {

    }

    void tearDown() {
    }

    @Test
    public void test() throws Exception {
        Map<String,String> params = new HashMap<>();
        params.put(Server.PORT, "8881");

        final Server server = new Server(params, Collections.singletonList(new IRequestHandler() {

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

                return new TestXmlServerCodec();
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

        IRequest request = new RequestImpl();
        request.setMethod(Methods.POST);
        request.setUri(new URI("http://localhost:8881/h1"));
        TestRequest payload = new TestRequest();
        payload.setRequestsType(RequestsTypes.CREATE_AGT);
        payload.getExtras().put(Extras.LOGIN, "123");
        payload.getExtras().put(Extras.PASSWORD, "pwd");
        request.setSource(payload);

        Client client = new Client(params, new XmlClientCodec());

        client.start();
        client.waitForRun();
        final IResponse response = client.send(request);
        final TestResponse responsePayload = (TestResponse) response.getSource();
        Assert.assertNotNull(responsePayload);
        Assert.assertEquals(responsePayload.getResultCode(), 0);
        client.stop();


        server.stop();
        server.waitForStop();

    }




}
