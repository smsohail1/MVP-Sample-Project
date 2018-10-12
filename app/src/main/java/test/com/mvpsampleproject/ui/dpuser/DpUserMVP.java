package test.com.mvpsampleproject.ui.dpuser;

import android.content.Context;

/**
 * Created by muhammad.sohail on 4/5/2018.
 */

public interface DpUserMVP {
    interface View {
        void showToastShortTime(String message);

        void showToastLongTime(String message);

        void showLoginScreen();


        void showProgressDialogPleaseWait();

        void hideProgressDialogPleaseWait();

        void refreshGallery(String[] filePaths);
        void refreshGallery(String filePath);
    }

    interface Presenter {
        void setView(DpUserMVP.View view);

        void onClickBtnClearData(Context context);

        void onClickLocalOMS();

        void onClickBtnCallBuildFile();

        void onClickBtnCallBuildNTFile();

    }

    interface Model {
        void fetchRecord(DpUserModel.IDpUser idpUser);
        void fetchNTRecord(DpUserModel.IDpUser idpUser);
        void uploadSingleBooking(com.tcs.pickupapp.data.room.model.Booking booking, com.tcs.pickupapp.data.rest.INetwork iNetwork);
        void fetchRecordsInFile(DpUserModel.IDPFileData idpFileData);
        void fetchNTRecordsInFile(DpUserModel.IDPFileData idpFileData);
        void clearData(DpUserModel.IDpUserClearData iDpUserClearData);
        void clearAllData(DpUserModel.IDpUserClearDataStatus iDpUserClearDataStatus);
        void fetchPickupAppTravelog(DpUserModel.IDpUserUploadTravelFileStatus iDpUserUploadTravelFileStatus);
    }
}














