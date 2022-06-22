package com.pkm.medicalinventory.util;

public class ErrorUtil {
    
    public static String getRootCaouseMessage(Throwable e)
    {
        Throwable innerException = e.getCause();
        String cause = e.getMessage();
        while(innerException != null)
        {
            cause           = innerException.getMessage();
            innerException  = innerException.getCause();
        }
        return cause;
    }
}
