package com.noname;

import org.junit.Test;
import org.mockito.Mockito;

public class MainTest {

    @Test
    public void testMainDb() throws Exception {
        final Main testObj = Mockito.mock(Main.class);
        //java -DisDbServer -Dport=8888 -jar TestWebService.jar
        setSysProps("-DisDbServer", "-Dport=8888");
        partialMock(testObj);
        testObj.run(new String[]{});
        Mockito.verify(testObj, Mockito.times(1)).startDB(Mockito.anyInt(), Mockito.any());


    }

    @Test
    public void testMainHttp() throws Exception {
        final Main testObj = Mockito.mock(Main.class);
        //java -DisHttpServer -Dport=8081 -DdbServer=localhost:8888 -jar TestWebService.jar
        setSysProps("-DisHttpServer", "-Dport=8081", "-DdbServer=localhost:8888");
        partialMock(testObj);
        testObj.run(new String[]{});
        Mockito.verify(testObj, Mockito.times(1)).publishDAO(Mockito.any());
        Mockito.verify(testObj, Mockito.times(1)).startHttpServer(Mockito.anyInt(), Mockito.any());


    }

    private void partialMock(Main testObj) throws Exception {
        Mockito.doCallRealMethod().when(testObj).run(Mockito.any());
        Mockito.doCallRealMethod().when(testObj).load(Mockito.any());
        Mockito.doCallRealMethod().when(testObj).printUsage();
        Mockito.doCallRealMethod().when(testObj).verify();
    }

    private void setSysProps(String... props) {
        clearProps();


        for (String prop : props) {
            final String[] split = removeD(prop).split("=");
            switch (split.length) {
                case 1:
                    System.setProperty(split[0], "");
                    break;
                case 2:
                    System.setProperty(split[0], split[1]);
                    break;
                default:
                    throw new UnsupportedOperationException("Unknown property = " + prop);

            }


        }
    }

    private void clearProps() {
        final Options[] values = Options.values();
        for (Options value : values) {
            System.clearProperty(value.getName());
        }
    }

    private String removeD(String prop) {
        return prop.substring(2);
    }


}