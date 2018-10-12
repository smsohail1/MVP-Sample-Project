package test.com.mvpsampleproject.di.module;

import com.tcs.pickupapp.ui.dimension.DimensionMVP;
import com.tcs.pickupapp.ui.dimension.DimensionModel;
import com.tcs.pickupapp.ui.dimension.DimensionPresenter;

import dagger.Module;
import dagger.Provides;

/**
 * Created by umair.irshad on 4/3/2018.
 */

@Module
public class DimensionModule {

    @Provides
    public DimensionMVP.Presenter provideDimensionPresenter(DimensionMVP.Model model){
        return new DimensionPresenter(model);
    }

    @Provides
    public DimensionMVP.Model provideDimensionModel(com.tcs.pickupapp.data.room.AppDatabase appDatabase){
        return new DimensionModel(appDatabase);
    }

}
