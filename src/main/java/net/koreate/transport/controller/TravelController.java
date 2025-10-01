package net.koreate.transport.controller;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

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
import net.koreate.transport.service.TransportService;
import net.koreate.transport.service.BusService;
import net.koreate.transport.vo.FlightSearchVO;
import net.koreate.transport.vo.TrainSearchVO;
import net.koreate.transport.vo.BusSearchVO;

/**
 * 통합 여행 검색 컨트롤러
 * 국내여행: 항공편+기차+버스 통합 검색
 * 해외여행: 목적지 기반 항공편 검색
 */
@Controller
@RequestMapping("/travel")
public class TravelController {
    
    private static final Logger logger = LoggerFactory.getLogger(TravelController.class);
    
    @Autowired
    private FlightService flightService;
    
    @Autowired 
    private TransportService transportService;
    
    @Autowired
    private BusService busService;
    
    private List<String> getCityAirportCodes(String city) {
        List<String> airports = new ArrayList<>();
        
        switch (city) {
            case "서울":
                airports.add("ICN");
                airports.add("GMP");
                break;
            case "부산":
                airports.add("PUS");
                break;
            case "제주":
                airports.add("CJU");
                break;
            case "대구":
                airports.add("TAE");
                break;
            case "광주":
                airports.add("KWJ");
                break;
            // 항공편이 없는 도시들도 빈 리스트 반환 (오류 방지)
            default:
                // 빈 리스트 반환 - 오류 없이 건너뛰기
                break;
        }
        
        return airports;
    }
    
 // 확장된 도시-터미널 매핑 (여러 터미널 지원)
    private List<String> getCityTerminalNames(String city) {
        List<String> terminals = new ArrayList<>();
        
        switch (city) {
            case "서울":
                terminals.add("서울고속버스터미널");
                terminals.add("동서울터미널");
                terminals.add("서울남부터미널");
                break;
            case "부산":
                terminals.add("부산서부터미널");
                terminals.add("부산종합버스터미널");
                terminals.add("부산동부터미널");
                break;
            case "대전":
                terminals.add("대전복합터미널");
                terminals.add("대전서부터미널");
                break;
            case "대구":
                terminals.add("대구동부터미널");
                terminals.add("대구북부터미널");
                terminals.add("대구서부터미널");
                break;
            case "광주":
                terminals.add("광주종합버스터미널");
                terminals.add("광주U-Square터미널");
                break;
            case "울산":
                terminals.add("울산시외버스터미널");
                terminals.add("울산고속버스터미널");
                break;
            case "전주":
                terminals.add("전주시외버스터미널");
                break;
            case "천안":
                terminals.add("천안종합버스터미널");
                break;
            case "수원":
                terminals.add("수원종합버스터미널");
                break;
            // 기타 도시들도 추가
            default:
                // 매핑되지 않은 도시는 빈 리스트 반환
                break;
        }
        
        return terminals;
    }
    
    /**
     * 국내여행 검색 폼
     */
    @GetMapping("/domestic")
    public String domesticTravelForm(Model model) {
        logger.info("국내여행 검색 폼 페이지 요청");
        
        try {
            // 국내 주요 도시 목록
            Map<String, Object> domesticData = getDomesticCitiesAndAirports();
            model.addAttribute("cities", domesticData.get("cities"));
            model.addAttribute("airports", domesticData.get("airports"));
            model.addAttribute("pageTitle", "국내여행 통합 검색");
            
            return "transport/travel/domestic";
            
        } catch (Exception e) {
            logger.error("국내여행 폼 로딩 중 오류: ", e);
            model.addAttribute("errorMessage", "페이지 로딩 중 오류가 발생했습니다: " + e.getMessage());
            return "error/error";
        }
    }
    
    /**
     * 해외여행 검색 폼  
     */
    @GetMapping("/international")
    public String internationalTravelForm(Model model) {
        logger.info("해외여행 검색 폼 페이지 요청");
        
        try {
            // 해외 인기 목적지 및 공항 목록
            Map<String, Object> internationalData = getInternationalDestinations();
            model.addAttribute("destinations", internationalData.get("destinations"));
            model.addAttribute("airports", internationalData.get("airports"));
            model.addAttribute("pageTitle", "해외여행 항공편 검색");
            
            return "transport/travel/international";
            
        } catch (Exception e) {
            logger.error("해외여행 폼 로딩 중 오류: ", e);
            model.addAttribute("errorMessage", "페이지 로딩 중 오류가 발생했습니다: " + e.getMessage());
            return "error/error";
        }
    }
    
