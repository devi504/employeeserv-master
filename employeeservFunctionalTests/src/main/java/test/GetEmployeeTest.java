package test;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.paypal.bfs.test.employeeserv.api.model.Address;
import com.paypal.bfs.test.employeeserv.api.model.Employee;
import org.springframework.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Random;

public class GetEmployeeTest {

  private static final String REST_URI
      = "http://localhost:8080/v1/bfs/employees";
  Random rd = new Random();

  public Employee createEmployee(Employee employee, int idempotentKey, Client client) {

    return client
        .target(REST_URI)
        .request(MediaType.APPLICATION_JSON).header("idempotent-key", idempotentKey)
        .post(Entity.entity(employee, MediaType.APPLICATION_JSON)).readEntity(Employee.class);

  }

  public Response getEmployee(int id, Client client) {

    return client
        .target(REST_URI)
        .path(String.valueOf(id))
        .request(MediaType.APPLICATION_JSON)
        .get();

  }

  private Client getClient() {
    return ClientBuilder.newClient().register(JacksonJaxbJsonProvider.class);
  }


  public Employee createEmployeeTestWithAllValidata() {
    Employee employee = new Employee();
    employee.setFirstName("aaaaaaaaaa");
    employee.setLastName("bbbbbbbbbb");
    employee.setDob("1989-10-1");
    Address address = new Address();
    address.setLine1("afdf");
    address.setLine2("sdfe");
    address.setCity("sdfsdf");
    address.setState("afdfs");
    address.setCountry("afdfs");
    address.setZipcode("2324242");
    employee.setAddress(address);
    return createEmployee(employee, rd.nextInt(), getClient());
  }

  @Test
  public void testGetEmployeeForExistentEmployee() {
    Employee employee = createEmployeeTestWithAllValidata();
    Response response = getEmployee(1, getClient());
    Assert.assertEquals(response.getStatus(), HttpStatus.OK.value());
    Employee result = response.readEntity(Employee.class);
    Assert.assertNotNull(result);
    Assert.assertEquals(result.getFirstName(), "aaaaaaaaaa");
  }

  @Test
  public void testGetEmployeeForNonExistentEmployee() {
    Employee employee = createEmployeeTestWithAllValidata();
    Response response = getEmployee(rd.nextInt(), getClient());
    Assert.assertEquals(response.getStatus(), HttpStatus.NOT_FOUND.value());

  }

}
