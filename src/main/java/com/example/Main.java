package com.example;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlet.DefaultServlet;

public class Main {
    public static void main(String[] args) throws Exception {
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));

        Server server = new Server(port);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        // Serve static site from classpath: /webapp
        context.setResourceBase(Main.class.getClassLoader().getResource("webapp").toExternalForm());

        // Default servlet serves index.html, css, js
        ServletHolder defaultServlet = new ServletHolder("default", DefaultServlet.class);
        defaultServlet.setInitParameter("dirAllowed", "false");
        defaultServlet.setInitParameter("welcomeServlets", "true");
        defaultServlet.setInitParameter("welcomeFiles", "index.html");
        context.addServlet(defaultServlet, "/");

        // API endpoint
        context.addServlet(new ServletHolder(new PersonServlet()), "/api/person");

        server.setHandler(context);

        // Ensure table exists on startup
        Db.init();

        server.start();
        System.out.println("Server running on http://localhost:" + port);
        server.join();
    }
}
