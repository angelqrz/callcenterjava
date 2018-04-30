package com.almundo.callcenter;

import com.almundo.callcenter.model.Employee;
import com.almundo.callcenter.model.EmployeeType;
import com.almundo.callcenter.process.Call;
import com.almundo.callcenter.process.Dispatcher;
import com.almundo.callcenter.util.Constants;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CallCenterTest {

    private static final Logger logger = LoggerFactory.getLogger(CallCenterTest.class);

    private Dispatcher dispatcher;
    private List<Employee> employees;

    /**
     * Initialize the dispatcher object and the list of employees that will be used in the tests
     */
    @Before
    public void initializeValues() {
        employees = new ArrayList<>();
        dispatcher = new Dispatcher();
    }

    /**
     * Process ten calls simultaneously with ten employees to answer the calls
     */
    @Test
    public void numberOfCallsReceivedShouldMatchCallsAnswered() {
        logger.info("##########Testing numberOfCallsReceivedShouldMatchCallsAnswered");
        //Set the number of each type of employee and the number of calls to process
        int callsToProcess = 10;
        int numberOfOperators = 6;
        int numberOfSupervisors = 3;
        int numberOfDirectors = 1;

        //Add employees to the list
        IntStream.range(0, numberOfOperators).forEach(i -> employees.add(new Employee(i + "Op", EmployeeType.OPERADOR)));
        IntStream.range(0, numberOfSupervisors).forEach(i -> employees.add(new Employee(i + "Sp", EmployeeType.SUPERVISOR)));
        IntStream.range(0, numberOfDirectors).forEach(i -> employees.add(new Employee(i + "Dr", EmployeeType.DIRECTOR)));

        //Set the employees list in the dispatcher
        dispatcher.setEmployees(employees);

        //Process the calls
        IntStream.range(0, callsToProcess).forEach(i -> dispatcher.dispatchCall(UUID.randomUUID().toString().substring(0, 8)));
        dispatcher.stopExecutor();
        dispatcher.waitForTermination();
        assertEquals("Calls processed", callsToProcess, dispatcher.getCallsAnswered());
        logger.info("******Calls processed: {}", dispatcher.getCallsAnswered());
    }

    /**
     * Process six calls but only four employees are available to answer
     * The remainder calls will be added to a queue and will be processed when an employee becomes available
     */
    @Test
    public void queueShouldBeEmptyAfterProcessingAllCalls() {
        logger.info("##########Testing queueShouldBeEmptyAfterProcessingAllCalls");

        //Set the number of each type of employee and the number of calls to process
        int callsToProcess = 6;
        int numberOfOperators = 2;
        int numberOfSupervisors = 1;
        int numberOfDirectors = 1;

        //Add employees to the list
        IntStream.range(0, numberOfOperators).forEach(i -> employees.add(new Employee(i + "Op", EmployeeType.OPERADOR)));
        IntStream.range(0, numberOfSupervisors).forEach(i -> employees.add(new Employee(i + "Sp", EmployeeType.SUPERVISOR)));
        IntStream.range(0, numberOfDirectors).forEach(i -> employees.add(new Employee(i + "Dr", EmployeeType.DIRECTOR)));

        //Set the employees list in the dispatcher
        dispatcher.setEmployees(employees);

        //Process the calls
        IntStream.range(0, callsToProcess).forEach(i -> dispatcher.dispatchCall(UUID.randomUUID().toString().substring(0, 8)));
        dispatcher.stopExecutor();
        dispatcher.waitForTermination();
        assertEquals("Calls processed", callsToProcess, dispatcher.getCallsAnswered());
        assertTrue("Queue empty", dispatcher.getCalls().isEmpty());
        logger.info("******Queue empty: {}", dispatcher.getCalls().isEmpty());
    }

    /**
     * Process thirteen calls, despite there are eleven employees available only ten calls can be answered at any moment
     * The remainder calls will be added to a queue and will be processed when there is a free slot
     */
    @Test
    public void noMoreThanTenCallsShouldBeProcessedSimultaneously() {
        logger.info("##########Testing noMoreThanTenCallsShouldBeProcessedSimultaneously");

        //Set the number of each type of employee and the number of calls to process
        int callsToProcess = 13;
        int numberOfOperators = 8;
        int numberOfSupervisors = 3;
        int numberOfDirectors = 1;

        //Add employees to the list
        IntStream.range(0, numberOfOperators).forEach(i -> employees.add(new Employee(i + "Op", EmployeeType.OPERADOR)));
        IntStream.range(0, numberOfSupervisors).forEach(i -> employees.add(new Employee(i + "Sp", EmployeeType.SUPERVISOR)));
        IntStream.range(0, numberOfDirectors).forEach(i -> employees.add(new Employee(i + "Dr", EmployeeType.DIRECTOR)));

        //Set the employees list in the dispatcher
        dispatcher.setEmployees(employees);

        //Set the semaphore count for the dispatcher
        dispatcher.setSemaphoreCount(numberOfOperators + numberOfSupervisors + numberOfDirectors);
        //Process the calls
        IntStream.range(0, callsToProcess).forEach(i -> dispatcher.dispatchCall(UUID.randomUUID().toString().substring(0, 8)));
        dispatcher.stopExecutor();
        dispatcher.waitForTermination();
        assertEquals("Calls processed", callsToProcess, dispatcher.getCallsAnswered());
        assertTrue("Queue empty", dispatcher.getCalls().isEmpty());
    }

    /**
     * Available employees should be returned in order
     * First all Operators should answer the calls, if there isn't any Operator free a Supervisor should answer, and finally Directors if there isn't any other available employee
     */
    @Test
    public void availableEmployeeShouldBeReturnedInOrder() {
        logger.info("##########Testing availableEmployeeShouldBeReturnedInOrder");

        //Set the number of each type of employee and the number of calls to process
        int numberOfOperators = 3;
        int numberOfSupervisors = 2;
        int numberOfDirectors = 1;
        Optional<Employee> availableEmployee;

        //Add employees to the list without any order (first Supervisors, then Directors and Operators at the end)
        IntStream.range(0, numberOfSupervisors).forEach(i -> employees.add(new Employee(i + "Sp", EmployeeType.SUPERVISOR)));
        IntStream.range(0, numberOfDirectors).forEach(i -> employees.add(new Employee(i + "Dr", EmployeeType.DIRECTOR)));
        IntStream.range(0, numberOfOperators).forEach(i -> employees.add(new Employee(i + "Op", EmployeeType.OPERADOR)));

        //Set the employees list in the dispatcher
        dispatcher.setEmployees(employees);

        availableEmployee = dispatcher.findAvailableEmployee();

        //All employees are available, an Operator should be returned
        availableEmployee.ifPresent(employee -> {
            assertEquals("Available operator", EmployeeType.OPERADOR, employee.getType());
            logger.info("******Employee Type: {}", employee.getType());
        });

        //Clear the employees list
        employees.clear();

        //Add employees to the list without any order (first Directors, then Operators and Supervisors at the end) setting all Operators busy
        IntStream.range(0, numberOfDirectors).forEach(i -> employees.add(new Employee(i + "Dr", EmployeeType.DIRECTOR)));
        IntStream.range(0, numberOfOperators).forEach(i -> employees.add(new Employee(i + "Op", EmployeeType.OPERADOR, true)));
        IntStream.range(0, numberOfSupervisors).forEach(i -> employees.add(new Employee(i + "Sp", EmployeeType.SUPERVISOR)));

        //Set the employees list in the dispatcher
        dispatcher.setEmployees(employees);

        availableEmployee = dispatcher.findAvailableEmployee();

        //All operators are busy, a Supervisor should be returned
        availableEmployee.ifPresent(employee -> {
            assertEquals("Available supervisor", EmployeeType.SUPERVISOR, employee.getType());
            logger.info("******Employee Type: {}", employee.getType());
        });
        //Clear the employees list
        employees.clear();

        //Add employees to the list without any order (first Supervisors, then Directors and Operators at the end) setting all Operators and Supervisors
        IntStream.range(0, numberOfSupervisors).forEach(i -> employees.add(new Employee(i + "Sp", EmployeeType.SUPERVISOR, true)));
        IntStream.range(0, numberOfDirectors).forEach(i -> employees.add(new Employee(i + "Dr", EmployeeType.DIRECTOR)));
        IntStream.range(0, numberOfOperators).forEach(i -> employees.add(new Employee(i + "Op", EmployeeType.OPERADOR, true)));

        //Set the employees list in the dispatcher
        dispatcher.setEmployees(employees);

        availableEmployee = dispatcher.findAvailableEmployee();

        //All operators and supervisors are busy, a Director should be returned
        availableEmployee.ifPresent(employee -> {
            assertEquals("Available director", EmployeeType.DIRECTOR, employee.getType());
            logger.info("******Employee Type: {}", employee.getType());
        });
    }

    /**
     * Calls duration should be between CALL_MIN_DURATION and CALL_MAX_DURATION (5 and 10 by default)
     */
    @Test
    public void callDurationShouldBeBetweenMinAndMaxCallDuration() {
        logger.info("##########Testing callDurationShouldBeBetweenMinAndMaxCallDuration");

        int callsToTest = 5;
        IntStream.range(0, callsToTest).forEach(i -> {
            Call call = new Call();
            assertTrue("Call duration", call.getDuration() >= Constants.CALL_MIN_DURATION && call.getDuration() <= Constants.CALL_MAX_DURATION);
            logger.info("******Call duration: {}", call.getDuration());
        });
    }
}
