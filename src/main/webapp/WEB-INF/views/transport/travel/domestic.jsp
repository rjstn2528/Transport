<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>국내여행 통합 검색 - Hee Transport</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        .travel-header {
            background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
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
            background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
            color: white;
            padding: 1.5rem;
            text-align: center;
        }
        .transport-types {
            display: flex;
            gap: 1rem;
            justify-content: center;
            margin-bottom: 2rem;
        }
        .transport-type {
            background: white;
            border: 2px solid #4facfe;
            border-radius: 15px;
            padding: 1rem;
            text-align: center;
            flex: 1;
            max-width: 150px;
            transition: all 0.3s;
        }
        .transport-type.active {
            background: #4facfe;
            color: white;
        }
        .transport-type i {
            font-size: 2rem;
            margin-bottom: 0.5rem;
            display: block;
        }
        .city-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
            gap: 0.5rem;
            margin-top: 1rem;
        }
        .city-item {
            padding: 0.5rem;
            border: 1px solid #dee2e6;
            border-radius: 8px;
            text-align: center;
            cursor: pointer;
            transition: all 0.3s;
            background: white;
        }
        .city-item:hover {
            background-color: #f8f9fa;
            border-color: #4facfe;
            transform: translateY(-2px);
        }
        .city-item.selected {
            background-color: #4facfe;
            color: white;
            border-color: #4facfe;
        }
        .city-item.disabled {
            opacity: 0.5;
            cursor: not-allowed;
            pointer-events: none;
        }
        .btn-search {
            background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
            border: none;
            padding: 12px 30px;
            border-radius: 25px;
            font-weight: 500;
            transition: all 0.3s;
        }
        .btn-search:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(79, 172, 254, 0.4);
        }
        .info-section {
            background-color: #f8f9fa;
            border-radius: 10px;
            padding: 1.5rem;
            margin-top: 2rem;
        }
        .feature-icon {
            width: 60px;
            height: 60px;
            background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
            color: white;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            margin: 0 auto 1rem;
        }
        
        /* 펄스 애니메이션 */
        @keyframes pulse {
            0% { transform: scale(1); }
            50% { transform: scale(1.05); }
            100% { transform: scale(1); }
        }
        
        /* 토스트 알림 스타일 */
        .route-toast {
            position: fixed;
            top: 20px;
            right: 20px;
            background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
            color: white;
            padding: 12px 20px;
            border-radius: 25px;
            box-shadow: 0 4px 15px rgba(40, 167, 69, 0.3);
            z-index: 9999;
            font-weight: 500;
            transform: translateX(100%);
            transition: transform 0.3s ease;
            max-width: 300px;
            word-wrap: break-word;
        }
    </style>
</head>
<body>
    <!-- 헤더 -->
    <div class="travel-header">
        <div class="container">
            <div class="row justify-content-center">
                <div class="col-md-8 text-center">
                    <h1><i class="fas fa-map-marked-alt"></i> 국내여행 통합 검색</h1>
                    <p class="lead">출발지와 도착지만 입력하면 항공편, 기차, 버스를 한번에 비교!</p>
                    <nav aria-label="breadcrumb">
                        <ol class="breadcrumb justify-content-center bg-transparent">
                            <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/" class="text-white">홈</a></li>
                            <li class="breadcrumb-item active text-white-50">국내여행</li>
                        </ol>
                    </nav>
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

