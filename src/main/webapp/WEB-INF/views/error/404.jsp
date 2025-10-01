<!-- 404.jsp (WEB-INF/views/error/404.jsp) -->
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>404 - 페이지를 찾을 수 없습니다</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
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
        .error-container {
            text-align: center;
            color: white;
            max-width: 500px;
            padding: 40px;
        }
        .error-code {
            font-size: 8rem;
            font-weight: bold;
            margin-bottom: 20px;
            text-shadow: 2px 2px 4px rgba(0,0,0,0.3);
        }
        .error-message {
            font-size: 1.5rem;
            margin-bottom: 20px;
        }
        .error-description {
            font-size: 1rem;
            margin-bottom: 40px;
            opacity: 0.8;
        }
        .btn-home {
            background: rgba(255,255,255,0.2);
            border: 2px solid white;
            color: white;
            padding: 12px 25px;
            border-radius: 25px;
            text-decoration: none;
            font-weight: bold;
            transition: all 0.3s;
            display: inline-flex;
            align-items: center;
            gap: 10px;
        }
        .btn-home:hover {
            background: white;
            color: #667eea;
            text-decoration: none;
            transform: translateY(-2px);
        }
    </style>
</head>
<body>
    <div class="error-container">
        <div class="error-code">404</div>
        <h1 class="error-message">페이지를 찾을 수 없습니다</h1>
        <p class="error-description">
            요청하신 페이지가 존재하지 않거나 이동되었을 수 있습니다.
        </p>
        <a href="${pageContext.request.contextPath}/transport/" class="btn-home">
            <i class="fas fa-home"></i> 메인으로 돌아가기
        </a>
    </div>
</body>
</html>