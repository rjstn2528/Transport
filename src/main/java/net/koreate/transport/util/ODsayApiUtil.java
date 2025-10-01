package net.koreate.transport.util;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.koreate.transport.vo.BusSearchVO;

@Component
public class ODsayApiUtil {

    @Value("${odsay.api.key}")
    private String apiKey;

    @Value("${odsay.api.base.url:https://api.odsay.com}")
    private String baseUrl;

    @Value("${odsay.api.timeout:15000}")
    private int timeout;

    @Value("${odsay.api.enabled:false}")
    private boolean enabled;

    private boolean apiEnabled = false;
    private RestTemplate restTemplate;
    private ObjectMapper objectMapper;
    
    // 터미널 ID 매핑 (ODsay API는 터미널 ID 기반)
    private Map<String, String> terminalIds;

    @PostConstruct
    public void init() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
        this.terminalIds = initTerminalIds();

        System.out.println("=== ODsay API 상세 설정 분석 ===");
        System.out.println("API 키 원본: '" + apiKey + "'");
        System.out.println("Base URL: '" + baseUrl + "'");
        System.out.println("Enabled 설정값: " + enabled);
        
        this.apiEnabled = isValidApiKey();
        System.out.println("최종 API 활성화 상태: " + apiEnabled);
        System.out.println("=== ODsay API 설정 분석 완료 ===");

