<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>고속/시외버스 조회 - Hee Transport</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        .bus-header {
            background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
            color: white;
            padding: 3rem 0;
            margin-bottom: 2rem;
        }
        .search-card {
            border: none;
            border-radius: 15px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.1);
            overflow: hidden;
        }
        .search-header {
            background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
            color: white;
            padding: 1.5rem;
            text-align: center;
        }
        .form-floating label {
            color: #6c757d;
        }
        .btn-search {
            background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
            border: none;
            padding: 12px 30px;
            border-radius: 25px;
            font-weight: 500;
            transition: all 0.3s;
        }
        .btn-search:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(40, 167, 69, 0.4);
        }
        .swap-btn {
            position: absolute;
            top: 50%;
            right: 15px;
            transform: translateY(-50%);
            background: #28a745;
            color: white;
            border: none;
            border-radius: 50%;
            width: 40px;
            height: 40px;
            cursor: pointer;
            transition: all 0.3s;
            z-index: 10;
        }
        .swap-btn:hover {
            transform: translateY(-50%) rotate(180deg);
            background: #1e7e34;
        }
        .terminal-row {
            position: relative;
        }
        .error-message {
            background-color: #f8d7da;
            color: #721c24;
            padding: 0.75rem 1rem;
            border-radius: 0.375rem;
            border: 1px solid #f5c6cb;
            margin-bottom: 1rem;
        }
        .info-section {
            background-color: #f8f9fa;
            border-radius: 10px;
            padding: 1.5rem;
            margin-top: 2rem;
        }
        .feature-icon {
            width: 50px;
            height: 50px;
            background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
            color: white;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            margin: 0 auto 1rem;
        }
        .loading {
            text-align: center;
            padding: 40px;
            display: none;
        }
        .popular-routes {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 1rem;
            margin-top: 1rem;
        }
        .route-item {
            padding: 1rem;
            border: 1px solid #dee2e6;
            border-radius: 8px;
            cursor: pointer;
            transition: all 0.3s;
            text-align: center;
        }
        .route-item:hover {
            background-color: #f8f9fa;
            border-color: #28a745;
            transform: translateY(-2px);
        }
        .service-feature {
            display: flex;
            align-items: center;
            margin-bottom: 0.5rem;
        }
        .service-feature i {
            color: #28a745;
            margin-right: 0.5rem;
            width: 20px;
        }
    </style>
