package test.com.mvpsampleproject.di.module;

import com.tcs.pickupapp.util.AppConstants;

import java.util.concurrent.TimeUnit;

import dagger.Module;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by muhammad.sohail on 5/14/2018.
 */

@Module
public class WebServiceFactoryModule {

    private static com.tcs.pickupapp.data.rest.WebService mService;

    public static com.tcs.pickupapp.data.rest.WebService getInstance() {
        if (mService == null) {

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();

            // set your desired log level
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

            // add your other interceptors …
            httpClient.readTimeout(60, TimeUnit.SECONDS);
            httpClient.connectTimeout(60, TimeUnit.SECONDS);

            // add logging as last interceptor
            httpClient.addInterceptor(logging);  // <-- this is the important line!


            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(AppConstants.BASE_URL_API)
                    .client(httpClient.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            mService = retrofit.create(com.tcs.pickupapp.data.rest.WebService.class);
        }
        return mService;
    }
}
