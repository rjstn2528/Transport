<!-- index.jsp (webapp/index.jsp) -->
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Hee Transport - 통합 교통편 조회 서비스</title>
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            margin: 0;
            padding: 0;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
        }
        .welcome-container {
            text-align: center;
            color: white;
            max-width: 600px;
            padding: 40px;
        }
        .logo {
            font-size: 4rem;
            margin-bottom: 20px;
            animation: bounce 2s infinite;
        }
        .title {
            font-size: 3rem;
            font-weight: bold;
            margin-bottom: 20px;
            text-shadow: 2px 2px 4px rgba(0,0,0,0.3);
        }
        .subtitle {
            font-size: 1.3rem;
            margin-bottom: 40px;
            opacity: 0.9;
        }
        .btn-enter {
            background: rgba(255,255,255,0.2);
            border: 2px solid white;
            color: white;
            padding: 15px 30px;
            border-radius: 50px;
            text-decoration: none;
            font-size: 1.1rem;
            font-weight: bold;
            transition: all 0.3s;
            display: inline-block;
        }
        .btn-enter:hover {
            background: white;
            color: #667eea;
            transform: translateY(-2px);
            box-shadow: 0 10px 25px rgba(0,0,0,0.2);
            text-decoration: none;
        }
        @keyframes bounce {
            0%, 20%, 50%, 80%, 100% {
                transform: translateY(0);
            }
            40% {
                transform: translateY(-10px);
            }
            60% {
                transform: translateY(-5px);
            }
        }
        .loading {
            display: none;
            margin-top: 20px;
        }
        .loading-text {
            margin-top: 10px;
            font-size: 0.9rem;
            opacity: 0.8;
        }
    </style>
</head>
<body>
    <div class="welcome-container">
        <div class="logo">🚀</div>
        <h1 class="title">Hee Transport</h1>
        <p class="subtitle">빠르고 편리한 통합 교통편 조회 서비스</p>
        
        <a href="${pageContext.request.contextPath}/transport/" class="btn-enter" onclick="showLoading()">
            🚄 서비스 시작하기
        </a>
        
        <div class="loading" id="loading">
            <div class="loading-text">시스템을 준비하고 있습니다...</div>
        </div>
    </div>

    <script>
        function showLoading() {
            document.getElementById('loading').style.display = 'block';
        }
        
        // 페이지 로드 시 환영 애니메이션
        window.addEventListener('load', function() {
            document.querySelector('.welcome-container').style.animation = 'fadeIn 1s ease-in';
        });
    </script>
</body>
</html>