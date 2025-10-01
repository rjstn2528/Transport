package net.koreate.transport.controller;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import net.koreate.transport.service.FlightService;
import net.koreate.transport.vo.FlightSearchVO;

/**
 * 항공편 조회 컨트롤러
 * Flask API와 연동하여 AMADEUS API 기반 항공편 정보 제공
 * JDK 11 호환 버전
 */
@Controller
@RequestMapping("/transport/flight")
public class FlightController {
    
    private static final Logger logger = LoggerFactory.getLogger(FlightController.class);
    
    @Autowired
    private FlightService flightService;
    
    // 생성자 추가
    public FlightController() {
        System.out.println("=== FlightController 생성됨 ===");
        logger.info("FlightController 초기화 완료");
    }
    
    /**
     * 항공편 검색 폼 페이지
     */
    @GetMapping("")
    public String flightSearchForm(Model model) {
        logger.info("항공편 검색 폼 페이지 요청");
        
        try {
            // 지원 공항 목록 조회
            model.addAttribute("airports", flightService.getSupportedAirports());
            model.addAttribute("pageTitle", "항공편 조회");
            
            // 수정된 경로: transport/flight/search
            return "transport/flight/search";
            
        } catch (Exception e) {
            logger.error("항공편 검색 폼 로딩 중 오류: ", e);
            model.addAttribute("errorMessage", "페이지 로딩 중 오류가 발생했습니다: " + e.getMessage());
            return "error/error";
        }
    }
    
    /**
     * 항공편 검색 결과 처리
     */
    @PostMapping("/search")
    public String searchFlights(
            @RequestParam("departureAirport") String departureAirport,
            @RequestParam("arrivalAirport") String arrivalAirport, 
            @RequestParam("departureDate") String departureDate,
            @RequestParam(value = "adults", defaultValue = "1") int adults,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        logger.info("항공편 검색 요청: {} -> {}, 날짜: {}, 승객: {}명", 
                   departureAirport, arrivalAirport, departureDate, adults);
        
        try {
            // 입력값 검증
            if (departureAirport == null || departureAirport.trim().isEmpty() ||
                arrivalAirport == null || arrivalAirport.trim().isEmpty() ||
                departureDate == null || departureDate.trim().isEmpty()) {
                
                redirectAttributes.addFlashAttribute("errorMessage", "출발공항, 도착공항, 날짜를 모두 입력해주세요.");
                return "redirect:/transport/flight";
            }
            
            if (departureAirport.equals(arrivalAirport)) {
                redirectAttributes.addFlashAttribute("errorMessage", "출발공항과 도착공항이 같을 수 없습니다.");
                return "redirect:/transport/flight";
            }
            
            if (adults < 1 || adults > 9) {
                redirectAttributes.addFlashAttribute("errorMessage", "승객 수는 1명에서 9명 사이여야 합니다.");
                return "redirect:/transport/flight";
            }
            
            // 항공편 검색 실행
            List<FlightSearchVO> flights = flightService.searchFlights(
                departureAirport.trim(), 
                arrivalAirport.trim(), 
                departureDate.trim(),
                adults
            );
            
            // 검색 파라미터를 Map으로 생성 (FlightSearchVO 생성자 문제 회피)
            Map<String, Object> searchParams = new HashMap<>();
            searchParams.put("departureAirport", departureAirport);
            searchParams.put("arrivalAirport", arrivalAirport);
            searchParams.put("departureDate", departureDate);
            searchParams.put("adults", adults);
            
            // 결과 모델에 추가
            model.addAttribute("flights", flights);
            model.addAttribute("searchParams", searchParams);
            model.addAttribute("resultCount", flights != null ? flights.size() : 0);
            model.addAttribute("pageTitle", "항공편 검색 결과");
            
            // 검색 결과에 따른 메시지
            if (flights == null || flights.isEmpty()) {
                model.addAttribute("noResults", true);
                model.addAttribute("message", "검색 조건에 맞는 항공편이 없습니다.");
            } else {
                model.addAttribute("hasResults", true);
                logger.info("항공편 검색 완료: {}건의 결과", flights.size());
            }
            
            // 수정된 경로: transport/flight/result
            return "transport/flight/result";
            
        } catch (Exception e) {
            logger.error("항공편 검색 중 오류 발생: ", e);
            model.addAttribute("errorMessage", "항공편 검색 중 오류가 발생했습니다: " + e.getMessage());
            
            // 에러 시에도 검색 파라미터 추가
            Map<String, Object> searchParams = new HashMap<>();
            searchParams.put("departureAirport", departureAirport);
            searchParams.put("arrivalAirport", arrivalAirport);
            searchParams.put("departureDate", departureDate);
            searchParams.put("adults", adults);
            model.addAttribute("searchParams", searchParams);
            
            // 수정된 경로: transport/flight/result
            return "transport/flight/result";
        }
    }
    
    /**
     * 공항 목록 조회 (AJAX용)
     */
    @GetMapping("/airports")
    public String getAirports(Model model) {
        try {
            model.addAttribute("airports", flightService.getSupportedAirports());
            // 수정된 경로: transport/flight/airports
            return "transport/flight/airports :: airportList";
        } catch (Exception e) {
            logger.error("공항 목록 조회 오류: ", e);
            model.addAttribute("errorMessage", e.getMessage());
            return "error/error";
        }
    }
}