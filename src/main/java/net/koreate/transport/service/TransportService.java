package net.koreate.transport.service;

import net.koreate.transport.vo.TrainSearchVO;
import java.util.List;

public interface TransportService {
    /**
     * 기차 조회
     */
    List<TrainSearchVO> searchTrains(String departure, String arrival, String searchDate) throws Exception;
    
    /**
     * 조회 결과를 DB에 저장 (캐싱 목적)
     */
    void saveSearchResult(TrainSearchVO trainVO) throws Exception;
    
    /**
     * 최근 조회 결과를 DB에서 가져오기
     */
    List<TrainSearchVO> getRecentSearchResults(String departure, String arrival, String searchDate) throws Exception;
    
    /**
     * 지원하는 역 목록
     */
    List<String> getSupportedStations();
    
    /**
     * 오래된 캐시 데이터 정리
     */
    void cleanupOldCache() throws Exception;
    
    /**
     * 전체 검색 통계
     */
    int getTotalSearchCount() throws Exception;
    
    /**
     * 인기 노선 조회
     */
    List<TrainSearchVO> getPopularRoutes() throws Exception;
    
    /**
     * 오늘 검색 결과 조회
     */
    List<TrainSearchVO> getTodaySearchResults() throws Exception;
    
    /**
     * 특정 노선 검색 횟수
     */
    int getRouteSearchCount(String departure, String arrival) throws Exception;
}
