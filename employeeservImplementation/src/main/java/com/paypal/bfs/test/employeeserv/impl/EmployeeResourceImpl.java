package com.paypal.bfs.test.employeeserv.impl;

import com.paypal.bfs.test.employeeserv.api.EmployeeResource;
import com.paypal.bfs.test.employeeserv.data.EmployeeDAO;
import com.paypal.bfs.test.employeeserv.data.EmployeeIdempotentKeyData;
import com.paypal.bfs.test.employeeserv.data.EmployeeIdempotentKeyDataDAO;
import com.paypal.bfs.test.employeeserv.api.model.Address;
import com.paypal.bfs.test.employeeserv.api.model.Employee;
import com.paypal.bfs.test.employeeserv.exception.BfsBusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Implementation class for employee resource.
 */
@RestController
public class EmployeeResourceImpl implements EmployeeResource {

  private static final Logger LOG = LoggerFactory.getLogger(EmployeeResourceImpl.class);

  @Inject
  EmployeeDAO employeeDAO;

  @Inject
  EmployeeIdempotentKeyDataDAO idempotentKeyDataDAO;


  /**
   * Retrieves employee for the supplied id.
   *
   * @param id employee id.
   */
  @Override
  @RequestMapping("/v1/bfs/employees/{id}")
  public ResponseEntity<Employee> employeeGetById(@PathVariable("id") String id) {

    LOG.info("getting employee {}", id);
    Optional<com.paypal.bfs.test.employeeserv.data.Employee> employee1 = employeeDAO.findById(
        Integer.parseInt(id));

    if (employee1.isPresent()) {
      LOG.info("found employee {}", employee1.get());
      Employee response = prepareResponseEntity(employee1.get());
      return new ResponseEntity<>(response, HttpStatus.OK);
    } else {
      LOG.info(" not found employee for {}", id);
      throw new BfsBusinessException("employee not found", "id", "request param",
          HttpStatus.NOT_FOUND);
    }

  }

  /**
   * Creates employee.
   */
  @Override
  @RequestMapping("/v1/bfs/employees")
  public ResponseEntity<Employee> createEmployee(@Valid @RequestBody Employee employee,
      @RequestHeader("idempotent-key") int idempotentKey) {

    List<EmployeeIdempotentKeyData> idempotentKeyDataList = idempotentKeyDataDAO.
        findByIdempotentKey(idempotentKey);

    com.paypal.bfs.test.employeeserv.data.Employee employee1;
    if (CollectionUtils.isEmpty(idempotentKeyDataList)) {
      employee1 = saveEmployeeToRepository(employee, idempotentKey);
      Employee response = prepareResponseEntity(employee1);
      return new ResponseEntity<>(response, HttpStatus.CREATED);
    } else {
      LOG.info("employee exists in DB");
      employee1 = idempotentKeyDataList.get(0).getEmployee();
      Employee response = prepareResponseEntity(employee1);
      return new ResponseEntity<>(response, HttpStatus.OK);
    }


  }

  private com.paypal.bfs.test.employeeserv.data.Employee saveEmployeeToRepository(Employee employee,
      int idempotentKey) {

    com.paypal.bfs.test.employeeserv.data.Employee employeeData = new com.paypal.bfs.test.employeeserv.data.Employee(
        employee.getFirstName(), employee.getLastName());
    long dob;
    try {
      dob = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(employee.getDob()).getTime() / 1000;
    } catch (ParseException e) {
      throw new BfsBusinessException("wrong data format", null, null, HttpStatus.BAD_REQUEST);
    }
    com.paypal.bfs.test.employeeserv.data.Address addressData = new com.paypal.bfs.test.employeeserv.data.Address();
    addressData.setLine1(employee.getAddress().getLine1());
    addressData.setLine2(employee.getAddress().getLine2());
    addressData.setCity(employee.getAddress().getCity());
    addressData.setState(employee.getAddress().getState());
    addressData.setCountry(employee.getAddress().getCountry());
    addressData.setZipcode(employee.getAddress().getZipcode());
    employeeData.setAddress(addressData);
    EmployeeIdempotentKeyData idempotentKeyData = new EmployeeIdempotentKeyData();
    idempotentKeyData.setIdempotentKey(idempotentKey);
    employeeData.setIdempotentData(idempotentKeyData);
    employeeData.setDateOfBirth(dob);
    com.paypal.bfs.test.employeeserv.data.Employee employee1 = employeeDAO.save(employeeData);
    return employee1;

  }

  private Employee prepareResponseEntity(com.paypal.bfs.test.employeeserv.data.Employee employee) {
    Employee response = new Employee();
    response.setId(employee.getEmployeeId());
    response.setFirstName(employee.getFirstName());
    response.setLastName(employee.getLastName());
    String dob = new java.text.SimpleDateFormat("yyyy-MM-dd")
        .format(new java.util.Date(employee.getDateOfBirth() * 1000));
    Address address = new Address();
    address.setLine1(employee.getAddress().getLine1());
    address.setLine2(employee.getAddress().getLine2());
    address.setCity(employee.getAddress().getCity());
    address.setState(employee.getAddress().getState());
    address.setCountry(employee.getAddress().getCountry());
    address.setZipcode(employee.getAddress().getZipcode());
    response.setAddress(address);
    response.setDob(dob);
    return response;
  }
}
