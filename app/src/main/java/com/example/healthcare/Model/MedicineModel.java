package com.example.healthcare.Model;

public class MedicineModel {
    private String img_url;
    private String medicineId;
    private String medicineName;
    private String medicinePrice;
    private String medicineQuantity;
    private String medicineType;
    private String medicineUnit;
    private String orderDate;

    // Default constructor required for Firestore
    public MedicineModel() {
    }

    public MedicineModel(String img_url, String medicineId, String medicineName, String medicinePrice, String medicineQuantity, String medicineType, String medicineUnit, String orderDate) {
        this.img_url = img_url;
        this.medicineId = medicineId;
        this.medicineName = medicineName;
        this.medicinePrice = medicinePrice;
        this.medicineQuantity = medicineQuantity;
        this.medicineType = medicineType;
        this.medicineUnit = medicineUnit;
        this.orderDate = orderDate;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getMedicineId() {
        return medicineId;
    }

    public void setMedicineId(String medicineId) {
        this.medicineId = medicineId;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public String getMedicinePrice() {
        return medicinePrice;
    }

    public void setMedicinePrice(String medicinePrice) {
        this.medicinePrice = medicinePrice;
    }

    public String getMedicineQuantity() {
        return medicineQuantity;
    }

    public void setMedicineQuantity(String medicineQuantity) {
        this.medicineQuantity = medicineQuantity;
    }

    public String getMedicineType() {
        return medicineType;
    }

    public void setMedicineType(String medicineType) {
        this.medicineType = medicineType;
    }

    public String getMedicineUnit() {
        return medicineUnit;
    }

    public void setMedicineUnit(String medicineUnit) {
        this.medicineUnit = medicineUnit;
    }
}
