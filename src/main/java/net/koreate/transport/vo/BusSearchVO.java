package net.koreate.transport.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusSearchVO {
    private Long searchId;
    private String busType;              // 고속버스, 시외버스
    private String busCompany;           // 버스회사명
    private String busGrade;             // 우등, 일반, 프리미엄
    private String departureTerminal;    // 출발터미널
    private String arrivalTerminal;      // 도착터미널
    private String departureTime;        // 출발시간
    private String arrivalTime;          // 도착시간
    private String duration;             // 소요시간
    private String price;                // 요금
    private String remainingSeats;       // 잔여좌석
    private String searchDate;           // 조회날짜 (YYYYMMDD)
    private Date createdDate;            // 조회시간
    private String busNumber;   		 // 버스 번호
    
    // 통계용 필드
    private Integer searchCount;         // 검색 횟수

    // 편의 생성자
    public BusSearchVO(String busType, String busCompany, String busNumber, String busGrade,
            String departureTerminal, String arrivalTerminal, 
            String departureTime, String arrivalTime, String duration,
            String price, String remainingSeats, String searchDate) {
			this.busType = busType;
			this.busCompany = busCompany;
			this.busNumber = busNumber;  // 추가
			this.busGrade = busGrade;
			this.departureTerminal = departureTerminal;
			this.arrivalTerminal = arrivalTerminal;
			this.departureTime = departureTime;
			this.arrivalTime = arrivalTime;
			this.duration = duration;
			this.price = price;
			this.remainingSeats = remainingSeats;
			this.searchDate = searchDate;
			this.createdDate = new Date();
			}
			
			// Getter/Setter 추가
			public String getBusNumber() {
			return busNumber;
			}
			
			public void setBusNumber(String busNumber) {
			this.busNumber = busNumber;
			}
			
			// 또는 busNumber가 없을 때 대체값을 반환하는 메서드
			public String getBusIdentifier() {
			if (busNumber != null && !busNumber.trim().isEmpty()) {
			  return busNumber;
			}
			// busNumber가 없으면 회사명과 시간 조합으로 대체
			return busCompany + "_" + departureTime.replace(":", "");
			}


    // 가격 표시용 메서드
    public String getFormattedPrice() {
        return price != null ? price : "요금 문의";
    }

    // 예약 가능 여부 체크
    public boolean isAvailable() {
        return "예약가능".equals(remainingSeats) || 
               (remainingSeats != null && remainingSeats.contains("잔여"));
    }

    // 버스 정보 요약
    public String getBusInfo() {
        return busType + " (" + busCompany + ")";
    }

    // 등급별 아이콘
    public String getGradeIcon() {
        switch (busGrade) {
            case "프리미엄":
                return "👑";
            case "우등":
                return "⭐";
            case "일반":
            default:
                return "🚌";
        }
    }

    // 소요시간을 분 단위로 변환 (정렬용)
    public int getDurationInMinutes() {
        if (duration == null) return 0;
        
        try {
            // "4시간 15분" 형태를 분으로 변환
            String[] parts = duration.replace("시간", "").replace("분", "").split(" ");
            int hours = 0, minutes = 0;
            
            if (parts.length >= 1) hours = Integer.parseInt(parts[0].trim());
            if (parts.length >= 2) minutes = Integer.parseInt(parts[1].trim());
            
            return hours * 60 + minutes;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    // 빌더 패턴 사용 예시
    public static BusSearchVO createSample(String departure, String arrival, String date) {
        return BusSearchVO.builder()
                .busType("고속버스")
                .busCompany("동양고속")
                .busGrade("우등")
                .departureTerminal(departure)
                .arrivalTerminal(arrival)
                .departureTime("08:00")
                .arrivalTime("12:30")
                .duration("4시간 30분")
                .price("27,000원")
                .remainingSeats("예약가능")
                .searchDate(date)
                .createdDate(new Date())
                .build();
    }
}