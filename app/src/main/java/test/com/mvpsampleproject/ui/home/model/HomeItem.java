package test.com.mvpsampleproject.ui.home.model;

/**
 * Created by shahrukh.malik on 09, April, 2018
 */
public class HomeItem {
    private int nameResId;
    private int imgResId;

    public HomeItem(int nameResId, int imgResId) {
        this.nameResId = nameResId;
        this.imgResId = imgResId;
    }

    public int getNameResId() {
        return nameResId;
    }

    public void setNameResId(int nameResId) {
        this.nameResId = nameResId;
    }

    public int getImgResId() {
        return imgResId;
    }

    public void setImgResId(int imgResId) {
        this.imgResId = imgResId;
    }
}
