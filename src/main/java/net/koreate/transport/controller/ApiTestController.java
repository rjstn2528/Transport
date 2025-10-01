package net.koreate.transport.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import net.koreate.transport.util.TaGoApiUtil;
import net.koreate.transport.vo.TrainSearchVO;

@Controller
@RequestMapping("/api/test")
public class ApiTestController {

    @Autowired(required = false)
    private TaGoApiUtil taGoApiUtil;
    

    /**
     * TAGO API 상태 확인
     */
    @GetMapping("/tago/status")
    @ResponseBody
    public Map<String, Object> checkTaGoApiStatus() {
        Map<String, Object> result = new HashMap<>();
        
        if (taGoApiUtil != null) {
            result.put("available", true);
            result.put("status", taGoApiUtil.checkApiStatus());
            result.putAll(taGoApiUtil.getApiInfo());
        } else {
            result.put("available", false);
            result.put("error", "TaGoApiUtil이 주입되지 않음");
        }
        
        return result;
    }
    
    /**
     * TAGO API 간단한 테스트 (서울→부산)
     */
    @GetMapping("/tago/search")
    @ResponseBody
    public Map<String, Object> testTaGoApiSearch(
            @RequestParam(defaultValue = "서울") String departure,
            @RequestParam(defaultValue = "부산") String arrival,
            @RequestParam(defaultValue = "20240925") String date) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            if (taGoApiUtil != null) {
                List<TrainSearchVO> trains = taGoApiUtil.searchTrains(departure, arrival, date);
                
                result.put("success", true);
                result.put("count", trains.size());
                result.put("trains", trains);
                result.put("departure", departure);
                result.put("arrival", arrival);
                result.put("date", date);
            } else {
                result.put("success", false);
                result.put("error", "TaGoApiUtil을 사용할 수 없습니다");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        return result;
    }

    /**
     * 모든 API 상태 종합 확인
     */
    @GetMapping("/all-apis")
    @ResponseBody
    public Map<String, Object> checkAllApis() {
        Map<String, Object> result = new HashMap<>();
        
        // TAGO API 상태
        Map<String, Object> tagoStatus = new HashMap<>();
        if (taGoApiUtil != null) {
            tagoStatus.put("available", true);
            tagoStatus.put("healthy", taGoApiUtil.checkApiStatus());
            tagoStatus.putAll(taGoApiUtil.getApiInfo());
        } else {
            tagoStatus.put("available", false);
            tagoStatus.put("error", "Not injected");
        }
        result.put("tago", tagoStatus);
        
        
        // 우선순위 정보
        result.put("priority", "1. TAGO API → 2. ODsay API → 3. Python Crawler");
        result.put("timestamp", new java.util.Date());
        
        return result;
    }
    
    /**
     * API 순서 테스트 (실제 서비스처럼)
     */
    @GetMapping("/sequence")
    @ResponseBody
    public Map<String, Object> testApiSequence(
            @RequestParam(defaultValue = "서울") String departure,
            @RequestParam(defaultValue = "부산") String arrival,
            @RequestParam(defaultValue = "20240925") String date) {
        
        Map<String, Object> result = new HashMap<>();
        result.put("departure", departure);
        result.put("arrival", arrival);
        result.put("date", date);
        
        // 1순위: TAGO API
        try {
            if (taGoApiUtil != null && taGoApiUtil.checkApiStatus()) {
                List<TrainSearchVO> trains = taGoApiUtil.searchTrains(departure, arrival, date);
                if (!trains.isEmpty()) {
                    result.put("success", true);
                    result.put("source", "TAGO API");
                    result.put("count", trains.size());
                    result.put("trains", trains);
                    return result;
                }
            }
            result.put("tago_tried", true);
            result.put("tago_result", "no_data");
        } catch (Exception e) {
            result.put("tago_error", e.getMessage());
        }
        
        // 모든 API 실패
        result.put("success", false);
        result.put("source", "none");
        result.put("message", "모든 API에서 데이터를 가져올 수 없습니다");
        
        return result;
    }
}