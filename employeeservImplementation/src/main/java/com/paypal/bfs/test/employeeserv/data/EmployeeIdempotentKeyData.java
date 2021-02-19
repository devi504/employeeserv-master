package com.paypal.bfs.test.employeeserv.data;

import javax.persistence.*;

@Entity
public class EmployeeIdempotentKeyData {

  @Column(name = "ID")
  private @Id
  @GeneratedValue
  Integer id;

  @Column(name = "IDEMPOTENT_KEY")
  private Integer idempotentKey;

  @OneToOne(fetch = FetchType.LAZY)
  private Employee employee;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Integer getIdempotentKey() {
    return idempotentKey;
  }

  public void setIdempotentKey(Integer idempotentKey) {
    this.idempotentKey = idempotentKey;
  }

  public Employee getEmployee() {
    return employee;
  }

  public void setEmployee(Employee employee) {
    this.employee = employee;
  }
}
