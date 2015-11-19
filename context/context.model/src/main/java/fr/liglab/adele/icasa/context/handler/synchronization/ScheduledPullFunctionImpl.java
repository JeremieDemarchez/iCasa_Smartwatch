package fr.liglab.adele.icasa.context.handler.synchronization;

import org.wisdom.api.concurrent.ManagedFutureTask;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * Created by aygalinc on 17/11/15.
 */
public class ScheduledPullFunctionImpl implements ScheduledFunction {

    private final Function m_function;

    private final String m_stateId;

    private final SynchronizationHandler m_handler;

    private final long m_period;

    private final TimeUnit m_unit;

    private  ManagedFutureTask m_futureTask;

    public ScheduledPullFunctionImpl(Function function,long period,TimeUnit unit,String stateId,SynchronizationHandler handler) {
        this.m_function = function;
        this.m_period = period;
        this.m_unit = unit;
        this.m_stateId = stateId;
        this.m_handler = handler;
    }

    @Override
    public Object apply(Object object) {
        return m_function.apply(object);
    }

    @Override
    public void run() {
        Object returnObj = m_function.apply(null);
        m_handler.update(m_stateId,returnObj);
    }

    @Override
    public long getPeriod() {
        return m_period;
    }

    @Override
    public TimeUnit getUnit() {
        return m_unit;
    }

    @Override
    public void submitted(ManagedFutureTask futureTask) {
        m_futureTask = futureTask;
    }

    @Override
    public ManagedFutureTask task() {
        return m_futureTask;
    }
}
