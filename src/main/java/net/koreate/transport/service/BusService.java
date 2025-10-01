package net.koreate.transport.service;

import net.koreate.transport.vo.BusSearchVO;
import java.util.List;
import java.util.Map;

public interface BusService {
    
    /**
     * 버스 조회 (고속/시외 통합)
     */
    List<BusSearchVO> searchBuses(String departureTerminal, String arrivalTerminal, String searchDate) throws Exception;

    /**
     * 버스 검색 결과 저장
     */
    void saveBusSearchResult(BusSearchVO busVO) throws Exception;

    /**
     * 최근 버스 조회 결과 가져오기 (캐시)
     */
    List<BusSearchVO> getRecentBusSearchResults(String departureTerminal, String arrivalTerminal, String searchDate) throws Exception;

    /**
     * 지원하는 버스터미널 목록
     */
    List<String> getSupportedBusTerminals();

    /**
     * 오래된 버스 캐시 정리
     */
    void cleanupOldBusCache() throws Exception;

    /**
     * 전체 버스 검색 횟수
     */
    int getTotalBusSearchCount() throws Exception;

    /**
     * 인기 버스 노선 조회
     */
    List<BusSearchVO> getPopularBusRoutes() throws Exception;

    /**
     * 오늘 버스 검색 결과
     */
    List<BusSearchVO> getTodayBusSearchResults() throws Exception;

    /**
     * 특정 노선 버스 검색 횟수
     */
    int getBusRouteSearchCount(String departureTerminal, String arrivalTerminal) throws Exception;

    /**
     * 버스 유형별 통계
     */
    List<BusSearchVO> getBusTypeStatistics() throws Exception;

    /**
     * 활성 버스 노선 조회
     */
    List<String> getActiveBusRoutes() throws Exception;

    /**
     * 버스 요금 계산
     */
    Map<String, Object> calculateBusFare(String departureTerminal, String arrivalTerminal, String busGrade) throws Exception;

    /**
     * 터미널 상세 정보 조회
     */
    Map<String, Object> getTerminalInfo(String terminalName) throws Exception;

    /**
     * 버스 예약 가능 여부 확인
     */
    boolean checkBusAvailability(String departureTerminal, String arrivalTerminal, String searchDate, String departureTime) throws Exception;

    /**
     * 실시간 버스 위치 정보 (ODsay API 연동시 사용)
     */
    Map<String, Object> getRealtimeBusLocation(String busId) throws Exception;

    /**
     * 버스 시간표 업데이트
     */
    void updateBusSchedule() throws Exception;

    /**
     * 터미널 목록 새로고침 (Python 서버에서 가져오기)
     */
    void refreshTerminalList() throws Exception;
}