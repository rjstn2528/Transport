package net.koreate.transport.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Controller
@RequestMapping("/debug/tago")
public class TaGoApiTestController {

    @Value("${tago.api.key}")
    private String serviceKey;

    @Value("${tago.api.base.url}")
    private String baseUrl;

    /**
     * TAGO API 직접 테스트 (가장 간단한 호출)
     */
    @GetMapping("/raw-test")
    @ResponseBody
    public Map<String, Object> rawApiTest(
            @RequestParam(defaultValue = "서울") String depPlaceNm,
            @RequestParam(defaultValue = "부산") String arrPlaceNm,
            @RequestParam(defaultValue = "20240301") String depPlandTime) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 기본 URL 구성
            String testUrl = baseUrl + "/getStrtpntAlocFndTrainInfo" +
                    "?serviceKey=" + serviceKey +
                    "&pageNo=1" +
                    "&numOfRows=10" +
                    "&_type=json" +
                    "&depPlaceNm=" + depPlaceNm +
                    "&arrPlaceNm=" + arrPlaceNm +
                    "&depPlandTime=" + depPlandTime;
            
            System.out.println("RAW 테스트 URL: " + testUrl.replace(serviceKey, "***"));
            
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept", "application/json");
            headers.set("User-Agent", "Mozilla/5.0");
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(testUrl, HttpMethod.GET, entity, String.class);
            
            result.put("success", true);
            result.put("status_code", response.getStatusCode().value());
            result.put("response_body", response.getBody());
            result.put("test_url", testUrl.replace(serviceKey, "***"));
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
            result.put("error_class", e.getClass().getSimpleName());
        }
        
        return result;
    }

    /**
     * API 키 상태 확인
     */
    @GetMapping("/key-status")
    @ResponseBody
    public Map<String, Object> checkKeyStatus() {
        Map<String, Object> result = new HashMap<>();
        
        result.put("service_key_length", serviceKey != null ? serviceKey.length() : 0);
        result.put("service_key_starts_with", serviceKey != null ? serviceKey.substring(0, Math.min(10, serviceKey.length())) + "..." : "null");
        result.put("base_url", baseUrl);
        result.put("key_valid_format", serviceKey != null && serviceKey.length() > 20 && !serviceKey.contains("${"));
        
        return result;
    }

    /**
     * 다양한 파라미터로 테스트
     */
    @GetMapping("/param-test")
    @ResponseBody
    public Map<String, Object> parameterTest() {
        Map<String, Object> result = new HashMap<>();
        
        // 여러 날짜와 구간으로 테스트
        String[][] testCases = {
            {"서울", "부산", "20240301"},
            {"Seoul", "Busan", "20240301"},
            {"서울", "부산", "20240301" + "0600"},
            {"서울역", "부산역", "20240301"}
        };
        
        for (int i = 0; i < testCases.length; i++) {
            try {
                String[] testCase = testCases[i];
                
                UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl + "/getStrtpntAlocFndTrainInfo")
                        .queryParam("serviceKey", serviceKey)
                        .queryParam("pageNo", "1")
                        .queryParam("numOfRows", "5")
                        .queryParam("_type", "json")
                        .queryParam("depPlaceNm", testCase[0])
                        .queryParam("arrPlaceNm", testCase[1])
                        .queryParam("depPlandTime", testCase[2]);

                String apiUrl = builder.toUriString();
                
                RestTemplate restTemplate = new RestTemplate();
                ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);
                
                Map<String, Object> testResult = new HashMap<>();
                testResult.put("test_case", String.join(",", testCase));
                testResult.put("status", response.getStatusCode().value());
                testResult.put("response_preview", response.getBody().substring(0, Math.min(200, response.getBody().length())));
                testResult.put("is_xml", response.getBody().startsWith("<"));
                testResult.put("contains_error", response.getBody().contains("ERROR"));
                
                result.put("test_" + i, testResult);
                
            } catch (Exception e) {
                Map<String, Object> errorResult = new HashMap<>();
                errorResult.put("error", e.getMessage());
                result.put("test_" + i, errorResult);
            }
        }
        
        return result;
    }

    /**
     * 공공데이터포털 샘플 URL 테스트
     */
    @GetMapping("/sample-test")
    @ResponseBody
    public Map<String, Object> sampleUrlTest() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 공공데이터포털 가이드의 샘플 형식
            String sampleUrl = "https://apis.data.go.kr/1613000/TrainInfoService/getStrtpntAlocFndTrainInfo" +
                    "?serviceKey=" + serviceKey +
                    "&pageNo=1" +
                    "&numOfRows=10" +
                    "&_type=json" +
                    "&depPlaceNm=%EC%84%9C%EC%9A%B8" +  // 서울 (URL 인코딩)
                    "&arrPlaceNm=%EB%B6%80%EC%82%B0" +  // 부산 (URL 인코딩)
                    "&depPlandTime=20240301";
            
            System.out.println("샘플 URL 테스트: " + sampleUrl.replace(serviceKey, "***"));
            
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.getForEntity(sampleUrl, String.class);
            
            result.put("success", true);
            result.put("status_code", response.getStatusCode().value());
            result.put("content_type", response.getHeaders().getContentType());
            result.put("response_length", response.getBody().length());
            result.put("response_body", response.getBody());
            result.put("is_xml_response", response.getBody().startsWith("<"));
            
            // XML에서 에러 확인
            if (response.getBody().startsWith("<")) {
                result.put("xml_contains_error", response.getBody().contains("ERROR"));
                result.put("xml_contains_service_error", response.getBody().contains("SERVICE ERROR"));
                result.put("xml_contains_auth_error", response.getBody().contains("APPLICATION_ERROR"));
            }
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        return result;
    }
}