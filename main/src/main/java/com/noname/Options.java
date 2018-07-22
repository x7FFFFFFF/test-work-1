package com.noname;

import com.noname.options.IOption;

public enum  Options implements IOption {

    iS_DB_SERVER {


        static final String iS_DB_SERVER = "-isDbServer";

        @Override
        public Class<?> getType() {
            return Boolean.class;
        }

        @Override
        public String getName() {
            return iS_DB_SERVER;
        }
    },
    iS_HTTP_SERVER {


        static final String iS_HTTP_SERVER = "-isHttpServer";

        @Override
        public Class<?> getType() {
            return Boolean.class;
        }

        @Override
        public String getName() {
            return iS_HTTP_SERVER;
        }
    },
    PORT {


        static final String PORT = "-port";

        @Override
        public Class<?> getType() {
            return Integer.class;
        }

        @Override
        public String getName() {
            return PORT;
        }
    },
    DB_SERVER {


        static final String DB_SERVER = "-dbServer";

        @Override
        public Class<?> getType() {
            return String.class;
        }

        @Override
        public String getName() {
            return DB_SERVER;
        }
    },

    JDBC_URL {


        static final String JDBC_URL = "-jdbc";

        @Override
        public Class<?> getType() {
            return String.class;
        }

        @Override
        public String getName() {
            return JDBC_URL;
        }
    },


}
