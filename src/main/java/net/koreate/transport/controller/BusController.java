package net.koreate.transport.controller;

import net.koreate.transport.service.BusService;
import net.koreate.transport.vo.BusSearchVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/transport/bus")
public class BusController {

    @Autowired
    private BusService busService;

    /**
     * 버스 조회 메인 페이지
     */
    @GetMapping("/")
    public String busMainPage() {
        return "redirect:/transport/bus";
    }

    /**
     * 버스 조회 페이지
     */
    @GetMapping("")
    public String busSearchForm(Model model) {
        model.addAttribute("title", "고속/시외버스 조회");
        model.addAttribute("terminals", busService.getSupportedBusTerminals());
        return "transport/bus/search";
    }

    /**
     * 버스 조회 결과
     */
    @PostMapping("/search")
    public String searchBuses(HttpServletRequest request, Model model) {
        try {
            String departureTerminal = request.getParameter("departureTerminal");
            String arrivalTerminal = request.getParameter("arrivalTerminal");
            String searchDate = request.getParameter("searchDate");

            // 파라미터 검증
            if (departureTerminal == null || arrivalTerminal == null || searchDate == null) {
                model.addAttribute("error", "모든 필드를 입력해주세요.");
                model.addAttribute("terminals", busService.getSupportedBusTerminals());
                return "transport/bus/search";
            }

            if (departureTerminal.equals(arrivalTerminal)) {
                model.addAttribute("error", "출발터미널과 도착터미널이 같을 수 없습니다.");
                model.addAttribute("terminals", busService.getSupportedBusTerminals());
                return "transport/bus/search";
            }

            // 날짜 형식 변환 (YYYY-MM-DD -> YYYYMMDD)
            String formattedDate = searchDate.replace("-", "");

            // 서비스 호출
            List<BusSearchVO> busList = busService.searchBuses(departureTerminal, arrivalTerminal, formattedDate);

            // 모델에 데이터 추가
            model.addAttribute("busList", busList);
            model.addAttribute("departureTerminal", departureTerminal);
            model.addAttribute("arrivalTerminal", arrivalTerminal);
            model.addAttribute("searchDate", searchDate);
            model.addAttribute("resultCount", busList.size());

            return "transport/bus/result";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "조회 중 오류가 발생했습니다: " + e.getMessage());
            model.addAttribute("terminals", busService.getSupportedBusTerminals());
            return "transport/bus/search";
        }
    }

    /**
     * AJAX용 버스 조회 API
     */
    @PostMapping("/api/search")
    @ResponseBody
    public List<BusSearchVO> searchBusesApi(@RequestParam String departureTerminal,
                                            @RequestParam String arrivalTerminal,
                                            @RequestParam String searchDate) {
        try {
            String formattedDate = searchDate.replace("-", "");
            return busService.searchBuses(departureTerminal, arrivalTerminal, formattedDate);
        } catch (Exception e) {
            throw new RuntimeException("버스 조회 실패", e);
        }
    }

    /**
     * 지원 버스터미널 목록 조회 API
     */
    @GetMapping("/api/terminals")
    @ResponseBody
    public List<String> getBusTerminals() {
        return busService.getSupportedBusTerminals();
    }

    /**
     * 버스 노선별 통계 API
     */
    @GetMapping("/api/stats")
    @ResponseBody
    public Object getBusStats() {
        try {
            return java.util.Map.of(
                "totalBusSearches", busService.getTotalBusSearchCount(),
                "todayBusResults", busService.getTodayBusSearchResults().size(),
                "popularBusRoutes", busService.getPopularBusRoutes(),
                "busTypeStats", busService.getBusTypeStatistics()
            );
        } catch (Exception e) {
            throw new RuntimeException("버스 통계 조회 실패", e);
        }
    }

    /**
     * 인기 버스 노선 페이지
     */
    @GetMapping("/popular")
    public String popularBusRoutes(Model model) {
        try {
            List<BusSearchVO> popularRoutes = busService.getPopularBusRoutes();
            model.addAttribute("popularRoutes", popularRoutes);
            model.addAttribute("title", "인기 버스 노선");
            return "transport/bus/popular";
        } catch (Exception e) {
            model.addAttribute("error", "인기 노선 조회 중 오류가 발생했습니다.");
            return "transport/bus/search";
        }
    }

    /**
     * 버스 예약 가이드 페이지
     */
    @GetMapping("/guide")
    public String busGuide(Model model) {
        model.addAttribute("title", "버스 예약 가이드");
        return "transport/bus/guide";
    }

    /**
     * 터미널 정보 상세 페이지
     */
    @GetMapping("/terminal/{terminalName}")
    public String terminalInfo(@PathVariable String terminalName, Model model) {
        try {
            // 터미널 상세 정보 조회 로직
            model.addAttribute("terminalName", terminalName);
            model.addAttribute("title", terminalName + " 상세 정보");
            return "transport/bus/terminal-info";
        } catch (Exception e) {
            return "redirect:/transport/bus";
        }
    }

    /**
     * 실시간 버스 현황 API
     */
    @GetMapping("/api/realtime")
    @ResponseBody
    public Object getRealtimeBusStatus() {
        try {
            return java.util.Map.of(
                "timestamp", new java.util.Date(),
                "activeRoutes", busService.getActiveBusRoutes(),
                "systemStatus", "operational"
            );
        } catch (Exception e) {
            return java.util.Map.of(
                "error", "실시간 정보 조회 실패",
                "timestamp", new java.util.Date()
            );
        }
    }

    /**
     * 버스 요금 계산 API
     */
    @PostMapping("/api/fare")
    @ResponseBody
    public Object calculateBusFare(@RequestParam String departureTerminal,
                                  @RequestParam String arrivalTerminal,
                                  @RequestParam String busGrade) {
        try {
            return busService.calculateBusFare(departureTerminal, arrivalTerminal, busGrade);
        } catch (Exception e) {
            return java.util.Map.of(
                "error", "요금 계산 실패",
                "message", e.getMessage()
            );
        }
    }

    /**
     * 서버 상태 확인
     */
    @GetMapping("/api/status")
    @ResponseBody
    public Object getBusServerStatus() {
        return java.util.Map.of(
            "status", "running",
            "service", "Bus Service",
            "timestamp", new java.util.Date().toString(),
            "supportedTerminals", busService.getSupportedBusTerminals().size()
        );
    }

    /**
     * 에러 처리 메서드
     */
    @ExceptionHandler(Exception.class)
    public String handleException(Exception e, Model model) {
        e.printStackTrace();
        model.addAttribute("error", "버스 서비스 오류: " + e.getMessage());
        model.addAttribute("terminals", busService.getSupportedBusTerminals());
        return "transport/bus/search";
    }
}