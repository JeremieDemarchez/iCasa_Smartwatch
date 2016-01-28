package fr.liglab.adele.icasa.context.runtime.handler;

import org.wisdom.api.concurrent.ManagedFutureTask;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * Created by aygalinc on 17/11/15.
 */
public class ScheduledPullFunctionImpl implements ScheduledFunction {

    private final Function myFunction;

    private final String myStateId;

    private final EntityHandler myHandler;

    private final long myPeriod;

    private final TimeUnit myUnit;

    private  ManagedFutureTask myFutureTask;

    public ScheduledPullFunctionImpl(Function function,long period,TimeUnit unit,String stateId,EntityHandler handler) {
        this.myFunction = function;
        this.myPeriod = period;
        this.myUnit = unit;
        this.myStateId = stateId;
        this.myHandler = handler;
    }

    @Override
    public Object apply(Object object) {
        return myFunction.apply(object);
    }

    @Override
    public void run() {
        Object returnObj = myFunction.apply(null);
        myHandler.update(myStateId, returnObj);
    }

    @Override
    public long getPeriod() {
        return myPeriod;
    }

    @Override
    public TimeUnit getUnit() {
        return myUnit;
    }

    @Override
    public void submitted(ManagedFutureTask futureTask) {
        myFutureTask = futureTask;
    }

    @Override
    public ManagedFutureTask task() {
        return myFutureTask;
    }
}
