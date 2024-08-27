package com.employee_basic_rest_api.Employee_rest_api_vertx_postgresql;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.SQLConnection;

import java.util.List;

public class EmployeeService {

  private final JDBCClient jdbcClient;

  public EmployeeService(JDBCClient jdbcClient) {
    this.jdbcClient = jdbcClient;
  }

  public Future<List<JsonObject>> getEmployees() {
//  Create a Promise<T> instance
    Promise<List<JsonObject>> promise = Promise.promise();
/*
    ar, res -> they are all handlers.
    If we want result from the handler, then we need to get ar.result()
    If we want error from the handler, then we need to get ar.cause()
    Similarly we have methods like succeeded(), failed()
 */
    jdbcClient.getConnection(ar -> {
      if(ar.succeeded()) {
        SQLConnection connection = ar.result();
        connection.query("select * from employee",
          res -> {
          if(res.succeeded()) {
            promise.complete(res.result().getRows());
          } else promise.fail(res.cause());
        });

      } else promise.fail(ar.cause());
    });

//  Return what Promise is delivering to Future
    return promise.future();
  }

  public Future<JsonObject> getEmployeeById(int id) {
    Promise<JsonObject> promise = Promise.promise();
    jdbcClient.getConnection(ar -> {
      if(ar.succeeded()) {
//      If we are going to use something from the method parameter, the go with queryWithParams() instead of query()
        ar.result().queryWithParams("select * from employee where id = ?",
          new JsonArray().add(id),
          res -> {
          if(res.succeeded()) {
            promise.complete(res.result().getRows().get(0)); //Based on type of Promise & Future
          } else promise.fail("Employee with ID: " + id + " not found!");
        });

      } else promise.fail(ar.cause());
    });

    return promise.future();
  }

  public Future<Employee> addEmployee(Employee employee) {
    Promise<Employee> promise = Promise.promise();
    jdbcClient.getConnection(ar -> {
      if(ar.succeeded()) {
        ar.result().queryWithParams("insert into employee(name, email) values(?, ?)",
          new JsonArray().add(employee.getName()).add(employee.getEmail()),
          res -> {
          if(res.succeeded()) {
            promise.complete(employee); //Based on type of Promise & Future
          } else promise.fail(res.cause());
          });

      } else promise.fail(ar.cause());
    });

    return promise.future();
  }

  public Future<Employee> updateEmployee(int id, Employee employee) {
    Promise<Employee> promise = Promise.promise();
    jdbcClient.getConnection(ar -> {
      if(ar.succeeded()) {
        ar.result().queryWithParams("update employee set name = ?, email = ? where id = ?",
          new JsonArray().add(employee.getName()).add(employee.getEmail()).add(id),
          res -> {
          if(res.succeeded()) {
            promise.complete(employee); //Based on type of Promise & Future
          } else promise.fail(res.cause());
          });

      } else promise.fail(ar.cause());
    });

    return promise.future();
  }

  public Future<Void> deleteEmployee(int id) {
    Promise<Void> promise = Promise.promise();
    jdbcClient.getConnection(ar -> {
      if(ar.succeeded()) {
        ar.result().queryWithParams("delete from employee where id=?",
          new JsonArray().add(id),
          res -> {
          if(res.succeeded()) {
            promise.complete(); //Based on type of Promise & Future
          } else promise.fail(res.cause());
          });

      } else promise.fail(ar.cause());
    });

    return promise.future();
  }
}
