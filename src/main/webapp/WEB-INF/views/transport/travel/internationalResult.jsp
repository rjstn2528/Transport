<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>해외여행 검색 결과 - Hee Transport</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        .travel-header {
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
            width: 50px;
            height: 50px;
            border-radius: 50%;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            display: flex;
            align-items: center;
            justify-content: center;
            font-weight: bold;
            font-size: 1.2rem;
        }
        .flight-time {
            font-size: 1.3rem;
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
            font-size: 1.2rem;
        }
        .best-price {
            background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
        }
        .departure-badge {
            background: #f8f9fa;
            border: 1px solid #dee2e6;
            color: #495057;
            padding: 0.25rem 0.75rem;
            border-radius: 15px;
            font-size: 0.8rem;
            font-weight: 500;
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
        .destination-header {
            background: white;
            border-radius: 15px;
            padding: 1.5rem;
            box-shadow: 0 5px 15px rgba(0,0,0,0.08);
            margin-bottom: 2rem;
            text-align: center;
        }
        .destination-flag {
            font-size: 3rem;
            margin-bottom: 1rem;
        }
        .cheapest-price {
            color: #28a745;
            font-weight: bold;
            font-size: 1.1rem;
        }
        .airport-group {
            background: #f8f9fa;
            border-radius: 10px;
            padding: 1rem;
            margin-bottom: 1rem;
        }
    </style>
</head>
<body>
    <!-- 헤더 -->
    <div class="travel-header">
        <div class="container">
            <div class="row align-items-center">
                <div class="col-md-8">
                    <h2><i class="fas fa-globe-asia"></i> 해외여행 검색 결과</h2>
                    <c:if test="${not empty searchParams}">
                        <p class="mb-0">
                            <strong>${searchParams.destination}</strong>행 | 
                            ${searchParams.departureDate} | ${searchParams.adults}명
                            <c:if test="${searchParams.departureRegion != 'all'}">
                                | ${searchParams.departureRegion} 출발
                            </c:if>
                        </p>
                    </c:if>
                </div>
                <div class="col-md-4 text-end">
                    <a href="${pageContext.request.contextPath}/travel/international" class="btn btn-light">
                        <i class="fas fa-search"></i> 새로 검색
                    </a>
                </div>
            </div>
        </div>
    </div>

    <div class="container">
        <!-- 에러 메시지 -->
        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger">
                <i class="fas fa-exclamation-triangle"></i> ${errorMessage}
            </div>
        </c:if>

        <!-- 목적지 정보 헤더 -->
        <c:if test="${not empty searchParams}">
            <div class="destination-header">
                <c:choose>
                    <c:when test="${searchParams.destination == '도쿄'}">
                        <div class="destination-flag">🇯🇵</div>
                        <h3>${searchParams.destination} (일본)</h3>
                        <p class="text-muted">나리타국제공항 (NRT) / 하네다공항 (HND)</p>
                    </c:when>
                    <c:when test="${searchParams.destination == '방콕'}">
                        <div class="destination-flag">🇹🇭</div>
                        <h3>${searchParams.destination} (태국)</h3>
                        <p class="text-muted">수완나품국제공항 (BKK) / 돈무앙국제공항 (DMK)</p>
                    </c:when>
                    <c:when test="${searchParams.destination == '싱가포르'}">
                        <div class="destination-flag">🇸🇬</div>
                        <h3>${searchParams.destination}</h3>
                        <p class="text-muted">창이공항 (SIN)</p>
                    </c:when>
                    <c:otherwise>
                        <div class="destination-flag">🌍</div>
                        <h3>${searchParams.destination}</h3>
                    </c:otherwise>
                </c:choose>
                
                <c:if test="${resultCount > 0}">
                    <div class="mt-3">
                        <span class="badge bg-success">${resultCount}개 항공편 발견</span>
                        <c:if test="${not empty flights}">
                            <span class="cheapest-price ms-3">
                                최저가: <fmt:formatNumber value="${flights[0].price}" pattern="#,###"/>원부터
                            </span>
                        </c:if>
                    </div>
                </c:if>
            </div>
        </c:if>

        <!-- 검색 정보 -->
        <c:if test="${not empty searchParams}">
            <div class="search-info">
                <div class="row align-items-center">
                    <div class="col-md-8">
                        <i class="fas fa-info-circle text-primary"></i>
                        <strong>검색 조건:</strong> 
                        ${searchParams.destination}행, ${searchParams.departureDate}, 승객 ${searchParams.adults}명
                        <c:choose>
                            <c:when test="${searchParams.departureRegion == 'all'}">
                                (전국 모든 공항에서 검색)
                            </c:when>
                            <c:when test="${searchParams.departureRegion == 'seoul'}">
                                (서울/경기 지역에서 검색)
                            </c:when>
                            <c:when test="${searchParams.departureRegion == 'busan'}">
                                (부산/경남 지역에서 검색)
                            </c:when>
                            <c:when test="${searchParams.departureRegion == 'jeju'}">
                                (제주에서 검색)
                            </c:when>
                        </c:choose>
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

        <!-- 검색 결과 영역 -->
        <div class="row">
            <div class="col-12">
                <!-- 검색 결과 없음 -->
                <c:if test="${!hasResults or empty flights}">
                    <div class="no-results">
                        <i class="fas fa-plane-slash fa-3x text-muted mb-3"></i>
                        <h4>검색 결과가 없습니다</h4>
                        <p class="text-muted">
                            선택하신 목적지로 가는 항공편이 없습니다.<br>
                            다른 날짜나 출발 지역을 시도해보세요.
                        </p>
                        <a href="${pageContext.request.contextPath}/travel/international" class="btn btn-primary">
                            <i class="fas fa-search"></i> 다시 검색
                        </a>
                    </div>
                </c:if>

                <!-- 항공편 목록 -->
                <c:if test="${hasResults and not empty flights}">
                    <div id="flightList">
                        <c:forEach var="flight" items="${flights}" varStatus="status">
                            <div class="flight-card" data-price="${flight.price}" data-airline="${flight.airlineCode}" data-departure="${flight.departureAirport}">
                                <div class="card-body p-3">
                                    <div class="row align-items-center">
                                        <!-- 항공사 정보 -->
                                        <div class="col-md-2 text-center">
                                            <div class="airline-logo mb-2">
                                                ${fn:substring(flight.airlineCode, 0, 2)}
                                            </div>
                                            <div class="fw-bold small">${flight.airlineName}</div>
                                            <div class="text-muted small">${flight.flightNumber}</div>
                                            <div class="departure-badge mt-2">${flight.departureAirport}</div>
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
                                                    <div class="text-muted small">
                                                        <c:choose>
                                                            <c:when test="${flight.departureAirport == 'ICN'}">인천</c:when>
                                                            <c:when test="${flight.departureAirport == 'GMP'}">김포</c:when>
                                                            <c:when test="${flight.departureAirport == 'PUS'}">부산</c:when>
                                                            <c:when test="${flight.departureAirport == 'CJU'}">제주</c:when>
                                                            <c:otherwise>${flight.departureAirport}</c:otherwise>
                                                        </c:choose>
                                                    </div>
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
                                                    <div class="text-muted small">${searchParams.destination}</div>
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
                                                        <c:otherwise>이코노미</c:otherwise>
                                                    </c:choose>
                                                </span>
                                            </div>
                                            <div class="small text-muted">
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
                                            <div class="price-tag mb-2 <c:if test='${status.index == 0}'>best-price</c:if>">
                                                <c:choose>
                                                    <c:when test="${flight.price > 0}">
                                                        <fmt:formatNumber value="${flight.price}" pattern="#,###"/>원
                                                    </c:when>
                                                    <c:otherwise>
                                                        요금문의
                                                    </c:otherwise>
                                                </c:choose>
                                                <c:if test="${status.index == 0}">
                                                    <br><small style="font-size: 0.7rem;">최저가</small>
                                                </c:if>
                                            </div>
                                            <button class="btn btn-book btn-sm" onclick="bookFlight('${flight.flightNumber}', '${flight.departureAirport}')">
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
                                                <h6><i class="fas fa-info-circle"></i> 항공편 상세</h6>
                                                <ul class="list-unstyled small">
                                                    <li><strong>항공사:</strong> ${flight.airlineName} (${flight.airlineCode})</li>
                                                    <li><strong>편명:</strong> ${flight.flightNumber}</li>
                                                    <li><strong>출발공항:</strong> 
                                                        <c:choose>
                                                            <c:when test="${flight.departureAirport == 'ICN'}">인천국제공항</c:when>
                                                            <c:when test="${flight.departureAirport == 'GMP'}">김포국제공항</c:when>
                                                            <c:when test="${flight.departureAirport == 'PUS'}">김해국제공항</c:when>
                                                            <c:when test="${flight.departureAirport == 'CJU'}">제주국제공항</c:when>
                                                            <c:otherwise>${flight.departureAirport}</c:otherwise>
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
                                                    <li><strong>출발:</strong> ${flight.departureTime}</li>
                                                    <li><strong>도착:</strong> ${flight.arrivalTime}</li>
                                                    <li><strong>승객:</strong> ${flight.adults}명</li>
                                                    <li><strong>목적지:</strong> ${searchParams.destination}</li>
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

        <!-- 추가 검색 제안 -->
        <c:if test="${hasResults and not empty flights}">
            <div class="row mt-4">
                <div class="col-12">
                    <div class="search-info">
                        <div class="row align-items-center">
                            <div class="col-md-8">
                                <h6><i class="fas fa-lightbulb text-warning"></i> 더 저렴한 항공편을 찾으시나요?</h6>
                                <p class="mb-0">날짜를 조정하거나 다른 출발 공항을 이용해보세요. 평일이나 새벽 시간대가 더 저렴할 수 있어요.</p>
                            </div>
                            <div class="col-md-4 text-end">
                                <a href="${pageContext.request.contextPath}/travel/international" class="btn btn-outline-primary">
                                    <i class="fas fa-calendar-alt"></i> 날짜 변경
                                </a>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </c:if>

        <!-- 공항별 그룹화 (옵션) -->
        <c:if test="${hasResults and resultCount > 5}">
            <div class="row mt-4">
                <div class="col-12">
                    <h5><i class="fas fa-map-marker-alt"></i> 출발 공항별 보기</h5>
                    <div class="row">
                        <div class="col-md-3">
                            <div class="airport-group">
                                <h6><i class="fas fa-plane-departure"></i> 인천공항 (ICN)</h6>
                                <p class="small text-muted">가장 많은 노선과 시간대</p>
                                <button class="btn btn-sm btn-outline-primary" onclick="filterByAirport('ICN')">
                                    인천 출발편만 보기
                                </button>
                            </div>
                        </div>
                        <div class="col-md-3">
                            <div class="airport-group">
                                <h6><i class="fas fa-plane-departure"></i> 김포공항 (GMP)</h6>
                                <p class="small text-muted">도심 접근성이 좋음</p>
                                <button class="btn btn-sm btn-outline-primary" onclick="filterByAirport('GMP')">
                                    김포 출발편만 보기
                                </button>
                            </div>
                        </div>
                        <div class="col-md-3">
                            <div class="airport-group">
                                <h6><i class="fas fa-plane-departure"></i> 김해공항 (PUS)</h6>
                                <p class="small text-muted">부산/경남 지역</p>
                                <button class="btn btn-sm btn-outline-primary" onclick="filterByAirport('PUS')">
                                    부산 출발편만 보기
                                </button>
                            </div>
                        </div>
                        <div class="col-md-3">
                            <div class="airport-group">
                                <h6><i class="fas fa-plane-departure"></i> 제주공항 (CJU)</h6>
                                <p class="small text-muted">제주 지역</p>
                                <button class="btn btn-sm btn-outline-primary" onclick="filterByAirport('CJU')">
                                    제주 출발편만 보기
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </c:if>
    </div>

    <!-- 푸터 -->
    <footer class="bg-light mt-5 py-4">
        <div class="container text-center">
            <p class="text-muted mb-0">
                <i class="fas fa-globe-asia"></i> Hee Transport - 통합 교통 조회 시스템 | 
                <a href="${pageContext.request.contextPath}/">홈</a> | 
                <a href="${pageContext.request.contextPath}/travel/domestic">국내여행</a> | 
                <a href="${pageContext.request.contextPath}/travel/international" class="text-primary">해외여행</a>
            </p>
            <small class="text-muted">
                실시간 항공편 정보는 AMADEUS API를 통해 제공됩니다.
            </small>
        </div>
    </footer>

    <!-- JavaScript -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // 공항별 필터링
        function filterByAirport(airportCode) {
            const flightCards = document.querySelectorAll('.flight-card');
            
            flightCards.forEach(card => {
                if (card.dataset.departure === airportCode) {
                    card.style.display = 'block';
                } else {
                    card.style.display = 'none';
                }
            });
        }

        // 항공편 예약
        function bookFlight(flightNumber, departureAirport) {
            const airportNames = {
                'ICN': '인천국제공항',
                'GMP': '김포국제공항', 
                'PUS': '김해국제공항',
                'CJU': '제주국제공항'
            };
            
            const message = `${airportNames[departureAirport]}에서 출발하는 ${flightNumber}편을 예약하시겠습니까?\n항공사 예약 사이트로 이동합니다.`;
            
            if (confirm(message)) {
                window.open('https://www.google.com/flights', '_blank');
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
                window.location.href = '${pageContext.request.contextPath}/travel/international';
            }
            
            // 숫자 키로 공항 필터링
            const airportMap = { '1': 'ICN', '2': 'GMP', '3': 'PUS', '4': 'CJU' };
            if (airportMap[e.key]) {
                filterByAirport(airportMap[e.key]);
            }
        });
    </script>
</body>
</html>