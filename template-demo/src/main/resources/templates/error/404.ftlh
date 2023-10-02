<!DOCTYPE html>
<html lang="${.lang}">
<head>
    <title>404 - Not Found</title>
    <meta charset="UTF-8">
</head>
<body>
<h1>Oops! Not Found</h1>
<div>Error: ${error}</div>
<div>Path: ${path}</div>
<div>Timestamp: ${timestamp?datetime?iso_utc}</div>
<p><a href="/">Go to main</a></p>
</body>
</html>