package main;

import static java.util.Locale.filter;
import org.apache.log4j.PropertyConfigurator;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import servlets.admin.PartialServlet;
import servlets.admin.category.ManageCategoryServlet;

public class Main {

    public static void main(String[] args) throws Exception {
        PropertyConfigurator.configure("log4j.properties");

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);

        context.addServlet(new ServletHolder(new PartialServlet()), "/admin/partital/*");
        context.addServlet(new ServletHolder(new ManageCategoryServlet()), "/admin/category");
//
//        FilterHolder authenFilter = new FilterHolder(new AuthenFilter());
//        authenFilter.setName("AuthenFilter");
//        context.addFilter(authenFilter, "/*", null);

        ContextHandler resourceHandler = new ContextHandler("/static");
        String resource = "./public";
        if (!resource.isEmpty()) {
            resourceHandler.setResourceBase(resource);
            resourceHandler.setHandler(new ResourceHandler());
        }

        ContextHandler avatarHandler = new ContextHandler("/avatar");
        String resourceUpload = "./avatar";
        avatarHandler.setResourceBase(resourceUpload);
        avatarHandler.setHandler(new ResourceHandler());

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{resourceHandler, avatarHandler, context});

        Server server = new Server(8080);

        server.setHandler(handlers);

        server.start();

        System.out.println("Server started");

        server.join();
    }
}
