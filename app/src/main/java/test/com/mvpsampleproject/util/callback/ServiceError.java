package test.com.mvpsampleproject.util.callback;

/**
 * Created by umair.irshad on 4/3/2018.
 */

public class ServiceError {

    private String message;
    private Throwable throwable;

    public ServiceError(){
        message = "";
    }

    public ServiceError(String message){
        this.message = message;
    }

    public ServiceError(String message, Throwable throwable){
        this.message = message;
        this.throwable = throwable;
    }

    public String getMessage() {
        return message;
    }

    public Throwable getThrowable() {
        return throwable;
    }
}
