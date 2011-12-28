<<<<<<< HEAD
package org.walkmanz.gardenz.persistent.jdbc;

import java.io.PrintStream;
import java.io.PrintWriter;


public class DaoException extends Exception {

	private Throwable throwable = null;
	
    public DaoException() {
        super();
    }

    public DaoException(String message, Throwable cause) {
        super(message, cause);
    }

    public DaoException(String message) {
        super(message);
    }
    
    public DaoException(Throwable cause) {
        super(cause.getMessage());
        throwable = cause;
    }
    
    public Throwable getRootCause() {
        return throwable;
    }

    public void printStackTrace(PrintWriter pw) {
        if (throwable == null) {
            super.printStackTrace(pw);
        } else {
            throwable.printStackTrace(pw);
        }
    }

    public void printStackTrace(PrintStream ps) {
        if (throwable == null) {
            super.printStackTrace(ps);
        } else {
            throwable.printStackTrace(ps);
        }
    }

    public void printStackTrace() {
        if (throwable == null) {
            super.printStackTrace();
        } else {
            throwable.printStackTrace();
        }
    }
=======
package org.walkmanz.gardenz.persistent.jdbc;

import java.io.PrintStream;
import java.io.PrintWriter;


public class DaoException extends Exception {

	private Throwable throwable = null;
	
    public DaoException() {
        super();
    }

    public DaoException(String message, Throwable cause) {
        super(message, cause);
    }

    public DaoException(String message) {
        super(message);
    }
    
    public DaoException(Throwable cause) {
        super(cause.getMessage());
        throwable = cause;
    }
    
    public Throwable getRootCause() {
        return throwable;
    }

    public void printStackTrace(PrintWriter pw) {
        if (throwable == null) {
            super.printStackTrace(pw);
        } else {
            throwable.printStackTrace(pw);
        }
    }

    public void printStackTrace(PrintStream ps) {
        if (throwable == null) {
            super.printStackTrace(ps);
        } else {
            throwable.printStackTrace(ps);
        }
    }

    public void printStackTrace() {
        if (throwable == null) {
            super.printStackTrace();
        } else {
            throwable.printStackTrace();
        }
    }
>>>>>>> 98ae9342edf48ad40a999cec1b2213e8fb3c2eae
}