</head>
<body>
    <!-- 헤더 -->
    <div class="bus-header">
        <div class="container">
            <div class="row justify-content-center">
                <div class="col-md-8 text-center">
                    <h1><i class="fas fa-bus"></i> 고속/시외버스 조회</h1>
                    <p class="lead">전국 고속버스와 시외버스 시간표를 실시간으로 조회하세요</p>
                    <nav aria-label="breadcrumb">
                        <ol class="breadcrumb justify-content-center bg-transparent">
                            <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/" class="text-white">홈</a></li>
                            <li class="breadcrumb-item active text-white-50">고속/시외버스 조회</li>
                        </ol>
                    </nav>
                </div>
            </div>
        </div>
    </div>

    <div class="container">
        <!-- 에러 메시지 -->
        <c:if test="${not empty error}">
            <div class="error-message">
                <i class="fas fa-exclamation-triangle"></i> ${error}
            </div>
        </c:if>

        <!-- 검색 폼 -->
        <div class="row justify-content-center">
            <div class="col-lg-8">
                <div class="search-card">
                    <div class="search-header">
                        <h3><i class="fas fa-search"></i> 버스 검색</h3>
                        <p class="mb-0">출발터미널과 도착터미널을 선택하고 날짜를 입력하세요</p>
                    </div>
                    
                    <div class="card-body p-4">
                        <form action="${pageContext.request.contextPath}/transport/bus/search" method="post" 
                              onsubmit="showLoading()" id="busSearchForm">
                            
                            <div class="row terminal-row g-3">
                                <div class="col-md-6">
                                    <div class="form-floating">
                                        <select class="form-select" id="departureTerminal" name="departureTerminal" required>
                                            <option value="">출발터미널을 선택하세요</option>
                                            <c:forEach var="terminal" items="${terminals}">
                                                <option value="${terminal}">${terminal}</option>
                                            </c:forEach>
                                        </select>
                                        <label for="departureTerminal"><i class="fas fa-map-marker-alt"></i> 출발터미널</label>
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <div class="form-floating">
                                        <select class="form-select" id="arrivalTerminal" name="arrivalTerminal" required>
                                            <option value="">도착터미널을 선택하세요</option>
                                            <c:forEach var="terminal" items="${terminals}">
                                                <option value="${terminal}">${terminal}</option>
                                            </c:forEach>
                                        </select>
                                        <label for="arrivalTerminal"><i class="fas fa-flag-checkered"></i> 도착터미널</label>
                                    </div>
                                </div>
                                <!-- 출발/도착 터미널 바꾸기 버튼 -->
                                <button type="button" class="swap-btn" onclick="swapTerminals()" title="출발터미널/도착터미널 바꾸기">
                                    <i class="fas fa-exchange-alt"></i>
                                </button>
                            </div>
                            
                            <div class="form-floating mb-3">
                                <input type="date" class="form-control" id="searchDate" name="searchDate" 
                                       min="<fmt:formatDate value='<%=new java.util.Date()%>' pattern='yyyy-MM-dd'/>" required>
                                <label for="searchDate"><i class="fas fa-calendar-alt"></i> 출발날짜</label>
                            </div>

                            <div class="text-center">
                                <button type="submit" class="btn btn-success btn-search btn-lg">
                                    <i class="fas fa-search"></i> 버스편 조회하기
                                </button>
                                <button type="button" class="btn btn-outline-secondary btn-lg ms-2" onclick="clearForm()">
                                    <i class="fas fa-undo"></i> 초기화
                                </button>
                            </div>
                        </form>

                        <div class="loading" id="loading">
                            <div class="spinner-border text-success" role="status">
                                <span class="visually-hidden">Loading...</span>
                            </div>
                            <p class="mt-3"><i class="fas fa-bus"></i> 버스 정보를 조회하고 있습니다...</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- 인기 노선 -->
        <div class="row mt-4">
            <div class="col-12">
                <div class="info-section">
                    <h5 class="text-center mb-3">
                        <i class="fas fa-fire text-danger"></i> 인기 버스 노선
                    </h5>
                    <div class="popular-routes">
                        <div class="route-item" onclick="setRoute('서울고속버스터미널', '부산서부터미널')">
                            <h6>서울 ↔ 부산</h6>
                            <small class="text-muted">고속버스 4시간 20분</small>
                            <br>
                            <button class="btn btn-sm btn-outline-success mt-2">선택하기</button>
                        </div>
                        <div class="route-item" onclick="setRoute('서울고속버스터미널', '대구북부터미널')">
                            <h6>서울 ↔ 대구</h6>
                            <small class="text-muted">고속버스 3시간 30분</small>
                            <br>
                            <button class="btn btn-sm btn-outline-success mt-2">선택하기</button>
                        </div>
                        <div class="route-item" onclick="setRoute('서울남부터미널', '광주종합버스터미널')">
                            <h6>서울 ↔ 광주</h6>
                            <small class="text-muted">고속버스 3시간 40분</small>
                            <br>
                            <button class="btn btn-sm btn-outline-success mt-2">선택하기</button>
                        </div>
                        <div class="route-item" onclick="setRoute('동서울터미널', '강릉시외버스터미널')">
                            <h6>동서울 ↔ 강릉</h6>
                            <small class="text-muted">고속버스 2시간 30분</small>
                            <br>
                            <button class="btn btn-sm btn-outline-success mt-2">선택하기</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- 서비스 안내 -->
        <div class="row mt-4">
            <div class="col-12">
                <div class="info-section">
                    <h4 class="text-center mb-4">
                        <i class="fas fa-star text-warning"></i> 버스 조회 서비스
                    </h4>
                    <div class="row">
                        <div class="col-md-4 text-center">
                            <div class="feature-icon">
                                <i class="fas fa-clock"></i>
                            </div>
                            <h6>실시간 시간표</h6>
                            <p class="text-muted small">고속버스와 시외버스 실시간 운행 정보 제공</p>
                        </div>
                        <div class="col-md-4 text-center">
                            <div class="feature-icon">
                                <i class="fas fa-bus"></i>
                            </div>
                            <h6>통합 검색</h6>
                            <p class="text-muted small">고속버스 & 시외버스를 한 번에 조회</p>
                        </div>
                        <div class="col-md-4 text-center">
                            <div class="feature-icon">
                                <i class="fas fa-won-sign"></i>
                            </div>
                            <h6>등급별 요금</h6>
                            <p class="text-muted small">우등, 일반 등급별 정확한 요금 정보</p>
                        </div>
                    </div>
                    
                    <!-- 상세 안내 -->
                    <div class="row mt-4">
                        <div class="col-md-6">
                            <h6><i class="fas fa-info-circle text-success"></i> 서비스 특징</h6>
                            <div class="service-feature">
                                <i class="fas fa-clock"></i>
                                <span>실시간 버스 시간표 제공</span>
                            </div>
                            <div class="service-feature">
                                <i class="fas fa-bus"></i>
                                <span>고속버스 & 시외버스 통합 조회</span>
                            </div>
                            <div class="service-feature">
                                <i class="fas fa-won-sign"></i>
                                <span>등급별 요금 정보 제공</span>
                            </div>
                            <div class="service-feature">
                                <i class="fas fa-chair"></i>
                                <span>잔여좌석 현황 확인</span>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <h6><i class="fas fa-route text-success"></i> 운행 정보</h6>
                            <div class="service-feature">
                                <i class="fas fa-map-signs"></i>
                                <span>전국 주요 터미널 연결</span>
                            </div>
                            <div class="service-feature">
                                <i class="fas fa-star"></i>
                                <span>우등고속, 일반고속, 시외버스</span>
                            </div>
                            <div class="service-feature">
                                <i class="fas fa-building"></i>
                                <span>주요 버스회사 정보 제공</span>
                            </div>
                            <div class="service-feature">
                                <i class="fas fa-shield-alt"></i>
                                <span>신뢰할 수 있는 운행 정보</span>
                            </div>
                        </div>
                    </div>
                </div>
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
    <script>
        function showLoading() {
            // 폼이 실제로 제출될 때만 로딩 표시
            const form = document.querySelector('form');
            if (form && form.checkValidity()) {
                document.getElementById('loading').style.display = 'block';
                document.querySelector('form').style.display = 'none';
            }
        }

        function hideLoading() {
            document.getElementById('loading').style.display = 'none';
            document.querySelector('form').style.display = 'block';
        }

        // 페이지 로드 시 로딩 숨기기
        document.addEventListener('DOMContentLoaded', function() {
            hideLoading();
            
            // 오늘 날짜를 기본값으로 설정
            const today = new Date().toISOString().split('T')[0];
            document.getElementById('searchDate').value = today;
            
            // 서울-부산을 기본값으로 설정
            document.getElementById('departureTerminal').value = '서울고속버스터미널';
            document.getElementById('arrivalTerminal').value = '부산서부터미널';
            
            // 출발터미널과 도착터미널이 같은지 확인
            function validateTerminals() {
                const departure = document.getElementById('departureTerminal').value;
                const arrival = document.getElementById('arrivalTerminal').value;
                
                if (departure && arrival && departure === arrival) {
                    alert('출발터미널과 도착터미널이 같을 수 없습니다.');
                    document.getElementById('arrivalTerminal').value = '';
                    return false;
                }
                return true;
            }
            
            document.getElementById('departureTerminal').addEventListener('change', validateTerminals);
            document.getElementById('arrivalTerminal').addEventListener('change', validateTerminals);
            
            // 뒤로가기 감지
            window.addEventListener('pageshow', function(event) {
                if (event.persisted) {
                    hideLoading();
                }
            });
        });

        function swapTerminals() {
            const departure = document.getElementById('departureTerminal');
            const arrival = document.getElementById('arrivalTerminal');
            
            const temp = departure.value;
            departure.value = arrival.value;
            arrival.value = temp;
        }

        function setRoute(dep, arr) {
            document.getElementById('departureTerminal').value = dep;
            document.getElementById('arrivalTerminal').value = arr;
            
            // 내일 날짜로 설정
            const tomorrow = new Date();
            tomorrow.setDate(tomorrow.getDate() + 1);
            document.getElementById('searchDate').value = tomorrow.toISOString().split('T')[0];
        }

        function clearForm() {
            document.getElementById('busSearchForm').reset();
            hideLoading(); // 로딩 숨기기 추가
            
            // 오늘 날짜로 재설정
            const today = new Date().toISOString().split('T')[0];
            document.getElementById('searchDate').value = today;
        }
    </script>
</body>
</html>