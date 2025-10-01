package net.koreate.transport.vo;

import java.sql.Timestamp;

/**
 * 항공편 검색 결과 VO
 */
public class FlightSearchVO {
    
    private Long searchId;                // 검색 ID (DB 기본키)
    private String airlineCode;           // 항공사 코드 (KE, OZ 등)
    private String airlineName;           // 항공사 이름 (대한항공, 아시아나 등)
    private String flightNumber;          // 편명 (KE706, OZ104 등)
    private String departureAirport;      // 출발공항 코드 (ICN, GMP 등)
    private String arrivalAirport;        // 도착공항 코드 (NRT, BKK 등)
    private String departureTime;         // 출발시간 (HH:mm 또는 YYYY-MM-DD HH:mm)
    private String arrivalTime;           // 도착시간 (HH:mm 또는 YYYY-MM-DD HH:mm)
    private String duration;              // 비행시간 (2시간 15분)
    private int price;                    // 가격 (원)
    private String currency;              // 통화 (KRW, USD 등)
    private String seatClass;             // 좌석 등급 (ECONOMY, BUSINESS, FIRST)
    private String remainingSeats;        // 잔여석 정보 (예약가능, 잔여 3석 등)
    private String searchDate;            // 검색한 날짜 (YYYY-MM-DD)
    private int adults;                   // 승객 수
    private Timestamp createdDate;        // 검색 생성일시
    
    // 기본 생성자
    public FlightSearchVO() {}
    
    // 검색 파라미터용 생성자
    public FlightSearchVO(String departureAirport, String arrivalAirport, String searchDate, int adults) {
        this.departureAirport = departureAirport;
        this.arrivalAirport = arrivalAirport;
        this.searchDate = searchDate;
        this.adults = adults;
    }
    
    // 전체 파라미터 생성자
    public FlightSearchVO(String airlineCode, String airlineName, String flightNumber,
                         String departureAirport, String arrivalAirport,
                         String departureTime, String arrivalTime, String duration,
                         int price, String currency, String seatClass, String remainingSeats,
                         String searchDate, int adults) {
        this.airlineCode = airlineCode;
        this.airlineName = airlineName;
        this.flightNumber = flightNumber;
        this.departureAirport = departureAirport;
        this.arrivalAirport = arrivalAirport;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.duration = duration;
        this.price = price;
        this.currency = currency;
        this.seatClass = seatClass;
        this.remainingSeats = remainingSeats;
        this.searchDate = searchDate;
        this.adults = adults;
        this.createdDate = new Timestamp(System.currentTimeMillis());
    }
    
    // Getter & Setter
    public Long getSearchId() {
        return searchId;
    }
    
    public void setSearchId(Long searchId) {
        this.searchId = searchId;
    }
    
    public String getAirlineCode() {
        return airlineCode;
    }
    
    public void setAirlineCode(String airlineCode) {
        this.airlineCode = airlineCode;
    }
    
    public String getAirlineName() {
        return airlineName;
    }
    
    public void setAirlineName(String airlineName) {
        this.airlineName = airlineName;
    }
    
    public String getFlightNumber() {
        return flightNumber;
    }
    
    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }
    
    public String getDepartureAirport() {
        return departureAirport;
    }
    
    public void setDepartureAirport(String departureAirport) {
        this.departureAirport = departureAirport;
    }
    
    public String getArrivalAirport() {
        return arrivalAirport;
    }
    
    public void setArrivalAirport(String arrivalAirport) {
        this.arrivalAirport = arrivalAirport;
    }
    
    public String getDepartureTime() {
        return departureTime;
    }
    
    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }
    
    public String getArrivalTime() {
        return arrivalTime;
    }
    
    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }
    
    public String getDuration() {
        return duration;
    }
    
    public void setDuration(String duration) {
        this.duration = duration;
    }
    
    public int getPrice() {
        return price;
    }
    
    public void setPrice(int price) {
        this.price = price;
    }
    
    public String getCurrency() {
        return currency;
    }
    
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    public String getSeatClass() {
        return seatClass;
    }
    
    public void setSeatClass(String seatClass) {
        this.seatClass = seatClass;
    }
    
    public String getRemainingSeats() {
        return remainingSeats;
    }
    
    public void setRemainingSeats(String remainingSeats) {
        this.remainingSeats = remainingSeats;
    }
    
    public String getSearchDate() {
        return searchDate;
    }
    
    public void setSearchDate(String searchDate) {
        this.searchDate = searchDate;
    }
    
    public int getAdults() {
        return adults;
    }
    
    public void setAdults(int adults) {
        this.adults = adults;
    }
    
    public Timestamp getCreatedDate() {
        return createdDate;
    }
    
    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }
    
    // 편의 메서드들
    
    /**
     * 가격을 포맷된 문자열로 반환
     */
    public String getFormattedPrice() {
        if (currency != null && currency.equals("KRW")) {
            return String.format("%,d원", price);
        } else {
            return String.format("%s %,d", currency != null ? currency : "KRW", price);
        }
    }
    
    /**
     * 출발공항과 도착공항을 화살표로 연결한 문자열
     */
    public String getRoute() {
        return departureAirport + " → " + arrivalAirport;
    }
    
    /**
     * 항공사와 편명을 함께 표시
     */
    public String getAirlineAndFlight() {
        return String.format("%s (%s)", 
               airlineName != null ? airlineName : airlineCode, 
               flightNumber != null ? flightNumber : "");
    }
    
    /**
     * 출발시간과 도착시간을 함께 표시
     */
    public String getTimeRange() {
        return String.format("%s - %s", 
               departureTime != null ? departureTime : "", 
               arrivalTime != null ? arrivalTime : "");
    }
    
    /**
     * 좌석 등급을 한글로 변환
     */
    public String getSeatClassKorean() {
        if (seatClass == null) return "일반석";
        
        switch (seatClass.toUpperCase()) {
            case "ECONOMY":
                return "일반석";
            case "BUSINESS":
                return "비즈니스석";
            case "FIRST":
                return "일등석";
            case "PREMIUM_ECONOMY":
                return "프리미엄 일반석";
            default:
                return seatClass;
        }
    }
    
    @Override
    public String toString() {
        return String.format("FlightSearchVO{searchId=%d, airline='%s', flight='%s', route='%s', " +
                           "departure='%s', arrival='%s', duration='%s', price=%d, seats='%s'}",
                           searchId, airlineName, flightNumber, getRoute(),
                           departureTime, arrivalTime, duration, price, remainingSeats);
    }
}