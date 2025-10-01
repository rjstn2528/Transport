package net.koreate.transport.util;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.koreate.transport.vo.BusSearchVO;
import net.koreate.transport.vo.TrainSearchVO;

@Component
public class PythonCrawlerUtil {
    
    @Value("${python.crawler.url:http://localhost:8000}")
    private String pythonCrawlerUrl;
    
    @Value("${python.crawler.timeout:120000}")
    private int timeout;
    
    // 기본 RestTemplate 대신 크롤링 전용 사용
    @Autowired
    @Qualifier("crawlerRestTemplate")  // 크롤링 전용 RestTemplate 주입
    private RestTemplate restTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    // 또는 크롤링 전용 RestTemplate을 직접 생성
    private RestTemplate createCrawlerRestTemplate() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(30000);    // 30초
        factory.setReadTimeout(180000);      // 3분
        
        return new RestTemplate(factory);
    }
    
    // 기존 메서드들은 그대로 유지...

    
    /**
     * Python 크롤링 서버에서 기차 정보 조회 (한글 인코딩 개선)
     */
 // PythonCrawlerUtil.java의 searchTrains 메서드도 수정

    public List<TrainSearchVO> searchTrains(String departure, String arrival, String searchDate) throws Exception {
        try {
            // UriComponentsBuilder로 안전한 URL 생성
            String url = UriComponentsBuilder.fromHttpUrl(pythonCrawlerUrl + "/search_trains")
                    .queryParam("departure", departure)
                    .queryParam("arrival", arrival)
                    .queryParam("date", searchDate)
                    .encode(StandardCharsets.UTF_8)
                    .toUriString();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Accept", "application/json; charset=utf-8");
            headers.set("User-Agent", "Hee-Transport-System/1.0");
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            System.out.println("Python 서버 호출: " + url);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                String responseBody = response.getBody();
                System.out.println("응답 받음: " + (responseBody != null ? responseBody.length() : 0) + "자");
                return parseTrainResponse(responseBody);
            } else {
                throw new Exception("Python 서버 응답 오류: " + response.getStatusCode());
            }
            
        } catch (Exception e) {
            System.err.println("Python 크롤러 호출 오류: " + e.getMessage());
            e.printStackTrace();
            
            // 샘플 데이터 제거 - 빈 리스트 반환
            System.err.println("실제 기차 데이터를 가져올 수 없습니다. 빈 결과를 반환합니다.");
            return new ArrayList<>();
        }
    }

    // getSampleTrainData 메서드도 완전 삭제
    // private List<TrainSearchVO> getSampleTrainData(...) { ... } // 이 메서드도 삭제
    
    /**
     * JSON 응답을 TrainSearchVO 리스트로 변환 (개선된 파싱)
     */
    private List<TrainSearchVO> parseTrainResponse(String jsonResponse) throws Exception {
        List<TrainSearchVO> trainList = new ArrayList<>();
        
        if (jsonResponse == null || jsonResponse.trim().isEmpty()) {
            System.err.println("⚠️ 빈 응답 받음");
            return trainList;
        }
        
        try {
            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            
            // 응답 구조 확인
            if (!rootNode.has("success") || !rootNode.get("success").asBoolean()) {
                String error = rootNode.has("error") ? rootNode.get("error").asText() : "알 수 없는 오류";
                throw new Exception("Python 서버 오류: " + error);
            }
            
            JsonNode dataNode = rootNode.get("data");
            if (dataNode != null && dataNode.isArray()) {
                for (JsonNode trainNode : dataNode) {
                    TrainSearchVO train = new TrainSearchVO();
                    
                    // 필수 필드 검증 후 설정
                    train.setTrainType(getJsonText(trainNode, "train_type", "KTX"));
                    train.setTrainNumber(getJsonText(trainNode, "train_number", "001"));
                    train.setDepartureStation(getJsonText(trainNode, "departure_station", ""));
                    train.setArrivalStation(getJsonText(trainNode, "arrival_station", ""));
                    train.setDepartureTime(getJsonText(trainNode, "departure_time", ""));
                    train.setArrivalTime(getJsonText(trainNode, "arrival_time", ""));
                    train.setDuration(getJsonText(trainNode, "duration", ""));
                    train.setAvailability(getJsonText(trainNode, "availability", "예약가능"));
                    train.setSearchDate(getJsonText(trainNode, "date", ""));
                    
                    // 가격 정보 파싱
                    JsonNode priceNode = trainNode.get("price");
                    if (priceNode != null) {
                        train.setGeneralPrice(getJsonText(priceNode, "general", "미정"));
                        train.setSpecialPrice(getJsonText(priceNode, "special", "-"));
                    } else {
                        train.setGeneralPrice("미정");
                        train.setSpecialPrice("-");
                    }
                    
                    trainList.add(train);
                }
            }
            
            System.out.println("📊 파싱 완료: " + trainList.size() + "건");
            return trainList;
            
        } catch (Exception e) {
            System.err.println("❌ JSON 파싱 오류: " + e.getMessage());
            System.err.println("📄 응답 내용: " + jsonResponse.substring(0, Math.min(500, jsonResponse.length())));
            throw new Exception("응답 파싱 실패: " + e.getMessage());
        }
    }
    
    /**
     * Python Flask 서버에서 버스 정보 조회
     */
 // PythonCrawlerUtil.java의 searchBuses 메서드 수정

    public List<BusSearchVO> searchBuses(String departureTerminal, String arrivalTerminal, String searchDate) throws Exception {
        List<BusSearchVO> busList = new ArrayList<>();
        
        try {
            // Python 서버 URL 구성
            String apiUrl = UriComponentsBuilder.fromHttpUrl(pythonCrawlerUrl + "/search_buses")
                    .queryParam("departure_terminal", departureTerminal)
                    .queryParam("arrival_terminal", arrivalTerminal)
                    .queryParam("date", searchDate)
                    .encode(StandardCharsets.UTF_8)
                    .toUriString();
            
            System.out.println("Python 버스 서버 호출: " + apiUrl);
            
            // HTTP 요청 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAcceptCharset(Arrays.asList(StandardCharsets.UTF_8));
            headers.set("User-Agent", "Hee-Transport-System/1.0");
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            // API 호출
            ResponseEntity<String> response = restTemplate.exchange(
                    apiUrl, HttpMethod.GET, entity, String.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                String responseBody = response.getBody();
                System.out.println("Python 서버 응답 수신 완료");
                
                // JSON 파싱
                JsonNode rootNode = objectMapper.readTree(responseBody);
                
                if (rootNode.has("success") && rootNode.get("success").asBoolean()) {
                    JsonNode dataNode = rootNode.get("data");
                    
                    if (dataNode != null && dataNode.isArray()) {
                        for (JsonNode busNode : dataNode) {
                            BusSearchVO busVO = new BusSearchVO();
                            
                            busVO.setBusType(getJsonText(busNode, "bus_type", "고속버스"));
                            busVO.setBusCompany(getJsonText(busNode, "bus_company", "정보없음"));
                            busVO.setBusGrade(getJsonText(busNode, "bus_grade", "일반"));
                            busVO.setDepartureTerminal(getJsonText(busNode, "departure_terminal", ""));
                            busVO.setArrivalTerminal(getJsonText(busNode, "arrival_terminal", ""));
                            busVO.setDepartureTime(getJsonText(busNode, "departure_time", ""));
                            busVO.setArrivalTime(getJsonText(busNode, "arrival_time", ""));
                            busVO.setDuration(getJsonText(busNode, "duration", ""));
                            busVO.setPrice(getJsonText(busNode, "price", ""));
                            busVO.setRemainingSeats(getJsonText(busNode, "remaining_seats", ""));
                            busVO.setSearchDate(searchDate);
                            busVO.setCreatedDate(new Date());
                            
                            busList.add(busVO);
                        }
                    }
                    
                    System.out.println("버스 데이터 파싱 완료: " + busList.size() + "건");
                } else {
                    String error = rootNode.has("error") ? rootNode.get("error").asText() : "알 수 없는 오류";
                    System.err.println("Python 서버 버스 조회 오류: " + error);
                }
            } else {
                System.err.println("HTTP 오류: " + response.getStatusCode());
            }
            
        } catch (Exception e) {
            System.err.println("버스 조회 예외 발생: " + e.getMessage());
            e.printStackTrace();
            // 샘플 데이터 제거 - 빈 리스트 반환
            System.err.println("실제 버스 데이터를 가져올 수 없습니다. 빈 결과를 반환합니다.");
        }
        
        return busList; // 빈 리스트이거나 실제 데이터
    }

    // getSampleBusData 메서드 완전 삭제
    
    /**
     * 버스 터미널 목록 조회
     */
    public List<String> getBusTerminals() throws Exception {
        List<String> terminals = new ArrayList<>();
        
        try {
            String apiUrl = pythonCrawlerUrl + "/bus_terminals";
            System.out.println("🌐 Python 서버 터미널 목록 조회: " + apiUrl);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAcceptCharset(Arrays.asList(StandardCharsets.UTF_8));
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<String> response = restTemplate.exchange(
                    apiUrl, HttpMethod.GET, entity, String.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                String responseBody = response.getBody();
                
                JsonNode rootNode = objectMapper.readTree(responseBody);
                
                if (rootNode.has("success") && rootNode.get("success").asBoolean()) {
                    JsonNode terminalsNode = rootNode.get("terminals");
                    
                    if (terminalsNode != null && terminalsNode.isArray()) {
                        for (JsonNode terminalNode : terminalsNode) {
                            String terminalName = terminalNode.get("name").asText();
                            terminals.add(terminalName);
                        }
                    }
                }
            }
            
        } catch (Exception e) {
            System.err.println("❌ 터미널 목록 조회 오류: " + e.getMessage());
            // 실패시 기본 터미널 목록 반환
            terminals = Arrays.asList(
                "서울고속버스터미널", "동서울터미널", "부산서부터미널", 
                "부산종합버스터미널", "대전복합터미널", "대구동부터미널",
                "광주종합버스터미널", "울산시외버스터미널"
            );
        }
        
        return terminals;
    }

    /**
     * 버스 서버 상태 확인
     */
    public boolean checkBusServerStatus() {
        try {
            String apiUrl = pythonCrawlerUrl + "/health";
            ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode rootNode = objectMapper.readTree(response.getBody());
                return rootNode.has("status") && "healthy".equals(rootNode.get("status").asText());
            }
        } catch (Exception e) {
            System.err.println("버스 서버 상태 확인 실패: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * JSON 노드에서 안전하게 텍스트 추출
     */
    private String getJsonText(JsonNode node, String fieldName, String defaultValue) {
        if (node.has(fieldName) && !node.get(fieldName).isNull()) {
            String value = node.get(fieldName).asText();
            try {
                // URL 디코딩 추가
                return URLDecoder.decode(value, StandardCharsets.UTF_8.toString());
            } catch (Exception e) {
                return value; // 디코딩 실패 시 원본 반환
            }
        }
        return defaultValue;
    }
    
    /**
     * 크롤링 서버 상태 체크
     */
    public boolean isServerAvailable() {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(pythonCrawlerUrl + "/health", String.class);
            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            System.err.println("❌ 서버 상태 체크 실패: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 크롤링 서버 상태 정보 조회
     */
    public String getServerStatus() {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(pythonCrawlerUrl + "/health", String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                return "🟢 연결됨 - " + response.getBody();
            } else {
                return "🟡 응답 오류 - " + response.getStatusCode();
            }
        } catch (Exception e) {
            return "🔴 연결 실패 - " + e.getMessage();
        }
    }
    
    
}