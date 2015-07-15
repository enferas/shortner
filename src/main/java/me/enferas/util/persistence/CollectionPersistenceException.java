package me.enferas.util.persistence;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Reports that an exception has occurred when trying to persist each of the
 * entities it holds.
 *
 * @author Alaa Sarhan
 */
public class CollectionPersistenceException extends PersistenceException {

    final private Map<Object, Throwable> exceptions;

    public CollectionPersistenceException(Map<Object, Throwable> exceptions) {
        super();
        this.exceptions = exceptions;
    }

    public CollectionPersistenceException(Map<Object, Throwable> exceptions, String message) {
        super(message);
        this.exceptions = exceptions;
    }

    public CollectionPersistenceException(String message, Object object, Throwable cause) {
        super(message, cause);
        this.exceptions = new HashMap<Object, Throwable>();
        this.exceptions.put(object, cause);
    }

    public CollectionPersistenceException(Object object, Throwable cause) {
        super(cause);
        this.exceptions = new HashMap<Object, Throwable>();
        this.exceptions.put(object, cause);
    }

    public Map<Object, Throwable> getExceptions() {
        return exceptions;
    }

    public Set getEntities() {
        return exceptions == null ? null : exceptions.keySet();
    }

    @Override
    public void printStackTrace() {
        super.printStackTrace();
        Logger logger = Logger.getLogger(CollectionPersistenceException.class.getName());
        for (Map.Entry<Object, Throwable> e : exceptions.entrySet()) {
            logger.log(Level.WARNING, "Exception occured while trying to update/refresh the object " + e.getKey(),
                    e.getValue());
        }
    }

    @Override
    public void printStackTrace(PrintStream s) {
        super.printStackTrace(s);
        for (Map.Entry<Object, Throwable> e : exceptions.entrySet()) {
            s.println("Exception occured while trying to update/refresh the object " + e.getKey());
            e.getValue().printStackTrace(s);
        }
    }

    @Override
    public void printStackTrace(PrintWriter s) {
        super.printStackTrace(s);
        for (Map.Entry<Object, Throwable> e : exceptions.entrySet()) {
            s.println("Exception occured while trying to update/refresh the object " + e.getKey());
            e.getValue().printStackTrace(s);
        }
    }

}
