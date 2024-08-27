package com.employee_basic_rest_api.Employee_rest_api_vertx_postgresql;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Employee {
  private int id;
  private String name;
  private String email;
}