    /**
     * 국내여행 통합 검색 (항공편 + 기차 + 버스)
     */
    @PostMapping("/domestic/search") 
    public String searchDomesticTravel(
            @RequestParam("departure") String departure,
            @RequestParam("arrival") String arrival,
            @RequestParam("departureDate") String departureDate,
            @RequestParam(value = "adults", defaultValue = "1") int adults,
            Model model,
            RedirectAttributes redirectAttributes) {

        logger.info("국내여행 통합 검색: {} -> {}, {}, {}명", departure, arrival, departureDate, adults);
        
        try {
            // 입력값 검증 (기존과 동일)
            if (departure == null || departure.trim().isEmpty() ||
                arrival == null || arrival.trim().isEmpty() ||
                departureDate == null || departureDate.trim().isEmpty()) {
                
                redirectAttributes.addFlashAttribute("errorMessage", "출발지, 도착지, 날짜를 모두 입력해주세요.");
                return "redirect:/travel/domestic";
            }
            
            if (departure.equals(arrival)) {
                redirectAttributes.addFlashAttribute("errorMessage", "출발지와 도착지가 같을 수 없습니다.");
                return "redirect:/travel/domestic";
            }
            
            // 검색 파라미터
            Map<String, Object> searchParams = new HashMap<>();
            searchParams.put("departure", departure);
            searchParams.put("arrival", arrival);
            searchParams.put("departureDate", departureDate);
            searchParams.put("adults", adults);
            
            // 통합 검색 결과를 담을 객체
            Map<String, Object> searchResults = new HashMap<>();
            List<FlightSearchVO> allFlights = new ArrayList<>();
            List<TrainSearchVO> allTrains = new ArrayList<>();
            List<BusSearchVO> allBuses = new ArrayList<>();
            
            // 1. 항공편 검색 - 확장된 매핑
            try {
                List<String> departureAirports = getCityAirportCodes(departure);
                List<String> arrivalAirports = getCityAirportCodes(arrival);
                
                for (String depAirport : departureAirports) {
                    for (String arrAirport : arrivalAirports) {
                        try {
                            List<FlightSearchVO> flights = flightService.searchFlights(
                                depAirport, arrAirport, departureDate, adults);
                            if (flights != null && !flights.isEmpty()) {
                                allFlights.addAll(flights);
                            }
                        } catch (Exception e) {
                            logger.debug("항공편 검색 실패: {} -> {}", depAirport, arrAirport);
                        }
                    }
                }
            } catch (Exception e) {
                logger.warn("항공편 검색 전체 실패: {}", e.getMessage());
            }
            
            // 2. 기차 검색 - 직접 도시명 사용
            try {
                List<TrainSearchVO> trains = transportService.searchTrains(departure, arrival, departureDate);
                if (trains != null) {
                    allTrains.addAll(trains);
                }
            } catch (Exception e) {
                logger.warn("기차 검색 실패: {}", e.getMessage());
            }
            
            // 3. 버스 검색 - 확장된 매핑
            try {
                List<String> departureTerminals = getCityTerminalNames(departure);
                List<String> arrivalTerminals = getCityTerminalNames(arrival);
                String formattedDate = departureDate.replace("-", "");
                
                for (String depTerminal : departureTerminals) {
                    for (String arrTerminal : arrivalTerminals) {
                        try {
                            List<BusSearchVO> buses = busService.searchBuses(depTerminal, arrTerminal, formattedDate);
                            if (buses != null && !buses.isEmpty()) {
                                allBuses.addAll(buses);
                            }
                        } catch (Exception e) {
                            logger.debug("버스 검색 실패: {} -> {}", depTerminal, arrTerminal);
                        }
                    }
                }
            } catch (Exception e) {
                logger.warn("버스 검색 전체 실패: {}", e.getMessage());
            }
            
            // 결과 정리 및 정렬
            allFlights.sort((a, b) -> Integer.compare(a.getPrice(), b.getPrice()));
            
            // searchResults에 데이터 담기
            searchResults.put("flights", allFlights);
            searchResults.put("trains", allTrains);
            searchResults.put("buses", allBuses);
            searchResults.put("flightCount", allFlights.size());
            searchResults.put("trainCount", allTrains.size());
            searchResults.put("busCount", allBuses.size());
            
            int totalCount = allFlights.size() + allTrains.size() + allBuses.size();
            
            // 모델에 데이터 추가
            model.addAttribute("searchParams", searchParams);
            model.addAttribute("searchResults", searchResults);
            model.addAttribute("totalCount", totalCount);
            model.addAttribute("hasResults", totalCount > 0);
            model.addAttribute("pageTitle", "국내여행 검색 결과");
            
            logger.info("통합 검색 완료 - 항공편:{}건, 기차:{}건, 버스:{}건", 
                       allFlights.size(), allTrains.size(), allBuses.size());
            
            return "transport/travel/domesticResult";
            
        } catch (Exception e) {
            logger.error("국내여행 검색 중 오류: ", e);
            model.addAttribute("errorMessage", "검색 중 오류가 발생했습니다: " + e.getMessage());
            return "transport/travel/domesticResult";
        }
    }
    
    
    
