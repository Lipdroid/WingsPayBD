package com.example.com.wingsbangladesh.Model;

/**
 * Created by Mou on 2/23/2017.
 */

public class ModelPrint {

    public String getBarcodeId() {
        return barcodeId;
    }

    public void setBarcodeId(String barcodeId) {
        this.barcodeId = barcodeId;
    }

    public String barcodeId;


    public String paperFlyOrder;

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getMarchentCode() {
        return marchentCode;
    }

    public void setMarchentCode(String printBarcode) {
        this.marchentCode = printBarcode;
    }

    public String getPaperFlyOrder() {
        return paperFlyOrder;
    }

    public void setPaperFlyOrder(String paperFlyOrder) {
        this.paperFlyOrder = paperFlyOrder;
    }

    public String barCode;
    public String marchentCode;

}
