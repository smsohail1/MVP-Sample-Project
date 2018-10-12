package test.com.mvpsampleproject.ui.booking.model;

import java.util.List;

/**
 * Created by umair.irshad on 5/3/2018.
 */

public class Product {

    private List<String> product;

    public Product(List<String> product) {
        this.product = product;
    }

    public List<String> getProduct() {
        return product;
    }

    public void setProduct(List<String> product) {
        this.product = product;
    }
}
