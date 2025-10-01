package net.koreate.transport.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import net.koreate.transport.vo.FlightSearchVO;

/**
 * Flight Data Access Object - JDK 11 Compatible Version
 */
@Repository
public class FlightDAO {
    
    private static final Logger logger = LoggerFactory.getLogger(FlightDAO.class);
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    /**
     * Get cached flight data within 5 minutes
     */
    public List<FlightSearchVO> getCachedFlights(String departureAirport, String arrivalAirport, String searchDate) {
        String sql = "SELECT SEARCH_ID, AIRLINE_CODE, AIRLINE_NAME, FLIGHT_NUMBER, " +
                   "DEPARTURE_AIRPORT, ARRIVAL_AIRPORT, DEPARTURE_TIME, ARRIVAL_TIME, " +
                   "DURATION, PRICE, CURRENCY, SEAT_CLASS, REMAINING_SEATS, " +
                   "SEARCH_DATE, ADULTS, CREATED_DATE " +
                   "FROM HEE_FLIGHT_SEARCH " +
                   "WHERE DEPARTURE_AIRPORT = ? " +
                   "AND ARRIVAL_AIRPORT = ? " +
                   "AND SEARCH_DATE = ? " +
                   "AND CREATED_DATE >= SYSDATE - INTERVAL '5' MINUTE " +
                   "ORDER BY DEPARTURE_TIME ASC";
        
        try {
            List<FlightSearchVO> flights = jdbcTemplate.query(sql, new FlightRowMapper(), 
                                                            departureAirport, arrivalAirport, searchDate);
            
            if (flights != null && !flights.isEmpty()) {
                logger.info("Cached flight data found: {} records ({} -> {})", 
                           flights.size(), departureAirport, arrivalAirport);
            }
            
            return flights;
            
        } catch (EmptyResultDataAccessException e) {
            logger.debug("No cached flight data: {} -> {}", departureAirport, arrivalAirport);
            return null;
        } catch (Exception e) {
            logger.error("Flight cache query error: ", e);
            return null;
        }
    }
    
    /**
     * Save flight search results to cache
     */
    public int saveFlightCache(List<FlightSearchVO> flights) {
        if (flights == null || flights.isEmpty()) {
            logger.warn("No flight data to save");
            return 0;
        }
        
        String sql = "INSERT INTO HEE_FLIGHT_SEARCH (" +
                   "AIRLINE_CODE, AIRLINE_NAME, FLIGHT_NUMBER, " +
                   "DEPARTURE_AIRPORT, ARRIVAL_AIRPORT, DEPARTURE_TIME, ARRIVAL_TIME, " +
                   "DURATION, PRICE, CURRENCY, SEAT_CLASS, REMAINING_SEATS, " +
                   "SEARCH_DATE, ADULTS" +
                   ") VALUES (" +
                   "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?" +
                   ")";
        
        int savedCount = 0;
        
        for (FlightSearchVO flight : flights) {
            try {
                int result = jdbcTemplate.update(sql,
                    flight.getAirlineCode(),
                    flight.getAirlineName(), 
                    flight.getFlightNumber(),
                    flight.getDepartureAirport(),
                    flight.getArrivalAirport(),
                    flight.getDepartureTime(),
                    flight.getArrivalTime(),
                    flight.getDuration(),
                    flight.getPrice(),
                    flight.getCurrency(),
                    flight.getSeatClass(),
                    flight.getRemainingSeats(),
                    flight.getSearchDate(),
                    flight.getAdults()
                );
                
                if (result > 0) {
                    savedCount++;
                }
                
            } catch (Exception e) {
                logger.error("Flight cache save error (flight: {}): {}", flight.getFlightNumber(), e.getMessage());
            }
        }
        
        logger.info("Flight cache save completed: {}/{} records", savedCount, flights.size());
        return savedCount;
    }
    
    /**
     * Delete old cache data (older than 1 day)
     */
    public int cleanupOldCache() {
        String sql = "DELETE FROM HEE_FLIGHT_SEARCH WHERE CREATED_DATE < SYSDATE - 1";
        
        try {
            int deletedCount = jdbcTemplate.update(sql);
            if (deletedCount > 0) {
                logger.info("Old flight cache deleted: {} records", deletedCount);
            }
            return deletedCount;
            
        } catch (Exception e) {
            logger.error("Flight cache cleanup error: ", e);
            return 0;
        }
    }
    
