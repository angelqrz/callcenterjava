package com.almundo.callcenter;

import com.almundo.callcenter.model.Employee;
import com.almundo.callcenter.model.EmployeeType;
import com.almundo.callcenter.process.Dispatcher;
import com.almundo.callcenter.util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.stream.IntStream;

/**
 * Main class of CallCenter app
 */
public class CallCenter {

    public static void main(String[] args) {
        //Initialize the dispatcher and the employee list that will be used during the run of the app
        Dispatcher dispatcher = new Dispatcher();
        List<Employee> employees = new ArrayList<>();

        //Add employees to the list
        IntStream.range(0, Constants.NUMBER_OF_OPERATORS).forEach(i -> employees.add(new Employee(i + "Op", EmployeeType.OPERADOR)));
        IntStream.range(0, Constants.NUMBER_OF_SUPERVISORS).forEach(i -> employees.add(new Employee(i + "Sp", EmployeeType.SUPERVISOR)));
        IntStream.range(0, Constants.NUMBER_OF_DIRECTORS).forEach(i -> employees.add(new Employee(i + "Dr", EmployeeType.DIRECTOR)));

        //Set the employee list to the dispatcher
        dispatcher.setEmployees(employees);

        Scanner in = new Scanner(System.in);
        String inputValue;
        int callsToProcess;
        //Read from the input the number of calls that will be processed until the word 'exit' is entered
        do {
            System.out.println("Number of calls to process ('exit' to finish) : ");
            inputValue = in.nextLine();
            try {
                callsToProcess = Integer.parseInt(inputValue);
                IntStream.range(0, callsToProcess).forEach(i -> dispatcher.dispatchCall(UUID.randomUUID().toString().substring(0, 8)));
            } catch (NumberFormatException e) {
                //If the value entered isn't a valid number or 'exit' it is ignored
            }
        } while (inputValue.compareToIgnoreCase("exit") != 0);

        //Stops the executors and all threads inside its pool
        dispatcher.stopExecutor();

        //Shows the total of calls processed during the run of the app
        System.out.println("Total calls processed = " + dispatcher.getCallsAnswered());
    }
}
