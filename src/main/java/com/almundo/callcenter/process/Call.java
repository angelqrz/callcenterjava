package com.almundo.callcenter.process;

import com.almundo.callcenter.model.Employee;
import com.almundo.callcenter.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.OptionalInt;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Class containing the information of a call and the methods for processing it
 */
public class Call {

    private static final Logger logger = LoggerFactory.getLogger(Call.class);

    private int duration;
    private String callId;
    private Employee employee;

    public Call() {
        this.duration = getCallDuration();
    }

    public Call(String callId, Employee employee) {
        this.duration = getCallDuration();
        this.callId = callId;
        this.employee = employee;
    }

    /**
     * Process a call  logging the information about the id, the duration and the employee that answered, and waiting for "duration" seconds to finish
     */
    public void answerCall() {
        try {
            logger.info("++++++++Answering call: {} Employee: {} Duration: {}", callId, employee.getId(), duration);
            TimeUnit.SECONDS.sleep(duration);
            logger.info("--------Call answered: {} Employee: {} Duration: {}", callId, employee.getId(), duration);
            employee.setBusy(false);
        } catch (InterruptedException e) {
            logger.error("Interrupted Exception",e);
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Calculate a random value between CALL_MIN_DURATION and CALL_MAX_DURATION
     *
     * @return the value calculated
     */
    private int getCallDuration() {
        int minCallDuration = Constants.CALL_MIN_DURATION;
        int maxCallDuration = Constants.CALL_MAX_DURATION;
        OptionalInt callDuration;
        do {
            callDuration = new Random().ints(minCallDuration, (maxCallDuration + 1)).limit(1).findFirst();
        } while (!callDuration.isPresent());
        return callDuration.getAsInt();
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getCallId() {
        return callId;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

}
