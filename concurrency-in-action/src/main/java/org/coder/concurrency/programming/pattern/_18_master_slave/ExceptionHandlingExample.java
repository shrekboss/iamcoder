package org.coder.concurrency.programming.pattern._18_master_slave;

import org.coder.concurrency.programming.pattern._18_master_slave._reusable.RetryInfo;
import org.coder.concurrency.programming.pattern._18_master_slave._reusable.SubTaskFailureException;
import org.coder.concurrency.programming.util.Debug;

import java.math.BigInteger;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class ExceptionHandlingExample {

    public void handle(ExecutionException e, Set<BigInteger> result) {
        Throwable cause = e.getCause();
        if (SubTaskFailureException.class.isInstance(cause)) {

            @SuppressWarnings("rawtypes") RetryInfo retryInfo = ((SubTaskFailureException) cause).retryInfo;

            Object subTask = retryInfo.subTask;
            Debug.info("retrying subtask:" + subTask);

            @SuppressWarnings("unchecked") Callable<Set<BigInteger>> redoCmd = retryInfo.redoCommand;
            try {
                result.addAll(redoCmd.call());
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

}