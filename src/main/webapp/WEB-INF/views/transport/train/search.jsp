<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>기차표 조회 - Hee Transport</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        .train-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
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
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 1.5rem;
            text-align: center;
        }
        .form-floating label {
            color: #6c757d;
        }
        .btn-search {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border: none;
            padding: 12px 30px;
            border-radius: 25px;
            font-weight: 500;
            transition: all 0.3s;
        }
        .btn-search:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
        }
        .swap-btn {
            position: absolute;
            top: 50%;
            right: 15px;
            transform: translateY(-50%);
            background: #667eea;
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
            background: #5a67d8;
        }
        .station-row {
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
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
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
            border-color: #667eea;
            transform: translateY(-2px);
        }
    </style>
</head>
<body>
    <!-- 헤더 -->
    <div class="train-header">
        <div class="container">
            <div class="row justify-content-center">
                <div class="col-md-8 text-center">
                    <h1><i class="fas fa-train"></i> 기차표 조회</h1>
                    <p class="lead">전국 기차 시간표와 요금을 실시간으로 조회하세요</p>
                    <nav aria-label="breadcrumb">
                        <ol class="breadcrumb justify-content-center bg-transparent">
                            <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/" class="text-white">홈</a></li>
                            <li class="breadcrumb-item active text-white-50">기차표 조회</li>
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
                        <h3><i class="fas fa-search"></i> 기차표 검색</h3>
                        <p class="mb-0">출발역과 도착역을 선택하고 날짜를 입력하세요</p>
                    </div>
                    
                    <div class="card-body p-4">
                        <form action="${pageContext.request.contextPath}/transport/train/search" method="post" 
                              onsubmit="showLoading()" id="trainSearchForm">
                            
                            <div class="row station-row g-3">
                                <div class="col-md-6">
                                    <div class="form-floating">
                                        <select class="form-select" id="departure" name="departure" required>
                                            <option value="">출발역을 선택하세요</option>
                                            <c:forEach var="station" items="${stations}">
                                                <option value="${station}">${station}</option>
                                            </c:forEach>
                                        </select>
                                        <label for="departure"><i class="fas fa-map-marker-alt"></i> 출발역</label>
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <div class="form-floating">
                                        <select class="form-select" id="arrival" name="arrival" required>
                                            <option value="">도착역을 선택하세요</option>
                                            <c:forEach var="station" items="${stations}">
                                                <option value="${station}">${station}</option>
                                            </c:forEach>
                                        </select>
                                        <label for="arrival"><i class="fas fa-flag-checkered"></i> 도착역</label>
                                    </div>
                                </div>
                                <!-- 출발역/도착역 바꾸기 버튼 -->
                                <button type="button" class="swap-btn" onclick="swapStations()" title="출발역/도착역 바꾸기">
                                    <i class="fas fa-exchange-alt"></i>
                                </button>
                            </div>
                            
                            <div class="form-floating mb-3">
                                <input type="date" class="form-control" id="searchDate" name="searchDate" 
                                       min="<fmt:formatDate value='<%=new java.util.Date()%>' pattern='yyyy-MM-dd'/>" required>
                                <label for="searchDate"><i class="fas fa-calendar-alt"></i> 출발날짜</label>
                            </div>

                            <div class="text-center">
                                <button type="submit" class="btn btn-primary btn-search btn-lg">
                                    <i class="fas fa-search"></i> 기차편 조회하기
                                </button>
                                <button type="button" class="btn btn-outline-secondary btn-lg ms-2" onclick="clearForm()">
                                    <i class="fas fa-undo"></i> 초기화
                                </button>
                            </div>
                        </form>

                        <div class="loading" id="loading">
                            <div class="spinner-border text-primary" role="status">
                                <span class="visually-hidden">Loading...</span>
                            </div>
                            <p class="mt-3"><i class="fas fa-train"></i> 기차 정보를 조회하고 있습니다...</p>
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
                        <i class="fas fa-fire text-danger"></i> 인기 기차 노선
                    </h5>
                    <div class="popular-routes">
                        <div class="route-item" onclick="setRoute('서울', '부산')">
                            <h6>서울 ↔ 부산</h6>
                            <small class="text-muted">KTX 2시간 40분</small>
                            <br>
                            <button class="btn btn-sm btn-outline-primary mt-2">선택하기</button>
                        </div>
                        <div class="route-item" onclick="setRoute('서울', '동대구')">
                            <h6>서울 ↔ 대구</h6>
                            <small class="text-muted">KTX 1시간 40분</small>
                            <br>
                            <button class="btn btn-sm btn-outline-primary mt-2">선택하기</button>
                        </div>
                        <div class="route-item" onclick="setRoute('서울', '광주')">
                            <h6>서울 ↔ 광주</h6>
                            <small class="text-muted">KTX 1시간 30분</small>
                            <br>
                            <button class="btn btn-sm btn-outline-primary mt-2">선택하기</button>
                        </div>
                        <div class="route-item" onclick="setRoute('서울', '목포')">
                            <h6>서울 ↔ 목포</h6>
                            <small class="text-muted">KTX 2시간 40분</small>
                            <br>
                            <button class="btn btn-sm btn-outline-primary mt-2">선택하기</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- 서비스 특징 -->
        <div class="row mt-4">
            <div class="col-12">
                <div class="info-section">
                    <h4 class="text-center mb-4">
                        <i class="fas fa-star text-warning"></i> 기차표 조회 서비스
                    </h4>
                    <div class="row">
                        <div class="col-md-4 text-center">
                            <div class="feature-icon">
                                <i class="fas fa-clock"></i>
                            </div>
                            <h6>실시간 조회</h6>
                            <p class="text-muted small">코레일 API 연동으로 실시간 기차 정보 제공</p>
                        </div>
                        <div class="col-md-4 text-center">
                            <div class="feature-icon">
                                <i class="fas fa-train"></i>
                            </div>
                            <h6>모든 열차</h6>
                            <p class="text-muted small">KTX, ITX-새마을, 무궁화호 등 모든 열차 정보</p>
                        </div>
                        <div class="col-md-4 text-center">
                            <div class="feature-icon">
                                <i class="fas fa-won-sign"></i>
                            </div>
                            <h6>요금 안내</h6>
                            <p class="text-muted small">일반실, 특실 등급별 정확한 요금 정보</p>
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
                <i class="fas fa-train"></i> Hee Transport - 통합 교통 조회 시스템 | 
                <a href="${pageContext.request.contextPath}/">홈</a> | 
                <a href="${pageContext.request.contextPath}/transport/train" class="text-primary">기차표 조회</a> | 
                <a href="${pageContext.request.contextPath}/transport/bus">버스</a> | 
                <a href="${pageContext.request.contextPath}/transport/flight">항공편</a>
            </p>
            <small class="text-muted">
                실시간 기차 정보는 코레일 API를 통해 제공됩니다.
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
            document.getElementById('departure').value = '서울';
            document.getElementById('arrival').value = '부산';
            
            // 출발역과 도착역이 같은지 확인
            function validateStations() {
                const departure = document.getElementById('departure').value;
                const arrival = document.getElementById('arrival').value;
                
                if (departure && arrival && departure === arrival) {
                    alert('출발역과 도착역이 같을 수 없습니다.');
                    document.getElementById('arrival').value = '';
                    return false;
                }
                return true;
            }
            
            document.getElementById('departure').addEventListener('change', validateStations);
            document.getElementById('arrival').addEventListener('change', validateStations);
            
            // 뒤로가기 감지
            window.addEventListener('pageshow', function(event) {
                if (event.persisted) {
                    hideLoading();
                }
            });
        });

        function swapStations() {
            const departure = document.getElementById('departure');
            const arrival = document.getElementById('arrival');
            
            const temp = departure.value;
            departure.value = arrival.value;
            arrival.value = temp;
        }

        function setRoute(dep, arr) {
            document.getElementById('departure').value = dep;
            document.getElementById('arrival').value = arr;
            
            // 내일 날짜로 설정
            const tomorrow = new Date();
            tomorrow.setDate(tomorrow.getDate() + 1);
            document.getElementById('searchDate').value = tomorrow.toISOString().split('T')[0];
        }

        function clearForm() {
            document.getElementById('trainSearchForm').reset();
            hideLoading(); // 로딩 숨기기 추가
            
            // 오늘 날짜로 재설정
            const today = new Date().toISOString().split('T')[0];
            document.getElementById('searchDate').value = today;
        }
    </script>
</body>
</html>