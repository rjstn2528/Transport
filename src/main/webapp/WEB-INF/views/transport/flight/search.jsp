<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>항공편 조회 - Hee Transport</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        .flight-header {
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
        .airport-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 1rem;
            margin-top: 1rem;
        }
        .airport-item {
            padding: 10px;
            border: 1px solid #dee2e6;
            border-radius: 8px;
            cursor: pointer;
            transition: all 0.3s;
        }
        .airport-item:hover {
            background-color: #f8f9fa;
            border-color: #667eea;
        }
        .airport-code {
            font-weight: bold;
            color: #667eea;
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
    </style>
</head>
<body>
    <!-- 헤더 -->
    <div class="flight-header">
        <div class="container">
            <div class="row justify-content-center">
                <div class="col-md-8 text-center">
                    <h1><i class="fas fa-plane"></i> 항공편 조회</h1>
                    <p class="lead">국내외 항공편을 실시간으로 검색하고 비교하세요</p>
                    <nav aria-label="breadcrumb">
                        <ol class="breadcrumb justify-content-center bg-transparent">
                            <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/" class="text-white">홈</a></li>
                            <li class="breadcrumb-item active text-white-50">항공편 조회</li>
                        </ol>
                    </nav>
                </div>
            </div>
        </div>
    </div>

    <div class="container">
        <!-- 에러 메시지 -->
        <c:if test="${not empty errorMessage}">
            <div class="error-message">
                <i class="fas fa-exclamation-triangle"></i> ${errorMessage}
            </div>
        </c:if>

        <!-- 검색 폼 -->
        <div class="row justify-content-center">
            <div class="col-lg-8">
                <div class="search-card">
                    <div class="search-header">
                        <h3><i class="fas fa-search"></i> 항공편 검색</h3>
                        <p class="mb-0">출발지와 목적지를 선택하고 날짜를 입력하세요</p>
                    </div>
                    
                    <div class="card-body p-4">
                        <form action="${pageContext.request.contextPath}/transport/flight/search" method="post" id="flightSearchForm">
                            <div class="row g-3">
                                <!-- 출발공항 -->
                                <div class="col-md-6">
                                    <div class="form-floating">
                                        <select class="form-select" id="departureAirport" name="departureAirport" required>
                                            <option value="">출발공항을 선택하세요</option>
                                            <!-- 국내공항 -->
                                            <optgroup label="🇰🇷 국내공항">
                                                <option value="ICN">ICN - 인천국제공항 (서울)</option>
                                                <option value="GMP">GMP - 김포국제공항 (서울)</option>
                                                <option value="PUS">PUS - 김해국제공항 (부산)</option>
                                                <option value="CJU">CJU - 제주국제공항 (제주)</option>
                                                <option value="TAE">TAE - 대구국제공항 (대구)</option>
                                                <option value="KWJ">KWJ - 광주공항 (광주)</option>
                                            </optgroup>
                                            <!-- 일본 -->
                                            <optgroup label="🇯🇵 일본">
                                                <option value="NRT">NRT - 나리타국제공항 (도쿄)</option>
                                                <option value="HND">HND - 하네다공항 (도쿄)</option>
                                                <option value="KIX">KIX - 간사이국제공항 (오사카)</option>
                                                <option value="ITM">ITM - 이타미공항 (오사카)</option>
                                                <option value="CTS">CTS - 신치토세공항 (삿포로)</option>
                                                <option value="FUK">FUK - 후쿠오카공항 (후쿠오카)</option>
                                            </optgroup>
                                            <!-- 중국 -->
                                            <optgroup label="🇨🇳 중국">
                                                <option value="PEK">PEK - 베이징수도국제공항 (베이징)</option>
                                                <option value="PKX">PKX - 베이징다싱국제공항 (베이징)</option>
                                                <option value="PVG">PVG - 상하이푸둥국제공항 (상하이)</option>
                                                <option value="SHA">SHA - 상하이훙차오국제공항 (상하이)</option>
                                                <option value="CAN">CAN - 광저우바이윈국제공항 (광저우)</option>
                                            </optgroup>
                                            <!-- 동남아시아 -->
                                            <optgroup label="🌏 동남아시아">
                                                <option value="BKK">BKK - 수완나품국제공항 (방콕)</option>
                                                <option value="DMK">DMK - 돈므앙국제공항 (방콕)</option>
                                                <option value="SIN">SIN - 창이공항 (싱가포르)</option>
                                                <option value="KUL">KUL - 쿠알라룸푸르국제공항 (쿠알라룸푸르)</option>
                                                <option value="CGK">CGK - 수카르노하타국제공항 (자카르타)</option>
                                            </optgroup>
                                        </select>
                                        <label for="departureAirport"><i class="fas fa-plane-departure"></i> 출발공항</label>
                                    </div>
                                </div>

                                <!-- 도착공항 -->
                                <div class="col-md-6">
                                    <div class="form-floating">
                                        <select class="form-select" id="arrivalAirport" name="arrivalAirport" required>
                                            <option value="">도착공항을 선택하세요</option>
                                            <!-- 국내공항 -->
                                            <optgroup label="🇰🇷 국내공항">
                                                <option value="ICN">ICN - 인천국제공항 (서울)</option>
                                                <option value="GMP">GMP - 김포국제공항 (서울)</option>
                                                <option value="PUS">PUS - 김해국제공항 (부산)</option>
                                                <option value="CJU">CJU - 제주국제공항 (제주)</option>
                                                <option value="TAE">TAE - 대구국제공항 (대구)</option>
                                                <option value="KWJ">KWJ - 광주공항 (광주)</option>
                                            </optgroup>
                                            <!-- 일본 -->
                                            <optgroup label="🇯🇵 일본">
                                                <option value="NRT">NRT - 나리타국제공항 (도쿄)</option>
                                                <option value="HND">HND - 하네다공항 (도쿄)</option>
                                                <option value="KIX">KIX - 간사이국제공항 (오사카)</option>
                                                <option value="ITM">ITM - 이타미공항 (오사카)</option>
                                                <option value="CTS">CTS - 신치토세공항 (삿포로)</option>
                                                <option value="FUK">FUK - 후쿠오카공항 (후쿠오카)</option>
                                            </optgroup>
                                            <!-- 중국 -->
                                            <optgroup label="🇨🇳 중국">
                                                <option value="PEK">PEK - 베이징수도국제공항 (베이징)</option>
                                                <option value="PKX">PKX - 베이징다싱국제공항 (베이징)</option>
                                                <option value="PVG">PVG - 상하이푸둥국제공항 (상하이)</option>
                                                <option value="SHA">SHA - 상하이훙차오국제공항 (상하이)</option>
                                                <option value="CAN">CAN - 광저우바이윈국제공항 (광저우)</option>
                                            </optgroup>
                                            <!-- 동남아시아 -->
                                            <optgroup label="🌏 동남아시아">
                                                <option value="BKK">BKK - 수완나품국제공항 (방콕)</option>
                                                <option value="DMK">DMK - 돈므앙국제공항 (방콕)</option>
                                                <option value="SIN">SIN - 창이공항 (싱가포르)</option>
                                                <option value="KUL">KUL - 쿠알라룸푸르국제공항 (쿠알라룸푸르)</option>
                                                <option value="CGK">CGK - 수카르노하타국제공항 (자카르타)</option>
                                            </optgroup>
                                        </select>
                                        <label for="arrivalAirport"><i class="fas fa-plane-arrival"></i> 도착공항</label>
                                    </div>
                                </div>

                                <!-- 출발날짜 -->
                                <div class="col-md-8">
                                    <div class="form-floating">
                                        <input type="date" class="form-control" id="departureDate" name="departureDate" 
                                               min="<fmt:formatDate value='<%=new java.util.Date()%>' pattern='yyyy-MM-dd'/>" required>
                                        <label for="departureDate"><i class="fas fa-calendar-alt"></i> 출발날짜</label>
                                    </div>
                                </div>

                                <!-- 승객 수 -->
                                <div class="col-md-4">
                                    <div class="form-floating">
                                        <select class="form-select" id="adults" name="adults">
                                            <option value="1" selected>1명</option>
                                            <option value="2">2명</option>
                                            <option value="3">3명</option>
                                            <option value="4">4명</option>
                                            <option value="5">5명</option>
                                            <option value="6">6명</option>
                                            <option value="7">7명</option>
                                            <option value="8">8명</option>
                                            <option value="9">9명</option>
                                        </select>
                                        <label for="adults"><i class="fas fa-users"></i> 승객 수</label>
                                    </div>
                                </div>
                            </div>

                            <!-- 검색 버튼 -->
                            <div class="text-center mt-4">
                                <button type="submit" class="btn btn-primary btn-search">
                                    <i class="fas fa-search"></i> 항공편 검색
                                </button>
                                <button type="button" class="btn btn-outline-secondary ms-2" onclick="clearForm()">
                                    <i class="fas fa-undo"></i> 초기화
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>

        <!-- 인기 노선 -->
        <div class="row mt-5">
            <div class="col-12">
                <div class="info-section">
                    <h4 class="text-center mb-4"><i class="fas fa-route"></i> 인기 노선</h4>
                    <div class="row">
                        <div class="col-md-3 text-center">
                            <div class="feature-icon">
                                <i class="fas fa-star"></i>
                            </div>
                            <h6>인천 ↔ 도쿄</h6>
                            <p class="text-muted small">최저가 32만원부터</p>
                            <button class="btn btn-sm btn-outline-primary" onclick="setRoute('ICN', 'NRT')">선택</button>
                        </div>
                        <div class="col-md-3 text-center">
                            <div class="feature-icon">
                                <i class="fas fa-fire"></i>
                            </div>
                            <h6>인천 ↔ 방콕</h6>
                            <p class="text-muted small">최저가 65만원부터</p>
                            <button class="btn btn-sm btn-outline-primary" onclick="setRoute('ICN', 'BKK')">선택</button>
                        </div>
                        <div class="col-md-3 text-center">
                            <div class="feature-icon">
                                <i class="fas fa-heart"></i>
                            </div>
                            <h6>인천 ↔ 싱가포르</h6>
                            <p class="text-muted small">최저가 78만원부터</p>
                            <button class="btn btn-sm btn-outline-primary" onclick="setRoute('ICN', 'SIN')">선택</button>
                        </div>
                        <div class="col-md-3 text-center">
                            <div class="feature-icon">
                                <i class="fas fa-plane"></i>
                            </div>
                            <h6>김포 ↔ 하네다</h6>
                            <p class="text-muted small">최저가 35만원부터</p>
                            <button class="btn btn-sm btn-outline-primary" onclick="setRoute('GMP', 'HND')">선택</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- 서비스 안내 -->
        <div class="row mt-4">
            <div class="col-12">
                <div class="info-section">
                    <div class="row">
                        <div class="col-md-4 text-center">
                            <div class="feature-icon">
                                <i class="fas fa-globe"></i>
                            </div>
                            <h6>실시간 검색</h6>
                            <p class="text-muted small">AMADEUS API를 통한 실시간 항공편 정보</p>
                        </div>
                        <div class="col-md-4 text-center">
                            <div class="feature-icon">
                                <i class="fas fa-won-sign"></i>
                            </div>
                            <h6>가격 비교</h6>
                            <p class="text-muted small">여러 항공사의 요금을 한 번에 비교</p>
                        </div>
                        <div class="col-md-4 text-center">
                            <div class="feature-icon">
                                <i class="fas fa-shield-alt"></i>
                            </div>
                            <h6>안전한 검색</h6>
                            <p class="text-muted small">신뢰할 수 있는 항공편 정보 제공</p>
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
                <i class="fas fa-plane"></i> Hee Transport - 통합 교통 조회 시스템 | 
                <a href="${pageContext.request.contextPath}/">홈</a> | 
                <a href="${pageContext.request.contextPath}/transport/train">기차</a> | 
                <a href="${pageContext.request.contextPath}/transport/bus">버스</a> | 
                <a href="${pageContext.request.contextPath}/transport/flight" class="text-primary">항공편</a>
            </p>
        </div>
    </footer>

    <!-- JavaScript -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // 폼 초기화
        function clearForm() {
            document.getElementById('flightSearchForm').reset();
            
            // 오늘 날짜로 설정
            const today = new Date().toISOString().split('T')[0];
            document.getElementById('departureDate').value = today;
        }

        // 인기 노선 선택
        function setRoute(departure, arrival) {
            document.getElementById('departureAirport').value = departure;
            document.getElementById('arrivalAirport').value = arrival;
            
            // 내일 날짜로 설정
            const tomorrow = new Date();
            tomorrow.setDate(tomorrow.getDate() + 1);
            document.getElementById('departureDate').value = tomorrow.toISOString().split('T')[0];
        }

        // 출발공항과 도착공항이 같은지 확인
        function validateAirports() {
            const departure = document.getElementById('departureAirport').value;
            const arrival = document.getElementById('arrivalAirport').value;
            
            if (departure && arrival && departure === arrival) {
                alert('출발공항과 도착공항이 같을 수 없습니다.');
                document.getElementById('arrivalAirport').value = '';
                return false;
            }
            return true;
        }

        // 이벤트 리스너 등록
        document.addEventListener('DOMContentLoaded', function() {
            // 오늘 날짜로 초기화
            const today = new Date().toISOString().split('T')[0];
            document.getElementById('departureDate').value = today;
            
            // 공항 선택 시 검증
            document.getElementById('departureAirport').addEventListener('change', validateAirports);
            document.getElementById('arrivalAirport').addEventListener('change', validateAirports);
            
            // 폼 제출 시 검증
            document.getElementById('flightSearchForm').addEventListener('submit', function(e) {
                if (!validateAirports()) {
                    e.preventDefault();
                    return false;
                }
            });
        });
    </script>
</body>
</html>