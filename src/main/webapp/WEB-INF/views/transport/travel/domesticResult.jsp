<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>국내여행 검색 결과 - Hee Transport</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        .travel-header {
            background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
            color: white;
            padding: 2rem 0;
            margin-bottom: 2rem;
        }
        .transport-tabs {
            display: flex;
            gap: 1rem;
            margin-bottom: 2rem;
            flex-wrap: wrap;
        }
        .transport-tab {
            padding: 1rem 1.5rem;
            border: 2px solid #4facfe;
            border-radius: 15px;
            background: white;
            color: #4facfe;
            cursor: pointer;
            transition: all 0.3s;
            flex: 1;
            min-width: 150px;
            text-align: center;
            position: relative;
        }
        .transport-tab.active {
            background: #4facfe;
            color: white;
        }
        .transport-tab .count-badge {
            position: absolute;
            top: -8px;
            right: -8px;
            background: #ff6b6b;
            color: white;
            border-radius: 50%;
            width: 24px;
            height: 24px;
            font-size: 0.8rem;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        .result-card {
            border: none;
            border-radius: 15px;
            box-shadow: 0 5px 15px rgba(0,0,0,0.08);
            margin-bottom: 1rem;
            overflow: hidden;
            transition: all 0.3s;
        }
        .result-card:hover {
            transform: translateY(-3px);
            box-shadow: 0 10px 25px rgba(0,0,0,0.15);
        }
        .transport-icon {
            width: 50px;
            height: 50px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-size: 1.5rem;
            margin-bottom: 1rem;
        }
        .flight-icon { background: linear-gradient(135deg, #fd7e14 0%, #fd9644 100%); }
        .train-icon { background: linear-gradient(135deg, #007bff 0%, #0d6efd 100%); }
        .bus-icon { background: linear-gradient(135deg, #28a745 0%, #20c997 100%); }
        .time-info {
            font-size: 1.2rem;
            font-weight: 600;
            color: #2c3e50;
        }
        .duration-info {
            color: #6c757d;
            font-size: 0.9rem;
        }
        .price-tag {
            background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
            color: white;
            padding: 0.5rem 1rem;
            border-radius: 25px;
            font-weight: 600;
            font-size: 1.1rem;
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
            background: linear-gradient(90deg, #4facfe 0%, #00f2fe 100%);
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
        .summary-stats {
            background: white;
            border-radius: 15px;
            padding: 1.5rem;
            box-shadow: 0 5px 15px rgba(0,0,0,0.08);
            margin-bottom: 2rem;
        }
        .stat-item {
            text-align: center;
            padding: 1rem;
        }
        .stat-number {
            font-size: 2rem;
            font-weight: bold;
            color: #4facfe;
        }
        .stat-label {
            font-size: 0.9rem;
            color: #6c757d;
        }
    </style>
</head>
<body>
    <!-- 헤더 -->
    <div class="travel-header">
        <div class="container">
            <div class="row align-items-center">
                <div class="col-md-8">
                    <h2><i class="fas fa-map-marked-alt"></i> 국내여행 검색 결과</h2>
                    <c:if test="${not empty searchParams}">
                        <p class="mb-0">
                            <strong>${searchParams.departure}</strong> → <strong>${searchParams.arrival}</strong> | 
                            ${searchParams.departureDate} | ${searchParams.adults}명
                        </p>
                    </c:if>
                </div>
                <div class="col-md-4 text-end">
                    <a href="${pageContext.request.contextPath}/travel/domestic" class="btn btn-light">
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

        <!-- 검색 정보 -->
        <c:if test="${not empty searchParams}">
            <div class="search-info">
                <div class="row align-items-center">
                    <div class="col-md-8">
                        <i class="fas fa-info-circle text-primary"></i>
                        <strong>검색 조건:</strong> 
                        ${searchParams.departure} → ${searchParams.arrival}, 
                        ${searchParams.departureDate}, 승객 ${searchParams.adults}명
                        
                        <c:if test="${totalCount > 0}">
                            | <span class="text-success"><strong>${totalCount}개</strong>의 교통편을 찾았습니다</span>
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

        <!-- 결과 요약 -->
        <c:if test="${hasResults}">
            <div class="summary-stats">
                <div class="row">
                    <div class="col-md-3">
                        <div class="stat-item">
                            <div class="stat-number">${searchResults.flightCount}</div>
                            <div class="stat-label">항공편</div>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="stat-item">
                            <div class="stat-number">${searchResults.trainCount}</div>
                            <div class="stat-label">기차/KTX</div>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="stat-item">
                            <div class="stat-number">${searchResults.busCount}</div>
                            <div class="stat-label">고속버스</div>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="stat-item">
                            <div class="stat-number">${totalCount}</div>
                            <div class="stat-label">전체</div>
                        </div>
                    </div>
                </div>
            </div>
        </c:if>

        <!-- 검색 결과 영역 -->
        <div class="row">
            <div class="col-12">
                <!-- 교통수단별 탭 -->
                <div class="transport-tabs">
                    <div class="transport-tab active" data-transport="all">
                        <i class="fas fa-th-list"></i>
                        <div>전체보기</div>
                        <span class="count-badge">${totalCount}</span>
                    </div>
                    <div class="transport-tab" data-transport="flight">
                        <i class="fas fa-plane"></i>
                        <div>항공편</div>
                        <c:if test="${searchResults.flightCount > 0}">
                            <span class="count-badge">${searchResults.flightCount}</span>
                        </c:if>
                    </div>
                    <div class="transport-tab" data-transport="train">
                        <i class="fas fa-train"></i>
                        <div>기차/KTX</div>
                        <c:if test="${searchResults.trainCount > 0}">
                            <span class="count-badge">${searchResults.trainCount}</span>
                        </c:if>
                    </div>
                    <div class="transport-tab" data-transport="bus">
                        <i class="fas fa-bus"></i>
                        <div>고속버스</div>
                        <c:if test="${searchResults.busCount > 0}">
                            <span class="count-badge">${searchResults.busCount}</span>
                        </c:if>
                    </div>
                </div>

                <!-- 검색 결과 없음 -->
                <c:if test="${!hasResults or totalCount == 0}">
                    <div class="no-results">
                        <i class="fas fa-search fa-3x text-muted mb-3"></i>
                        <h4>검색 결과가 없습니다</h4>
                        <p class="text-muted">
                            검색 조건에 맞는 교통편이 없습니다.<br>
                            다른 날짜나 경로를 시도해보세요.
                        </p>
                        <a href="${pageContext.request.contextPath}/travel/domestic" class="btn btn-primary">
                            <i class="fas fa-search"></i> 다시 검색
                        </a>
                    </div>
                </c:if>

                <!-- 검색 결과 목록 -->
                <c:if test="${hasResults and totalCount > 0}">
                    <div id="resultsList">
                        <!-- 항공편 결과 -->
                        <c:if test="${not empty searchResults.flights}">
                            <c:forEach var="flight" items="${searchResults.flights}" varStatus="status">
                                <div class="result-card" data-transport="flight" data-price="${flight.price}">
                                    <div class="card-body p-3">
                                        <div class="row align-items-center">
                                            <!-- 교통수단 정보 -->
                                            <div class="col-md-2 text-center">
                                                <div class="transport-icon flight-icon">
                                                    <i class="fas fa-plane"></i>
                                                </div>
                                                <div class="fw-bold">항공편</div>
                                                <div class="text-muted small">${flight.flightNumber}</div>
                                            </div>

                                            <!-- 시간 정보 -->
                                            <div class="col-md-4">
                                                <div class="row">
                                                    <div class="col-5">
                                                        <div class="time-info">${flight.departureTime}</div>
                                                        <div class="text-muted small">${flight.departureAirport}</div>
                                                    </div>
                                                    <div class="col-2 text-center">
                                                        <div class="route-visual">
                                                            <div class="route-line"></div>
                                                        </div>
                                                        <div class="duration-info">${flight.duration}</div>
                                                    </div>
                                                    <div class="col-5 text-end">
                                                        <div class="time-info">${flight.arrivalTime}</div>
                                                        <div class="text-muted small">${flight.arrivalAirport}</div>
                                                    </div>
                                                </div>
                                            </div>

                                            <!-- 추가 정보 -->
                                            <div class="col-md-3 text-center">
                                                <div class="mb-2">
                                                    <span class="badge bg-warning">${flight.airlineName}</span>
                                                </div>
                                                <div class="small text-muted">
                                                    좌석: ${flight.remainingSeats}
                                                </div>
                                            </div>

                                            <!-- 가격 및 예약 -->
                                            <div class="col-md-3 text-center">
                                                <div class="price-tag mb-2">
                                                    <fmt:formatNumber value="${flight.price}" pattern="#,###"/>원
                                                </div>
                                                <button class="btn btn-book btn-sm" onclick="bookTransport('flight', '${flight.flightNumber}')">
                                                    <i class="fas fa-ticket-alt"></i> 예약하기
                                                </button>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                        </c:if>

                        <!-- 기차 결과 -->
                        <c:if test="${not empty searchResults.trains}">
                            <c:forEach var="train" items="${searchResults.trains}" varStatus="status">
                                <div class="result-card" data-transport="train" data-price="${train.price}">
                                    <div class="card-body p-3">
                                        <div class="row align-items-center">
                                            <!-- 교통수단 정보 -->
                                            <div class="col-md-2 text-center">
                                                <div class="transport-icon train-icon">
                                                    <i class="fas fa-train"></i>
                                                </div>
                                                <div class="fw-bold">${train.trainType}</div>
                                                <div class="text-muted small">${train.trainNumber}</div>
                                            </div>

                                            <!-- 시간 정보 -->
                                            <div class="col-md-4">
                                                <div class="row">
                                                    <div class="col-5">
                                                        <div class="time-info">${train.departureTime}</div>
                                                        <div class="text-muted small">${train.departureStation}</div>
                                                    </div>
                                                    <div class="col-2 text-center">
                                                        <div class="route-visual">
                                                            <div class="route-line"></div>
                                                        </div>
                                                        <div class="duration-info">${train.duration}</div>
                                                    </div>
                                                    <div class="col-5 text-end">
                                                        <div class="time-info">${train.arrivalTime}</div>
                                                        <div class="text-muted small">${train.arrivalStation}</div>
                                                    </div>
                                                </div>
                                            </div>

                                            <!-- 추가 정보 -->
                                            <div class="col-md-3 text-center">
                                                <div class="mb-2">
                                                    <span class="badge bg-info">코레일</span>
                                                </div>
                                                <div class="small text-muted">
                                                    좌석: ${train.availability}
                                                </div>
                                            </div>

                                            <!-- 가격 및 예약 -->
                                            <div class="col-md-3 text-center">
                                                <div class="price-tag mb-2">
                                                    ${train.generalPrice}원
                                                </div>
                                                <button class="btn btn-book btn-sm" onclick="bookTransport('train', '${train.trainNumber}')">
                                                    <i class="fas fa-ticket-alt"></i> 예약하기
                                                </button>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                        </c:if>

                        <!-- 버스 결과 -->
                        <c:if test="${not empty searchResults.buses}">
                            <c:forEach var="bus" items="${searchResults.buses}" varStatus="status">
                                <div class="result-card" data-transport="bus" data-price="${bus.price}">
                                    <div class="card-body p-3">
                                        <div class="row align-items-center">
                                            <!-- 교통수단 정보 -->
                                            <div class="col-md-2 text-center">
                                                <div class="transport-icon bus-icon">
                                                    <i class="fas fa-bus"></i>
                                                </div>
                                                <div class="fw-bold">${bus.busType}</div>
                                                <div class="text-muted small">${bus.busCompany}</div>
                                            </div>

                                            <!-- 시간 정보 -->
                                            <div class="col-md-4">
                                                <div class="row">
                                                    <div class="col-5">
                                                        <div class="time-info">${bus.departureTime}</div>
                                                        <div class="text-muted small">${bus.departureTerminal}</div>
                                                    </div>
                                                    <div class="col-2 text-center">
                                                        <div class="route-visual">
                                                            <div class="route-line"></div>
                                                        </div>
                                                        <div class="duration-info">${bus.duration}</div>
                                                    </div>
                                                    <div class="col-5 text-end">
                                                        <div class="time-info">${bus.arrivalTime}</div>
                                                        <div class="text-muted small">${bus.arrivalTerminal}</div>
                                                    </div>
                                                </div>
                                            </div>

                                            <!-- 추가 정보 -->
                                            <div class="col-md-3 text-center">
                                                <div class="mb-2">
                                                    <span class="badge bg-success">${bus.busGrade}</span>
                                                </div>
                                                <div class="small text-muted">
                                                    좌석: ${bus.remainingSeats}
                                                </div>
                                            </div>

                                            <!-- 가격 및 예약 -->
                                            <div class="col-md-3 text-center">
                                                <div class="price-tag mb-2">
                                                    ${bus.price}원
                                                </div>
                                                <button class="btn btn-book btn-sm" onclick="bookTransport('bus', '${bus.busNumber}')">
                                                    <i class="fas fa-ticket-alt"></i> 예약하기
                                                </button>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                        </c:if>
                    </div>
                </c:if>
            </div>
        </div>

        <!-- 추가 검색 제안 -->
        <c:if test="${hasResults and totalCount > 0}">
            <div class="row mt-4">
                <div class="col-12">
                    <div class="search-info">
                        <div class="row align-items-center">
                            <div class="col-md-8">
                                <h6><i class="fas fa-lightbulb text-warning"></i> 더 좋은 옵션을 찾으시나요?</h6>
                                <p class="mb-0">날짜를 조정하거나 다른 경로를 시도해보세요. 시간대를 바꾸면 더 저렴한 옵션을 찾을 수도 있어요.</p>
                            </div>
                            <div class="col-md-4 text-end">
                                <a href="${pageContext.request.contextPath}/travel/domestic" class="btn btn-outline-primary">
                                    <i class="fas fa-calendar-alt"></i> 날짜 변경
                                </a>
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
                <i class="fas fa-map-marked-alt"></i> Hee Transport - 통합 교통 조회 시스템 | 
                <a href="${pageContext.request.contextPath}/">홈</a> | 
                <a href="${pageContext.request.contextPath}/travel/domestic" class="text-primary">국내여행</a> | 
                <a href="${pageContext.request.contextPath}/travel/international">해외여행</a>
            </p>
        </div>
    </footer>

    <!-- JavaScript -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // 교통수단별 탭 전환
        document.addEventListener('DOMContentLoaded', function() {
            // 탭 이벤트
            document.querySelectorAll('.transport-tab').forEach(tab => {
                tab.addEventListener('click', function() {
                    document.querySelectorAll('.transport-tab').forEach(t => t.classList.remove('active'));
                    this.classList.add('active');
                    
                    const transport = this.dataset.transport;
                    filterByTransport(transport);
                });
            });
        });

        // 교통수단별 필터링
        function filterByTransport(transport) {
            const cards = document.querySelectorAll('.result-card');
            
            cards.forEach(card => {
                if (transport === 'all' || card.dataset.transport === transport) {
                    card.style.display = 'block';
                } else {
                    card.style.display = 'none';
                }
            });
        }

        // 예약하기
        function bookTransport(type, number) {
            const bookingUrls = {
                flight: 'https://www.google.com/flights',
                train: 'https://www.letskorail.com',
                bus: 'https://www.kobus.co.kr'
            };
            
            const typeNames = {
                flight: '항공편',
                train: '기차',
                bus: '버스'
            };
            
            if (confirm(`${typeNames[type]} ${number}을(를) 예약하시겠습니까?\n예약 사이트로 이동합니다.`)) {
                window.open(bookingUrls[type], '_blank');
            }
        }

        // 키보드 단축키
        document.addEventListener('keydown', function(e) {
            // 숫자 키로 탭 전환
            if (e.key >= '1' && e.key <= '4') {
                const tabs = document.querySelectorAll('.transport-tab');
                const index = parseInt(e.key) - 1;
                if (tabs[index]) {
                    tabs[index].click();
                }
            }
        });
    </script>
</body>
</html>