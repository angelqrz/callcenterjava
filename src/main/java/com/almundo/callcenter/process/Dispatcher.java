package com.almundo.callcenter.process;

import com.almundo.callcenter.model.Employee;
import com.almundo.callcenter.model.EmployeeType;
import com.almundo.callcenter.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Class that handles incoming calls, it control the threads that are created for each call, contains a queue for the calls and a list of available employees to answer calls
 */
public class Dispatcher {

    private static final Logger logger = LoggerFactory.getLogger(Dispatcher.class);

    /**
     * List of available employees to answer calls
     */
    private List<Employee> employees;

    /**
     * The executor for the  threads that will be created for processing calls
     */
    private ExecutorService executor;

    /**
     * Queue to add incoming calls, the queue behaviour is FIFO
     */
    private ConcurrentLinkedQueue<String> calls;

    /**
     * Semaphore used for controlling the number of employees that aren't busy, it avoids the case that threads keeps looking for a free employee if there is none
     */
    private Semaphore semaphore;

    /**
     * A counter for the total number of calls processed
     */
    private AtomicInteger callsAnswered;

    /**
     * Default constructor of the class
     */
    public Dispatcher() {
        employees = new ArrayList<>();
        //Creates a new Thread Pool based on the number set in MAX_CONCURRENT_CALLS (10 as default)
        executor = Executors.newFixedThreadPool(Constants.MAX_CONCURRENT_CALLS);
        calls = new ConcurrentLinkedQueue<>();
        //Instantiate a new semaphore, setting the max number of available locks as the sum of NUMBER_OF_DIRECTORS, NUMBER_OF_OPERATORS and NUMBER_OF_SUPERVISORS
        semaphore = new Semaphore(Constants.NUMBER_OF_DIRECTORS + Constants.NUMBER_OF_OPERATORS + Constants.NUMBER_OF_SUPERVISORS, true);
        callsAnswered = new AtomicInteger(0);
    }

    /**
     * Dispatches incoming calls, for each new call it add the id to the queue and start the process of answering the call in a new thread using the executor
     *
     * @param callId the id of the new incoming call
     */
    public void dispatchCall(String callId) {
        logger.info("========New call: {}", callId);
        //Add the call to the queue
        calls.add(callId);
        //Process the call in a new thread
        executor.submit(() -> {
            try {
                //If available acquire a lock of the semaphore, if not it waits until a lock is released to continue
                semaphore.acquire();
                Optional<Employee> availableEmployee;
                do {
                    //Look for an available employee, this is inside a loop to avoid any extraordinary case when an employee is not found caused by an interrupted thread
                    availableEmployee = findAvailableEmployee();
                } while (!availableEmployee.isPresent());
                //Creates a new call using the first id in the queue (removing it from the queue) and the available employee
                Call call = new Call(calls.poll(), availableEmployee.get());
                //Process the call
                call.answerCall();
                //Increment the total of calls processed
                callsAnswered.incrementAndGet();
                //Release the lock on the semaphore, allowing another thread to take it
                semaphore.release();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    /**
     * Look for an available employee depending on its type, first it returns all free Operators, then all Supervisors and finally the Directors if there isn't another employee
     *
     * @return An employee that isn't busy
     */
    public synchronized Optional<Employee> findAvailableEmployee() {
        Optional<Employee> result = employees.stream()
                .filter(employee -> !employee.isBusy() && employee.getType() == EmployeeType.OPERADOR)
                .findFirst();
        if (!result.isPresent()) {
            result = employees.stream()
                    .filter(employee -> !employee.isBusy() && employee.getType() == EmployeeType.SUPERVISOR)
                    .findFirst();
            if (!result.isPresent()) {
                result = employees.stream()
                        .filter(employee -> !employee.isBusy() && employee.getType() == EmployeeType.DIRECTOR)
                        .findFirst();
            }
        }
        result.ifPresent(employee -> employee.setBusy(true));
        return result;
    }

    /**
     * Stops the executor waiting until all its threads are free
     */
    public void stopExecutor() {
        executor.shutdown();
    }

    /**
     * Waits for the executor to be stopped
     */
    public void waitForTermination() {
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            logger.error("Interrupted Exception",e);
            Thread.currentThread().interrupt();
        }
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    public int getCallsAnswered() {
        return callsAnswered.get();
    }

    public Queue<String> getCalls() {
        return calls;
    }

    /**
     * Set the semaphore count to a number different than the default (sum of  employees defined in Constants class)
     *
     * @param count the number of  locks that will be available in the semaphore
     */
    public void setSemaphoreCount(int count) {
        this.semaphore = new Semaphore(count, true);
    }
}