    /**
     * 해외여행 항공편 검색 (목적지 기반)
     */
    @PostMapping("/international/search")
    public String searchInternationalTravel(
            @RequestParam("destination") String destination, // 목적지 공항코드 또는 도시명
            @RequestParam("departureDate") String departureDate,
            @RequestParam(value = "adults", defaultValue = "1") int adults,
            @RequestParam(value = "departureRegion", defaultValue = "all") String departureRegion, // 출발 지역 (서울/부산/전국 등)
            Model model,
            RedirectAttributes redirectAttributes) {
        
        logger.info("해외여행 검색: 목적지={}, 출발지역={}, {}, {}명", destination, departureRegion, departureDate, adults);
        
        try {
            // 입력값 검증
            if (destination == null || destination.trim().isEmpty() ||
                departureDate == null || departureDate.trim().isEmpty()) {
                
                redirectAttributes.addFlashAttribute("errorMessage", "목적지와 날짜를 입력해주세요.");
                return "redirect:/travel/international";
            }
            
            // 검색 파라미터
            Map<String, Object> searchParams = new HashMap<>();
            searchParams.put("destination", destination);
            searchParams.put("departureDate", departureDate);
            searchParams.put("adults", adults);
            searchParams.put("departureRegion", departureRegion);
            
            // 목적지 공항코드 리스트 (나고야면 NGO, 도쿄면 NRT,HND 등)
            List<String> destinationAirports = getDestinationAirports(destination);
            
            // 출발 공항 리스트 (지역별)
            List<String> departureAirports = getDepartureAirportsByRegion(departureRegion);
            
            // 모든 조합으로 항공편 검색
            List<FlightSearchVO> allFlights = new ArrayList<>();
            
            for (String depAirport : departureAirports) {
                for (String arrAirport : destinationAirports) {
                    try {
                        List<FlightSearchVO> flights = flightService.searchFlights(
                            depAirport, arrAirport, departureDate, adults);
                        if (flights != null && !flights.isEmpty()) {
                            allFlights.addAll(flights);
                        }
                    } catch (Exception e) {
                        logger.warn("항공편 검색 실패: {} -> {}, {}", depAirport, arrAirport, e.getMessage());
                    }
                }
            }
            
            // 가격순 정렬
            allFlights.sort((a, b) -> Integer.compare(a.getPrice(), b.getPrice()));
            
            model.addAttribute("searchParams", searchParams);
            model.addAttribute("flights", allFlights);
            model.addAttribute("resultCount", allFlights.size());
            model.addAttribute("hasResults", !allFlights.isEmpty());
            model.addAttribute("pageTitle", "해외여행 검색 결과");
            
            return "transport/travel/internationalResult";
            
        } catch (Exception e) {
            logger.error("해외여행 검색 중 오류: ", e);
            model.addAttribute("errorMessage", "검색 중 오류가 발생했습니다: " + e.getMessage());
            return "transport/travel/internationalResult";
        }
    }
    