    /**
     * Delete cache data for specific route
     */
    public int deleteCacheByRoute(String departureAirport, String arrivalAirport) {
        String sql = "DELETE FROM HEE_FLIGHT_SEARCH WHERE DEPARTURE_AIRPORT = ? AND ARRIVAL_AIRPORT = ?";
        
        try {
            int deletedCount = jdbcTemplate.update(sql, departureAirport, arrivalAirport);
            logger.info("Flight cache deleted: {} records ({} -> {})", deletedCount, departureAirport, arrivalAirport);
            return deletedCount;
            
        } catch (Exception e) {
            logger.error("Flight cache delete error: ", e);
            return 0;
        }
    }
    
    /**
     * Get total cache count
     */
    public int getCacheCount() {
        String sql = "SELECT COUNT(*) FROM HEE_FLIGHT_SEARCH";
        
        try {
            return jdbcTemplate.queryForObject(sql, Integer.class);
        } catch (Exception e) {
            logger.error("Flight cache count error: ", e);
            return 0;
        }
    }
    
    /**
     * Get recent search count (last 1 hour)
     */
    public int getRecentSearchCount() {
        String sql = "SELECT COUNT(*) FROM HEE_FLIGHT_SEARCH WHERE CREATED_DATE >= SYSDATE - INTERVAL '1' HOUR";
        
        try {
            return jdbcTemplate.queryForObject(sql, Integer.class);
        } catch (Exception e) {
            logger.error("Recent flight search count error: ", e);
            return 0;
        }
    }
    
    /**
     * Get popular routes statistics (last 24 hours)
     */
    public List<String> getPopularRoutes(int limit) {
        String baseSql = "SELECT DEPARTURE_AIRPORT || ' → ' || ARRIVAL_AIRPORT as route, " +
                       "COUNT(*) as search_count " +
                       "FROM HEE_FLIGHT_SEARCH " +
                       "WHERE CREATED_DATE >= SYSDATE - 1 " +
                       "GROUP BY DEPARTURE_AIRPORT, ARRIVAL_AIRPORT " +
                       "ORDER BY COUNT(*) DESC, DEPARTURE_AIRPORT ASC";
        
        String sql = baseSql;
        if (limit > 0) {
            sql = "SELECT * FROM (" + baseSql + ") WHERE ROWNUM <= " + limit;
        }
        
        try {
            return jdbcTemplate.queryForList(sql, String.class);
        } catch (Exception e) {
            logger.error("Popular flight routes query error: ", e);
            return null;
        }
    }
    
    /**
     * FlightSearchVO RowMapper
     */
    private static class FlightRowMapper implements RowMapper<FlightSearchVO> {
        @Override
        public FlightSearchVO mapRow(ResultSet rs, int rowNum) throws SQLException {
            FlightSearchVO flight = new FlightSearchVO();
            
            flight.setSearchId(rs.getLong("SEARCH_ID"));
            flight.setAirlineCode(rs.getString("AIRLINE_CODE"));
            flight.setAirlineName(rs.getString("AIRLINE_NAME"));
            flight.setFlightNumber(rs.getString("FLIGHT_NUMBER"));
            flight.setDepartureAirport(rs.getString("DEPARTURE_AIRPORT"));
            flight.setArrivalAirport(rs.getString("ARRIVAL_AIRPORT"));
            flight.setDepartureTime(rs.getString("DEPARTURE_TIME"));
            flight.setArrivalTime(rs.getString("ARRIVAL_TIME"));
            flight.setDuration(rs.getString("DURATION"));
            flight.setPrice(rs.getInt("PRICE"));
            flight.setCurrency(rs.getString("CURRENCY"));
            flight.setSeatClass(rs.getString("SEAT_CLASS"));
            flight.setRemainingSeats(rs.getString("REMAINING_SEATS"));
            flight.setSearchDate(rs.getString("SEARCH_DATE"));
            flight.setAdults(rs.getInt("ADULTS"));
            flight.setCreatedDate(rs.getTimestamp("CREATED_DATE"));
            
            return flight;
        }
    }
}