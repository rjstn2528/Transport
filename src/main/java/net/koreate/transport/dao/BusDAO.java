package net.koreate.transport.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import net.koreate.transport.vo.BusSearchVO;

@Mapper
public interface BusDAO {
    
    /**
     * 버스 조회 결과 저장
     */
    @Insert("INSERT INTO HEE_BUS_SEARCH (" +
            "BUS_TYPE, BUS_COMPANY, BUS_GRADE, DEPARTURE_TERMINAL, ARRIVAL_TERMINAL, " +
            "DEPARTURE_TIME, ARRIVAL_TIME, DURATION, PRICE, REMAINING_SEATS, " +
            "SEARCH_DATE, CREATED_DATE" +
            ") VALUES (" +
            "#{busType}, #{busCompany}, #{busGrade}, #{departureTerminal}, #{arrivalTerminal}, " +
            "#{departureTime}, #{arrivalTime}, #{duration}, #{price}, #{remainingSeats}, " +
            "#{searchDate}, SYSDATE" +
            ")")
    @Options(useGeneratedKeys = true, keyProperty = "searchId")
    void insertBusSearchResult(BusSearchVO busVO);

    /**
     * 최근 버스 조회 결과 가져오기 (5분 이내)
     */
    @Select("SELECT " +
            "SEARCH_ID as searchId, BUS_TYPE as busType, BUS_COMPANY as busCompany, " +
            "BUS_GRADE as busGrade, DEPARTURE_TERMINAL as departureTerminal, " +
            "ARRIVAL_TERMINAL as arrivalTerminal, DEPARTURE_TIME as departureTime, " +
            "ARRIVAL_TIME as arrivalTime, DURATION as duration, PRICE as price, " +
            "REMAINING_SEATS as remainingSeats, SEARCH_DATE as searchDate, " +
            "CREATED_DATE as createdDate " +
            "FROM HEE_BUS_SEARCH " +
            "WHERE DEPARTURE_TERMINAL = #{departureTerminal} " +
            "AND ARRIVAL_TERMINAL = #{arrivalTerminal} " +
            "AND SEARCH_DATE = #{searchDate} " +
            "AND CREATED_DATE >= SYSDATE - INTERVAL '5' MINUTE " +
            "ORDER BY CREATED_DATE DESC, DEPARTURE_TIME ASC")
    List<BusSearchVO> getRecentBusSearchResults(@Param("departureTerminal") String departureTerminal, 
                                               @Param("arrivalTerminal") String arrivalTerminal, 
                                               @Param("searchDate") String searchDate);

    /**
     * 오래된 버스 캐시 데이터 삭제 (1일 이상)
     */
    @Delete("DELETE FROM HEE_BUS_SEARCH WHERE CREATED_DATE < SYSDATE - 1")
    void deleteOldBusCacheData();

    /**
     * 전체 버스 검색 통계
     */
    @Select("SELECT COUNT(*) FROM HEE_BUS_SEARCH")
    int getTotalBusSearchCount();

    /**
     * 인기 버스 노선 조회 (최근 7일)
     */
    @Select("SELECT " +
            "DEPARTURE_TERMINAL as departureTerminal, " +
            "ARRIVAL_TERMINAL as arrivalTerminal, " +
            "BUS_TYPE as busType, " +
            "COUNT(*) as searchCount " +
            "FROM HEE_BUS_SEARCH " +
            "WHERE CREATED_DATE >= SYSDATE - 7 " +
            "GROUP BY DEPARTURE_TERMINAL, ARRIVAL_TERMINAL, BUS_TYPE " +
            "ORDER BY COUNT(*) DESC " +
            "FETCH FIRST 10 ROWS ONLY")
    @Results({
        @Result(property = "departureTerminal", column = "departureTerminal"),
        @Result(property = "arrivalTerminal", column = "arrivalTerminal"),
        @Result(property = "busType", column = "busType"),
        @Result(property = "searchCount", column = "searchCount")
    })
    List<BusSearchVO> getPopularBusRoutes();

    /**
     * 특정 버스 노선의 검색 횟수 조회
     */
    @Select("SELECT COUNT(*) FROM HEE_BUS_SEARCH " +
            "WHERE DEPARTURE_TERMINAL = #{departureTerminal} " +
            "AND ARRIVAL_TERMINAL = #{arrivalTerminal} " +
            "AND CREATED_DATE >= SYSDATE - 7")
    int getBusRouteSearchCount(@Param("departureTerminal") String departureTerminal, 
                              @Param("arrivalTerminal") String arrivalTerminal);

    /**
     * 오늘 검색된 버스 정보 조회
     */
    @Select("SELECT " +
            "SEARCH_ID as searchId, BUS_TYPE as busType, BUS_COMPANY as busCompany, " +
            "BUS_GRADE as busGrade, DEPARTURE_TERMINAL as departureTerminal, " +
            "ARRIVAL_TERMINAL as arrivalTerminal, DEPARTURE_TIME as departureTime, " +
            "ARRIVAL_TIME as arrivalTime, DURATION as duration, PRICE as price, " +
            "REMAINING_SEATS as remainingSeats, SEARCH_DATE as searchDate, " +
            "CREATED_DATE as createdDate " +
            "FROM HEE_BUS_SEARCH " +
            "WHERE TO_CHAR(CREATED_DATE, 'YYYYMMDD') = TO_CHAR(SYSDATE, 'YYYYMMDD') " +
            "ORDER BY CREATED_DATE DESC")
    List<BusSearchVO> getTodayBusSearchResults();

