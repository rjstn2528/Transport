<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Hee Transport - 통합 교통편 조회</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <style>
        body {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }
        .main-container {
            max-width: 1200px;
            margin: 0 auto;
            padding: 50px 20px;
        }
        .header {
            text-align: center;
            color: white;
            margin-bottom: 60px;
        }
        .header h1 {
            font-size: 3.5rem;
            font-weight: bold;
            margin-bottom: 20px;
            text-shadow: 2px 2px 4px rgba(0,0,0,0.3);
        }
        .header p {
            font-size: 1.3rem;
            opacity: 0.9;
        }
        .transport-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
            gap: 30px;
            margin-bottom: 50px;
        }
        .transport-card {
            background: white;
            border-radius: 20px;
            padding: 40px 30px;
            text-align: center;
            box-shadow: 0 15px 35px rgba(0,0,0,0.1);
            transition: all 0.3s ease;
            text-decoration: none;
            color: inherit;
            position: relative;
            overflow: hidden;
            min-height: 280px; /* 모든 카드 동일한 최소 높이 */
            display: flex;
            flex-direction: column;
            justify-content: center;
        }
        .transport-card::before {
            content: '';
            position: absolute;
            top: 0;
            left: -100%;
            width: 100%;
            height: 100%;
            background: linear-gradient(90deg, transparent, rgba(255,255,255,0.4), transparent);
            transition: left 0.5s;
        }
        .transport-card:hover::before {
            left: 100%;
        }
        .transport-card:hover {
            transform: translateY(-10px);
            box-shadow: 0 25px 50px rgba(0,0,0,0.2);
            text-decoration: none;
            color: inherit;
        }
        .transport-icon {
            font-size: 4rem;
            margin-bottom: 20px;
            display: block;
        }
        .transport-card.train .transport-icon { color: #007bff; }
        .transport-card.bus .transport-icon { color: #28a745; }
        .transport-card.flight .transport-icon { color: #fd7e14; }
        .transport-card.ferry .transport-icon { color: #20c997; }
        .transport-card.domestic .transport-icon { color: #4facfe; }

        .transport-card h3 {
            font-size: 1.5rem;
            font-weight: bold;
            margin-bottom: 15px;
        }
        .transport-card p {
            color: #6c757d;
            margin-bottom: 20px;
            line-height: 1.5;
        }
        .status-badge {
            padding: 8px 16px;
            border-radius: 20px;
            font-size: 0.9rem;
            font-weight: bold;
            margin-top: auto;
        }
        .status-active {
            background: #d4edda;
            color: #155724;
        }
        .status-coming {
            background: #fff3cd;
            color: #856404;
        }
        .features {
            background: rgba(255,255,255,0.1);
            border-radius: 15px;
            padding: 40px;
            margin-top: 50px;
            color: white;
        }
        .features h2 {
            text-align: center;
            margin-bottom: 30px;
            font-size: 2rem;
        }
        .feature-list {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 20px;
        }
        .feature-item {
            display: flex;
            align-items: center;
            padding: 15px;
            background: rgba(255,255,255,0.1);
            border-radius: 10px;
        }
        .feature-item i {
            font-size: 1.5rem;
            margin-right: 15px;
            color: #ffd700;
        }
        
        /* 상단 통합 검색 카드 스타일 */
        .top-cards {
            margin-bottom: 40px;
        }
        .top-card {
            height: 280px; /* 하단 카드들과 동일한 높이 */
        }
        
        @media (max-width: 768px) {
            .header h1 {
                font-size: 2.5rem;
            }
            .transport-grid {
                grid-template-columns: 1fr;
            }
            .top-card {
                height: 250px;
                margin-bottom: 20px;
            }
            .transport-card {
                min-height: 250px;
            }
        }
    </style>
</head>
<body>
    <div class="main-container">
        <div class="header">
            <h1>🚀 Hee Transport</h1>
            <p>빠르고 편리한 통합 교통편 조회 서비스</p>
        </div>

        <!-- 상단: 통합 여행 검색 (2개) -->
        <div class="row top-cards mb-4">
            <div class="col-md-6 mb-3">
                <a href="${pageContext.request.contextPath}/travel/domestic" class="transport-card domestic top-card">
                    <i class="fas fa-map-marked-alt transport-icon"></i>
                    <h3>국내여행 통합검색</h3>
                    <p>출발지→도착지 입력으로<br>항공편·기차·버스를 한번에 비교</p>
                    <span class="status-badge status-active">이용 가능</span>
                </a>
            </div>
            <div class="col-md-6 mb-3">
                <a href="${pageContext.request.contextPath}/travel/international" class="transport-card ferry top-card">
                    <i class="fas fa-globe-asia transport-icon"></i>
                    <h3>해외여행 항공검색</h3>
                    <p>목적지 선택만으로<br>모든 출발지 항공편을 쉽게 검색</p>
                    <span class="status-badge status-active">이용 가능</span>
                </a>
            </div>
        </div>

        <!-- 하단: 개별 교통수단 (3개) -->
        <div class="transport-grid" style="grid-template-columns: repeat(3, 1fr);">
            <a href="${pageContext.request.contextPath}/transport/train" class="transport-card train">
                <i class="fas fa-train transport-icon"></i>
                <h3>기차 / KTX</h3>
                <p>전국 기차 시간표와 요금을<br>실시간으로 조회하세요</p>
                <span class="status-badge status-active">이용 가능</span>
            </a>

            <a href="${pageContext.request.contextPath}/transport/bus" class="transport-card bus">
                <i class="fas fa-bus transport-icon"></i>
                <h3>고속 / 시외버스</h3>
                <p>전국 시외버스 노선과<br>배차 정보를 확인하세요</p>
                <span class="status-badge status-active">이용 가능</span>
            </a>

            <a href="${pageContext.request.contextPath}/transport/flight" class="transport-card flight">
                <i class="fas fa-plane transport-icon"></i>
                <h3>항공편</h3>
                <p>국내외 항공편 스케줄과<br>가격을 비교해보세요</p>
                <span class="status-badge status-active">이용 가능</span>
            </a>
        </div>

        <div class="features">
            <h2>🌟 서비스 특징</h2>
            <div class="feature-list">
                <div class="feature-item">
                    <i class="fas fa-clock"></i>
                    <span>실시간 정보 제공</span>
                </div>
                <div class="feature-item">
                    <i class="fas fa-mobile-alt"></i>
                    <span>모바일 최적화</span>
                </div>
                <div class="feature-item">
                    <i class="fas fa-search"></i>
                    <span>통합 검색 기능</span>
                </div>
                <div class="feature-item">
                    <i class="fas fa-chart-line"></i>
                    <span>가격 비교 분석</span>
                </div>
                <div class="feature-item">
                    <i class="fas fa-bookmark"></i>
                    <span>즐겨찾기 기능</span>
                </div>
                <div class="feature-item">
                    <i class="fas fa-bell"></i>
                    <span>알림 서비스</span>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>