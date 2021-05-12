<head>
<jsp:directive.include
	file="/WEB-INF/jsp/prelude/include-head-meta.jspf" />
<title>My Home Page</title>
</head>
<body>
	<div class="container-lg">
		<h1>${message}</h1>
		<form class="form-to-signIn" action="/" method="GET">
            <button class="btn btn-lg btn-primary btn-block" type="submit">跳回首页</button>
        </from>
	</div>
</body>