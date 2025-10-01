package net.koreate.transport.service;

import java.util.List;
import java.util.Map;

import net.koreate.transport.vo.FlightSearchVO;

/**
 * 항공편 조회 서비스 인터페이스
 */
public interface FlightService {
    
    /**
     * 항공편 검색
     * @param departureAirport 출발공항
     * @param arrivalAirport 도착공항  
     * @param departureDate 출발날짜 (YYYY-MM-DD)
     * @param adults 성인 승객 수
     * @return 항공편 목록
     */
    List<FlightSearchVO> searchFlights(String departureAirport, String arrivalAirport, 
                                     String departureDate, int adults) throws Exception;
    
    /**
     * 지원하는 공항 목록 조회
     * @return 공항 목록 (지역별)
     */
    Map<String, Object> getSupportedAirports() throws Exception;
    
    /**
     * Flask API 서버 상태 확인
     * @return 서버 상태
     */
    boolean checkApiServerStatus();
}
