package com.awesome.demo;

/**
 * Created by jiang.zheng on 2017/11/9.
 */
public class Applicant {

    private String firstName;
    private String lastName;

    private int id;
    private int requestAmount;
    private int creditScore;

    private boolean approved;

    public Applicant(int _id, String _firstName, String _lastName, int _requestAmount, int _creditScore) {
        id = _id;
        firstName = _firstName;
        lastName = _lastName;
        requestAmount = _requestAmount;
        creditScore = _creditScore;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRequestAmount() {
        return requestAmount;
    }

    public void setRequestAmount(int requestAmount) {
        this.requestAmount = requestAmount;
    }

    public int getCreditScore() {
        return creditScore;
    }

    public void setCreditScore(int creditScore) {
        this.creditScore = creditScore;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }
}
