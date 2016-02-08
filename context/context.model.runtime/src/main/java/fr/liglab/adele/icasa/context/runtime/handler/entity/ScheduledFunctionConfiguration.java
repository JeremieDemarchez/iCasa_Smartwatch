package fr.liglab.adele.icasa.context.runtime.handler.entity;

import java.util.concurrent.TimeUnit;

/**
 * Utility class to create Scheduled Function
 */
class ScheduledFunctionConfiguration {
    private final String myField;

    private final Long myPeriod;

    private final TimeUnit myUnit;

    ScheduledFunctionConfiguration(String field,Long period,TimeUnit unit){
        myField = field;
        myPeriod = period;
        myUnit = unit;
    }

    String getFieldName(){
        return myField;
    }

    Long getPeriod(){
        return myPeriod;
    }

    TimeUnit getUnit(){
        return myUnit;
    }
}