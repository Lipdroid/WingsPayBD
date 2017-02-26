package com.example.com.wingsbangladesh.Model;

/**
 * Created by sabbir on 2/22/17.
 */

public class ModelPickUpSummary {


    public String pickup_id;

    public String getMarchent_id() {
        return marchent_id;
    }
    public void setMarchent_id(String marchent_id) {
        this.marchent_id = marchent_id;
    }

    public String getPickup_id() {
        return pickup_id;
    }

    public void setPickup_id(String pickup_id) {
        this.pickup_id = pickup_id;
    }

    public String getTotal_order_count() {
        return total_order_count;
    }

    public void setTotal_order_count(String total_order_count) {
        this.total_order_count = total_order_count;
    }
    public String getL_s_splus_order_count() {
        return l_s_splus_order_count;
    }

    public void setL_s_splus_order_count(String l_s_splus_order_count) {
        this.l_s_splus_order_count = l_s_splus_order_count;
    }
    public String getExpress_order_count() {
        return express_order_count;
    }

    public void setExpress_order_count(String express_order_count) {
        this.express_order_count = express_order_count;
    }
    public String marchent_id;
    public String total_order_count;
    public String l_s_splus_order_count;
    public String express_order_count;
}
