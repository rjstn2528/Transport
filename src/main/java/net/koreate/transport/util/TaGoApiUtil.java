package net.koreate.transport.util;

import java.util.ArrayList;
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

import net.koreate.transport.vo.TrainSearchVO;

@Component
public class TaGoApiUtil {

    @Value("${tago.api.key}")
    private String serviceKey;

    @Value("${tago.api.base.url}")
    private String baseUrl;

    @Value("${tago.api.enabled:false}")
    private boolean enabled;

    private boolean apiEnabled = false;
    private RestTemplate restTemplate;
    private ObjectMapper objectMapper;
    
    // 완전한 기차역 ID 매핑
    private static final Map<String, String> STATION_ID_MAP = new HashMap<>();
    
    static {
        // === 경부선 (KTX) ===
        STATION_ID_MAP.put("행신", "NAT010415");
        STATION_ID_MAP.put("서울", "NAT010000");
        STATION_ID_MAP.put("영등포", "NAT010019");
        STATION_ID_MAP.put("광명", "NAT010058");
        STATION_ID_MAP.put("수원", "NAT010184");
        STATION_ID_MAP.put("천안아산", "NAT010754");
        STATION_ID_MAP.put("오송", "NAT011271");
        STATION_ID_MAP.put("대전", "NAT011668");
        STATION_ID_MAP.put("김천구미", "NAT013271");
        STATION_ID_MAP.put("서대구", "NAT013278");
        STATION_ID_MAP.put("동대구", "NAT013271");  // 김천구미 ID로 동대구 검색
        STATION_ID_MAP.put("대구", "NAT013271");    // 김천구미 ID로 대구 검색
        STATION_ID_MAP.put("경산", "NAT013421");
        STATION_ID_MAP.put("밀양", "NAT013632");
        STATION_ID_MAP.put("구포", "NAT014184");
        STATION_ID_MAP.put("부산", "NAT014445");
        
        // === 호남선 (KTX) ===
        STATION_ID_MAP.put("서대전", "NAT011395");
        STATION_ID_MAP.put("계룡", "NAT011581");
        STATION_ID_MAP.put("논산", "NAT011668");
        STATION_ID_MAP.put("익산", "NAT012267");
        STATION_ID_MAP.put("김제", "NAT012551");
        STATION_ID_MAP.put("정읍", "NAT012848");
        STATION_ID_MAP.put("장성", "NAT031348");
        STATION_ID_MAP.put("광주송정", "NAT031702");
        STATION_ID_MAP.put("광주", "NAT031702");
        STATION_ID_MAP.put("송정", "NAT031702");
        STATION_ID_MAP.put("광주광역시", "NAT031702");
        STATION_ID_MAP.put("나주", "NAT032003");
        STATION_ID_MAP.put("목포", "NAT032292");
        
        // === 호남고속선 ===
        STATION_ID_MAP.put("공주", "NAT011786");
        
        // === 전라선 (KTX) ===
        STATION_ID_MAP.put("전주", "NAT021239");
        STATION_ID_MAP.put("남원", "NAT021848");
        STATION_ID_MAP.put("곡성", "NAT032201");
        STATION_ID_MAP.put("구례구", "NAT032402");
        STATION_ID_MAP.put("순천", "NAT033397");
        STATION_ID_MAP.put("여천", "NAT033565");
        STATION_ID_MAP.put("여수엑스포", "NAT033744");
        STATION_ID_MAP.put("여수", "NAT033744");
        STATION_ID_MAP.put("여수EXPO", "NAT033744");
        
        // === 경전선 (KTX) ===
        STATION_ID_MAP.put("진영", "NAT014506");
        STATION_ID_MAP.put("창원중앙", "NAT014540");
        STATION_ID_MAP.put("창원", "NAT014553");
        STATION_ID_MAP.put("마산", "NAT014589");
        STATION_ID_MAP.put("진주", "NAT024064");
        
        // === 중앙선 (KTX) ===
        STATION_ID_MAP.put("청량리", "NAT010043");
        STATION_ID_MAP.put("용문", "NAT010514");
        STATION_ID_MAP.put("지평", "NAT010647");
        STATION_ID_MAP.put("제천", "NAT022134");
        STATION_ID_MAP.put("단양", "NAT022266");
        STATION_ID_MAP.put("풍기", "NAT022384");
        STATION_ID_MAP.put("영주", "NAT022475");
        STATION_ID_MAP.put("안동", "NAT022707");
        STATION_ID_MAP.put("의성", "NAT023047");
        STATION_ID_MAP.put("신경주", "NAT013707");
        
        // === 중부내륙선 (KTX) ===
        STATION_ID_MAP.put("부발", "NAT010361");
        STATION_ID_MAP.put("이천", "NAT010413");
        STATION_ID_MAP.put("여주", "NAT010498");
        STATION_ID_MAP.put("세종", "NAT011847");
        STATION_ID_MAP.put("조치원", "NAT011936");
        STATION_ID_MAP.put("청주", "NAT021467");
        STATION_ID_MAP.put("오근장", "NAT021621");
        STATION_ID_MAP.put("추풍령", "NAT022089");
        
        // === 강릉선 (KTX) ===
        STATION_ID_MAP.put("평창", "NAT025036");
        STATION_ID_MAP.put("진부", "NAT025064");
        STATION_ID_MAP.put("강릉", "NAT025145");
        
        // === 동해선 (KTX) ===
        STATION_ID_MAP.put("포항", "NAT023507");
        STATION_ID_MAP.put("신경주", "NAT013707");
        STATION_ID_MAP.put("울산", "NAT014445");
        
        // === 별칭 처리 ===
        STATION_ID_MAP.put("서울역", "NAT010000");
        STATION_ID_MAP.put("부산역", "NAT014445");
        STATION_ID_MAP.put("용산", "NAT010032");
        STATION_ID_MAP.put("용산역", "NAT010032");
        STATION_ID_MAP.put("대전역", "NAT011668");
        STATION_ID_MAP.put("광명역", "NAT010058");
        STATION_ID_MAP.put("광주역", "NAT031702");
        STATION_ID_MAP.put("목포역", "NAT032292");
        STATION_ID_MAP.put("대구역", "NAT013271");
        STATION_ID_MAP.put("동대구역", "NAT013271");
        STATION_ID_MAP.put("김천", "NAT013271");
        STATION_ID_MAP.put("구미", "NAT013271");
        
        // === ITX/무궁화호 주요역 추가 ===
        STATION_ID_MAP.put("춘천", "NAT010710");
        STATION_ID_MAP.put("원주", "NAT020634");
        STATION_ID_MAP.put("제천", "NAT022134");
        STATION_ID_MAP.put("태백", "NAT024511");
        STATION_ID_MAP.put("정동진", "NAT025089");
        STATION_ID_MAP.put("동해", "NAT025145");
        STATION_ID_MAP.put("삼척", "NAT025278");
    }

