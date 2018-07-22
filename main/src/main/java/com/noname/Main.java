package com.noname;

import com.noname.dao.DAOFactory;
import com.noname.dao.GenericDao;
import com.noname.dao.OptionsDAO;
import com.noname.dao.OptionsDAOBuilder;
import com.noname.h2.H2DBService;
import com.noname.h2.H2Dao;
import com.noname.misc.Environment;
import com.noname.options.CommandOptions;
import com.noname.options.server.Server;
import com.noname.server.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Properties;

public class Main {

    private static final String JDBC_URL_PATTERN = "jdbc.url.pattern";
    private static final String JDBC_USER = "jdbc.user";
    private static final String JDBC_PASSWORD = "jdbc.password";
    public static final String HTTP_URL = "http.url";

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            printUsage();
            System.exit(0);
        }
        CommandOptions<Options> options = new CommandOptions<>(args, Options.class);
        verify(options);
        final Boolean isDbServer = options.getValue(Options.iS_DB_SERVER, Boolean.class);
        final Boolean isHttpServer = options.getValue(Options.iS_HTTP_SERVER, Boolean.class);
        final Integer port = options.getValue(Options.PORT, Integer.class);
        Properties properties = load("settings.properties");

        if (isDbServer) {
            System.out.println("Starting DB server on port " + port);
            startDB(port, properties);
        } else if (isHttpServer) {
            publishDAO(options, properties);
            startHttpServer(port, properties);
        }


    }

    private static Properties load(String filename) throws IOException {
        Properties res = new Properties();
        try (InputStream inputStream = ClassLoader.getSystemResourceAsStream(filename)){
            res.load(inputStream);
        }
        return res;
    }

    private static void publishDAO(CommandOptions<Options> options, Properties properties) {
        final String dbServer = options.getValue(Options.DB_SERVER, String.class);
        if (dbServer == null) {
            System.out.println("Missing argument: " + Options.DB_SERVER.getName());
            System.exit(0);
        }
        final String urlPattern = properties.getProperty(JDBC_URL_PATTERN);
        final String user = properties.getProperty(JDBC_USER);
        final String pwd = properties.getProperty(JDBC_PASSWORD);
        final String url = String.format(urlPattern, dbServer);
        OptionsDAO optDAO = new OptionsDAOBuilder()
                .setDaoImplClass(H2Dao.class)
                .setUrl(url)
                .setUser(user)
                .setPassword(pwd)
                .build();
        final GenericDao instance = DAOFactory.getInstance(optDAO);
        Environment.INSTANCE.register(GenericDao.class, instance);


    }

    private static void startDB(Integer port, Properties properties) throws Exception {
        final H2DBService h2DBService = new H2DBService(port);
        h2DBService.start();

    }

    private static void startHttpServer(Integer port, Properties properties) throws Exception {
        final String url = properties.getProperty(HTTP_URL);
        Service service = new Server(port, Collections.singletonList(new HttpHandler(url)));
        service.start();
    }

    private static void printUsage() {
        System.out.println("Arguments example:");
        System.out.println(" -isDbServer -port <db_port_number_listen>");
        System.out.println("or");
        System.out.println("-isHttpServer -port <http_port_number_listen> -dbServer <db_port_number_listen>");
    }

    private static void verify(CommandOptions<Options> options) {

        final Boolean isHttpServer = options.getValue(Options.iS_HTTP_SERVER, Boolean.class);
        final Boolean isDbServer = options.getValue(Options.iS_DB_SERVER, Boolean.class);
        if (isHttpServer && isDbServer) {
            System.out.println("Usage both options: " + Options.iS_HTTP_SERVER.getName() + ", " + Options.iS_DB_SERVER.getName() + "is not supported");
            System.exit(0);
        }
        final String dbServer = options.getValue(Options.DB_SERVER, String.class);
        if (isHttpServer && dbServer == null) {
            System.out.println("Missing argument: " + Options.DB_SERVER.getName());
            System.exit(0);
        }

    }
}
