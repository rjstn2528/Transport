package net.koreate.transport.service;

import net.koreate.transport.dao.TransportDAO;
import net.koreate.transport.vo.TrainSearchVO;
import net.koreate.transport.util.PythonCrawlerUtil;
import net.koreate.transport.util.TaGoApiUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

@Service
public class TransportServiceImpl implements TransportService {

    @Autowired
    private TransportDAO transportDAO;

    @Autowired
    private PythonCrawlerUtil pythonCrawlerUtil;

    
    // TAGO API 유틸리티 추가
    @Autowired(required = false)
    private TaGoApiUtil taGoApiUtil;

    @Override
    public List<TrainSearchVO> searchTrains(String departure, String arrival, String searchDate) throws Exception {
        
        System.out.println("=== 기차 조회 시작 ===");
        System.out.println("구간: " + departure + " → " + arrival);
        System.out.println("날짜: " + searchDate);
        
        // 1. 먼저 DB에서 최근 조회 결과 확인 (5분 이내)
        List<TrainSearchVO> cachedResults = transportDAO.getRecentSearchResults(departure, arrival, searchDate);
        
        if (cachedResults != null && !cachedResults.isEmpty()) {
            System.out.println("✅ 캐시된 기차 결과 반환 - " + cachedResults.size() + "건");
            return cachedResults;
        }
        
        // 2. TAGO API 시도 (1순위 - 공식 API)
        List<TrainSearchVO> tagoResults = new ArrayList<>();
        if (taGoApiUtil != null) {
            System.out.println("🔄 TAGO API 호출 시작");
            try {
                tagoResults = taGoApiUtil.searchTrains(departure, arrival, searchDate);
                
                if (tagoResults != null && !tagoResults.isEmpty()) {
                    System.out.println("✅ TAGO API 성공 - " + tagoResults.size() + "건");
                    // saveTrainResults(tagoResults);  // DB 저장 비활성화
                    System.out.println("=== 기차 조회 완료 (TAGO API) ===");
                    return tagoResults;
                } else {
                    System.out.println("⚠️ TAGO API 결과 없음");
                }
            } catch (Exception e) {
                System.err.println("⚠️ TAGO API 오류: " + e.getMessage());
            }
        } else {
            System.out.println("⚠️ TaGoApiUtil이 주입되지 않음");
        }
        

        
        // 4. Python 크롤러로 대체 (3순위 - 마지막 대안)
        System.out.println("⚠️ 모든 API 실패, Python 크롤러로 대체");
        List<TrainSearchVO> crawledResults = pythonCrawlerUtil.searchTrains(departure, arrival, searchDate);
        
        // 5. 결과를 DB에 저장 (캐싱)
        if (crawledResults != null && !crawledResults.isEmpty()) {
            saveTrainResults(crawledResults);
            System.out.println("💾 " + crawledResults.size() + "건 기차 데이터 DB 저장 완료");
        }
        
        System.out.println("=== 기차 조회 완료 ===");
        return crawledResults;
    }

    /**
     * 기차 검색 결과를 DB에 저장
     */
    private void saveTrainResults(List<TrainSearchVO> trains) {
        for (TrainSearchVO train : trains) {
            try {
                transportDAO.insertSearchResult(train);
            } catch (Exception e) {
                // 중복 데이터 등으로 인한 오류는 로그만 남기고 계속 진행
                System.err.println("⚠️ 기차 DB 저장 오류 (무시): " + e.getMessage());
            }
        }
    }

    @Override
    public void saveSearchResult(TrainSearchVO trainVO) throws Exception {
        transportDAO.insertSearchResult(trainVO);
    }

    @Override
    public List<TrainSearchVO> getRecentSearchResults(String departure, String arrival, String searchDate) throws Exception {
        return transportDAO.getRecentSearchResults(departure, arrival, searchDate);
    }

    @Override
    public List<String> getSupportedStations() {
        return Arrays.asList(
            // KTX 주요역
            "서울", "용산", "영등포", "광명", "천안아산", "오송", "대전", 
            "김천구미", "동대구", "신경주", "울산", "부산",
            "광주송정", "목포", "여수EXPO", "순천",
            
            // ITX-새마을/무궁화호 추가역
            "청량리", "왕십리", "구로", "안양", "수원", "평택", "천안",
            "조치원", "서대전", "계룡", "논산", "익산", "정읍", "광주",
            "나주", "함평", "신태인", "장성",
            
            // 동해선 
            "포항", "경주", "울산", "태화강", "밀양", "진영", "창원중앙",
            "마산", "진주", "순천", "여천", "여수",
            
            // 경춘선/중앙선 
            "춘천", "남춘천", "청량리", "상봉", "양평", "용문", "지평",
            "원주", "제천", "단양", "영주", "안동", "의성", "경주"
        );
    }

    @Override
    public void cleanupOldCache() throws Exception {
        transportDAO.deleteOldCacheData();
        System.out.println("🗑️ 오래된 캐시 데이터 정리 완료");
    }

    @Override
    public int getTotalSearchCount() throws Exception {
        return transportDAO.getTotalSearchCount();
    }

    @Override
    public List<TrainSearchVO> getPopularRoutes() throws Exception {
        return transportDAO.getPopularRoutes();
    }

    @Override
    public List<TrainSearchVO> getTodaySearchResults() throws Exception {
        return transportDAO.getTodaySearchResults();
    }

    @Override
    public int getRouteSearchCount(String departure, String arrival) throws Exception {
        return transportDAO.getRouteSearchCount(departure, arrival);
    }
}