<!--         교통수단 안내
        <div class="transport-types">
            <div class="transport-type active">
                <i class="fas fa-plane"></i>
                <div>항공편</div>
                <small>빠르고 편리</small>
            </div>
            <div class="transport-type active">
                <i class="fas fa-train"></i>
                <div>기차/KTX</div>
                <small>안전하고 정확</small>
            </div>
            <div class="transport-type active">
                <i class="fas fa-bus"></i>
                <div>고속버스</div>
                <small>경제적이고 편안</small>
            </div>
        </div> -->

        <!-- 검색 폼 -->
        <div class="row justify-content-center">
            <div class="col-lg-8">
                <div class="search-card">
                    <div class="search-header">
                        <h3><i class="fas fa-search"></i> 어디로 떠나시나요?</h3>
                        <p class="mb-0">출발지와 도착지를 선택하고 모든 교통편을 비교해보세요</p>
                    </div>
                    
                    <div class="card-body p-4">
                        <form action="${pageContext.request.contextPath}/travel/domestic/search" method="post" id="domesticSearchForm">
                            <div class="row g-3">
                                <!-- 출발지 -->
                                <div class="col-md-6" id="departureSection">
                                    <label class="form-label fw-bold">
                                        <i class="fas fa-map-marker-alt text-primary"></i> 출발지
                                    </label>
                                    <input type="text" class="form-control form-control-lg" 
                                           id="departure" name="departure" 
                                           placeholder="출발 도시를 선택하세요" readonly required>
                                    
                                    <div class="city-grid mt-2" id="departureCities">
                                        <div class="city-item" data-city="서울">서울</div>
                                        <div class="city-item" data-city="부산">부산</div>
                                        <div class="city-item" data-city="대구">대구</div>
                                        <div class="city-item" data-city="광주">광주</div>
                                        <div class="city-item" data-city="대전">대전</div>
                                        <div class="city-item" data-city="울산">울산</div>
                                        <div class="city-item" data-city="제주">제주</div>
                                        <div class="city-item" data-city="인천">인천</div>
                                        <div class="city-item" data-city="수원">수원</div>
                                        <div class="city-item" data-city="전주">전주</div>
                                        <div class="city-item" data-city="강릉">강릉</div>
                                        <div class="city-item" data-city="춘천">춘천</div>
                                        <div class="city-item" data-city="여수">여수</div>
                                        <div class="city-item" data-city="순천">순천</div>
                                        <div class="city-item" data-city="목포">목포</div>
                                        <div class="city-item" data-city="포항">포항</div>
                                        <div class="city-item" data-city="경주">경주</div>
                                        <div class="city-item" data-city="천안">천안</div>
                                        <div class="city-item" data-city="안동">안동</div>
                                        <div class="city-item" data-city="원주">원주</div>
                                        <div class="city-item" data-city="진주">진주</div>
                                        <div class="city-item" data-city="창원">창원</div>
                                        <div class="city-item" data-city="청주">청주</div>
                                        <div class="city-item" data-city="충주">충주</div>
                                        <div class="city-item" data-city="제천">제천</div>
                                        <div class="city-item" data-city="속초">속초</div>
                                        <div class="city-item" data-city="구미">구미</div>
                                    </div>
                                </div>

                                <!-- 도착지 -->
                                <div class="col-md-6" id="arrivalSection">
                                    <label class="form-label fw-bold">
                                        <i class="fas fa-flag-checkered text-success"></i> 도착지
                                    </label>
                                    <input type="text" class="form-control form-control-lg" 
                                           id="arrival" name="arrival" 
                                           placeholder="도착 도시를 선택하세요" readonly required>
                                    
                                    <div class="city-grid mt-2" id="arrivalCities">
                                        <div class="city-item" data-city="서울">서울</div>
                                        <div class="city-item" data-city="부산">부산</div>
                                        <div class="city-item" data-city="대구">대구</div>
                                        <div class="city-item" data-city="광주">광주</div>
                                        <div class="city-item" data-city="대전">대전</div>
                                        <div class="city-item" data-city="울산">울산</div>
                                        <div class="city-item" data-city="제주">제주</div>
                                        <div class="city-item" data-city="인천">인천</div>
                                        <div class="city-item" data-city="수원">수원</div>
                                        <div class="city-item" data-city="전주">전주</div>
                                        <div class="city-item" data-city="강릉">강릉</div>
                                        <div class="city-item" data-city="춘천">춘천</div>
                                        <div class="city-item" data-city="여수">여수</div>
                                        <div class="city-item" data-city="순천">순천</div>
                                        <div class="city-item" data-city="목포">목포</div>
                                        <div class="city-item" data-city="포항">포항</div>
                                        <div class="city-item" data-city="경주">경주</div>
                                        <div class="city-item" data-city="천안">천안</div>
                                        <div class="city-item" data-city="안동">안동</div>
                                        <div class="city-item" data-city="원주">원주</div>
                                        <div class="city-item" data-city="진주">진주</div>
                                        <div class="city-item" data-city="창원">창원</div>
                                        <div class="city-item" data-city="청주">청주</div>
                                        <div class="city-item" data-city="충주">충주</div>
                                        <div class="city-item" data-city="제천">제천</div>
                                        <div class="city-item" data-city="속초">속초</div>
                                        <div class="city-item" data-city="구미">구미</div>
                                    </div>
                                </div>

                                <!-- 출발날짜 -->
                                <div class="col-md-8">
                                    <label class="form-label fw-bold">
                                        <i class="fas fa-calendar-alt text-warning"></i> 출발날짜
                                    </label>
                                    <input type="date" class="form-control form-control-lg" 
                                           id="departureDate" name="departureDate" 
                                           min="<fmt:formatDate value='<%=new java.util.Date()%>' pattern='yyyy-MM-dd'/>" required>
                                </div>

                                <!-- 승객 수 -->
                                <div class="col-md-4">
                                    <label class="form-label fw-bold">
                                        <i class="fas fa-users text-info"></i> 승객 수
                                    </label>
                                    <select class="form-select form-select-lg" id="adults" name="adults">
                                        <option value="1" selected>1명</option>
                                        <option value="2">2명</option>
                                        <option value="3">3명</option>
                                        <option value="4">4명</option>
                                        <option value="5">5명</option>
                                        <option value="6">6명</option>
                                    </select>
                                </div>
                            </div>

                            <!-- 검색 버튼 -->
                            <div class="text-center mt-4">
                                <button type="submit" class="btn btn-primary btn-search btn-lg">
                                    <i class="fas fa-search"></i> 모든 교통편 검색하기
                                </button>
                                <button type="button" class="btn btn-outline-secondary btn-lg ms-2" onclick="clearForm()">
                                    <i class="fas fa-undo"></i> 초기화
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>

        <!-- 인기 노선 -->
        <div class="row mt-4">
            <div class="col-12">
                <div class="info-section">
                    <h5 class="text-center mb-3">
                        <i class="fas fa-fire text-danger"></i> 인기 국내여행 노선
                    </h5>
                    <div class="row">
                        <div class="col-md-4 text-center">
                            <div class="border rounded p-3 mb-2">
                                <h6>서울 ↔ 부산</h6>
                                <small class="text-muted">KTX 2시간 30분 | 항공편 1시간 20분</small>
                                <br>
                                <button class="btn btn-sm btn-outline-primary mt-2" onclick="setRoute('서울', '부산')">
                                    선택하기
                                </button>
                            </div>
                        </div>
                        <div class="col-md-4 text-center">
                            <div class="border rounded p-3 mb-2">
                                <h6>서울 ↔ 제주</h6>
                                <small class="text-muted">항공편만 이용 가능 | 1시간 30분</small>
                                <br>
                                <button class="btn btn-sm btn-outline-primary mt-2" onclick="setRoute('서울', '제주')">
                                    선택하기
                                </button>
                            </div>
                        </div>
                        <div class="col-md-4 text-center">
                            <div class="border rounded p-3 mb-2">
                                <h6>서울 ↔ 대구</h6>
                                <small class="text-muted">KTX 1시간 50분 | 버스 4시간</small>
                                <br>
                                <button class="btn btn-sm btn-outline-primary mt-2" onclick="setRoute('서울', '대구')">
                                    선택하기
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- 서비스 특징 (맨 아래로 이동) -->
        <div class="row mt-5">
            <div class="col-12">
                <div class="info-section">
                    <h4 class="text-center mb-4">
                        <i class="fas fa-star text-warning"></i> 국내여행 통합 검색의 장점
                    </h4>
                    <div class="row">
                        <div class="col-md-3 text-center">
                            <div class="feature-icon">
                                <i class="fas fa-clock"></i>
                            </div>
                            <h6>시간 절약</h6>
                            <p class="text-muted small">한 번의 검색으로 모든 교통편을 비교</p>
                        </div>
                        <div class="col-md-3 text-center">
                            <div class="feature-icon">
                                <i class="fas fa-won-sign"></i>
                            </div>
                            <h6>최저가 발견</h6>
                            <p class="text-muted small">항공편, 기차, 버스 중 가장 저렴한 옵션 선택</p>
                        </div>
                        <div class="col-md-3 text-center">
                            <div class="feature-icon">
                                <i class="fas fa-route"></i>
                            </div>
                            <h6>다양한 선택</h6>
                            <p class="text-muted small">시간대, 가격, 편의성에 따라 선택 가능</p>
                        </div>
                        <div class="col-md-3 text-center">
                            <div class="feature-icon">
                                <i class="fas fa-mobile-alt"></i>
                            </div>
                            <h6>간편한 예약</h6>
                            <p class="text-muted small">원하는 교통편 선택 후 바로 예약 사이트 연결</p>
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
        let selectedDeparture = null;
        let selectedArrival = null;

        document.addEventListener('DOMContentLoaded', function() {
            console.log('DOM 로드 완료');
            
            // 오늘 날짜로 초기화
            const today = new Date().toISOString().split('T')[0];
            document.getElementById('departureDate').value = today;
            
            // 출발지 도시 선택
            const departureCities = document.querySelectorAll('#departureCities .city-item');
            console.log('출발지 도시 개수:', departureCities.length);
            
            departureCities.forEach(item => {
                item.addEventListener('click', function() {
                    console.log('출발지 클릭:', this.dataset.city);
                    
                    // 기존 선택 해제
                    departureCities.forEach(el => {
                        el.classList.remove('selected');
                    });
                    
                    // 새로운 선택
                    this.classList.add('selected');
                    selectedDeparture = this.dataset.city;
                    document.getElementById('departure').value = selectedDeparture;
                    
                    // 출발지 입력 필드 하이라이트
                    showInputFeedback('departure', selectedDeparture);
                    
                    // 동일한 도착지 선택 방지
                    updateArrivalOptions();
                });
            });
            
            // 도착지 도시 선택
            const arrivalCities = document.querySelectorAll('#arrivalCities .city-item');
            console.log('도착지 도시 개수:', arrivalCities.length);
            
            arrivalCities.forEach(item => {
                item.addEventListener('click', function() {
                    if (this.classList.contains('disabled')) {
                        console.log('비활성화된 도시 클릭:', this.dataset.city);
                        return;
                    }
                    
                    console.log('도착지 클릭:', this.dataset.city);
                    
                    // 기존 선택 해제
                    arrivalCities.forEach(el => {
                        el.classList.remove('selected');
                    });
                    
                    // 새로운 선택
                    this.classList.add('selected');
                    selectedArrival = this.dataset.city;
                    document.getElementById('arrival').value = selectedArrival;
                    
                    // 도착지 입력 필드 하이라이트
                    showInputFeedback('arrival', selectedArrival);
                    
                    // 두 도시 모두 선택되면 검색 버튼 하이라이트
                    if (selectedDeparture && selectedArrival) {
                        setTimeout(() => {
                            highlightSearchButton();
                        }, 500);
                    }
                });
            });
            
            // 폼 검증
            document.getElementById('domesticSearchForm').addEventListener('submit', function(e) {
                console.log('폼 제출:', selectedDeparture, selectedArrival);
                
                if (!selectedDeparture || !selectedArrival) {
                    e.preventDefault();
                    alert('출발지와 도착지를 모두 선택해주세요.');
                    return false;
                }
                
                if (selectedDeparture === selectedArrival) {
                    e.preventDefault();
                    alert('출발지와 도착지가 같을 수 없습니다.');
                    return false;
                }
            });
        });

        // 도착지 옵션 업데이트
        function updateArrivalOptions() {
            const arrivalCities = document.querySelectorAll('#arrivalCities .city-item');
            arrivalCities.forEach(item => {
                if (item.dataset.city === selectedDeparture) {
                    item.classList.add('disabled');
                    item.style.opacity = '0.5';
                    item.style.pointerEvents = 'none';
                    item.classList.remove('selected');
                    
                    // 선택된 도착지가 비활성화되는 경우
                    if (selectedArrival === selectedDeparture) {
                        selectedArrival = null;
                        document.getElementById('arrival').value = '';
                    }
                } else {
                    item.classList.remove('disabled');
                    item.style.opacity = '1';
                    item.style.pointerEvents = 'auto';
                }
            });
        }

        // 인기 노선 선택
        function setRoute(departure, arrival) {
            console.log('인기 노선 선택:', departure, arrival);
            
            selectedDeparture = departure;
            selectedArrival = arrival;
            
            document.getElementById('departure').value = departure;
            document.getElementById('arrival').value = arrival;
            
            // 시각적 선택 표시
            document.querySelectorAll('.city-item').forEach(item => {
                item.classList.remove('selected');
            });
            
            // 출발지 선택 표시
            document.querySelectorAll('#departureCities .city-item').forEach(item => {
                if (item.dataset.city === departure) {
                    item.classList.add('selected');
                }
            });
            
            // 도착지 선택 표시  
            document.querySelectorAll('#arrivalCities .city-item').forEach(item => {
                if (item.dataset.city === arrival) {
                    item.classList.add('selected');
                }
            });
            
            updateArrivalOptions();
            
            // 시각적 피드백들
            showRouteFeedback(departure, arrival);
            scrollToSearchForm();
            
            // 1초 후 검색 버튼 하이라이트
            setTimeout(() => {
                highlightSearchButton();
            }, 1000);
            
            // 내일 날짜로 설정
            const tomorrow = new Date();
            tomorrow.setDate(tomorrow.getDate() + 1);
            document.getElementById('departureDate').value = tomorrow.toISOString().split('T')[0];
        }

        // 입력 필드 피드백
        function showInputFeedback(fieldId, value) {
            const input = document.getElementById(fieldId);
            input.style.transition = 'all 0.3s ease';
            input.style.backgroundColor = '#d4edda';
            input.style.borderColor = '#4facfe';
            input.style.transform = 'scale(1.02)';
            
            setTimeout(() => {
                input.style.backgroundColor = '';
                input.style.borderColor = '';
                input.style.transform = '';
            }, 2000);
        }

        // 노선 선택 피드백
        function showRouteFeedback(departure, arrival) {
            // 토스트 알림 생성
            createRouteToast(`${departure} → ${arrival} 노선이 선택되었습니다!`);
            
            // 입력 필드들 하이라이트
            showInputFeedback('departure', departure);
            setTimeout(() => {
                showInputFeedback('arrival', arrival);
            }, 200);
        }

        // 부드러운 스크롤
        function scrollToSearchForm() {
            const searchCard = document.querySelector('.search-card');
            if (searchCard) {
                searchCard.scrollIntoView({ 
                    behavior: 'smooth', 
                    block: 'start',
                    inline: 'nearest'
                });
            }
        }

        // 토스트 알림 생성
        function createRouteToast(message) {
            // 기존 토스트 제거
            const existingToast = document.querySelector('.route-toast');
            if (existingToast) {
                existingToast.remove();
            }
            
            // 토스트 엘리먼트 생성
            const toast = document.createElement('div');
            toast.className = 'route-toast';
            toast.innerHTML = `
                <i class="fas fa-check-circle me-2"></i>
                ${message}
            `;
            
            // DOM에 추가
            document.body.appendChild(toast);
            
            // 애니메이션으로 나타내기
            setTimeout(() => {
                toast.style.transform = 'translateX(0)';
            }, 100);
            
            // 3초 후 자동 제거
            setTimeout(() => {
                toast.style.transform = 'translateX(100%)';
                setTimeout(() => {
                    if (toast.parentNode) {
                        toast.parentNode.removeChild(toast);
                    }
                }, 300);
            }, 3000);
        }

        // 검색 버튼 하이라이트
        function highlightSearchButton() {
            const searchBtn = document.querySelector('.btn-search');
            if (searchBtn) {
                searchBtn.style.animation = 'pulse 1s ease-in-out 2';
                setTimeout(() => {
                    searchBtn.style.animation = '';
                }, 2000);
            }
        }

        // 폼 초기화
        function clearForm() {
            console.log('폼 초기화');
            
            selectedDeparture = null;
            selectedArrival = null;
            
            document.getElementById('domesticSearchForm').reset();
            document.querySelectorAll('.city-item').forEach(item => {
                item.classList.remove('selected', 'disabled');
                item.style.opacity = '1';
                item.style.pointerEvents = 'auto';
            });
            
            // 오늘 날짜로 재설정
            const today = new Date().toISOString().split('T')[0];
            document.getElementById('departureDate').value = today;
        }

        // 키보드 단축키
        document.addEventListener('keydown', function(e) {
            if (e.ctrlKey && e.key === 'Enter') {
                document.getElementById('domesticSearchForm').submit();
            }
        });
    </script>
</body>
</html>