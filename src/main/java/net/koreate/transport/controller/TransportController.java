package net.koreate.transport.controller;

import net.koreate.transport.service.TransportService;
import net.koreate.transport.vo.TrainSearchVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/transport")
public class TransportController {

    @Autowired
    private TransportService transportService;

    /**
     * 메인 페이지 - 교통수단 선택
     */
    @GetMapping("/")
    public String main() {
        return "transport/main";
    }

    /**
     * 루트 경로도 메인으로 연결
     */
    @GetMapping("")
    public String mainRoot() {
        return "transport/main";
    }

    /**
     * 기차 조회 페이지
     */
    @GetMapping("/train")
    public String trainSearchForm(Model model) {
        model.addAttribute("title", "기차표 조회");
        model.addAttribute("stations", transportService.getSupportedStations());
        return "transport/train/search";
    }

    /**
     * 기차 조회 결과
     */
    @PostMapping("/train/search")
    public String searchTrains(HttpServletRequest request, Model model) {
        try {
            String departure = request.getParameter("departure");
            String arrival = request.getParameter("arrival");
            String searchDate = request.getParameter("searchDate");

            // 파라미터 검증
            if (departure == null || arrival == null || searchDate == null) {
                model.addAttribute("error", "모든 필드를 입력해주세요.");
                model.addAttribute("stations", transportService.getSupportedStations());
                return "transport/train/search";
            }

            if (departure.equals(arrival)) {
                model.addAttribute("error", "출발역과 도착역이 같을 수 없습니다.");
                model.addAttribute("stations", transportService.getSupportedStations());
                return "transport/train/search";
            }

            // 날짜 형식 변환 (YYYY-MM-DD -> YYYYMMDD)
            String formattedDate = searchDate.replace("-", "");

            // 서비스 호출
            List<TrainSearchVO> trainList = transportService.searchTrains(departure, arrival, formattedDate);

            // 모델에 데이터 추가
            model.addAttribute("trainList", trainList);
            model.addAttribute("departure", departure);
            model.addAttribute("arrival", arrival);
            model.addAttribute("searchDate", searchDate);
            model.addAttribute("resultCount", trainList.size());

            return "transport/train/result";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "조회 중 오류가 발생했습니다: " + e.getMessage());
            model.addAttribute("stations", transportService.getSupportedStations());
            return "transport/train/search";
        }
    }

    /**
     * AJAX용 기차 조회 API
     */
    @PostMapping("/api/train/search")
    @ResponseBody
    public List<TrainSearchVO> searchTrainsApi(@RequestParam String departure,
                                               @RequestParam String arrival,
                                               @RequestParam String searchDate) {
        try {
            String formattedDate = searchDate.replace("-", "");
            return transportService.searchTrains(departure, arrival, formattedDate);
        } catch (Exception e) {
            throw new RuntimeException("기차 조회 실패", e);
        }
    }

    /**
     * 인기 노선 조회 API
     */
    @GetMapping("/api/popular-routes")
    @ResponseBody
    public List<TrainSearchVO> getPopularRoutes() {
        try {
            return transportService.getPopularRoutes();
        } catch (Exception e) {
            throw new RuntimeException("인기 노선 조회 실패", e);
        }
    }

    /**
     * 검색 통계 API
     */
    @GetMapping("/api/stats")
    @ResponseBody
    public Object getSearchStats() {
        try {
            return java.util.Map.of(
                "totalSearches", transportService.getTotalSearchCount(),
                "todayResults", transportService.getTodaySearchResults().size(),
                "popularRoutes", transportService.getPopularRoutes()
            );
        } catch (Exception e) {
            throw new RuntimeException("통계 조회 실패", e);
        }
    }

    /**
     * 지원 역 목록 조회 API
     */
    @GetMapping("/api/stations")
    @ResponseBody
    public List<String> getStations() {
        return transportService.getSupportedStations();
    }
    
    /**
     * 서버 상태 확인
     */
    @GetMapping("/api/status")
    @ResponseBody
    public Object getServerStatus() {
        return java.util.Map.of(
            "status", "running",
            "timestamp", new java.util.Date().toString(),
            "server", "Spring Transport API"
        );
    }

    /**
     * 간단한 테스트 API
     */
    @GetMapping("/api/test")
    @ResponseBody
    public Object testApi() {
        try {
            return java.util.Map.of(
                "success", true,
                "message", "Spring API 정상 작동",
                "pythonServerTest", "준비 중",
                "timestamp", new java.util.Date().toString()
            );
        } catch (Exception e) {
            return java.util.Map.of(
                "success", false,
                "error", e.getMessage()
            );
        }
    }
}