package com.employee_basic_rest_api.Employee_rest_api_vertx_postgresql;

import io.vertx.core.Vertx;

public class Application {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new MainVerticle());
  }
}
