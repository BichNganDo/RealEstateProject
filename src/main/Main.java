package main;

import org.apache.log4j.PropertyConfigurator;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import servlets.admin.PartialServlet;
import servlets.admin.api.APIBannerServlet;
import servlets.admin.api.APICategoryServlet;
import servlets.admin.banner.AddBannerServlet;
import servlets.admin.banner.EditBannerServlet;
import servlets.admin.banner.ManageBannerServlet;
import servlets.admin.cate_news.AddCategoryNewsServlet;
import servlets.admin.cate_news.ManageCategoryNewsServlet;
import servlets.admin.category.AddCategoryServlet;
import servlets.admin.category.EditCategoryServlet;
import servlets.admin.category.ManageCategoryServlet;

public class Main {

    public static void main(String[] args) throws Exception {
        PropertyConfigurator.configure("log4j.properties");

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);

        context.addServlet(new ServletHolder(new PartialServlet()), "/admin/partital/*");
        context.addServlet(new ServletHolder(new ManageCategoryServlet()), "/admin/category");
        context.addServlet(new ServletHolder(new AddCategoryServlet()), "/admin/category/add");
        context.addServlet(new ServletHolder(new EditCategoryServlet()), "/admin/category/edit");

        context.addServlet(new ServletHolder(new ManageBannerServlet()), "/admin/banner");
        context.addServlet(new ServletHolder(new AddBannerServlet()), "/admin/banner/add");
        context.addServlet(new ServletHolder(new EditBannerServlet()), "/admin/banner/edit");

        context.addServlet(new ServletHolder(new ManageCategoryNewsServlet()), "/admin/category_news");
        context.addServlet(new ServletHolder(new AddCategoryNewsServlet()), "/admin/category_news/add");

        context.addServlet(new ServletHolder(new APICategoryServlet()), "/admin/api/category");
        context.addServlet(new ServletHolder(new APIBannerServlet()), "/admin/api/banner");

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

        ContextHandler imageBannerHandler = new ContextHandler("/upload");
        String resourceUpload = "./upload";
        imageBannerHandler.setResourceBase(resourceUpload);
        imageBannerHandler.setHandler(new ResourceHandler());

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{resourceHandler, imageBannerHandler, context});

        Server server = new Server(8080);

        server.setHandler(handlers);

        server.start();

        System.out.println("Server started");

        server.join();
    }
}
