package net.koreate.transport.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.koreate.transport.dao.FlightDAO;
import net.koreate.transport.service.FlightService;
import net.koreate.transport.util.AmadeusApiUtil;
import net.koreate.transport.vo.FlightSearchVO;

/**
 * 항공편 조회 서비스 구현체 - JDK 11 호환 버전
 */
@Service
public class FlightServiceImpl implements FlightService {
    
    private static final Logger logger = LoggerFactory.getLogger(FlightServiceImpl.class);
    
    @Autowired
    private FlightDAO flightDAO;
    
    @Autowired 
    private AmadeusApiUtil amadeusApiUtil;
    
    @Override
    public List<FlightSearchVO> searchFlights(String departureAirport, String arrivalAirport, 
                                            String departureDate, int adults) throws Exception {
        
        logger.info("항공편 검색 서비스 시작: {} -> {}, {}, {}명", 
                   departureAirport, arrivalAirport, departureDate, adults);
        
        List<FlightSearchVO> flights = new ArrayList<>();
        
        try {
            // 1. 캐시된 데이터 확인 (5분 이내)
            flights = flightDAO.getCachedFlights(departureAirport, arrivalAirport, departureDate);
            
            if (flights != null && !flights.isEmpty()) {
                logger.info("캐시된 항공편 데이터 사용: {}건", flights.size());
                return flights;
            }
            
            // 2. Flask API를 통한 실시간 검색
            flights = amadeusApiUtil.searchFlights(departureAirport, arrivalAirport, departureDate, adults);
            
            if (flights != null && !flights.isEmpty()) {
                // 3. 검색 결과를 DB에 캐시 저장
                try {
                    flightDAO.saveFlightCache(flights);
                    logger.info("항공편 검색 결과 캐시 저장 완료: {}건", flights.size());
                } catch (Exception e) {
                    logger.warn("항공편 캐시 저장 실패 (검색은 성공): {}", e.getMessage());
                }
                
                return flights;
            }
            
            // 4. API 실패 시 빈 목록 반환
            logger.warn("항공편 검색 결과 없음 또는 API 실패");
            return new ArrayList<>();
            
        } catch (Exception e) {
            logger.error("항공편 검색 서비스 오류: ", e);
            throw new Exception("항공편 검색 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    @Override
    public Map<String, Object> getSupportedAirports() throws Exception {
        try {
            return amadeusApiUtil.getSupportedAirports();
        } catch (Exception e) {
            logger.error("지원 공항 목록 조회 오류: ", e);
            throw new Exception("공항 목록을 불러올 수 없습니다: " + e.getMessage());
        }
    }
    
    @Override
    public boolean checkApiServerStatus() {
        try {
            return amadeusApiUtil.checkApiServerStatus();
        } catch (Exception e) {
            logger.error("API 서버 상태 확인 오류: ", e);
            return false;
        }
    }
}