package test.com.mvpsampleproject.util.callback;

/**
 * Created by umair.irshad on 4/3/2018.
 */

public interface ServiceListener<T> {

    void onSuccess(T object);
    void onError(ServiceError error);

}
