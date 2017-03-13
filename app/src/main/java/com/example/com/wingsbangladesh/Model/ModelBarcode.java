package com.example.com.wingsbangladesh.Model;

/**
 * Created by Mou on 2/25/2017.
 */

public class ModelBarcode {

    private String barcode_id;
    private String barcode;
    private String paperfy_order_id;
    private String marchent_ref;

    public String getMarchent_code() {
        return marchent_code;
    }

    public void setMarchent_code(String marchent_code) {
        this.marchent_code = marchent_code;
    }

    public String getCustomer_phone() {
        return customer_phone;
    }

    public void setCustomer_phone(String customer_phone) {
        this.customer_phone = customer_phone;
    }

    public String getProduct_price() {
        return product_price;
    }

    public void setProduct_price(String product_price) {
        this.product_price = product_price;
    }

    public String getBarcode_id() {
        return barcode_id;
    }

    public void setBarcode_id(String barcode_id) {
        this.barcode_id = barcode_id;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getPaperfy_order_id() {
        return paperfy_order_id;
    }

    public void setPaperfy_order_id(String paperfy_order_id) {
        this.paperfy_order_id = paperfy_order_id;
    }

    public String getMarchent_ref() {
        return marchent_ref;
    }

    public void setMarchent_ref(String marchent_ref) {
        this.marchent_ref = marchent_ref;
    }

    private String marchent_code;
    private String product_price;
    private String customer_phone;


}