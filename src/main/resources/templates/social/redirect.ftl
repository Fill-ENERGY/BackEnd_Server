<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>카카오 OAuth 정보</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f7f7f7;
            color: #333;
            text-align: center;
            padding-top: 50px;
        }
        #container {
            background-color: white;
            border-radius: 8px;
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
            display: inline-block;
            padding: 30px;
            width: 350px;
        }
        h1 {
            color: #ffeb00;
            font-size: 24px;
            margin-bottom: 20px;
        }
        ol {
            text-align: left;
            padding: 0;
            margin: 20px 0;
        }
        li {
            background-color: #fafafa;
            border-radius: 4px;
            margin-bottom: 10px;
            padding: 10px;
            border: 1px solid #ddd;
        }
        li span {
            font-weight: bold;
            color: #555;
        }
    </style>
</head>
<body>
<div id="container">
    <h1>카카오 OAuth 정보</h1>
    <ol>
        <li><span>token_type:</span> ${authInfo.token_type}</li>
        <li><span>access_token:</span> ${authInfo.access_token}</li>
        <li><span>expires_in:</span> ${authInfo.expires_in}</li>
        <li><span>refresh_token:</span> ${authInfo.refresh_token}</li>
        <li><span>refresh_token_expires_in:</span> ${authInfo.refresh_token_expires_in}</li>
        <li><span>scope:</span> ${authInfo.scope}</li>
    </ol>
</div>
</body>
</html>
