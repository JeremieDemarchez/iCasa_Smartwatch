package fr.liglab.adele.icasa.context.runtime.handler.entity;

import org.wisdom.api.concurrent.ManagedFutureTask;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * Created by aygalinc on 17/11/15.
 */
public interface ScheduledFunction extends Function,Runnable {

    long getPeriod();

    TimeUnit getUnit();

    void submitted(ManagedFutureTask futureTask);

    ManagedFutureTask task();
}
