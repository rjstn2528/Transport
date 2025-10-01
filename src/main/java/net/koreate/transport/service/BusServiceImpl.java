// BusServiceImpl.java 업데이트 - ODsay API 연동

package net.koreate.transport.service;

import net.koreate.transport.dao.BusDAO;
import net.koreate.transport.vo.BusSearchVO;
import net.koreate.transport.util.PythonCrawlerUtil;
import net.koreate.transport.util.ODsayApiUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BusServiceImpl implements BusService {

    @Autowired
    private BusDAO busDAO;

    @Autowired
    private PythonCrawlerUtil pythonCrawlerUtil;

    @Autowired
    private ODsayApiUtil odsayApiUtil;

    @Value("${odsay.api.enabled:false}")
    private boolean odsayEnabled;

    @Override
    public List<BusSearchVO> searchBuses(String departureTerminal, String arrivalTerminal, String searchDate) throws Exception {
        
        System.out.println("=== 버스 조회 시작 ===");
        System.out.println("구간: " + departureTerminal + " → " + arrivalTerminal);
        System.out.println("날짜: " + searchDate);
        System.out.println("ODsay API 활성화: " + odsayEnabled);
        
        // 1. 먼저 DB에서 최근 조회 결과 확인 (5분 이내)
        List<BusSearchVO> cachedResults = busDAO.getRecentBusSearchResults(departureTerminal, arrivalTerminal, searchDate);
        
        if (cachedResults != null && !cachedResults.isEmpty()) {
            System.out.println("✅ 캐시된 버스 결과 반환 - " + cachedResults.size() + "건");
            return cachedResults;
        }
        
        // 2. 실제 데이터 조회 (우선순위: ODsay API > Python 크롤러)
        List<BusSearchVO> results = new ArrayList<>();
        
        if (odsayEnabled) {
            System.out.println("🔄 ODsay API 호출 시작");
            results = odsayApiUtil.searchBuses(departureTerminal, arrivalTerminal, searchDate);
            
            if (results.isEmpty()) {
                System.out.println("⚠️ ODsay API 결과 없음, Python 크롤러로 대체");
                results = pythonCrawlerUtil.searchBuses(departureTerminal, arrivalTerminal, searchDate);
            }
        } else {
            System.out.println("🔄 Python 크롤러 호출 시작");
            results = pythonCrawlerUtil.searchBuses(departureTerminal, arrivalTerminal, searchDate);
        }
        
        // 3. 결과를 DB에 저장 (캐싱)
        if (results != null && !results.isEmpty()) {
            for (BusSearchVO bus : results) {
                try {
                    busDAO.insertBusSearchResult(bus);
                } catch (Exception e) {
                    // 중복 데이터 등으로 인한 오류는 로그만 남기고 계속 진행
                    System.err.println("⚠️ 버스 DB 저장 오류 (무시): " + e.getMessage());
                }
            }
            System.out.println("💾 " + results.size() + "건 버스 데이터 DB 저장 완료");
        } else {
            System.out.println("❌ 버스 조회 결과 없음");
        }
        
        System.out.println("=== 버스 조회 완료 ===");
        return results;
    }

    /**
     * API 상태 정보 조회
     */
    public Map<String, Object> getApiStatus() {
        Map<String, Object> status = new HashMap<>();
        
        // ODsay API 상태
        if (odsayEnabled) {
            status.put("odsay", odsayApiUtil.getApiInfo());
        } else {
            status.put("odsay", Map.of("enabled", false, "reason", "API 비활성화"));
        }
        
        // Python 크롤러 상태
        try {
            boolean pythonStatus = pythonCrawlerUtil.checkBusServerStatus();
            status.put("python_crawler", Map.of(
                "status", pythonStatus ? "healthy" : "unavailable",
                "url", "http://localhost:8000"
            ));
        } catch (Exception e) {
            status.put("python_crawler", Map.of(
                "status", "error",
                "error", e.getMessage()
            ));
        }
        
        // 전체 상태 결정
        boolean hasValidSource = odsayEnabled || 
                                (boolean) ((Map<String, Object>) status.get("python_crawler")).get("status").equals("healthy");
        status.put("overall_status", hasValidSource ? "operational" : "limited");
        
        return status;
    }

    // 나머지 메서드들은 이전과 동일...
    
    @Override
    public void saveBusSearchResult(BusSearchVO busVO) throws Exception {
        busDAO.insertBusSearchResult(busVO);
    }

    @Override
    public List<BusSearchVO> getRecentBusSearchResults(String departureTerminal, String arrivalTerminal, String searchDate) throws Exception {
        return busDAO.getRecentBusSearchResults(departureTerminal, arrivalTerminal, searchDate);
    }

    @Override
    public List<String> getSupportedBusTerminals() {
        return Arrays.asList(
            // 서울/경기권
            "서울고속버스터미널", "동서울터미널", "서울남부터미널", "상봉터미널",
            "인천종합버스터미널", "수원종합버스터미널", "성남종합버스터미널",
            "안양종합버스터미널", "부천종합버스터미널", "의정부버스터미널",
            
            // 강원도 (현재 없음!)
            "춘천시외버스터미널", "원주시외버스터미널", "강릉시외버스터미널",
            "속초시외버스터미널", "동해시외버스터미널", "삼척시외버스터미널",
            
            // 부산/경남권
            "부산서부터미널", "부산종합버스터미널", "부산동부터미널", "부산사상터미널",
            "창원종합버스터미널", "마산시외버스터미널", "진주시외버스터미널",
            "통영종합버스터미널", "거제시외버스터미널", "김해시외버스터미널",
            
            // 대구/경북권  
            "대구동부터미널", "대구북부터미널", "대구서부터미널",
            "포항시외버스터미널", "경주시외버스터미널", "안동터미널",
            "구미시외버스터미널", "영주시외버스터미널",
            
            // 대전/충청권
            "대전복합터미널", "대전서부터미널", "천안종합버스터미널",
            "청주시외버스터미널", "충주시외버스터미널", "제천시외버스터미널",
            
            // 광주/전라권
            "광주종합버스터미널", "광주U-Square터미널", "전주시외버스터미널",
            "순천종합버스터미널", "목포종합버스터미널", "여수시외버스터미널",
            "군산시외버스터미널", "익산시외버스터미널",
            
            // 기타
            "울산시외버스터미널", "울산고속버스터미널",
            "제주시외버스터미널", "서귀포시외버스터미널"
        );
    }

	@Override
	public void cleanupOldBusCache() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getTotalBusSearchCount() throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<BusSearchVO> getPopularBusRoutes() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<BusSearchVO> getTodayBusSearchResults() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getBusRouteSearchCount(String departureTerminal, String arrivalTerminal) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<BusSearchVO> getBusTypeStatistics() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getActiveBusRoutes() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> calculateBusFare(String departureTerminal, String arrivalTerminal, String busGrade)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> getTerminalInfo(String terminalName) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean checkBusAvailability(String departureTerminal, String arrivalTerminal, String searchDate,
			String departureTime) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Map<String, Object> getRealtimeBusLocation(String busId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateBusSchedule() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void refreshTerminalList() throws Exception {
		// TODO Auto-generated method stub
		
	}

    // 기타 메서드들도 동일하게 구현...
}