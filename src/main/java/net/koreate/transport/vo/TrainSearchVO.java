package net.koreate.transport.vo;

import java.util.Date;

public class TrainSearchVO {
    private Long searchId;
    private String trainType;
    private String trainNumber;
    private String departureStation;
    private String arrivalStation;
    private String departureTime;
    private String arrivalTime;
    private String duration;
    private String generalPrice;
    private String specialPrice;
    private String availability;
    private String searchDate;
    private Date createdDate;
    
    // 추가: JSP에서 사용할 price 필드
    private int price;

    // 기본 생성자
    public TrainSearchVO() {}

    // 전체 생성자
    public TrainSearchVO(String trainType, String trainNumber, String departureStation, 
                        String arrivalStation, String departureTime, String arrivalTime, 
                        String duration, String generalPrice, String specialPrice, 
                        String availability, String searchDate) {
        this.trainType = trainType;
        this.trainNumber = trainNumber;
        this.departureStation = departureStation;
        this.arrivalStation = arrivalStation;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.duration = duration;
        this.generalPrice = generalPrice;
        this.specialPrice = specialPrice;
        this.availability = availability;
        this.searchDate = searchDate;
        
        // price 필드 자동 계산
        this.price = calculatePrice();
    }

    // Getter/Setter 메서드들
    public Long getSearchId() { return searchId; }
    public void setSearchId(Long searchId) { this.searchId = searchId; }

    public String getTrainType() { return trainType; }
    public void setTrainType(String trainType) { this.trainType = trainType; }

    public String getTrainNumber() { return trainNumber; }
    public void setTrainNumber(String trainNumber) { this.trainNumber = trainNumber; }

    public String getDepartureStation() { return departureStation; }
    public void setDepartureStation(String departureStation) { this.departureStation = departureStation; }

    public String getArrivalStation() { return arrivalStation; }
    public void setArrivalStation(String arrivalStation) { this.arrivalStation = arrivalStation; }

    public String getDepartureTime() { return departureTime; }
    public void setDepartureTime(String departureTime) { this.departureTime = departureTime; }

    public String getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(String arrivalTime) { this.arrivalTime = arrivalTime; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public String getGeneralPrice() { return generalPrice; }
    public void setGeneralPrice(String generalPrice) { 
        this.generalPrice = generalPrice;
        this.price = calculatePrice(); // 가격 변경 시 price도 업데이트
    }

    public String getSpecialPrice() { return specialPrice; }
    public void setSpecialPrice(String specialPrice) { this.specialPrice = specialPrice; }

    public String getAvailability() { return availability; }
    public void setAvailability(String availability) { this.availability = availability; }

    public String getSearchDate() { return searchDate; }
    public void setSearchDate(String searchDate) { this.searchDate = searchDate; }

    public Date getCreatedDate() { return createdDate; }
    public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }

    // 새로 추가: price 필드
    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }

    /**
     * generalPrice에서 숫자만 추출하여 int로 변환
     */
    private int calculatePrice() {
        if (generalPrice == null || generalPrice.trim().isEmpty() || "-".equals(generalPrice)) {
            return 0;
        }
        
        try {
            // "59,800원" → "59800"으로 변환
            String numericPrice = generalPrice.replaceAll("[^0-9]", "");
            return numericPrice.isEmpty() ? 0 : Integer.parseInt(numericPrice);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Override
    public String toString() {
        return "TrainSearchVO{" +
                "searchId=" + searchId +
                ", trainType='" + trainType + '\'' +
                ", trainNumber='" + trainNumber + '\'' +
                ", departureStation='" + departureStation + '\'' +
                ", arrivalStation='" + arrivalStation + '\'' +
                ", departureTime='" + departureTime + '\'' +
                ", arrivalTime='" + arrivalTime + '\'' +
                ", duration='" + duration + '\'' +
                ", generalPrice='" + generalPrice + '\'' +
                ", specialPrice='" + specialPrice + '\'' +
                ", availability='" + availability + '\'' +
                ", searchDate='" + searchDate + '\'' +
                ", price=" + price +
                ", createdDate=" + createdDate +
                '}';
    }
}