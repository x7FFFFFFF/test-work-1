package com.noname.options;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class CommandOptionsTest {


    enum Options implements IOption {
        PORT {


            static final String TCP_PORT = "-tcpPort";

            @Override
            public Class<?> getType() {
                return Integer.class;
            }

            @Override
            public String getName() {
                return TCP_PORT;
            }
        },
        ALLOW_OTHERS {

            static final String TCP_ALLOW_OTHERS = "-tcpAllowOthers";

            @Override
            public Class<?> getType() {
                return Boolean.class;
            }

            @Override
            public String getName() {
                return TCP_ALLOW_OTHERS;
            }
        },

        BASE_DIR {
            static final String BASE_DIR1 = "-baseDir";

            @Override
            public Class<?> getType() {
                return String.class;
            }

            @Override
            public String getName() {
                return BASE_DIR1;
            }
        },

        SERVER {
            static final String SERVER_STR = "-server";

            @Override
            public Class<?> getType() {
                return String.class;
            }

            @Override
            public String getName() {
                return SERVER_STR;
            }
        },
        SERVER_LIST {
            static final String SERVER_LIST = "-serverList";
            @Override
            public Class<?> getType() {
                return String.class;
            }

            @Override
            public String getName() {
                return SERVER_LIST;
            }
        }


        ;



    }

    @Test
    public void testParseArgs() {
        String[] args = new String[]{"-tcpPort", "9123", "-tcpAllowOthers", "-baseDir", "/tmp", "-server", "localhost:9090", "-server", "localhost:9091",  "-serverList",
                "localhost:9101,localhost:9102"};
        CommandOptions<Options> options = new CommandOptions<>(args, Options.class);

        Integer port = options.getValue(Options.PORT, Integer.class);
        Boolean tcpAllowOthers = options.getValue(Options.ALLOW_OTHERS, Boolean.class);
        List<String> servers = options.getValues(Options.SERVER, String.class);
        List<String> serverList = options.getValues(Options.SERVER_LIST, String.class);


        Assert.assertEquals(Integer.valueOf(9123), port);
        Assert.assertEquals(Boolean.TRUE, tcpAllowOthers);
        Assert.assertEquals(2, servers.size());
        Assert.assertEquals("localhost:9090", servers.get(0));
        Assert.assertEquals("localhost:9091", servers.get(1));

        Assert.assertEquals(2, serverList.size());
        Assert.assertEquals("localhost:9101", serverList.get(0));
        Assert.assertEquals("localhost:9102", serverList.get(1));

    }
}