    // ========== 헬퍼 메서드들 ==========
    
    /**
     * 국내 도시 및 공항 데이터
     */
    private Map<String, Object> getDomesticCitiesAndAirports() {
        Map<String, Object> data = new HashMap<>();
        
        // 국내 주요 도시
        List<Map<String, String>> cities = new ArrayList<>();
        cities.add(createCityData("서울", "ICN,GMP"));
        cities.add(createCityData("부산", "PUS"));
        cities.add(createCityData("제주", "CJU"));
        cities.add(createCityData("대구", "TAE"));
        cities.add(createCityData("광주", "KWJ"));
        cities.add(createCityData("울산", ""));
        cities.add(createCityData("대전", ""));
        cities.add(createCityData("전주", ""));
        cities.add(createCityData("강릉", ""));
        
        // 국내 공항
        List<Map<String, String>> airports = new ArrayList<>();
        airports.add(createAirportData("ICN", "인천국제공항", "서울"));
        airports.add(createAirportData("GMP", "김포국제공항", "서울"));
        airports.add(createAirportData("PUS", "김해국제공항", "부산"));
        airports.add(createAirportData("CJU", "제주국제공항", "제주"));
        airports.add(createAirportData("TAE", "대구국제공항", "대구"));
        airports.add(createAirportData("KWJ", "광주공항", "광주"));
        
        data.put("cities", cities);
        data.put("airports", airports);
        return data;
    }
    
    /**
     * 해외 목적지 데이터
     */
    private Map<String, Object> getInternationalDestinations() {
        Map<String, Object> data = new HashMap<>();
        
        // 해외 인기 목적지
        List<Map<String, String>> destinations = new ArrayList<>();
        
        // 일본
        destinations.add(createDestinationData("도쿄", "일본", "NRT,HND"));
        destinations.add(createDestinationData("오사카", "일본", "KIX,ITM"));
        destinations.add(createDestinationData("나고야", "일본", "NGO"));
        destinations.add(createDestinationData("후쿠오카", "일본", "FUK"));
        destinations.add(createDestinationData("삿포로", "일본", "CTS"));
        
        // 중국
        destinations.add(createDestinationData("베이징", "중국", "PEK,PKX"));
        destinations.add(createDestinationData("상하이", "중국", "PVG,SHA"));
        destinations.add(createDestinationData("광저우", "중국", "CAN"));
        
        // 동남아시아
        destinations.add(createDestinationData("방콕", "태국", "BKK,DMK"));
        destinations.add(createDestinationData("싱가포르", "싱가포르", "SIN"));
        destinations.add(createDestinationData("쿠알라룸푸르", "말레이시아", "KUL"));
        destinations.add(createDestinationData("자카르타", "인도네시아", "CGK"));
        
        data.put("destinations", destinations);
        data.put("airports", getInternationalAirports());
        return data;
    }
    
    /**
     * 도시명으로 공항코드 찾기
     */
    private String getCityAirportCode(String city) {
        Map<String, String> cityAirportMap = new HashMap<>();
        cityAirportMap.put("서울", "ICN"); // 기본값으로 인천공항
        cityAirportMap.put("부산", "PUS");
        cityAirportMap.put("제주", "CJU");
        cityAirportMap.put("대구", "TAE");
        cityAirportMap.put("광주", "KWJ");
        
        return cityAirportMap.get(city);
    }
    
    /**
     * 도시명으로 터미널명 찾기
     */
    private String getCityTerminalName(String city) {
        Map<String, String> cityTerminalMap = new HashMap<>();
        cityTerminalMap.put("서울", "서울고속버스터미널");
        cityTerminalMap.put("부산", "부산서부터미널");
        cityTerminalMap.put("대전", "대전복합터미널");
        cityTerminalMap.put("대구", "대구동부터미널");
        cityTerminalMap.put("광주", "광주종합버스터미널");
        cityTerminalMap.put("울산", "울산시외버스터미널");
        cityTerminalMap.put("전주", "전주시외버스터미널");
        
        return cityTerminalMap.get(city);
    }
    
