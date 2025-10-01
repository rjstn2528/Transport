package net.koreate.transport.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import net.koreate.transport.service.TransportService;
import net.koreate.transport.util.TaGoApiUtil;
import net.koreate.transport.vo.TrainSearchVO;

@Controller
@RequestMapping("/transport")
public class TestController {

    @Autowired
    private TransportService transportService;

    @Autowired(required = false)
    private TaGoApiUtil taGoApiUtil;

    /**
     * 기본 테스트
     */
    @GetMapping("/test")
    @ResponseBody
    public Map<String, Object> basicTest() {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Transport 테스트 컨트롤러 작동 중");
        result.put("timestamp", new java.util.Date());
        return result;
    }

    /**
     * TAGO API 상태 테스트
     */
    @GetMapping("/test/tago")
    @ResponseBody
    public Map<String, Object> tagoTest() {
        Map<String, Object> result = new HashMap<>();
        
        if (taGoApiUtil != null) {
            result.put("tago_available", true);
            result.put("tago_status", taGoApiUtil.checkApiStatus());
            result.putAll(taGoApiUtil.getApiInfo());
        } else {
            result.put("tago_available", false);
            result.put("error", "TaGoApiUtil이 주입되지 않음");
        }
        
        return result;
    }

    /**
     * 과거 날짜로 기차 조회 테스트
     */
    @GetMapping("/test/train-past")
    @ResponseBody
    public Map<String, Object> testTrainPast() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 과거 날짜로 강제 테스트 (2024년 3월 1일)
            List<TrainSearchVO> trains = transportService.searchTrains("서울", "부산", "20240301");
            
            result.put("success", true);
            result.put("count", trains.size());
            result.put("trains", trains);
            result.put("test_params", Map.of(
                "departure", "서울",
                "arrival", "부산", 
                "date", "20240301"
            ));
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        return result;
    }

    /**
     * Python 서버 연결 테스트
     */
    @GetMapping("/test/python")
    @ResponseBody
    public Map<String, Object> testPython() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Python 크롤러 직접 호출 테스트
            java.net.URL url = new java.net.URL("http://localhost:8000/health");
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            
            int responseCode = conn.getResponseCode();
            
            if (responseCode == 200) {
                java.io.BufferedReader reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(conn.getInputStream())
                );
                String line;
                StringBuilder response = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                
                result.put("success", true);
                result.put("python_server_status", "연결됨");
                result.put("response_code", responseCode);
                result.put("response_body", response.toString());
            } else {
                result.put("success", false);
                result.put("python_server_status", "연결 실패");
                result.put("response_code", responseCode);
            }
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("python_server_status", "연결 불가");
            result.put("error", e.getMessage());
        }
        
        return result;
    }

    /**
     * 전체 시스템 상태 테스트
     */
    @GetMapping("/test/system")
    @ResponseBody
    public Map<String, Object> testSystem() {
        Map<String, Object> result = new HashMap<>();
        
        // TAGO API 테스트
        Map<String, Object> tagoStatus = new HashMap<>();
        if (taGoApiUtil != null) {
            tagoStatus.put("available", true);
            tagoStatus.put("healthy", taGoApiUtil.checkApiStatus());
        } else {
            tagoStatus.put("available", false);
        }
        
        // Python 서버 테스트
        Map<String, Object> pythonStatus = new HashMap<>();
        try {
            java.net.URL url = new java.net.URL("http://localhost:8000/health");
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(3000);
            
            pythonStatus.put("available", conn.getResponseCode() == 200);
            pythonStatus.put("url", "http://localhost:8000");
        } catch (Exception e) {
            pythonStatus.put("available", false);
            pythonStatus.put("error", e.getMessage());
        }
        
        // 데이터베이스 테스트
        Map<String, Object> dbStatus = new HashMap<>();
        try {
            int count = transportService.getTotalSearchCount();
            dbStatus.put("available", true);
            dbStatus.put("total_searches", count);
        } catch (Exception e) {
            dbStatus.put("available", false);
            dbStatus.put("error", e.getMessage());
        }
        
        result.put("timestamp", new java.util.Date());
        result.put("tago_api", tagoStatus);
        result.put("python_server", pythonStatus);
        result.put("database", dbStatus);
        result.put("priority", "1. TAGO API → 2. Python 크롤러");
        
        return result;
    }

    /**
     * 샘플 데이터 테스트
     */
    @GetMapping("/test/sample")
    @ResponseBody
    public Map<String, Object> testSample() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 샘플 데이터로 직접 테스트
            List<TrainSearchVO> trains = transportService.searchTrains("서울", "부산", "20991231");
            
            result.put("success", true);
            result.put("count", trains.size());
            result.put("message", "샘플 데이터 테스트 (미래 날짜로 강제)");
            result.put("trains", trains.size() > 0 ? trains.subList(0, Math.min(3, trains.size())) : trains);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        return result;
    }
}