    @PostConstruct
    public void init() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();

        System.out.println("=== TAGO API 초기화 ===");
        System.out.println("Service Key 설정: " + (serviceKey != null && !serviceKey.trim().isEmpty()));
        System.out.println("Base URL: " + baseUrl);
        System.out.println("Enabled: " + enabled);
        System.out.println("지원 역 수: " + STATION_ID_MAP.size() + "개");
        
        this.apiEnabled = isValidServiceKey();
        System.out.println("TAGO API 활성화 상태: " + apiEnabled);
        System.out.println("=== TAGO API 초기화 완료 ===");
    }

    private boolean isValidServiceKey() {
        return enabled && 
               serviceKey != null && 
               !serviceKey.trim().isEmpty() && 
               !serviceKey.contains("${") &&
               serviceKey.length() > 20;
    }

    /**
     * TAGO API로 기차 조회 (개선된 버전)
     */
    public List<TrainSearchVO> searchTrains(String depPlaceNm, String arrPlaceNm, String depPlandTime) {
        if (!apiEnabled) {
            System.out.println("TAGO API가 비활성화되어 있습니다.");
            return new ArrayList<>();
        }

        try {
            // 1. 역 이름을 역 ID로 변환
            String depPlaceId = getStationId(depPlaceNm);
            String arrPlaceId = getStationId(arrPlaceNm);
            
            System.out.println("🎯 역 매핑 시도: " + depPlaceNm + "(" + depPlaceId + ") -> " + arrPlaceNm + "(" + arrPlaceId + ")");
            
            // 2. 광주/목포 특별 처리 - 다른 ID들도 시도
            if ((depPlaceNm.equals("광주") || arrPlaceNm.equals("광주") || 
                 depPlaceNm.equals("목포") || arrPlaceNm.equals("목포")) && 
                (depPlaceId == null || arrPlaceId == null)) {
                
                System.out.println("🔍 호남선 역 특별 처리 시도");
                
                // 호남선 관련 다양한 ID 시도
                String[] possibleIds = {
                    "NAT031702",  // 광주송정 현재
                    "NAT032292",  // 목포 현재  
                    "NAT031701", "NAT031700", "NAT031703",  // 광주 가능한 다른 ID
                    "NAT032291", "NAT032290", "NAT032293"   // 목포 가능한 다른 ID
                };
                
                for (String testId : possibleIds) {
                    String testDepId = depPlaceId;
                    String testArrId = arrPlaceId;
                    
                    if ((depPlaceNm.equals("광주") || depPlaceNm.equals("목포")) && depPlaceId == null) {
                        testDepId = testId;
                    }
                    if ((arrPlaceNm.equals("광주") || arrPlaceNm.equals("목포")) && arrPlaceId == null) {
                        testArrId = testId;
                    }
                    
                    if (testDepId != null && testArrId != null) {
                        System.out.println("🧪 호남선 ID 테스트: " + testId);
                        
                        List<TrainSearchVO> testResult = callTaGoAPI(testDepId, testArrId, depPlandTime, depPlaceNm, arrPlaceNm);
                        if (!testResult.isEmpty()) {
                            System.out.println("✅ 올바른 ID 발견: " + testId + " - " + testResult.size() + "건");
                            // 캐시에 추가
                            if (depPlaceNm.equals("광주") && depPlaceId == null) {
                                STATION_ID_MAP.put("광주", testId);
                                STATION_ID_MAP.put("광주송정", testId);
                            }
                            if (arrPlaceNm.equals("광주") && arrPlaceId == null) {
                                STATION_ID_MAP.put("광주", testId);
                                STATION_ID_MAP.put("광주송정", testId);
                            }
                            if (depPlaceNm.equals("목포") && depPlaceId == null) {
                                STATION_ID_MAP.put("목포", testId);
                            }
                            if (arrPlaceNm.equals("목포") && arrPlaceId == null) {
                                STATION_ID_MAP.put("목포", testId);
                            }
                            return testResult;
                        }
                    }
                }
            }
            
            if (depPlaceId == null || arrPlaceId == null) {
                System.out.println("⚠️ 지원하지 않는 역: " + depPlaceNm + " -> " + arrPlaceNm);
                System.out.println("출발역 ID: " + depPlaceId + ", 도착역 ID: " + arrPlaceId);
                
                // 동적으로 역 ID 조회 시도
                if (depPlaceId == null) {
                    depPlaceId = findStationIdByName(depPlaceNm);
                }
                if (arrPlaceId == null) {
                    arrPlaceId = findStationIdByName(arrPlaceNm);
                }
                
                if (depPlaceId == null || arrPlaceId == null) {
                    System.out.println("❌ 역 ID를 찾을 수 없습니다.");
                    return new ArrayList<>();
                }
            }
            
            System.out.println("✅ 역 매핑 완료: " + depPlaceNm + "(" + depPlaceId + ") -> " + arrPlaceNm + "(" + arrPlaceId + ")");
            
            // 3. API 호출
            List<TrainSearchVO> result = callTaGoAPI(depPlaceId, arrPlaceId, depPlandTime, depPlaceNm, arrPlaceNm);
            
            if (!result.isEmpty()) {
                System.out.println("✅ TAGO API 성공: " + result.size() + "건");
                return result;
            } else {
                System.out.println("ℹ️ TAGO API 응답: 해당 날짜에 운행 정보가 없습니다.");
                return new ArrayList<>();
            }

        } catch (Exception e) {
            System.err.println("❌ TAGO API 오류: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * 역 이름으로 역 ID 조회
     */
    private String getStationId(String stationName) {
        if (stationName == null || stationName.trim().isEmpty()) {
            return null;
        }
        
        String trimmedName = stationName.trim();
        
        // 직접 매핑 확인
        String stationId = STATION_ID_MAP.get(trimmedName);
        if (stationId != null) {
            return stationId;
        }
        
        // "역" 제거 후 재시도
        if (trimmedName.endsWith("역")) {
            String nameWithoutStation = trimmedName.substring(0, trimmedName.length() - 1);
            stationId = STATION_ID_MAP.get(nameWithoutStation);
            if (stationId != null) {
                return stationId;
            }
        }
        
        return null;
    }

    /**
     * 동적으로 역 ID 조회 (시도별 기차역 목록 API 사용)
     */
    private String findStationIdByName(String stationName) {
        try {
            System.out.println("🔍 동적 역 검색 시작: " + stationName);
            
            UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(baseUrl + "/getCtyAcctoTrainSttnList")
                .queryParam("serviceKey", serviceKey)
                .queryParam("pageNo", "1")
                .queryParam("numOfRows", "1000")
                .queryParam("_type", "json");

            String apiUrl = builder.toUriString();
            System.out.println("역 목록 API 호출: " + apiUrl.replace(serviceKey, "***"));
            
            ResponseEntity<String> response = restTemplate.exchange(
                apiUrl, HttpMethod.GET, createHeaders(), String.class);
            
            System.out.println("역 목록 API 응답 상태: " + response.getStatusCode());
            
            if (response.getStatusCode() == HttpStatus.OK) {
                String responseBody = response.getBody();
                System.out.println("역 목록 API 응답 샘플: " + 
                    (responseBody != null && responseBody.length() > 200 ? 
                     responseBody.substring(0, 200) + "..." : responseBody));
                
                JsonNode rootNode = objectMapper.readTree(responseBody);
                JsonNode responseNode = rootNode.path("response");
                JsonNode headerNode = responseNode.path("header");
                String resultCode = headerNode.path("resultCode").asText();
                
                if (!"00".equals(resultCode)) {
                    System.out.println("❌ 역 목록 API 오류: " + resultCode);
                    return null;
                }
                
                JsonNode bodyNode = responseNode.path("body");
                JsonNode itemsNode = bodyNode.path("items");
                JsonNode itemArray = itemsNode.path("item");
                
                System.out.println("검색할 역명: " + stationName);
                System.out.println("역 목록 항목 수: " + (itemArray.isArray() ? itemArray.size() : "단일객체"));
                
                if (itemArray.isArray()) {
                    for (JsonNode item : itemArray) {
                        String nodename = item.path("nodename").asText();
                        String nodeid = item.path("nodeid").asText();
                        
                        // 정확한 일치 우선
                        if (stationName.equals(nodename)) {
                            System.out.println("✅ 정확한 일치 발견: " + stationName + " -> " + nodeid);
                            STATION_ID_MAP.put(stationName, nodeid);
                            return nodeid;
                        }
                        
                        // 부분 일치 확인
                        if (nodename.contains(stationName) || stationName.contains(nodename)) {
                            System.out.println("✅ 부분 일치 발견: " + stationName + " -> " + nodename + " (" + nodeid + ")");
                            STATION_ID_MAP.put(stationName, nodeid);
                            return nodeid;
                        }
                    }
                }
                
                System.out.println("❌ 동적 검색에서 매칭되는 역을 찾지 못함: " + stationName);
            } else {
                System.out.println("❌ 역 목록 API HTTP 오류: " + response.getStatusCode());
            }
            
            return null;
            
        } catch (Exception e) {
            System.err.println("❌ 동적 역 검색 오류: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * TAGO API 호출
     */
    private List<TrainSearchVO> callTaGoAPI(String depPlaceId, String arrPlaceId, String depPlandTime, 
                                           String depPlaceNm, String arrPlaceNm) throws Exception {
        
        String[] dateFormats = {
            depPlandTime,  // 원본 (20250923)
            depPlandTime + "0600"  // 시간 추가 (202509230600)
        };
        
        for (String dateFormat : dateFormats) {
            try {
                System.out.println("📅 날짜 형식 시도: " + dateFormat);
                
                UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(baseUrl + "/getStrtpntAlocFndTrainInfo")
                    .queryParam("serviceKey", serviceKey)
                    .queryParam("pageNo", "1")
                    .queryParam("numOfRows", "50")
                    .queryParam("_type", "json")
                    .queryParam("depPlaceId", depPlaceId)
                    .queryParam("arrPlaceId", arrPlaceId)
                    .queryParam("depPlandTime", dateFormat);

                String apiUrl = builder.toUriString();
                System.out.println("🚄 TAGO API 호출: " + apiUrl.replace(serviceKey, "***"));
                
                ResponseEntity<String> response = restTemplate.exchange(
                    apiUrl, HttpMethod.GET, createHeaders(), String.class);

                System.out.println("📡 TAGO 응답 상태: " + response.getStatusCode());
                
                if (response.getStatusCode() == HttpStatus.OK) {
                    String responseBody = response.getBody();
                    System.out.println("📋 TAGO API 원본 응답: " + responseBody);
                    
                    if (responseBody != null && responseBody.trim().startsWith("<")) {
                        System.out.println("⚠️ XML 오류 응답 수신: " + responseBody);
                        
                        if (responseBody.contains("SERVICE ERROR") || responseBody.contains("APPLICATION_ERROR")) {
                            System.out.println("🔄 서비스 오류 - 다음 날짜 형식 시도");
                            continue;
                        }
                    } else {
                        List<TrainSearchVO> result = parseTaGoResponse(
                            responseBody, depPlandTime, depPlaceNm, arrPlaceNm);
                        System.out.println("✅ TAGO API 파싱 완료: " + result.size() + "건");
                        return result;
                    }
                } else {
                    System.out.println("❌ HTTP 오류: " + response.getStatusCode() + " - " + response.getBody());
                }
            } catch (Exception e) {
                System.err.println("❌ 날짜 형식 " + dateFormat + " 실패: " + e.getMessage());
            }
        }
        
        return new ArrayList<>();
    }
    
    /**
     * HTTP 헤더 생성
     */
    private HttpEntity<String> createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Accept", "application/json");
        headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
        return new HttpEntity<>(headers);
    }

    /**
     * TAGO API 응답 파싱
     */
    private List<TrainSearchVO> parseTaGoResponse(String responseBody, String searchDate, 
                                                 String departureStation, String arrivalStation) {
        List<TrainSearchVO> trains = new ArrayList<>();

        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);
            System.out.println("📊 JSON 루트 노드: " + rootNode.toString());
            
            JsonNode responseNode = rootNode.path("response");
            JsonNode headerNode = responseNode.path("header");
            String resultCode = headerNode.path("resultCode").asText();
            String resultMsg = headerNode.path("resultMsg").asText();
            
            System.out.println("📋 TAGO API 결과 코드: " + resultCode);
            System.out.println("💬 TAGO API 결과 메시지: " + resultMsg);
            
            if (!"00".equals(resultCode)) {
                System.out.println("⚠️ TAGO API 오류: " + resultCode + " - " + resultMsg);
                return trains;
            }
            
            JsonNode bodyNode = responseNode.path("body");
            System.out.println("📦 Body 노드: " + bodyNode.toString());
            
            int totalCount = bodyNode.path("totalCount").asInt(0);
            System.out.println("📊 Total Count: " + totalCount);
            
            if (totalCount == 0) {
                System.out.println("ℹ️ API 응답: 해당 날짜에 운행 정보가 없습니다 (totalCount=0)");
                return trains;
            }
            
            JsonNode itemsNode = bodyNode.path("items");
            JsonNode itemArray = itemsNode.path("item");
            
            if (itemArray.isArray() && itemArray.size() > 0) {
                System.out.println("🚄 기차 데이터 발견: " + itemArray.size() + "개 항목");
                
                for (JsonNode itemNode : itemArray) {
                    TrainSearchVO train = parseTrainItem(itemNode, searchDate, departureStation, arrivalStation);
                    if (train != null) {
                        trains.add(train);
                    }
                }
            } else if (!itemsNode.isMissingNode() && !itemsNode.isNull()) {
                TrainSearchVO train = parseTrainItem(itemsNode, searchDate, departureStation, arrivalStation);
                if (train != null) {
                    trains.add(train);
                }
            }

        } catch (Exception e) {
            System.err.println("❌ TAGO API 응답 파싱 오류: " + e.getMessage());
            e.printStackTrace();
        }

        return trains;
    }

    /**
     * 개별 기차 항목 파싱
     */
    private TrainSearchVO parseTrainItem(JsonNode itemNode, String searchDate, 
                                       String departureStation, String arrivalStation) {
        
        if (itemNode == null || itemNode.isMissingNode() || itemNode.isNull()) {
            System.out.println("❌ 유효하지 않은 데이터 노드");
            return null;
        }
        
        System.out.println("🔍 파싱 중인 항목: " + itemNode.toString());
        
        TrainSearchVO train = new TrainSearchVO();
        
        // API 응답의 실제 도착지명 확인
        String apiArrivalStation = getStringValue(itemNode, "arrplacename", "");
        String apiDepartureStation = getStringValue(itemNode, "depplacename", "");
        
        // 사용자가 요청한 역과 API 응답 역이 다른 경우 처리
        if (!arrivalStation.equals("김천구미") && apiArrivalStation.equals("동대구") && 
            (arrivalStation.equals("동대구") || arrivalStation.equals("대구"))) {
            train.setDepartureStation(departureStation);
            train.setArrivalStation(arrivalStation);
        } else if (arrivalStation.equals("김천구미") && apiArrivalStation.equals("동대구")) {
            train.setDepartureStation(departureStation);
            train.setArrivalStation("김천구미");
        } else {
            train.setDepartureStation(departureStation);
            train.setArrivalStation(arrivalStation);
        }
        
        train.setSearchDate(searchDate);
        
        String trainType = getStringValue(itemNode, "traingradename", "");
        String trainNumber = getStringValue(itemNode, "trainno", "");
        
        System.out.println("🚂 기차 종류: '" + trainType + "', 번호: '" + trainNumber + "'");
        System.out.println("🎯 API 응답역: " + apiDepartureStation + " -> " + apiArrivalStation);
        System.out.println("👤 사용자 요청: " + departureStation + " -> " + arrivalStation);
        
        if (trainType.trim().isEmpty() && trainNumber.trim().isEmpty()) {
            System.out.println("❌ 기차 종류와 번호가 모두 비어있음");
            return null;
        }
        
        train.setTrainType(trainType.isEmpty() ? "기차" : trainType);
        train.setTrainNumber(trainNumber.isEmpty() ? "정보없음" : trainNumber);
        
        String depTime = getStringValue(itemNode, "depplandtime", "");
        String arrTime = getStringValue(itemNode, "arrplandtime", "");
        
        System.out.println("🕐 출발시간: '" + depTime + "', 도착시간: '" + arrTime + "'");
        
        train.setDepartureTime(formatTime(depTime));
        train.setArrivalTime(formatTime(arrTime));
        train.setDuration(calculateDuration(depTime, arrTime));
        
        String adultcharge = getStringValue(itemNode, "adultcharge", "0");
        System.out.println("💰 요금: '" + adultcharge + "'");
        
        train.setGeneralPrice(formatPrice(adultcharge));
        train.setSpecialPrice("정보없음");
        train.setAvailability("예약가능");
        train.setCreatedDate(new Date());
        
        System.out.println("✅ 파싱 성공: " + trainType + " " + trainNumber + " " + 
                          train.getDepartureTime() + "->" + train.getArrivalTime() + " (" + train.getGeneralPrice() + ")");
        
        return train;
    }

    // 유틸리티 메서드들
    private String getStringValue(JsonNode node, String fieldName, String defaultValue) {
        JsonNode fieldNode = node.path(fieldName);
        return fieldNode.isMissingNode() ? defaultValue : fieldNode.asText(defaultValue);
    }

    private String formatTime(String timeStr) {
        if (timeStr == null || timeStr.length() < 4) {
            System.out.println("⚠️ 시간 형식 오류: " + timeStr);
            return timeStr != null ? timeStr : "정보없음";
        }
        
        try {
            if (timeStr.length() >= 14) {
                String time = timeStr.substring(8, 12);
                return time.substring(0, 2) + ":" + time.substring(2);
            } else if (timeStr.length() >= 12) {
                String time = timeStr.substring(8, 12);
                return time.substring(0, 2) + ":" + time.substring(2);
            } else if (timeStr.length() == 4) {
                return timeStr.substring(0, 2) + ":" + timeStr.substring(2);
            }
            
            System.out.println("⚠️ 예상하지 못한 시간 형식: " + timeStr);
            return timeStr;
            
        } catch (Exception e) {
            System.err.println("❌ 시간 포맷팅 오류: " + timeStr + " - " + e.getMessage());
            return timeStr;
        }
    }

    private String calculateDuration(String depTime, String arrTime) {
        try {
            if (depTime == null || arrTime == null || depTime.length() < 4 || arrTime.length() < 4) {
                return "정보없음";
            }
            
            String depTimeOnly = depTime.length() >= 12 ? depTime.substring(8, 12) : depTime;
            String arrTimeOnly = arrTime.length() >= 12 ? arrTime.substring(8, 12) : arrTime;
            
            int depMinutes = Integer.parseInt(depTimeOnly.substring(0, 2)) * 60 + 
                           Integer.parseInt(depTimeOnly.substring(2));
            int arrMinutes = Integer.parseInt(arrTimeOnly.substring(0, 2)) * 60 + 
                           Integer.parseInt(arrTimeOnly.substring(2));

            if (arrMinutes < depMinutes) {
                arrMinutes += 24 * 60;
            }

            int totalMinutes = arrMinutes - depMinutes;
            int hours = totalMinutes / 60;
            int minutes = totalMinutes % 60;

            if (hours > 0 && minutes > 0) {
                return hours + "시간 " + minutes + "분";
            } else if (hours > 0) {
                return hours + "시간";
            } else {
                return minutes + "분";
            }

        } catch (Exception e) {
            return "정보없음";
        }
    }

    private String formatPrice(String fareStr) {
        if (fareStr == null || fareStr.trim().isEmpty() || "0".equals(fareStr.trim())) {
            return "정보없음";
        }
        
        try {
            int fare = Integer.parseInt(fareStr.trim());
            if (fare <= 0) {
                return "정보없음";
            }
            return String.format("%,d원", fare);
        } catch (NumberFormatException e) {
            System.out.println("요금 파싱 오류: " + fareStr);
            return fareStr.endsWith("원") ? fareStr : fareStr + "원";
        }
    }

    public boolean checkApiStatus() {
        return apiEnabled && isValidServiceKey();
    }

    public Map<String, Object> getApiInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("enabled", apiEnabled);
        info.put("hasServiceKey", isValidServiceKey());
        info.put("baseUrl", baseUrl);
        info.put("status", checkApiStatus() ? "healthy" : "unavailable");
        info.put("supportedStations", STATION_ID_MAP.keySet());
        info.put("stationCount", STATION_ID_MAP.size());
        return info;
    }
}