    /**
     * 버스 유형별 통계 조회
     */
    @Select("SELECT " +
            "BUS_TYPE as busType, " +
            "COUNT(*) as searchCount " +
            "FROM HEE_BUS_SEARCH " +
            "WHERE CREATED_DATE >= SYSDATE - 7 " +
            "GROUP BY BUS_TYPE " +
            "ORDER BY COUNT(*) DESC")
    @Results({
        @Result(property = "busType", column = "busType"),
        @Result(property = "searchCount", column = "searchCount")
    })
    List<BusSearchVO> getBusTypeStatistics();

    /**
     * 특정 기간 버스 검색 결과 조회
     */
    @Select("SELECT " +
            "SEARCH_ID as searchId, BUS_TYPE as busType, BUS_COMPANY as busCompany, " +
            "BUS_GRADE as busGrade, DEPARTURE_TERMINAL as departureTerminal, " +
            "ARRIVAL_TERMINAL as arrivalTerminal, DEPARTURE_TIME as departureTime, " +
            "ARRIVAL_TIME as arrivalTime, DURATION as duration, PRICE as price, " +
            "REMAINING_SEATS as remainingSeats, SEARCH_DATE as searchDate, " +
            "CREATED_DATE as createdDate " +
            "FROM HEE_BUS_SEARCH " +
            "WHERE CREATED_DATE >= TO_DATE(#{startDate}, 'YYYYMMDD') " +
            "AND CREATED_DATE < TO_DATE(#{endDate}, 'YYYYMMDD') + 1 " +
            "ORDER BY CREATED_DATE DESC")
    List<BusSearchVO> getBusSearchResultsByPeriod(@Param("startDate") String startDate, 
                                                  @Param("endDate") String endDate);

    /**
     * 터미널별 운행 회사 조회
     */
    @Select("SELECT DISTINCT " +
            "BUS_COMPANY as busCompany, " +
            "DEPARTURE_TERMINAL as departureTerminal " +
            "FROM HEE_BUS_SEARCH " +
            "WHERE DEPARTURE_TERMINAL = #{terminalName} " +
            "AND CREATED_DATE >= SYSDATE - 30 " +
            "ORDER BY BUS_COMPANY")
    @Results({
        @Result(property = "busCompany", column = "busCompany"),
        @Result(property = "departureTerminal", column = "departureTerminal")
    })
    List<BusSearchVO> getBusCompaniesByTerminal(@Param("terminalName") String terminalName);

    /**
     * 시간대별 버스 운행 통계
     */
    @Select("SELECT " +
            "SUBSTR(DEPARTURE_TIME, 1, 2) as hourGroup, " +
            "COUNT(*) as busCount " +
            "FROM HEE_BUS_SEARCH " +
            "WHERE CREATED_DATE >= SYSDATE - 7 " +
            "GROUP BY SUBSTR(DEPARTURE_TIME, 1, 2) " +
            "ORDER BY hourGroup")
    @Results({
        @Result(property = "hourGroup", column = "hourGroup"),
        @Result(property = "busCount", column = "busCount")
    })
    List<Map<String, Object>> getBusStatsByHour();

    /**
     * 가격대별 버스 분포 조회
     */
    @Select("SELECT " +
            "CASE " +
            "    WHEN CAST(REPLACE(REPLACE(PRICE, '원', ''), ',', '') AS NUMBER) < 20000 THEN '2만원 미만' " +
            "    WHEN CAST(REPLACE(REPLACE(PRICE, '원', ''), ',', '') AS NUMBER) < 30000 THEN '2-3만원' " +
            "    WHEN CAST(REPLACE(REPLACE(PRICE, '원', ''), ',', '') AS NUMBER) < 40000 THEN '3-4만원' " +
            "    ELSE '4만원 이상' " +
            "END as priceRange, " +
            "COUNT(*) as busCount " +
            "FROM HEE_BUS_SEARCH " +
            "WHERE PRICE IS NOT NULL " +
            "AND CREATED_DATE >= SYSDATE - 7 " +
            "GROUP BY " +
            "CASE " +
            "    WHEN CAST(REPLACE(REPLACE(PRICE, '원', ''), ',', '') AS NUMBER) < 20000 THEN '2만원 미만' " +
            "    WHEN CAST(REPLACE(REPLACE(PRICE, '원', ''), ',', '') AS NUMBER) < 30000 THEN '2-3만원' " +
            "    WHEN CAST(REPLACE(REPLACE(PRICE, '원', ''), ',', '') AS NUMBER) < 40000 THEN '3-4만원' " +
            "    ELSE '4만원 이상' " +
            "END " +
            "ORDER BY MIN(CAST(REPLACE(REPLACE(PRICE, '원', ''), ',', '') AS NUMBER))")
    List<Map<String, Object>> getBusStatsByPriceRange();

    /**
     * 터미널별 검색 통계
     */
    @Select("SELECT " +
            "DEPARTURE_TERMINAL as terminalName, " +
            "COUNT(*) as searchCount " +
            "FROM HEE_BUS_SEARCH " +
            "WHERE CREATED_DATE >= SYSDATE - 30 " +
            "GROUP BY DEPARTURE_TERMINAL " +
            "ORDER BY COUNT(*) DESC")
    @Results({
        @Result(property = "terminalName", column = "terminalName"),
        @Result(property = "searchCount", column = "searchCount")
    })
    List<Map<String, Object>> getSearchStatsByTerminal();
}