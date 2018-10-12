package test.com.mvpsampleproject.di.module;

import com.tcs.pickupapp.ui.delete_cn_sequence.DeletedCNSequenceMVP;
import com.tcs.pickupapp.ui.delete_cn_sequence.DeletedCNSequenceModel;
import com.tcs.pickupapp.ui.delete_cn_sequence.DeletedCNSequencePresenter;

import dagger.Module;
import dagger.Provides;

/**
 * Created by abdul.ahad on 04-Apr-18.
 */

@Module
public class DeletedCNSequenceModule {

    @Provides
    public DeletedCNSequenceMVP.Presenter providerDeletedCNSequencePresenter(DeletedCNSequenceMVP.Model model) {
        return new DeletedCNSequencePresenter(model);
    }

    @Provides
    public DeletedCNSequenceMVP.Model providerDeletedCNSequenceModel(com.tcs.pickupapp.data.room.AppDatabase appDatabase) {
        return new DeletedCNSequenceModel(appDatabase);
    }
}
