<head>
    <jsp:directive.include file="/WEB-INF/jsp/prelude/include-head-meta.jspf" />
    <title>My Home Page</title>
    <style>
        .bd-placeholder-img {
            font-size: 1.125rem;
            text-anchor: middle;
            -webkit-user-select: none;
            -moz-user-select: none;
            -ms-user-select: none;
            user-select: none;
        }

        @media (min-width: 768px) {
            .bd-placeholder-img-lg {
                font-size: 3.5rem;
            }
        }
    </style>
</head>
<body>
<div class="container">
    <form class="form-signin" action="${pageContext.request.contextPath}/login" method="POST">
        <h1 class="h3 mb-3 font-weight-normal">登录</h1>
        <label for="inputEmail" class="sr-only">请输出电子邮件</label> <input
            type="email" id="inputEmail" name="email" class="form-control"
            placeholder="请输入电子邮件" required autofocus> <label
            for="inputPassword" class="sr-only">Password</label> <input
            type="password" id="inputPassword" name="password" class="form-control"
            placeholder="请输入密码" required>
        <div class="checkbox mb-3">
            <label> <input type="checkbox" value="remember-me">
                Remember me
            </label>
        </div>
        <button class="btn btn-lg btn-primary btn-block" type="submit">Sign
            in</button>
        <p class="mt-5 mb-3 text-muted">&copy; 2017-2021</p>
    </form>

    <!-- 出错显示的信息框 -->
    <c:if test="${ not empty err_msg }">
        <div class="alert alert-warning alert-dismissible" role="alert">
            <button type="button" class="close" data-dismiss="alert" >
                <span>&times;</span>
            </button>
            <strong>${err_msg}</strong>
        </div>
    </c:if>
</div>
</body>