        if (apiEnabled) {
            testConnection();
        }
    }

    /**
     * 터미널 ID 초기화 (ODsay API 터미널 조회 통해 실제 ID 획득)
     */
    private Map<String, String> initTerminalIds() {
        Map<String, String> terminalIds = new HashMap<>();
        
        // 실제 ODsay API를 통해 터미널 ID 조회
        if (apiEnabled) {
            // 고속버스 터미널들
            String[] expressTerminals = {
                "서울고속버스터미널", "동서울터미널", "서울남부터미널", "상봉터미널"
            };
            
            for (String terminalName : expressTerminals) {
                String terminalId = searchTerminalId(terminalName, 4); // 4: 고속버스터미널
                if (terminalId != null) {
                    terminalIds.put(terminalName, terminalId);
                    System.out.println("고속터미널 ID: " + terminalName + " → " + terminalId);
                }
            }
            
            // 시외버스 터미널들
            String[] intercityTerminals = {
                "부산서부터미널", "부산종합버스터미널", "부산동부터미널",
                "대전복합터미널", "대전서부터미널", "대구동부터미널", 
                "대구서부터미널", "광주종합버스터미널", "안양종합버스터미널"
            };
            
            for (String terminalName : intercityTerminals) {
                String terminalId = searchTerminalId(terminalName, 6); // 6: 시외버스터미널
                if (terminalId != null) {
                    terminalIds.put(terminalName, terminalId);
                    System.out.println("시외터미널 ID: " + terminalName + " → " + terminalId);
                }
            }
        }
        
        // API 실패 시 fallback ID들
        if (terminalIds.isEmpty()) {
            System.out.println("터미널 ID 조회 실패, 임시 ID 사용");
            terminalIds.put("서울고속버스터미널", "4000001");
            terminalIds.put("부산서부터미널", "6000001");
            // 필요시 더 추가
        }
        
        return terminalIds;
    }

    /**
     * 터미널 이름으로 실제 터미널 ID 조회
     */
    private String searchTerminalId(String terminalName, int stationClass) {
        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl + "/v1/api/searchStation")
                    .queryParam("apiKey", URLEncoder.encode(apiKey, "UTF-8"))
                    .queryParam("stationName", URLEncoder.encode(terminalName, "UTF-8"))
                    .queryParam("stationClass", stationClass);

            String apiUrl = builder.toUriString();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode rootNode = objectMapper.readTree(response.getBody());
                JsonNode resultNode = rootNode.get("result");
                
                if (resultNode != null && resultNode.has("station") && resultNode.get("station").isArray()) {
                    JsonNode stationArray = resultNode.get("station");
                    if (stationArray.size() > 0) {
                        JsonNode firstStation = stationArray.get(0);
                        if (firstStation.has("stationID")) {
                            return firstStation.get("stationID").asText();
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("터미널 ID 조회 실패 (" + terminalName + "): " + e.getMessage());
        }
        
        return null;
    }

    /**
     * API 키 유효성 검증
     */
    private boolean isValidApiKey() {
        if (!enabled || apiKey == null || apiKey.trim().isEmpty() || 
            "YOUR_API_KEY".equals(apiKey) || apiKey.length() <= 10 || 
            apiKey.contains("${")) {
            return false;
        }
        return true;
    }

    /**
     * 초기 연결 테스트
     */
    private void testConnection() {
        try {
            boolean isHealthy = checkApiStatus();
            System.out.println("ODsay API 연결 테스트: " + (isHealthy ? "성공" : "실패"));
        } catch (Exception e) {
            System.out.println("ODsay API 연결 테스트 실패: " + e.getMessage());
        }
    }

    /**
     * ODsay API로 고속/시외버스 조회 (올바른 엔드포인트 사용)
     */
    public List<BusSearchVO> searchBuses(String departureTerminal, String arrivalTerminal, String searchDate) {
        if (!apiEnabled) {
            System.out.println("ODsay API가 비활성화되어 있습니다.");
            return new ArrayList<>();
        }

        try {
            // 터미널 ID 가져오기
            String depTerminalId = terminalIds.get(departureTerminal);
            String arrTerminalId = terminalIds.get(arrivalTerminal);

            if (depTerminalId == null || arrTerminalId == null) {
                System.out.println("터미널 ID를 찾을 수 없습니다:");
                System.out.println("   출발: " + departureTerminal + " → " + depTerminalId);
                System.out.println("   도착: " + arrivalTerminal + " → " + arrTerminalId);
                return new ArrayList<>();
            }

            // 고속/시외버스 통합 검색 API 호출
            List<BusSearchVO> result = searchInterBusSchedule(
                depTerminalId, arrTerminalId, searchDate, departureTerminal, arrivalTerminal
            );

            if (!result.isEmpty()) {
                System.out.println("ODsay API 성공: 고속/시외버스 통합 검색 (결과: " + result.size() + "건)");
                return result;
            }

            // 개별 API 시도
            List<BusSearchVO> expressResult = searchExpressBus(depTerminalId, arrTerminalId, searchDate, departureTerminal, arrivalTerminal);
            if (!expressResult.isEmpty()) {
                result.addAll(expressResult);
            }

            List<BusSearchVO> intercityResult = searchIntercityBus(depTerminalId, arrTerminalId, searchDate, departureTerminal, arrivalTerminal);
            if (!intercityResult.isEmpty()) {
                result.addAll(intercityResult);
            }

            if (!result.isEmpty()) {
                System.out.println("ODsay API 성공: 개별 API 조합 (결과: " + result.size() + "건)");
                return result;
            }

            System.out.println("ODsay API에서 결과를 찾을 수 없습니다.");

        } catch (Exception e) {
            System.err.println("ODsay API 전체 오류: " + e.getMessage());
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    /**
     * 고속/시외버스 통합 검색 API (권장)
     */
    private List<BusSearchVO> searchInterBusSchedule(String startStationID, String endStationID, 
                                                   String searchDate, String departureTerminal, String arrivalTerminal) throws Exception {
        
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl + "/v1/api/searchInterBusSchedule")
                .queryParam("apiKey", URLEncoder.encode(apiKey, "UTF-8"))
                .queryParam("startStationID", startStationID)
                .queryParam("endStationID", endStationID);

        String apiUrl = builder.toUriString();
        System.out.println("API 호출: 고속/시외버스 통합 검색");
        System.out.println("URL: " + apiUrl.replace(apiKey, "***"));
        
        return callBusApi(apiUrl, searchDate, departureTerminal, arrivalTerminal, "통합");
    }

    /**
     * 고속버스 운행정보 검색
     */
    private List<BusSearchVO> searchExpressBus(String startStationID, String endStationID, 
                                             String searchDate, String departureTerminal, String arrivalTerminal) throws Exception {
        
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl + "/v1/api/expressServiceTime")
                .queryParam("apiKey", URLEncoder.encode(apiKey, "UTF-8"))
                .queryParam("startStationID", startStationID)
                .queryParam("endStationID", endStationID);

        String apiUrl = builder.toUriString();
        System.out.println("API 호출: 고속버스 검색");
        
        return callBusApi(apiUrl, searchDate, departureTerminal, arrivalTerminal, "고속버스");
    }

    /**
     * 시외버스 운행정보 검색
     */
    private List<BusSearchVO> searchIntercityBus(String startStationID, String endStationID, 
                                               String searchDate, String departureTerminal, String arrivalTerminal) throws Exception {
        
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl + "/v1/api/intercityServiceTime")
                .queryParam("apiKey", URLEncoder.encode(apiKey, "UTF-8"))
                .queryParam("startStationID", startStationID)
                .queryParam("endStationID", endStationID);

        String apiUrl = builder.toUriString();
        System.out.println("API 호출: 시외버스 검색");
        
        return callBusApi(apiUrl, searchDate, departureTerminal, arrivalTerminal, "시외버스");
    }

    /**
     * 공통 버스 API 호출 메서드
     */
    private List<BusSearchVO> callBusApi(String apiUrl, String searchDate, String departureTerminal, 
                                       String arrivalTerminal, String busType) throws Exception {
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                apiUrl, HttpMethod.GET, entity, String.class);

        System.out.println("응답 상태: " + response.getStatusCode());
        
        if (response.getStatusCode() == HttpStatus.OK) {
            String responseBody = response.getBody();
            return parseBusResponse(responseBody, searchDate, departureTerminal, arrivalTerminal, busType);
        } else {
            throw new Exception("HTTP " + response.getStatusCode() + ": " + response.getBody());
        }
    }

    /**
     * 버스 API 응답 파싱
     */
    private List<BusSearchVO> parseBusResponse(String responseBody, String searchDate,
                                             String departureTerminal, String arrivalTerminal, String busType) {
        List<BusSearchVO> buses = new ArrayList<>();

        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);
            
            // 에러 체크
            if (rootNode.has("error")) {
                JsonNode errorNode = rootNode.get("error");
                if (errorNode.isArray() && errorNode.size() > 0) {
                    String errorMsg = errorNode.get(0).get("message").asText();
                    System.out.println("ODsay API 에러: " + errorMsg);
                    return buses;
                }
            }
            
            JsonNode resultNode = rootNode.get("result");
            if (resultNode == null) {
                System.out.println("result 노드가 없습니다.");
                return buses;
            }

            // 통합 API 응답 구조
            if (resultNode.has("schedule")) {
                JsonNode scheduleArray = resultNode.get("schedule");
                if (scheduleArray.isArray()) {
                    for (JsonNode scheduleNode : scheduleArray) {
                        BusSearchVO bus = parseScheduleNode(scheduleNode, searchDate, departureTerminal, arrivalTerminal, busType);
                        if (bus != null) {
                            buses.add(bus);
                        }
                    }
                }
            }
            // 개별 API 응답 구조 (expressServiceTime, intercityServiceTime)
            else if (resultNode.has("station")) {
                JsonNode stationArray = resultNode.get("station");
                if (stationArray.isArray()) {
                    for (JsonNode stationNode : stationArray) {
                        List<BusSearchVO> stationBuses = parseStationNode(stationNode, searchDate, departureTerminal, arrivalTerminal, busType);
                        buses.addAll(stationBuses);
                    }
                }
            }

            System.out.println("ODsay API 파싱 완료: " + buses.size() + "건 (" + busType + ")");

        } catch (Exception e) {
            System.err.println("ODsay API 응답 파싱 오류: " + e.getMessage());
            e.printStackTrace();
        }

        return buses;
    }

    /**
     * 통합 API 스케줄 노드 파싱
     */
    private BusSearchVO parseScheduleNode(JsonNode scheduleNode, String searchDate,
                                        String departureTerminal, String arrivalTerminal, String busType) {
        try {
            BusSearchVO bus = new BusSearchVO();
            
            bus.setDepartureTerminal(departureTerminal);
            bus.setArrivalTerminal(arrivalTerminal);
            bus.setSearchDate(searchDate);
            bus.setCreatedDate(new Date());
            bus.setBusType(busType);
            
            // 출발시간
            bus.setDepartureTime(getTextValue(scheduleNode, "departureTime", "정보없음"));
            
            // 소요시간
            int wasteTime = scheduleNode.has("wasteTime") ? scheduleNode.get("wasteTime").asInt() : 0;
            bus.setDuration(wasteTime > 0 ? wasteTime + "분" : "정보없음");
            
            // 요금
            int fare = scheduleNode.has("fare") ? scheduleNode.get("fare").asInt() : 0;
            bus.setPrice(fare > 0 ? String.format("%,d원", fare) : "정보없음");
            
            // 버스 등급
            int busClass = scheduleNode.has("busClass") ? scheduleNode.get("busClass").asInt() : 1;
            switch (busClass) {
                case 1: bus.setBusGrade("일반"); break;
                case 2: bus.setBusGrade("우등"); break;
                case 3: bus.setBusGrade("프리미엄"); break;
                case 4: bus.setBusGrade("심야일반"); break;
                case 5: bus.setBusGrade("심야우등"); break;
                default: bus.setBusGrade("일반"); break;
            }
            
            bus.setBusCompany("정보없음");
            bus.setArrivalTime("정보없음");
            bus.setRemainingSeats("예약가능");
            
            return bus;
            
        } catch (Exception e) {
            System.err.println("스케줄 노드 파싱 오류: " + e.getMessage());
            return null;
        }
    }

    /**
     * 개별 API 스테이션 노드 파싱
     */
    private List<BusSearchVO> parseStationNode(JsonNode stationNode, String searchDate,
                                             String departureTerminal, String arrivalTerminal, String busType) {
        List<BusSearchVO> buses = new ArrayList<>();
        
        try {
            String wasteTime = getTextValue(stationNode, "wasteTime", "정보없음");
            String normalFare = getTextValue(stationNode, "normalFare", "0");
            String specialFare = getTextValue(stationNode, "specialFare", "0");
            String schedule = getTextValue(stationNode, "schedule", "");
            
            // 스케줄 파싱 (시간표가 있는 경우)
            if (!schedule.isEmpty() && !schedule.equals("정보없음")) {
                String[] times = schedule.split(",");
                for (String time : times) {
                    time = time.trim();
                    if (!time.isEmpty()) {
                        BusSearchVO bus = new BusSearchVO();
                        
                        bus.setDepartureTerminal(departureTerminal);
                        bus.setArrivalTerminal(arrivalTerminal);
                        bus.setSearchDate(searchDate);
                        bus.setCreatedDate(new Date());
                        bus.setBusType(busType);
                        bus.setDepartureTime(time);
                        bus.setDuration(wasteTime);
                        bus.setPrice(formatPrice(normalFare));
                        bus.setBusGrade("일반");
                        bus.setBusCompany("정보없음");
                        bus.setArrivalTime("정보없음");
                        bus.setRemainingSeats("예약가능");
                        
                        buses.add(bus);
                    }
                }
            } else {
                // 기본 버스 정보만 있는 경우
                BusSearchVO bus = new BusSearchVO();
                
                bus.setDepartureTerminal(departureTerminal);
                bus.setArrivalTerminal(arrivalTerminal);
                bus.setSearchDate(searchDate);
                bus.setCreatedDate(new Date());
                bus.setBusType(busType);
                bus.setDepartureTime("정보없음");
                bus.setDuration(wasteTime);
                bus.setPrice(formatPrice(normalFare));
                bus.setBusGrade("일반");
                bus.setBusCompany("정보없음");
                bus.setArrivalTime("정보없음");
                bus.setRemainingSeats("예약가능");
                
                buses.add(bus);
            }
            
        } catch (Exception e) {
            System.err.println("스테이션 노드 파싱 오류: " + e.getMessage());
        }
        
        return buses;
    }

    /**
     * JSON 노드에서 텍스트 값 안전하게 가져오기
     */
    private String getTextValue(JsonNode node, String fieldName, String defaultValue) {
        if (node != null && node.has(fieldName) && !node.get(fieldName).isNull()) {
            return node.get(fieldName).asText();
        }
        return defaultValue;
    }

    /**
     * 가격 포맷팅
     */
    private String formatPrice(String fareStr) {
        try {
            int fare = Integer.parseInt(fareStr);
            return fare > 0 ? String.format("%,d원", fare) : "정보없음";
        } catch (NumberFormatException e) {
            return fareStr.endsWith("원") ? fareStr : fareStr + "원";
        }
    }

    /**
     * API 상태 확인 (도시코드 조회로 테스트)
     */
    public boolean checkApiStatus() {
        if (!apiEnabled || !isValidApiKey()) {
            return false;
        }

        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl + "/v1/api/searchCID")
                    .queryParam("apiKey", URLEncoder.encode(apiKey, "UTF-8"));

            ResponseEntity<String> response = restTemplate.getForEntity(builder.toUriString(), String.class);
            return response.getStatusCode() == HttpStatus.OK;

        } catch (Exception e) {
            System.err.println("ODsay API 상태 확인 실패: " + e.getMessage());
            return false;
        }
    }

    /**
     * API 설정 정보 반환
     */
    public Map<String, Object> getApiInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("enabled", apiEnabled);
        info.put("hasApiKey", isValidApiKey());
        info.put("baseUrl", baseUrl);
        info.put("timeout", timeout);
        info.put("status", checkApiStatus() ? "healthy" : "unavailable");
        info.put("terminalCount", terminalIds.size());
        return info;
    }
}