package com.paypal.bfs.test.employeeserv.data;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Employee {

  @Column(name = "EMPLOYEE_ID")
  private @Id
  @GeneratedValue
  Integer employeeId;
  private String firstName;
  private String lastName;
  private long dateOfBirth;
  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "id", referencedColumnName = "id")
  private Address address;

  @OneToOne(cascade = CascadeType.ALL, mappedBy = "employee")
  @JoinColumn(name = "idempotentKey", referencedColumnName = "IDEMPOTENT_KEY")
  private EmployeeIdempotentKeyData idempotentData;

  public Employee() {
  }

  public Employee(String firstName, String lastName) {
    this.firstName = firstName;
    this.lastName = lastName;
  }

  public EmployeeIdempotentKeyData getIdempotentData() {
    return idempotentData;
  }

  public void setIdempotentData(EmployeeIdempotentKeyData idempotentData) {
    this.idempotentData = idempotentData;
  }


  public Integer getEmployeeId() {
    return employeeId;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setEmployeeId(Integer employeeId) {
    this.employeeId = employeeId;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Employee employee = (Employee) o;
    return Objects.equals(employeeId, employee.employeeId) &&
        Objects.equals(firstName, employee.firstName) &&
        Objects.equals(lastName, employee.lastName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(employeeId, firstName, lastName);
  }

  public Address getAddress() {
    return address;
  }

  public void setAddress(Address address) {
    this.address = address;
  }

  @PrePersist
  @PreUpdate
  public void updateIdempotentKeyData() {
    idempotentData.setEmployee(this);
  }

  public long getDateOfBirth() {
    return dateOfBirth;
  }

  public void setDateOfBirth(long dateOfBirth) {
    this.dateOfBirth = dateOfBirth;
  }
}
