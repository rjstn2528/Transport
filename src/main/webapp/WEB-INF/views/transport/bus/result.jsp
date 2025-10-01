<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>버스 조회 결과 - Hee Transport</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        .bus-header {
            background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
            color: white;
            padding: 2rem 0;
            margin-bottom: 2rem;
        }
        .search-info {
            background: rgba(255,255,255,0.1);
            padding: 1rem;
            border-radius: 10px;
            margin-top: 1rem;
        }
        .route-info {
            font-size: 1.5rem;
            font-weight: bold;
            display: flex;
            align-items: center;
            gap: 1rem;
            justify-content: center;
            flex-wrap: wrap;
        }
        .date-info {
            font-size: 1.1rem;
            opacity: 0.9;
            text-align: center;
            margin-top: 0.5rem;
        }
        .result-summary {
            background: white;
            padding: 1.5rem;
            border-radius: 12px;
            margin-bottom: 2rem;
            text-align: center;
            box-shadow: 0 5px 15px rgba(0,0,0,0.1);
        }
        .bus-item {
            background: white;
            border-radius: 15px;
            margin-bottom: 1.5rem;
            overflow: hidden;
            transition: all 0.3s;
            box-shadow: 0 5px 15px rgba(0,0,0,0.08);
        }
        .bus-item:hover {
            transform: translateY(-3px);
            box-shadow: 0 10px 25px rgba(0,0,0,0.15);
        }
        .bus-header-info {
            background: #f8f9fa;
            padding: 1.5rem;
            border-bottom: 1px solid #e0e0e0;
            display: flex;
            justify-content: space-between;
            align-items: center;
            flex-wrap: wrap;
            gap: 1rem;
        }
        .bus-info {
            display: flex;
            align-items: center;
            gap: 1rem;
        }
        .bus-type {
            background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
            color: white;
            padding: 0.5rem 1rem;
            border-radius: 20px;
            font-weight: bold;
            font-size: 0.9rem;
        }
        .bus-type.intercity {
            background: linear-gradient(135deg, #20c997 0%, #17a2b8 100%);
        }
        .bus-company {
            color: #6c757d;
            font-size: 1.1rem;
            font-weight: 500;
        }
        .bus-grade {
            padding: 0.4rem 0.8rem;
            border-radius: 15px;
            font-size: 0.8rem;
            font-weight: bold;
        }
        .grade-premium {
            background: #ffd700;
            color: #856404;
        }
        .grade-first {
            background: #e7f3ff;
            color: #0056b3;
        }
        .grade-normal {
            background: #e9ecef;
            color: #495057;
        }
        .seat-status {
            padding: 0.5rem 1rem;
            border-radius: 20px;
            font-size: 0.9rem;
            font-weight: bold;
        }
        .seat-available {
            background: #d4edda;
            color: #155724;
        }
        .seat-limited {
            background: #fff3cd;
            color: #856404;
        }
        .seat-soldout {
            background: #f8d7da;
            color: #721c24;
        }
        .bus-details {
            padding: 2rem;
            display: grid;
            grid-template-columns: 1fr auto 1fr;
            gap: 2rem;
            align-items: center;
        }
        .terminal-info {
            text-align: center;
        }
        .time {
            font-size: 1.8rem;
            font-weight: 600;
            color: #2c3e50;
            margin-bottom: 0.5rem;
        }
        .terminal {
            color: #6c757d;
            font-size: 1rem;
            font-weight: 500;
        }
        .duration-info {
            text-align: center;
            padding: 1rem;
            background: #f8f9fa;
            border-radius: 10px;
        }
        .duration {
            font-size: 1.1rem;
            color: #28a745;
            font-weight: bold;
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 0.5rem;
        }
        .route-visual {
            display: flex;
            align-items: center;
            gap: 0.5rem;
            margin: 0.5rem 0;
        }
        .route-line {
            flex: 1;
            height: 2px;
            background: linear-gradient(90deg, #28a745 0%, #20c997 100%);
            position: relative;
        }
        .route-line::after {
            content: '🚌';
            position: absolute;
            right: -10px;
            top: -8px;
            font-size: 0.8rem;
        }
        .price-section {
            padding: 1.5rem;
            background: #f8f9fa;
            border-top: 1px solid #e0e0e0;
            text-align: center;
        }
        .price-info {
            display: inline-flex;
            align-items: center;
            gap: 1rem;
            background: white;
            padding: 1rem 1.5rem;
            border-radius: 10px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.05);
        }
        .price-label {
            font-size: 0.9rem;
            color: #6c757d;
        }
        .price-value {
            font-size: 1.3rem;
            font-weight: bold;
            color: #28a745;
        }
        .no-result {
            text-align: center;
            padding: 3rem;
            background: white;
            border-radius: 15px;
            box-shadow: 0 10px 25px rgba(0,0,0,0.1);
            color: #6c757d;
        }
        .no-result i {
            font-size: 4rem;
            margin-bottom: 1rem;
        }
        @media (max-width: 768px) {
            .bus-details {
                grid-template-columns: 1fr;
                gap: 1.5rem;
                text-align: center;
            }
            .bus-header-info {
                flex-direction: column;
                text-align: center;
            }
        }
    </style>
</head>
<body>
    <!-- 헤더 -->
    <div class="bus-header">
        <div class="container">
            <div class="row align-items-center">
                <div class="col-md-8">
                    <h2><i class="fas fa-bus"></i> 버스 조회 결과</h2>
                    <div class="search-info">
                        <div class="route-info">
                            <span><i class="fas fa-map-marker-alt"></i> ${departureTerminal}</span>
                            <i class="fas fa-arrow-right"></i>
                            <span><i class="fas fa-flag-checkered"></i> ${arrivalTerminal}</span>
                        </div>
                        <div class="date-info">
                            <i class="fas fa-calendar-alt"></i> ${searchDate}
                        </div>
                    </div>
                </div>
                <div class="col-md-4 text-end">
                    <a href="${pageContext.request.contextPath}/transport/bus" class="btn btn-light">
                        <i class="fas fa-search"></i> 새로 검색
                    </a>
                </div>
            </div>
        </div>
    </div>

    <div class="container">
        <!-- 검색 결과 요약 -->
        <div class="result-summary">
            <h4>
                <i class="fas fa-list"></i> 
                총 <span class="text-success">${resultCount}개</span>의 버스편을 찾았습니다
            </h4>
            <p class="mb-0 text-muted">
                <i class="fas fa-clock"></i> 고속버스 & 시외버스 통합 조회 결과입니다
            </p>
        </div>

        <!-- 버스 목록 또는 결과 없음 -->
        <div class="row">
            <div class="col-12">
                <c:choose>
                    <c:when test="${empty busList}">
                        <div class="no-result">
                            <i class="fas fa-bus"></i>
                            <h4>조회된 버스편이 없습니다</h4>
                            <p class="text-muted">
                                다른 날짜나 터미널을 선택해보세요.
                            </p>
                            <a href="${pageContext.request.contextPath}/transport/bus" class="btn btn-success mt-3">
                                <i class="fas fa-redo"></i> 다시 조회하기
                            </a>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div id="busList">
                            <c:forEach var="bus" items="${busList}" varStatus="status">
                                <div class="bus-item">
                                    <div class="bus-header-info">
                                        <div class="bus-info">
                                            <span class="bus-type <c:if test='${bus.busType eq "시외버스"}'>intercity</c:if>">
                                                ${bus.busType}
                                            </span>
                                            <span class="bus-company">${bus.busCompany}</span>
                                            <span class="bus-grade 
                                                <c:choose>
                                                    <c:when test='${bus.busGrade eq "프리미엄"}'>grade-premium</c:when>
                                                    <c:when test='${bus.busGrade eq "우등"}'>grade-first</c:when>
                                                    <c:otherwise>grade-normal</c:otherwise>
                                                </c:choose>
                                            ">
                                                <c:choose>
                                                    <c:when test='${bus.busGrade eq "프리미엄"}'>👑</c:when>
                                                    <c:when test='${bus.busGrade eq "우등"}'>⭐</c:when>
                                                    <c:otherwise>🚌</c:otherwise>
                                                </c:choose>
                                                ${bus.busGrade}
                                            </span>
                                        </div>
                                        <div class="seat-status 
                                            <c:choose>
                                                <c:when test='${bus.remainingSeats eq "예약가능"}'>seat-available</c:when>
                                                <c:when test='${bus.remainingSeats eq "매진"}'>seat-soldout</c:when>
                                                <c:otherwise>seat-limited</c:otherwise>
                                            </c:choose>
                                        ">
                                            <i class="fas fa-chair"></i> ${bus.remainingSeats}
                                        </div>
                                    </div>
                                    
                                    <div class="bus-details">
                                        <div class="terminal-info">
                                            <div class="time">${bus.departureTime}</div>
                                            <div class="terminal">${bus.departureTerminal}</div>
                                        </div>
                                        <div class="duration-info">
                                            <div class="route-visual">
                                                <div class="route-line"></div>
                                            </div>
                                            <div class="duration">
                                                <i class="fas fa-clock"></i> ${bus.duration}
                                            </div>
                                        </div>
                                        <div class="terminal-info">
                                            <div class="time">${bus.arrivalTime}</div>
                                            <div class="terminal">${bus.arrivalTerminal}</div>
                                        </div>
                                    </div>
                                    
                                    <div class="price-section">
                                        <div class="price-info">
                                            <div class="price-label">
                                                <i class="fas fa-won-sign"></i> 요금
                                            </div>
                                            <div class="price-value">${bus.price}</div>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>

    <!-- 푸터 -->
    <footer class="bg-light mt-5 py-4">
        <div class="container text-center">
            <p class="text-muted mb-0">
                <i class="fas fa-bus"></i> Hee Transport - 통합 교통 조회 시스템 | 
                <a href="${pageContext.request.contextPath}/">홈</a> | 
                <a href="${pageContext.request.contextPath}/transport/train">기차표 조회</a> | 
                <a href="${pageContext.request.contextPath}/transport/bus" class="text-success">고속/시외버스</a> | 
                <a href="${pageContext.request.contextPath}/transport/flight">항공편</a>
            </p>
            <small class="text-muted">
                실시간 버스 정보는 고속버스 통합예매시스템 API를 통해 제공됩니다.
            </small>
        </div>
    </footer>

    <!-- JavaScript -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>