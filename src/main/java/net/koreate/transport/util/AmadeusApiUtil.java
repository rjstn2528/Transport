package net.koreate.transport.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import net.koreate.transport.vo.FlightSearchVO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * AMADEUS API 연동 유틸리티 클래스
 * Flask 서버를 통해 AMADEUS API와 통신
 * JDK 11 호환 버전
 */
@Component
public class AmadeusApiUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(AmadeusApiUtil.class);
    
    @Value("${python.crawler.url:http://localhost:8000}")
    private String pythonServerUrl;
    
    @Value("${python.crawler.timeout:15000}")
    private int timeoutMs;
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    public AmadeusApiUtil(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * 항공편 검색
     */
    public List<FlightSearchVO> searchFlights(String departureAirport, String arrivalAirport, 
                                            String departureDate, int adults) throws Exception {
        
        logger.info("Flask API를 통한 항공편 검색: {} -> {}, {}, {}명", 
                   departureAirport, arrivalAirport, departureDate, adults);
        
        try {
            // URL 구성
            String url = UriComponentsBuilder.fromHttpUrl(pythonServerUrl + "/search_flights")
                    .queryParam("departure_airport", departureAirport)
                    .queryParam("arrival_airport", arrivalAirport)
                    .queryParam("departure_date", departureDate)
                    .queryParam("adults", adults)
                    .encode()
                    .toUriString();
            
            logger.debug("항공편 검색 요청 URL: {}", url);
            
            // HTTP 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("Accept", "application/json;charset=UTF-8");
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            // API 호출
            ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, String.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                return parseFlightResponse(response.getBody(), departureDate, adults);
            } else {
                logger.error("Flask API 응답 오류: HTTP {}", response.getStatusCode());
                throw new Exception("Flask API 호출 실패: " + response.getStatusCode());
            }
            
        } catch (Exception e) {
            logger.error("항공편 검색 API 호출 오류: ", e);
            throw new Exception("항공편 검색 중 오류 발생: " + e.getMessage());
        }
    }
    
    /**
     * Flask API 응답을 FlightSearchVO 목록으로 변환
     */
    private List<FlightSearchVO> parseFlightResponse(String responseBody, String searchDate, int adults) throws Exception {
        List<FlightSearchVO> flights = new ArrayList<>();
        
        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);
            
            if (!rootNode.get("success").asBoolean()) {
                String errorMsg = rootNode.has("error") ? rootNode.get("error").asText() : "알 수 없는 오류";
                logger.warn("Flask API 오류 응답: {}", errorMsg);
                
                // 오류 시에도 샘플 데이터가 있다면 사용
                if (rootNode.has("flights")) {
                    logger.info("오류 상황에서 샘플 데이터 사용");
                } else {
                    throw new Exception("항공편 검색 실패: " + errorMsg);
                }
            }
            
            // 항공편 데이터 파싱
            JsonNode flightsNode = rootNode.get("data");
            if (flightsNode == null) {
                flightsNode = rootNode.get("flights"); // 백업 필드
            }
            
            if (flightsNode != null && flightsNode.isArray()) {
                for (JsonNode flightNode : flightsNode) {
                    try {
                        FlightSearchVO flight = parseFlightNode(flightNode, searchDate, adults);
                        if (flight != null) {
                            flights.add(flight);
                        }
                    } catch (Exception e) {
                        logger.warn("항공편 데이터 파싱 오류: {}", e.getMessage());
                    }
                }
            }
            
            logger.info("항공편 파싱 완료: {}건", flights.size());
            return flights;
            
        } catch (Exception e) {
            logger.error("항공편 응답 파싱 오류: ", e);
            throw new Exception("응답 데이터 파싱 실패: " + e.getMessage());
        }
    }
    
    /**
     * 개별 항공편 노드를 FlightSearchVO로 변환
     */
    private FlightSearchVO parseFlightNode(JsonNode flightNode, String searchDate, int adults) {
        try {
            FlightSearchVO flight = new FlightSearchVO();
            
            // 필수 필드 설정
            flight.setAirlineCode(getStringValue(flightNode, "airline_code"));
            flight.setAirlineName(getStringValue(flightNode, "airline_name"));
            flight.setFlightNumber(getStringValue(flightNode, "flight_number"));
            flight.setDepartureAirport(getStringValue(flightNode, "departure_airport"));
            flight.setArrivalAirport(getStringValue(flightNode, "arrival_airport"));
            flight.setDepartureTime(getStringValue(flightNode, "departure_time"));
            flight.setArrivalTime(getStringValue(flightNode, "arrival_time"));
            flight.setDuration(getStringValue(flightNode, "duration"));
            
            // 가격 정보 (정수형으로 변환)
            if (flightNode.has("price")) {
                JsonNode priceNode = flightNode.get("price");
                if (priceNode.isNumber()) {
                    flight.setPrice(priceNode.asInt());
                } else if (priceNode.isTextual()) {
                    // "450000" 또는 "450,000원" 형태의 문자열 처리
                    String priceStr = priceNode.asText().replaceAll("[^0-9]", "");
                    if (!priceStr.isEmpty()) {
                        flight.setPrice(Integer.parseInt(priceStr));
                    }
                }
            }
            
            // 기타 정보
            flight.setCurrency(getStringValue(flightNode, "currency", "KRW"));
            flight.setSeatClass(getStringValue(flightNode, "seat_class", "ECONOMY"));
            flight.setRemainingSeats(getStringValue(flightNode, "remaining_seats", "예약가능"));
            flight.setSearchDate(searchDate);
            flight.setAdults(adults);
            
            return flight;
            
        } catch (Exception e) {
            logger.error("항공편 노드 파싱 오류: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * JsonNode에서 문자열 값을 안전하게 추출
     */
    private String getStringValue(JsonNode node, String fieldName) {
        return getStringValue(node, fieldName, null);
    }
    
    private String getStringValue(JsonNode node, String fieldName, String defaultValue) {
        if (node.has(fieldName) && !node.get(fieldName).isNull()) {
            return node.get(fieldName).asText();
        }
        return defaultValue;
    }
    
    /**
     * 지원하는 공항 목록 조회
     */
    public Map<String, Object> getSupportedAirports() throws Exception {
        try {
            String url = pythonServerUrl + "/airports";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, String.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                JsonNode rootNode = objectMapper.readTree(response.getBody());
                
                if (rootNode.get("success").asBoolean()) {
                    JsonNode airportsNode = rootNode.get("airports");
                    return objectMapper.convertValue(airportsNode, Map.class);
                } else {
                    throw new Exception("공항 목록 조회 실패");
                }
            } else {
                throw new Exception("Flask API 호출 실패: " + response.getStatusCode());
            }
            
        } catch (Exception e) {
            logger.error("공항 목록 조회 오류: ", e);
            
            // 기본 공항 목록 반환
            return getDefaultAirports();
        }
    }
    
    /**
     * 기본 공항 목록 (API 실패 시 사용)
     */
    private Map<String, Object> getDefaultAirports() {
        Map<String, Object> airports = new HashMap<>();
        
        List<Map<String, String>> domestic = new ArrayList<>();
        domestic.add(createAirportMap("ICN", "인천국제공항", "서울"));
        domestic.add(createAirportMap("GMP", "김포국제공항", "서울"));
        domestic.add(createAirportMap("PUS", "김해국제공항", "부산"));
        domestic.add(createAirportMap("CJU", "제주국제공항", "제주"));
        
        List<Map<String, String>> japan = new ArrayList<>();
        japan.add(createAirportMap("NRT", "나리타국제공항", "도쿄"));
        japan.add(createAirportMap("HND", "하네다공항", "도쿄"));
        japan.add(createAirportMap("KIX", "간사이국제공항", "오사카"));
        
        List<Map<String, String>> china = new ArrayList<>();
        china.add(createAirportMap("PEK", "베이징수도국제공항", "베이징"));
        china.add(createAirportMap("PVG", "상하이푸둥국제공항", "상하이"));
        
        List<Map<String, String>> southeast = new ArrayList<>();
        southeast.add(createAirportMap("BKK", "수완나품국제공항", "방콕"));
        southeast.add(createAirportMap("SIN", "창이공항", "싱가포르"));
        
        airports.put("국내공항", domestic);
        airports.put("일본", japan);
        airports.put("중국", china);
        airports.put("동남아시아", southeast);
        
        return airports;
    }
    
    private Map<String, String> createAirportMap(String code, String name, String city) {
        Map<String, String> airport = new HashMap<>();
        airport.put("code", code);
        airport.put("name", name);
        airport.put("city", city);
        return airport;
    }
    
    /**
     * Flask API 서버 상태 확인
     */
    public boolean checkApiServerStatus() {
        try {
            String url = pythonServerUrl + "/health";
            
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                JsonNode rootNode = objectMapper.readTree(response.getBody());
                return "healthy".equals(rootNode.get("status").asText());
            }
            
            return false;
            
        } catch (Exception e) {
            logger.debug("Flask API 서버 상태 확인 실패: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * AMADEUS API 상태 확인
     */
    public boolean checkAmadeusApiStatus() {
        try {
            String url = pythonServerUrl + "/amadeus/status";
            
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                JsonNode rootNode = objectMapper.readTree(response.getBody());
                JsonNode amadeusNode = rootNode.get("amadeus_api");
                
                return amadeusNode.get("api_configured").asBoolean() && 
                       amadeusNode.get("token_valid").asBoolean();
            }
            
            return false;
            
        } catch (Exception e) {
            logger.debug("AMADEUS API 상태 확인 실패: {}", e.getMessage());
            return false;
        }
    }
}