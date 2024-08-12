<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>카카오 OAuth 테스트 페이지</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f7f7f7;
            text-align: center;
            padding-top: 50px;
        }
        #container {
            background-color: white;
            border-radius: 8px;
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
            display: inline-block;
            padding: 30px;
            width: 300px;
        }
        h1 {
            color: #ffeb00;
            font-size: 24px;
            margin-bottom: 20px;
        }
        p {
            font-size: 16px;
            margin-bottom: 30px;
            color: #333;
        }
        button {
            background-color: #ffeb00;
            border: none;
            border-radius: 4px;
            color: black;
            font-size: 16px;
            padding: 10px 20px;
            cursor: pointer;
        }
        button:hover {
            background-color: #ffcc00;
        }
    </style>
</head>
<body>
<div id="container">
    <h1>카카오 OAuth<br>테스트 페이지</h1>
    <p>이 페이지는 카카오 OAuth 테스트 페이지입니다.</p>
    <button onclick="popupKakaoLogin()">KakaoLogin</button>
</div>
<script>
    function popupKakaoLogin() {
        window.open('${loginUrl}', 'popupKakaoLogin', 'width=730,height=400,scrollbars=0,toolbar=0,menubar=no')
    }
</script>
</body>
</html>