    /**
     * 목적지별 공항코드 리스트
     */
    private List<String> getDestinationAirports(String destination) {
        List<String> airports = new ArrayList<>();
        
        switch (destination) {
            case "도쿄": airports.add("NRT"); airports.add("HND"); break;
            case "오사카": airports.add("KIX"); airports.add("ITM"); break;
            case "나고야": airports.add("NGO"); break;
            case "후쿠오카": airports.add("FUK"); break;
            case "삿포로": airports.add("CTS"); break;
            case "베이징": airports.add("PEK"); airports.add("PKX"); break;
            case "상하이": airports.add("PVG"); airports.add("SHA"); break;
            case "광저우": airports.add("CAN"); break;
            case "방콕": airports.add("BKK"); airports.add("DMK"); break;
            case "싱가포르": airports.add("SIN"); break;
            case "쿠알라룸푸르": airports.add("KUL"); break;
            case "자카르타": airports.add("CGK"); break;
            default: 
                // 공항코드로 직접 입력된 경우
                airports.add(destination);
        }
        
        return airports;
    }
    
    /**
     * 지역별 출발 공항 리스트
     */
    private List<String> getDepartureAirportsByRegion(String region) {
        List<String> airports = new ArrayList<>();
        
        switch (region) {
            case "seoul": airports.add("ICN"); airports.add("GMP"); break;
            case "busan": airports.add("PUS"); break;
            case "jeju": airports.add("CJU"); break;
            case "all":
            default:
                airports.add("ICN");
                airports.add("GMP"); 
                airports.add("PUS");
                airports.add("CJU");
                airports.add("TAE");
                airports.add("KWJ");
        }
        
        return airports;
    }
    
    // 데이터 생성 헬퍼 메서드들
    private Map<String, String> createCityData(String name, String airports) {
        Map<String, String> city = new HashMap<>();
        city.put("name", name);
        city.put("airports", airports);
        return city;
    }
    
    private Map<String, String> createAirportData(String code, String name, String city) {
        Map<String, String> airport = new HashMap<>();
        airport.put("code", code);
        airport.put("name", name);
        airport.put("city", city);
        return airport;
    }
    
    private Map<String, String> createDestinationData(String name, String country, String airports) {
        Map<String, String> destination = new HashMap<>();
        destination.put("name", name);
        destination.put("country", country);
        destination.put("airports", airports);
        return destination;
    }
    
    private List<Map<String, String>> getInternationalAirports() {
        List<Map<String, String>> airports = new ArrayList<>();
        
        // 일본
        airports.add(createAirportData("NRT", "나리타국제공항", "도쿄"));
        airports.add(createAirportData("HND", "하네다공항", "도쿄"));
        airports.add(createAirportData("KIX", "간사이국제공항", "오사카"));
        airports.add(createAirportData("ITM", "이타미공항", "오사카"));
        airports.add(createAirportData("NGO", "주부국제공항", "나고야"));
        airports.add(createAirportData("FUK", "후쿠오카공항", "후쿠오카"));
        airports.add(createAirportData("CTS", "신치토세공항", "삿포로"));
        
        // 중국
        airports.add(createAirportData("PEK", "베이징수도국제공항", "베이징"));
        airports.add(createAirportData("PKX", "베이징다싱국제공항", "베이징"));
        airports.add(createAirportData("PVG", "상하이푸둥국제공항", "상하이"));
        airports.add(createAirportData("SHA", "상하이훙차오국제공항", "상하이"));
        airports.add(createAirportData("CAN", "광저우바이윈국제공항", "광저우"));
        
        // 동남아시아
        airports.add(createAirportData("BKK", "수완나품국제공항", "방콕"));
        airports.add(createAirportData("DMK", "돈무앙국제공항", "방콕"));
        airports.add(createAirportData("SIN", "창이공항", "싱가포르"));
        airports.add(createAirportData("KUL", "쿠알라룸푸르국제공항", "쿠알라룸푸르"));
        airports.add(createAirportData("CGK", "수카르노하타국제공항", "자카르타"));
        
        return airports;
    }
}