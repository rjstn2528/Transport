<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>해외여행 항공편 검색 - Hee Transport</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        .travel-header {
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
        .destination-grid {
            display: grid !important;
            grid-template-columns: repeat(5, 1fr);
            gap: 1rem;
            margin-top: 1rem;
            min-height: 150px;
            /* 위치와 가시성 강제 설정 */
            position: relative;
            z-index: 1;
            width: 100%;
            overflow: visible;
        }
        
        /* 반응형 처리 */
        @media (max-width: 1200px) {
            .destination-grid {
                grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
            }
        }
        
        @media (max-width: 768px) {
            .destination-grid {
                grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
            }
        }
        .destination-item {
            padding: 1rem !important;
            border: 2px solid #dee2e6 !important;
            border-radius: 12px !important;
            cursor: pointer !important;
            transition: all 0.3s !important;
            background: white !important;
            text-align: center !important;
            /* 텍스트 가시성 강제 설정 */
            color: #333 !important;
            font-size: 14px !important;
            line-height: 1.5 !important;
            font-family: "Malgun Gothic", "맑은 고딕", Arial, sans-serif !important;
            /* 위치와 표시 강제 설정 */
            display: block !important;
            position: relative !important;
            z-index: 2 !important;
            width: auto !important;
            height: auto !important;
            min-height: 100px !important;
            overflow: visible !important;
        }
        .destination-item:hover {
            background-color: #f8f9fa;
            border-color: #667eea;
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(0,0,0,0.1);
        }
        .destination-item.selected {
            background-color: #667eea;
            color: white;
            border-color: #667eea;
        }
        .country-flag {
            font-size: 2rem;
            margin-bottom: 0.5rem;
            display: block;
        }
        .destination-name {
            font-weight: bold;
            margin-bottom: 0.25rem;
            /* 텍스트 가시성 강화 */
            color: #333 !important;
            font-size: 16px !important;
            font-family: "Malgun Gothic", "맑은 고딕", Arial, sans-serif !important;
        }
        .destination-country {
            font-size: 0.9rem;
            opacity: 0.8;
            /* 텍스트 가시성 강화 */
            color: #666 !important;
            font-family: "Malgun Gothic", "맑은 고딕", Arial, sans-serif !important;
        }
        .region-tabs {
            display: flex;
            gap: 0.5rem;
            margin-bottom: 1rem;
            flex-wrap: wrap;
        }
        .region-tab {
            padding: 0.5rem 1rem;
            border: 2px solid #667eea;
            border-radius: 20px;
            background: white;
            color: #667eea;
            cursor: pointer;
            transition: all 0.3s;
            font-size: 0.9rem;
        }
        .region-tab.active {
            background: #667eea;
            color: white;
        }
        .departure-region {
            background-color: #f8f9fa;
            border-radius: 10px;
            padding: 1rem;
            margin-bottom: 1rem;
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
        .info-section {
            background-color: #f8f9fa;
            border-radius: 10px;
            padding: 1.5rem;
            margin-top: 2rem;
        }
        .feature-icon {
            width: 60px;
            height: 60px;
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
    <div class="travel-header">
        <div class="container">
            <div class="row justify-content-center">
                <div class="col-md-8 text-center">
                    <h1><i class="fas fa-globe-asia"></i> 해외여행 항공편 검색</h1>
                    <p class="lead">목적지만 선택하면 모든 출발지에서 가는 항공편을 한번에 비교!</p>
                    <nav aria-label="breadcrumb">
                        <ol class="breadcrumb justify-content-center bg-transparent">
                            <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/" class="text-white">홈</a></li>
                            <li class="breadcrumb-item active text-white-50">해외여행</li>
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

        <!-- 검색 폼 -->
        <div class="row justify-content-center">
            <div class="col-lg-10">
                <div class="search-card">
                    <div class="search-header">
                        <h3><i class="fas fa-search"></i> 어느 나라로 떠나시나요?</h3>
                        <p class="mb-0">목적지를 선택하면 모든 한국 공항에서 출발하는 항공편을 찾아드려요</p>
                    </div>
                    
                    <div class="card-body p-4">
                        <form action="${pageContext.request.contextPath}/travel/international/search" method="post" id="internationalSearchForm">
                            <!-- 출발 지역 선택 -->
                            <div class="departure-region">
                                <label class="form-label fw-bold mb-3">
                                    <i class="fas fa-plane-departure text-primary"></i> 출발 지역 (선택사항)
                                </label>
                                <div class="region-tabs">
                                    <div class="region-tab active" data-region="all">전국 모든 공항</div>
                                    <div class="region-tab" data-region="seoul">서울/경기 (인천, 김포)</div>
                                    <div class="region-tab" data-region="busan">부산/경남 (김해)</div>
                                    <div class="region-tab" data-region="jeju">제주</div>
                                </div>
                                <input type="hidden" id="departureRegion" name="departureRegion" value="all">
                                <small class="text-muted">
                                    <i class="fas fa-info-circle"></i> 출발 지역을 제한하면 해당 지역 공항에서만 검색합니다
                                </small>
                            </div>

                            <div class="row g-3">
                                <!-- 목적지 선택 -->
                                <div class="col-12">
                                    <label class="form-label fw-bold">
                                        <i class="fas fa-map-marker-alt text-success"></i> 목적지 선택
                                    </label>
                                    <input type="text" class="form-control form-control-lg" 
                                           id="destination" name="destination" 
                                           placeholder="가고 싶은 목적지를 선택하세요" readonly required>
                                    
                                    <!-- 지역별 탭 -->
                                    <div class="region-tabs mt-3">
                                        <div class="region-tab active" data-destinations="japan">일본</div>
                                        <div class="region-tab" data-destinations="china">중국</div>
                                        <div class="region-tab" data-destinations="southeast">동남아시아</div>
                                        <div class="region-tab" data-destinations="usa">미주</div>
                                        <div class="region-tab" data-destinations="europe">유럽</div>
                                    </div>
                                    
                                    <!-- ✅ 수정: 목적지 그리드에 ID 추가 -->
                                    <div id="destinationGrid" class="destination-grid">
                                        <!-- JavaScript가 동적으로 생성할 영역 -->
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
                                    <i class="fas fa-search"></i> 항공편 검색하기
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

        <!-- 인기 목적지 -->
        <div class="row mt-4">
            <div class="col-12">
                <div class="info-section">
                    <h5 class="text-center mb-3">
                        <i class="fas fa-fire text-danger"></i> 인기 해외여행 목적지
                    </h5>
                    <div class="row">
                        <div class="col-md-4 text-center">
                            <div class="border rounded p-3 mb-2">
                                <span style="font-size: 2rem;">🇯🇵</span>
                                <h6>도쿄 (일본)</h6>
                                <small class="text-muted">인천/김포 ↔ 나리타/하네다</small>
                                <br>
                                <button class="btn btn-sm btn-outline-primary mt-2" onclick="selectDestination('도쿄')">
                                    선택하기
                                </button>
                            </div>
                        </div>
                        <div class="col-md-4 text-center">
                            <div class="border rounded p-3 mb-2">
                                <span style="font-size: 2rem;">🇹🇭</span>
                                <h6>방콕 (태국)</h6>
                                <small class="text-muted">인천 ↔ 수완나품/돈무앙</small>
                                <br>
                                <button class="btn btn-sm btn-outline-primary mt-2" onclick="selectDestination('방콕')">
                                    선택하기
                                </button>
                            </div>
                        </div>
                        <div class="col-md-4 text-center">
                            <div class="border rounded p-3 mb-2">
                                <span style="font-size: 2rem;">🇸🇬</span>
                                <h6>싱가포르</h6>
                                <small class="text-muted">인천 ↔ 창이공항</small>
                                <br>
                                <button class="btn btn-sm btn-outline-primary mt-2" onclick="selectDestination('싱가포르')">
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
                        <i class="fas fa-star text-warning"></i> 해외여행 검색의 특별함
                    </h4>
                    <div class="row">
                        <div class="col-md-3 text-center">
                            <div class="feature-icon">
                                <i class="fas fa-globe"></i>
                            </div>
                            <h6>전국 공항 검색</h6>
                            <p class="text-muted small">인천, 김포, 부산, 제주 등 모든 공항에서 출발하는 항공편</p>
                        </div>
                        <div class="col-md-3 text-center">
                            <div class="feature-icon">
                                <i class="fas fa-won-sign"></i>
                            </div>
                            <h6>최저가 발견</h6>
                            <p class="text-muted small">여러 출발지를 비교해서 가장 저렴한 항공편 선택</p>
                        </div>
                        <div class="col-md-3 text-center">
                            <div class="feature-icon">
                                <i class="fas fa-clock"></i>
                            </div>
                            <h6>시간 절약</h6>
                            <p class="text-muted small">목적지 하나만 선택하면 모든 경로를 자동 검색</p>
                        </div>
                        <div class="col-md-3 text-center">
                            <div class="feature-icon">
                                <i class="fas fa-route"></i>
                            </div>
                            <h6>다양한 선택</h6>
                            <p class="text-muted small">시간대, 공항, 항공사별로 최적의 옵션 제공</p>
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
                <i class="fas fa-globe-asia"></i> Hee Transport - 통합 교통 조회 시스템 | 
                <a href="${pageContext.request.contextPath}/">홈</a> | 
                <a href="${pageContext.request.contextPath}/travel/domestic">국내여행</a> | 
                <a href="${pageContext.request.contextPath}/travel/international" class="text-primary">해외여행</a>
            </p>
        </div>
    </footer>

    <!-- JavaScript -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        let selectedDestination = null;
        let selectedRegion = 'all';

        // 목적지 데이터
        const destinations = {
            japan: [
                { name: '도쿄', country: '일본', flag: '🇯🇵' },
                { name: '오사카', country: '일본', flag: '🇯🇵' },
                { name: '나고야', country: '일본', flag: '🇯🇵' },
                { name: '후쿠오카', country: '일본', flag: '🇯🇵' },
                { name: '삿포로', country: '일본', flag: '🇯🇵' }
            ],
            china: [
                { name: '베이징', country: '중국', flag: '🇨🇳' },
                { name: '상하이', country: '중국', flag: '🇨🇳' },
                { name: '광저우', country: '중국', flag: '🇨🇳' }
            ],
            southeast: [
                { name: '방콕', country: '태국', flag: '🇹🇭' },
                { name: '싱가포르', country: '싱가포르', flag: '🇸🇬' },
                { name: '쿠알라룸푸르', country: '말레이시아', flag: '🇲🇾' },
                { name: '자카르타', country: '인도네시아', flag: '🇮🇩' }
            ],
            usa: [
                { name: '뉴욕', country: '미국', flag: '🇺🇸' },
                { name: '로스앤젤레스', country: '미국', flag: '🇺🇸' },
                { name: '시애틀', country: '미국', flag: '🇺🇸' }
            ],
            europe: [
                { name: '런던', country: '영국', flag: '🇬🇧' },
                { name: '파리', country: '프랑스', flag: '🇫🇷' },
                { name: '로마', country: '이탈리아', flag: '🇮🇹' }
            ]
        };

        document.addEventListener('DOMContentLoaded', function() {
            console.log('해외여행 페이지 로드 완료');
            
            // 오늘 날짜로 초기화
            const today = new Date().toISOString().split('T')[0];
            document.getElementById('departureDate').value = today;
            
            // 출발 지역 탭 이벤트
            document.querySelectorAll('.departure-region .region-tab').forEach(tab => {
                tab.addEventListener('click', function() {
                    document.querySelectorAll('.departure-region .region-tab').forEach(t => t.classList.remove('active'));
                    this.classList.add('active');
                    selectedRegion = this.dataset.region;
                    document.getElementById('departureRegion').value = selectedRegion;
                });
            });
            
            // 목적지 지역 탭 이벤트
            document.querySelectorAll('[data-destinations]').forEach(tab => {
                tab.addEventListener('click', function() {
                    document.querySelectorAll('[data-destinations]').forEach(t => t.classList.remove('active'));
                    this.classList.add('active');
                    
                    const region = this.dataset.destinations;
                    loadDestinations(region);
                });
            });
            
            // ✅ 수정: 초기 일본 목적지 로드
            loadDestinations('japan');
            
            // 폼 검증
            document.getElementById('internationalSearchForm').addEventListener('submit', function(e) {
                console.log('폼 제출 시도:', selectedDestination);
                if (!selectedDestination) {
                    e.preventDefault();
                    alert('목적지를 선택해주세요.');
                    return false;
                }
            });
        });

        // 목적지 목록 로드 (DOM 조작 방식으로 변경)
        function loadDestinations(region) {
            console.log('목적지 로드 시작:', region);
            
            const grid = document.getElementById('destinationGrid');
            if (!grid) {
                console.error('destinationGrid 요소를 찾을 수 없습니다');
                return;
            }
            
            // 기존 내용 제거
            grid.innerHTML = '';
            
            if (!destinations[region]) {
                console.error('지역 데이터 없음:', region);
                return;
            }
            
            console.log(`${region} 지역에 ${destinations[region].length}개 목적지 로딩...`);
            
            destinations[region].forEach((dest, index) => {
                console.log(`목적지 생성: ${dest.name} (${dest.country})`);
                
                const item = document.createElement('div');
                item.className = 'destination-item';
                item.dataset.destination = dest.name;
                item.dataset.region = region;
                
                // 스타일 설정
                item.style.cssText = `
                    padding: 1rem;
                    border: 2px solid #dee2e6;
                    border-radius: 12px;
                    cursor: pointer;
                    background: white;
                    color: black;
                    text-align: center;
                    transition: all 0.3s;
                    margin-bottom: 1rem;
                `;
                
                // 템플릿 리터럴 대신 개별 DOM 요소 생성
                const flag = document.createElement('span');
                flag.className = 'country-flag';
                flag.textContent = dest.flag;
                flag.style.cssText = 'font-size: 2rem; display: block; margin-bottom: 0.5rem;';
                
                const name = document.createElement('div');
                name.className = 'destination-name';
                name.textContent = dest.name;
                name.style.cssText = 'font-weight: bold; color: black; font-size: 16px; margin-bottom: 0.25rem;';
                
                const country = document.createElement('div');
                country.className = 'destination-country';
                country.textContent = dest.country;
                country.style.cssText = 'color: #666; font-size: 14px;';
                
                // 요소들 조립
                item.appendChild(flag);
                item.appendChild(name);
                item.appendChild(country);
                
                // 클릭 이벤트 등록
                item.addEventListener('click', function() {
                    console.log('목적지 클릭:', dest.name);
                    
                    // 기존 선택 해제
                    document.querySelectorAll('.destination-item').forEach(el => {
                        el.classList.remove('selected');
                        el.style.backgroundColor = 'white';
                        el.style.color = 'black';
                        // 하위 요소 색상도 복원
                        const nameEl = el.querySelector('.destination-name');
                        const countryEl = el.querySelector('.destination-country');
                        if (nameEl) nameEl.style.color = 'black';
                        if (countryEl) countryEl.style.color = '#666';
                    });
                    
                    // 현재 아이템 선택
                    this.classList.add('selected');
                    this.style.backgroundColor = '#667eea';
                    this.style.color = 'white';
                    
                    // 선택된 아이템의 하위 텍스트들도 흰색으로 변경
                    const nameEl = this.querySelector('.destination-name');
                    const countryEl = this.querySelector('.destination-country');
                    if (nameEl) nameEl.style.color = 'white';
                    if (countryEl) countryEl.style.color = 'white';
                    
                    // 전역 변수 및 input 설정
                    selectedDestination = dest.name;
                    document.getElementById('destination').value = selectedDestination;
                    
                    console.log('목적지 선택 완료:', selectedDestination);
                });
                
                // hover 효과 추가
                item.addEventListener('mouseenter', function() {
                    if (!this.classList.contains('selected')) {
                        this.style.backgroundColor = '#f8f9fa';
                        this.style.borderColor = '#667eea';
                        this.style.transform = 'translateY(-2px)';
                    }
                });
                
                item.addEventListener('mouseleave', function() {
                    if (!this.classList.contains('selected')) {
                        this.style.backgroundColor = 'white';
                        this.style.borderColor = '#dee2e6';
                        this.style.transform = 'translateY(0)';
                    }
                });
                
                // DOM에 추가
                grid.appendChild(item);
                console.log(`DOM 추가 완료: ${dest.name}`);
            });
            
            // 최종 확인
            const addedItems = grid.querySelectorAll('.destination-item');
            console.log(`최종 결과: ${addedItems.length}개 목적지 아이템이 DOM에 추가됨`);
            
            console.log(`${region} 목적지 로딩 완료`);
        }

        // 인기 목적지 선택
        // 개선된 selectDestination 함수
        function selectDestination(destination) {
            console.log('인기 목적지 선택 시작:', destination);
            
            // 즉시 값 설정
            selectedDestination = destination;
            document.getElementById('destination').value = destination;
            
            // 해당 지역 탭으로 변경
            let region = 'japan';
            for (const [key, dests] of Object.entries(destinations)) {
                if (dests.some(d => d.name === destination)) {
                    region = key;
                    break;
                }
            }
            
            // 지역 탭 변경
            document.querySelectorAll('[data-destinations]').forEach(t => t.classList.remove('active'));
            const targetTab = document.querySelector(`[data-destinations="${region}"]`);
            if (targetTab) {
                targetTab.classList.add('active');
            }
            
            // 목적지 그리드 로드
            loadDestinations(region);
            
            // 1. 즉시 시각적 피드백 제공
            showDestinationSelectedFeedback(destination);
            
            // 2. 부드러운 스크롤 애니메이션으로 검색 폼으로 이동
            scrollToSearchForm();
            
            // 3. 목적지 그리드에서 선택 표시 (약간의 딜레이 후)
            setTimeout(() => {
                const targetItem = document.querySelector(`[data-destination="${destination}"]`);
                if (targetItem) {
                    // 기존 선택 해제
                    document.querySelectorAll('.destination-item').forEach(el => {
                        el.classList.remove('selected');
                        el.style.backgroundColor = 'white';
                        el.style.color = 'black';
                        const nameEl = el.querySelector('.destination-name');
                        const countryEl = el.querySelector('.destination-country');
                        if (nameEl) nameEl.style.color = 'black';
                        if (countryEl) countryEl.style.color = '#666';
                    });
                    
                    // 새로운 목적지 선택 표시
                    targetItem.classList.add('selected');
                    targetItem.style.backgroundColor = '#667eea';
                    targetItem.style.color = 'white';
                    
                    const nameEl = targetItem.querySelector('.destination-name');
                    const countryEl = targetItem.querySelector('.destination-country');
                    if (nameEl) nameEl.style.color = 'white';
                    if (countryEl) countryEl.style.color = 'white';
                }
            }, 300);
            
            // 4. 내일 날짜로 설정
            const tomorrow = new Date();
            tomorrow.setDate(tomorrow.getDate() + 1);
            document.getElementById('departureDate').value = tomorrow.toISOString().split('T')[0];
            
            console.log('목적지 선택 완료:', destination);
        }
        
        function showDestinationSelectedFeedback(destination) {
            // 목적지 입력 필드 하이라이트
            const destinationInput = document.getElementById('destination');
            destinationInput.style.transition = 'all 0.3s ease';
            destinationInput.style.backgroundColor = '#d4edda';
            destinationInput.style.borderColor = '#28a745';
            destinationInput.style.transform = 'scale(1.02)';
            
            // 토스트 알림 생성
            createToastNotification(`${destination} 목적지가 선택되었습니다!`);
            
            // 3초 후 원래 상태로 복원
            setTimeout(() => {
                destinationInput.style.backgroundColor = '';
                destinationInput.style.borderColor = '';
                destinationInput.style.transform = '';
            }, 3000);
        }

        // 부드러운 스크롤 함수
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
        
        // 토스트 알림 생성 함수
        function createToastNotification(message) {
            // 기존 토스트 제거
            const existingToast = document.querySelector('.destination-toast');
            if (existingToast) {
                existingToast.remove();
            }
            
            // 토스트 엘리먼트 생성
            const toast = document.createElement('div');
            toast.className = 'destination-toast';
            toast.innerHTML = `
                <i class="fas fa-check-circle me-2"></i>
                ${message}
            `;
            
            // 토스트 스타일
            toast.style.cssText = `
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

        // 추가: 검색 버튼에 펄스 효과 (선택사항)
        function highlightSearchButton() {
            const searchBtn = document.querySelector('.btn-search');
            if (searchBtn) {
                searchBtn.style.animation = 'pulse 1s ease-in-out 2';
                setTimeout(() => {
                    searchBtn.style.animation = '';
                }, 2000);
            }
        }

        // CSS 애니메이션 추가 (페이지 로드 시 head에 추가)
        function addPulseAnimation() {
            const style = document.createElement('style');
            style.textContent = `
                @keyframes pulse {
                    0% { transform: scale(1); }
                    50% { transform: scale(1.05); }
                    100% { transform: scale(1); }
                }
            `;
            document.head.appendChild(style);
        }

        // 페이지 로드 시 애니메이션 추가
        document.addEventListener('DOMContentLoaded', function() {
            addPulseAnimation();
        });

        // 개선된 selectDestination 함수에 검색 버튼 하이라이트 추가
        function selectDestination(destination) {
            console.log('인기 목적지 선택 시작:', destination);
            
            selectedDestination = destination;
            document.getElementById('destination').value = destination;
            
            let region = 'japan';
            for (const [key, dests] of Object.entries(destinations)) {
                if (dests.some(d => d.name === destination)) {
                    region = key;
                    break;
                }
            }
            
            document.querySelectorAll('[data-destinations]').forEach(t => t.classList.remove('active'));
            const targetTab = document.querySelector(`[data-destinations="${region}"]`);
            if (targetTab) {
                targetTab.classList.add('active');
            }
            
            loadDestinations(region);
            
            // 시각적 피드백들
            showDestinationSelectedFeedback(destination);
            scrollToSearchForm();
            
            // 1초 후 검색 버튼 하이라이트
            setTimeout(() => {
                highlightSearchButton();
            }, 1000);
            
            setTimeout(() => {
                const targetItem = document.querySelector(`[data-destination="${destination}"]`);
                if (targetItem) {
                    document.querySelectorAll('.destination-item').forEach(el => {
                        el.classList.remove('selected');
                        el.style.backgroundColor = 'white';
                        el.style.color = 'black';
                        const nameEl = el.querySelector('.destination-name');
                        const countryEl = el.querySelector('.destination-country');
                        if (nameEl) nameEl.style.color = 'black';
                        if (countryEl) countryEl.style.color = '#666';
                    });
                    
                    targetItem.classList.add('selected');
                    targetItem.style.backgroundColor = '#667eea';
                    targetItem.style.color = 'white';
                    
                    const nameEl = targetItem.querySelector('.destination-name');
                    const countryEl = targetItem.querySelector('.destination-country');
                    if (nameEl) nameEl.style.color = 'white';
                    if (countryEl) countryEl.style.color = 'white';
                }
            }, 300);
            
            const tomorrow = new Date();
            tomorrow.setDate(tomorrow.getDate() + 1);
            document.getElementById('departureDate').value = tomorrow.toISOString().split('T')[0];
            
            console.log('목적지 선택 완료:', destination);
        }       
        

        // 폼 초기화
        function clearForm() {
            console.log('폼 초기화');
            
            selectedDestination = null;
            selectedRegion = 'all';
            
            document.getElementById('internationalSearchForm').reset();
            document.querySelectorAll('.region-tab').forEach(tab => tab.classList.remove('active'));
            document.querySelectorAll('.departure-region .region-tab').forEach((tab, index) => {
                if (index === 0) tab.classList.add('active');
            });
            document.querySelectorAll('[data-destinations]').forEach((tab, index) => {
                if (index === 0) tab.classList.add('active');
            });
            
            loadDestinations('japan');
            
            // 오늘 날짜로 재설정
            const today = new Date().toISOString().split('T')[0];
            document.getElementById('departureDate').value = today;
            document.getElementById('departureRegion').value = 'all';
        }

        // 키보드 단축키
        document.addEventListener('keydown', function(e) {
            if (e.ctrlKey && e.key === 'Enter') {
                document.getElementById('internationalSearchForm').submit();
            }
        });
    </script>
</body>
</html>