<!-- NOTE: Custom Error Page with FreeMarker -->
<!DOCTYPE html>
<html lang="${.lang}">
<head>
    <title>${status!""} - ${error!"None"}</title>
    <meta charset="UTF-8">
</head>
<body>
<h1>Oops!</h1>
<div>Error: ${error!"None"}</div>
<div>Message: ${message!""}</div>
<div>Status: ${status!""}</div>
<div>Timestamp: ${timestamp?datetime?iso_utc}</div>
</body>
</html>