package com.almundo.callcenter;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

/**
 * Class to run the tests of the app
 */
public class CallCenterTestRunner {

    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(CallCenterTest.class);

        for (Failure failure : result.getFailures()) {
            System.out.println(failure.toString());
        }

        System.out.println(result.wasSuccessful());
    }
}
