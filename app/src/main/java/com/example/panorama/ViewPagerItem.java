package com.example.panorama;

public class ViewPagerItem {

    float totalNumber;
    String cashOrNet;
    int leftImageId;
    int rightImageId;

    public ViewPagerItem(float totalNumber, String cashOrNet, int leftImageId, int rightImageId) {
        this.totalNumber = totalNumber;
        this.cashOrNet = cashOrNet;
        this.leftImageId = leftImageId;
        this.rightImageId = rightImageId;
    }
}
