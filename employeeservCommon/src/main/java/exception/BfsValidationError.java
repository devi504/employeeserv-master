package com.paypal.bfs.test.employeeserv.exception;


import java.util.ArrayList;
import java.util.List;

public class BfsValidationError {

  private List<com.paypal.bfs.test.employeeserv.exception.BfsBusinessError> validationErrors = new ArrayList<>();

  public List<com.paypal.bfs.test.employeeserv.exception.BfsBusinessError> getValidationErrors() {
    return validationErrors;
  }

  public void setValidationErrors(List<com.paypal.bfs.test.employeeserv.exception.BfsBusinessError> validationErrors) {
    this.validationErrors = validationErrors;
  }
}