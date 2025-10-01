<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>항공편 검색 결과 - Hee Transport</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        .flight-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 2rem 0;
            margin-bottom: 2rem;
        }
        .flight-card {
            border: none;
            border-radius: 15px;
            box-shadow: 0 5px 15px rgba(0,0,0,0.08);
            margin-bottom: 1rem;
            overflow: hidden;
            transition: all 0.3s;
        }
        .flight-card:hover {
            transform: translateY(-3px);
            box-shadow: 0 10px 25px rgba(0,0,0,0.15);
        }
        .airline-logo {
            width: 40px;
            height: 40px;
            border-radius: 50%;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            display: flex;
            align-items: center;
            justify-content: center;
            font-weight: bold;
        }
        .flight-time {
            font-size: 1.2rem;
            font-weight: 600;
            color: #2c3e50;
        }
        .flight-duration {
            color: #6c757d;
            font-size: 0.9rem;
        }
        .price-tag {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 0.5rem 1rem;
            border-radius: 25px;
            font-weight: 600;
            font-size: 1.1rem;
        }
        .seat-status {
            font-size: 0.85rem;
            padding: 0.25rem 0.5rem;
            border-radius: 15px;
        }
        .seat-available { background-color: #d4edda; color: #155724; }
        .seat-limited { background-color: #fff3cd; color: #856404; }
        .seat-full { background-color: #f8d7da; color: #721c24; }
        .search-info {
            background-color: #f8f9fa;
            border-radius: 10px;
            padding: 1rem;
            margin-bottom: 2rem;
        }
        .no-results {
            text-align: center;
            padding: 3rem;
            color: #6c757d;
        }
        .btn-book {
            background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
            border: none;
            color: white;
            padding: 0.5rem 1.5rem;
            border-radius: 20px;
            font-weight: 500;
            transition: all 0.3s;
        }
        .btn-book:hover {
            transform: translateY(-1px);
            box-shadow: 0 3px 10px rgba(40, 167, 69, 0.3);
            color: white;
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
            background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
            position: relative;
        }
        .route-line::after {
            content: '✈';
            position: absolute;
            right: -10px;
            top: -8px;
            color: #667eea;
            font-size: 0.8rem;
        }
        .airline-badge {
            font-size: 0.75rem;
            padding: 0.25rem 0.5rem;
            border-radius: 10px;
            background-color: #e9ecef;
            color: #495057;
        }
        .error-alert {
            background-color: #f8d7da;
            color: #721c24;
            padding: 1rem;
            border-radius: 0.375rem;
            border: 1px solid #f5c6cb;
            margin-bottom: 1rem;
        }
    </style>
</head>
<body>
    <!-- 헤더 -->
    <div class="flight-header">
        <div class="container">
            <div class="row align-items-center">
                <div class="col-md-8">
                    <h2><i class="fas fa-plane"></i> 항공편 검색 결과</h2>
                    <c:if test="${not empty searchParams}">
                        <p class="mb-0">
                            <strong>${searchParams.departureAirport}</strong> → <strong>${searchParams.arrivalAirport}</strong> | 
                            ${searchParams.searchDate} | ${searchParams.adults}명
                        </p>
                    </c:if>
                </div>
                <div class="col-md-4 text-end">
                    <a href="${pageContext.request.contextPath}/transport/flight" class="btn btn-light">
                        <i class="fas fa-search"></i> 새로 검색
                    </a>
                </div>
            </div>
        </div>
    </div>

    <div class="container">
        <!-- 에러 메시지 -->
        <c:if test="${not empty errorMessage}">
            <div class="error-alert">
                <i class="fas fa-exclamation-triangle"></i> ${errorMessage}
            </div>
        </c:if>

        <!-- 검색 정보 -->
        <c:if test="${not empty searchParams}">
            <div class="search-info">
                <div class="row align-items-center">
                    <div class="col-md-8">
                        <i class="fas fa-info-circle text-primary"></i>
                        <strong>검색 조건:</strong> 
                        ${searchParams.departureAirport} → ${searchParams.arrivalAirport}, 
                        ${searchParams.searchDate}, 승객 ${searchParams.adults}명
                        
                        <c:if test="${resultCount > 0}">
                            | <span class="text-success"><strong>${resultCount}개</strong>의 항공편을 찾았습니다</span>
                        </c:if>
                    </div>
                    <div class="col-md-4 text-end">
                        <small class="text-muted">
                            <i class="fas fa-clock"></i> 
                            <fmt:formatDate value="<%=new java.util.Date()%>" pattern="yyyy-MM-dd HH:mm"/> 기준
                        </small>
                    </div>
                </div>
            </div>
        </c:if>

        <!-- 항공편 검색 결과 영역 -->
        <div class="row">
            <div class="col-12">
                <!-- 검색 결과 없음 -->
                <c:if test="${noResults or empty flights}">
                    <div class="no-results">
                        <i class="fas fa-plane-slash fa-3x text-muted mb-3"></i>
                        <h4>검색 결과가 없습니다</h4>
                        <p class="text-muted">
                            검색 조건에 맞는 항공편이 없습니다.<br>
                            다른 날짜나 공항을 시도해보세요.
                        </p>
                        <a href="${pageContext.request.contextPath}/transport/flight" class="btn btn-primary">
                            <i class="fas fa-search"></i> 다시 검색
                        </a>
                    </div>
                </c:if>

                <!-- 항공편 목록 -->
                <c:if test="${hasResults and not empty flights}">
                    <div id="flightList">
                        <c:forEach var="flight" items="${flights}" varStatus="status">
                            <div class="flight-card" data-price="${flight.price}" data-airline="${flight.airlineCode}">
                                <div class="card-body p-3">
                                    <div class="row align-items-center">
                                        <!-- 항공사 정보 -->
                                        <div class="col-md-2 text-center">
                                            <div class="airline-logo mb-2">
                                                ${fn:substring(flight.airlineCode, 0, 2)}
                                            </div>
                                            <div class="airline-badge">
                                                <c:choose>
                                                    <c:when test="${not empty flight.airlineName}">
                                                        ${flight.airlineName}
                                                    </c:when>
                                                    <c:otherwise>
                                                        ${flight.airlineCode}
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                            <div class="text-muted small">${flight.flightNumber}</div>
                                        </div>

                                        <!-- 시간 정보 -->
                                        <div class="col-md-4">
                                            <div class="row">
                                                <div class="col-5">
                                                    <div class="flight-time">
                                                        <c:choose>
                                                            <c:when test="${fn:contains(flight.departureTime, ' ')}">
                                                                ${fn:substringAfter(flight.departureTime, ' ')}
                                                            </c:when>
                                                            <c:otherwise>
                                                                ${flight.departureTime}
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </div>
                                                    <div class="text-muted small">${flight.departureAirport}</div>
                                                </div>
                                                <div class="col-2 text-center">
                                                    <div class="route-visual">
                                                        <div class="route-line"></div>
                                                    </div>
                                                    <div class="flight-duration">
                                                        <c:choose>
                                                            <c:when test="${not empty flight.duration}">
                                                                ${flight.duration}
                                                            </c:when>
                                                            <c:otherwise>
                                                                -
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </div>
                                                </div>
                                                <div class="col-5 text-end">
                                                    <div class="flight-time">
                                                        <c:choose>
                                                            <c:when test="${fn:contains(flight.arrivalTime, ' ')}">
                                                                ${fn:substringAfter(flight.arrivalTime, ' ')}
                                                            </c:when>
                                                            <c:otherwise>
                                                                ${flight.arrivalTime}
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </div>
                                                    <div class="text-muted small">${flight.arrivalAirport}</div>
                                                </div>
                                            </div>
                                        </div>

                                        <!-- 좌석 및 등급 정보 -->
                                        <div class="col-md-3 text-center">
                                            <div class="mb-2">
                                                <span class="badge bg-secondary">
                                                    <c:choose>
                                                        <c:when test="${not empty flight.seatClass}">
                                                            <c:choose>
                                                                <c:when test="${flight.seatClass == 'ECONOMY'}">이코노미</c:when>
                                                                <c:when test="${flight.seatClass == 'BUSINESS'}">비즈니스</c:when>
                                                                <c:when test="${flight.seatClass == 'FIRST'}">퍼스트</c:when>
                                                                <c:otherwise>${flight.seatClass}</c:otherwise>
                                                            </c:choose>
                                                        </c:when>
                                                        <c:otherwise>일반</c:otherwise>
                                                    </c:choose>
                                                </span>
                                            </div>
                                            <div class="seat-status 
                                                <c:choose>
                                                    <c:when test="${fn:contains(flight.remainingSeats, '예약가능')}">seat-available</c:when>
                                                    <c:when test="${fn:contains(flight.remainingSeats, '잔여') or fn:contains(flight.remainingSeats, '석')}">seat-limited</c:when>
                                                    <c:otherwise>seat-full</c:otherwise>
                                                </c:choose>
                                            ">
                                                <i class="fas fa-chair"></i> 
                                                <c:choose>
                                                    <c:when test="${not empty flight.remainingSeats}">
                                                        ${flight.remainingSeats}
                                                    </c:when>
                                                    <c:otherwise>
                                                        예약가능
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                        </div>

                                        <!-- 가격 및 예약 -->
                                        <div class="col-md-3 text-center">
                                            <div class="price-tag mb-2">
                                                <c:choose>
                                                    <c:when test="${flight.price > 0}">
                                                        <fmt:formatNumber value="${flight.price}" pattern="#,###"/>원
                                                    </c:when>
                                                    <c:otherwise>
                                                        요금문의
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                            <button class="btn btn-book btn-sm" onclick="bookFlight('${flight.flightNumber}')">
                                                <i class="fas fa-ticket-alt"></i> 예약하기
                                            </button>
                                            <div class="text-muted small mt-1">
                                                <c:if test="${not empty flight.currency and flight.currency != 'KRW'}">
                                                    (${flight.currency})
                                                </c:if>
                                            </div>
                                        </div>
                                    </div>

                                    <!-- 추가 정보 (접기/펼치기) -->
                                    <div class="collapse" id="flightDetails${status.index}">
                                        <hr>
                                        <div class="row">
                                            <div class="col-md-6">
                                                <h6><i class="fas fa-info-circle"></i> 상세 정보</h6>
                                                <ul class="list-unstyled small">
                                                    <li><strong>항공사:</strong> 
                                                        <c:choose>
                                                            <c:when test="${not empty flight.airlineName}">
                                                                ${flight.airlineName} (${flight.airlineCode})
                                                            </c:when>
                                                            <c:otherwise>
                                                                ${flight.airlineCode}
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </li>
                                                    <li><strong>편명:</strong> ${flight.flightNumber}</li>
                                                    <li><strong>좌석등급:</strong> 
                                                        <c:choose>
                                                            <c:when test="${not empty flight.seatClass}">
                                                                <c:choose>
                                                                    <c:when test="${flight.seatClass == 'ECONOMY'}">이코노미</c:when>
                                                                    <c:when test="${flight.seatClass == 'BUSINESS'}">비즈니스</c:when>
                                                                    <c:when test="${flight.seatClass == 'FIRST'}">퍼스트</c:when>
                                                                    <c:otherwise>${flight.seatClass}</c:otherwise>
                                                                </c:choose>
                                                            </c:when>
                                                            <c:otherwise>일반</c:otherwise>
                                                        </c:choose>
                                                    </li>
                                                    <li><strong>소요시간:</strong> 
                                                        <c:choose>
                                                            <c:when test="${not empty flight.duration}">
                                                                ${flight.duration}
                                                            </c:when>
                                                            <c:otherwise>
                                                                -
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </li>
                                                </ul>
                                            </div>
                                            <div class="col-md-6">
                                                <h6><i class="fas fa-route"></i> 여행 정보</h6>
                                                <ul class="list-unstyled small">
                                                    <li><strong>출발:</strong> ${flight.departureAirport} ${flight.departureTime}</li>
                                                    <li><strong>도착:</strong> ${flight.arrivalAirport} ${flight.arrivalTime}</li>
                                                    <li><strong>승객:</strong> ${flight.adults}명</li>
                                                    <li><strong>검색일:</strong> ${flight.searchDate}</li>
                                                </ul>
                                            </div>
                                        </div>
                                    </div>

                                    <!-- 상세정보 토글 버튼 -->
                                    <div class="text-center mt-2">
                                        <button class="btn btn-link btn-sm text-decoration-none" 
                                                data-bs-toggle="collapse" 
                                                data-bs-target="#flightDetails${status.index}"
                                                aria-expanded="false">
                                            <i class="fas fa-chevron-down"></i> 상세 정보
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:if>
            </div>
        </div>
    </div>

    <!-- 푸터 -->
    <footer class="bg-light mt-5 py-4">
        <div class="container text-center">
            <p class="text-muted mb-0">
                <i class="fas fa-plane"></i> Hee Transport - 통합 교통 조회 시스템 | 
                <a href="${pageContext.request.contextPath}/">홈</a> | 
                <a href="${pageContext.request.contextPath}/transport/train">기차</a> | 
                <a href="${pageContext.request.contextPath}/transport/bus">버스</a> | 
                <a href="${pageContext.request.contextPath}/transport/flight" class="text-primary">항공편</a>
            </p>
            <small class="text-muted">
                실시간 항공편 정보는 AMADEUS API를 통해 제공됩니다.
            </small>
        </div>
    </footer>

    <!-- 예약 확인 모달 -->
    <div class="modal fade" id="bookingModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title"><i class="fas fa-ticket-alt"></i> 항공편 예약</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <div class="alert alert-info">
                        <i class="fas fa-info-circle"></i>
                        현재는 검색 기능만 제공됩니다. 실제 예약은 해당 항공사 웹사이트에서 진행해주세요.
                    </div>
                    <div id="selectedFlightInfo"></div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">닫기</button>
                    <button type="button" class="btn btn-primary" onclick="goToAirlineWebsite()">
                        <i class="fas fa-external-link-alt"></i> 항공사 사이트로 이동
                    </button>
                </div>
            </div>
        </div>
    </div>

    <!-- JavaScript -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        let selectedFlight = null;

        // 항공편 예약 함수
        function bookFlight(flightNumber) {
            // 선택된 항공편 정보 찾기
            const flights = [
                <c:forEach var="flight" items="${flights}" varStatus="status">
                {
                    flightNumber: '${flight.flightNumber}',
                    airlineName: '${not empty flight.airlineName ? flight.airlineName : flight.airlineCode}',
                    airlineCode: '${flight.airlineCode}',
                    route: '${flight.departureAirport} → ${flight.arrivalAirport}',
                    departureTime: '${flight.departureTime}',
                    arrivalTime: '${flight.arrivalTime}',
                    price: '<c:choose><c:when test="${flight.price > 0}"><fmt:formatNumber value="${flight.price}" pattern="#,###"/>원</c:when><c:otherwise>요금문의</c:otherwise></c:choose>',
                    seatClass: '<c:choose><c:when test="${not empty flight.seatClass}"><c:choose><c:when test="${flight.seatClass == 'ECONOMY'}">이코노미</c:when><c:when test="${flight.seatClass == 'BUSINESS'}">비즈니스</c:when><c:when test="${flight.seatClass == 'FIRST'}">퍼스트</c:when><c:otherwise>${flight.seatClass}</c:otherwise></c:choose></c:when><c:otherwise>일반</c:otherwise></c:choose>'
                }<c:if test="${!status.last}">,</c:if>
                </c:forEach>
            ];

            selectedFlight = flights.find(f => f.flightNumber === flightNumber);
            
            if (selectedFlight) {
                document.getElementById('selectedFlightInfo').innerHTML = `
                    <div class="card">
                        <div class="card-body">
                            <h6 class="card-title">${selectedFlight.airlineName} ${selectedFlight.flightNumber}</h6>
                            <p class="card-text">
                                <strong>노선:</strong> ${selectedFlight.route}<br>
                                <strong>출발:</strong> ${selectedFlight.departureTime}<br>
                                <strong>도착:</strong> ${selectedFlight.arrivalTime}<br>
                                <strong>좌석등급:</strong> ${selectedFlight.seatClass}<br>
                                <strong>가격:</strong> <span class="text-primary fw-bold">${selectedFlight.price}</span>
                            </p>
                        </div>
                    </div>
                `;
                
                const modal = new bootstrap.Modal(document.getElementById('bookingModal'));
                modal.show();
            }
        }

        // 항공사 웹사이트로 이동
        function goToAirlineWebsite() {
            if (selectedFlight) {
                const airlineUrls = {
                    'KE': 'https://www.koreanair.com',
                    'OZ': 'https://flyasiana.com',
                    'LJ': 'https://www.jinair.com',
                    'TW': 'https://www.twayair.com',
                    'ZE': 'https://www.eastarjet.com',
                    'BX': 'https://www.airbusan.com',
                    'JL': 'https://www.jal.co.jp',
                    'NH': 'https://www.ana.co.jp',
                    'SQ': 'https://www.singaporeair.com',
                    'TG': 'https://www.thaiairways.com'
                };
                
                const url = airlineUrls[selectedFlight.airlineCode] || 'https://www.google.com/flights';
                window.open(url, '_blank');
            }
        }

        document.addEventListener('DOMContentLoaded', function() {
            // 상세 정보 토글 이벤트
            document.querySelectorAll('[data-bs-toggle="collapse"]').forEach(button => {
                button.addEventListener('click', function() {
                    const icon = this.querySelector('i');
                    const isExpanded = this.getAttribute('aria-expanded') === 'true';
                    
                    if (isExpanded) {
                        icon.classList.remove('fa-chevron-down');
                        icon.classList.add('fa-chevron-up');
                    } else {
                        icon.classList.remove('fa-chevron-up');
                        icon.classList.add('fa-chevron-down');
                    }
                });
            });
        });

        // 키보드 단축키
        document.addEventListener('keydown', function(e) {
            // Ctrl + F: 새로 검색
            if (e.ctrlKey && e.key === 'f') {
                e.preventDefault();
                window.location.href = '${pageContext.request.contextPath}/transport/flight';
            }
        });
    </script>
</body>
</html>