package com.employee_basic_rest_api.Employee_rest_api_vertx_postgresql;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start() {

    JDBCClient jdbcClient = JDBCClient.createShared(vertx, new JsonObject()
      .put("url", "jdbc:postgresql://localhost:5432/employee_vertx1")
      .put("driver_class", "org.postgresql.Driver")
      .put("user", "postgres")
      .put("password", "11223344Abc$")
      .put("max_pool_size", 30));

    EmployeeService employeeService = new EmployeeService(jdbcClient);


//  Create an HTTP server
    HttpServer httpServer = vertx.createHttpServer();

//  Create a router instance
    Router router = Router.router(vertx);
    router.route().handler(BodyHandler.create());

//  Use the router instance and generate routes
    Route getAll = router.get("/employees")
      .handler(routingContext -> {
        HttpServerResponse response = routingContext.response();
        employeeService.getEmployees().onComplete(res -> {
          if(res.succeeded()) {
            response
              .putHeader("content-type", "application/json")
              .end(new JsonArray(res.result()).encodePrettily()); //convert from List<JsonObject> to JsonArray
          } else response.setStatusCode(500).end(res.cause().getMessage());
        });
      });

    Route getById = router.get("/employees/:id")
      .handler(routingContext -> {
        HttpServerResponse response = routingContext.response();
        int id = Integer.parseInt(routingContext.pathParam("id"));
        employeeService.getEmployeeById(id).onComplete(res -> {
          if(res.succeeded()) {
            response
              .putHeader("content-type", "application/json")
              .end(res.result().encodePrettily()); //Already a JsonObject
          } else response.setStatusCode(404).end(res.cause().getMessage());
        });
      });

    Route add = router.post("/employees")
      .handler(routingContext -> {
        HttpServerResponse response = routingContext.response();
        System.out.println(routingContext.body().asJsonObject());
        Employee employee = routingContext.body().asJsonObject().mapTo(Employee.class);
        employeeService.addEmployee(employee).onComplete(res -> {
          response
            .putHeader("content-type", "application/json")
            .end(JsonObject.mapFrom(res.result()).encodePrettily()); //Convert from Employee to JsonObject
        });
      });

    Route update = router.put("/employees/:id")
      .handler(routingContext -> {
        HttpServerResponse response = routingContext.response();
        int id = Integer.parseInt(routingContext.pathParam("id"));
        Employee employee = routingContext.body().asPojo(Employee.class);
        employeeService.updateEmployee(id, employee).onComplete(res -> {
          if(res.succeeded()) {
            response
              .putHeader("content-type", "application/json")
              .end(JsonObject.mapFrom(res.result()).encodePrettily()); //Convert from Employee to JsonObject ---> It accepts String/ Buffer/ JsonObject/ JsonArray
          } else response.setStatusCode(404).end(res.cause().getMessage());
        });
      });

    Route delete = router.delete("/employees/:id")
      .handler(routingContext -> {
        HttpServerResponse response = routingContext.response();
        int id = Integer.parseInt(routingContext.pathParam("id"));
        employeeService.deleteEmployee(id).onComplete(res -> {
          if(res.succeeded()) {
            response
              .putHeader("content-type", "application/json")
              .end("Deleted!");
          } else response.setStatusCode(404).end(res.cause().getMessage());
        });
      });

//  Make the server to use the router instance and listen to particular port
    httpServer.requestHandler(router).listen(8091);
  }
}
