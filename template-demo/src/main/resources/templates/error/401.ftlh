<!DOCTYPE html>
<html lang="${.lang}">
<head>
    <title>401 - Unauthorized</title>
    <meta charset="UTF-8">
</head>
<body>
<h1>Oops! Not Authenticated</h1>
<div>Error: ${error}</div>
<div>Path: ${path}</div>
<div>Timestamp: ${timestamp?datetime?iso_utc}</div>
<p><a href="/">Go to main</a></p>
</body>
</html>