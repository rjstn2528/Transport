<!-- coming-soon.jsp (WEB-INF/views/transport/coming-soon.jsp) -->
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${title} - Hee Transport</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <style>
        body {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }
        .coming-soon-container {
            max-width: 600px;
            margin: 100px auto;
            padding: 40px;
            text-align: center;
        }
        .coming-soon-card {
            background: white;
            border-radius: 20px;
            padding: 60px 40px;
            box-shadow: 0 15px 35px rgba(0,0,0,0.1);
        }
        .icon {
            font-size: 5rem;
            color: #007bff;
            margin-bottom: 30px;
        }
        .title {
            font-size: 2rem;
            font-weight: bold;
            color: #333;
            margin-bottom: 20px;
        }
        .message {
            font-size: 1.1rem;
            color: #6c757d;
            margin-bottom: 40px;
        }
        .btn-home {
            background: linear-gradient(45deg, #007bff, #0056b3);
            color: white;
            padding: 15px 30px;
            border: none;
            border-radius: 10px;
            text-decoration: none;
            display: inline-flex;
            align-items: center;
            gap: 10px;
            font-weight: bold;
            transition: all 0.3s;
        }
        .btn-home:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 20px rgba(0,123,255,0.3);
            color: white;
            text-decoration: none;
        }
        .features {
            margin-top: 40px;
            padding-top: 30px;
            border-top: 1px solid #e9ecef;
        }
        .feature-item {
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 10px;
            margin-bottom: 15px;
            color: #6c757d;
        }
        .feature-item i {
            color: #007bff;
        }
    </style>
</head>
<body>
    <div class="coming-soon-container">
        <div class="coming-soon-card">
            <div class="icon">
                <i class="fas fa-tools"></i>
            </div>
            <h1 class="title">${title}</h1>
            <p class="message">${message}</p>
            
            <div class="features">
                <h5>곧 추가될 기능들</h5>
                <div class="feature-item">
                    <i class="fas fa-clock"></i>
                    <span>실시간 정보 제공</span>
                </div>
                <div class="feature-item">
                    <i class="fas fa-search"></i>
                    <span>통합 검색 기능</span>
                </div>
                <div class="feature-item">
                    <i class="fas fa-mobile-alt"></i>
                    <span>모바일 최적화</span>
                </div>
            </div>
            
            <a href="${pageContext.request.contextPath}/transport/" class="btn-home">
                <i class="fas fa-home"></i> 메인으로 돌아가기
            </a>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>