package net.koreate.transport.dao;

import net.koreate.transport.vo.TrainSearchVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TransportDAO {
    
    /**
     * 기차 조회 결과 저장 (IDENTITY 방식)
     */
    @Insert("INSERT INTO HEE_TRAIN_SEARCH (" +
            "TRAIN_TYPE, TRAIN_NUMBER, DEPARTURE_STATION, ARRIVAL_STATION, " +
            "DEPARTURE_TIME, ARRIVAL_TIME, DURATION, GENERAL_PRICE, SPECIAL_PRICE, " +
            "AVAILABILITY, SEARCH_DATE, CREATED_DATE" +
            ") VALUES (" +
            "#{trainType}, #{trainNumber}, #{departureStation}, #{arrivalStation}, " +
            "#{departureTime}, #{arrivalTime}, #{duration}, #{generalPrice}, #{specialPrice}, " +
            "#{availability}, #{searchDate}, SYSDATE" +
            ")")
    @Options(useGeneratedKeys = true, keyProperty = "searchId")
    void insertSearchResult(TrainSearchVO trainVO);
    
    /**
     * 최근 조회 결과 가져오기 (5분 이내)
     */
    @Select("SELECT " +
            "SEARCH_ID as searchId, TRAIN_TYPE as trainType, TRAIN_NUMBER as trainNumber, " +
            "DEPARTURE_STATION as departureStation, ARRIVAL_STATION as arrivalStation, " +
            "DEPARTURE_TIME as departureTime, ARRIVAL_TIME as arrivalTime, " +
            "DURATION as duration, GENERAL_PRICE as generalPrice, SPECIAL_PRICE as specialPrice, " +
            "AVAILABILITY as availability, SEARCH_DATE as searchDate, CREATED_DATE as createdDate " +
            "FROM HEE_TRAIN_SEARCH " +
            "WHERE DEPARTURE_STATION = #{departure} " +
            "AND ARRIVAL_STATION = #{arrival} " +
            "AND SEARCH_DATE = #{searchDate} " +
            "AND CREATED_DATE >= SYSDATE - INTERVAL '5' MINUTE " +
            "ORDER BY CREATED_DATE DESC, DEPARTURE_TIME ASC")
    List<TrainSearchVO> getRecentSearchResults(@Param("departure") String departure, 
                                              @Param("arrival") String arrival, 
                                              @Param("searchDate") String searchDate);
    
    /**
     * 오래된 캐시 데이터 삭제 (1일 이상)
     */
    @Delete("DELETE FROM HEE_TRAIN_SEARCH WHERE CREATED_DATE < SYSDATE - 1")
    void deleteOldCacheData();
    
    /**
     * 전체 검색 통계
     */
    @Select("SELECT COUNT(*) FROM HEE_TRAIN_SEARCH")
    int getTotalSearchCount();
    
    /**
     * 인기 노선 조회 (최근 7일)
     */
    @Select("SELECT " +
            "DEPARTURE_STATION as departureStation, " +
            "ARRIVAL_STATION as arrivalStation, " +
            "COUNT(*) as searchCount " +
            "FROM HEE_TRAIN_SEARCH " +
            "WHERE CREATED_DATE >= SYSDATE - 7 " +
            "GROUP BY DEPARTURE_STATION, ARRIVAL_STATION " +
            "ORDER BY COUNT(*) DESC " +
            "FETCH FIRST 10 ROWS ONLY")
    @Results({
        @Result(property = "departureStation", column = "departureStation"),
        @Result(property = "arrivalStation", column = "arrivalStation"),
        @Result(property = "searchCount", column = "searchCount")
    })
    List<TrainSearchVO> getPopularRoutes();
    
    /**
     * 특정 노선의 검색 횟수 조회
     */
    @Select("SELECT COUNT(*) FROM HEE_TRAIN_SEARCH " +
            "WHERE DEPARTURE_STATION = #{departure} " +
            "AND ARRIVAL_STATION = #{arrival} " +
            "AND CREATED_DATE >= SYSDATE - 7")
    int getRouteSearchCount(@Param("departure") String departure, 
                           @Param("arrival") String arrival);
    
    /**
     * 오늘 검색된 기차 정보 조회
     */
    @Select("SELECT " +
            "SEARCH_ID as searchId, TRAIN_TYPE as trainType, TRAIN_NUMBER as trainNumber, " +
            "DEPARTURE_STATION as departureStation, ARRIVAL_STATION as arrivalStation, " +
            "DEPARTURE_TIME as departureTime, ARRIVAL_TIME as arrivalTime, " +
            "DURATION as duration, GENERAL_PRICE as generalPrice, SPECIAL_PRICE as specialPrice, " +
            "AVAILABILITY as availability, SEARCH_DATE as searchDate, CREATED_DATE as createdDate " +
            "FROM HEE_TRAIN_SEARCH " +
            "WHERE TO_CHAR(CREATED_DATE, 'YYYYMMDD') = TO_CHAR(SYSDATE, 'YYYYMMDD') " +
            "ORDER BY CREATED_DATE DESC")
    List<TrainSearchVO> getTodaySearchResults();
}