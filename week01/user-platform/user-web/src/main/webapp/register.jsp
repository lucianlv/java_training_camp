<head>
    <jsp:directive.include
            file="/WEB-INF/jsp/prelude/include-head-meta.jspf" />
    <title>注册</title>
</head>
<body>
<div class="container-lg">
    <form class="form-horizontal" role="form" action="${pageContext.request.contextPath}/register" method="post">
        <dev class="form-group row">
            <h2>注册</h2>
        </dev>
        <div class="form-group row">
            <label class="col-sm-1 col-form-label">用户名</label>
            <dev class="col-sm-4">
                <input type="text" class="form-control" name="username" required autofocus>
            </dev>
        </div>
        <div class="form-group row">
            <label class="col-sm-1 col-form-label">邮箱</label>
            <dev class="col-sm-4">
                <input type="email" class="form-control" name="email" required>
            </dev>
        </div>
        <div class="form-group row">
            <label class="col-sm-1 col-form-label">手机号码</label>
            <dev class="col-sm-4">
                <input type="text" class="form-control" name="phone" required>
            </dev>
        </div>
        <div class="form-group row">
            <label class="col-sm-1 col-form-label">密码</label>
            <dev class="col-sm-4">
                <input type="password" class="form-control" name="password" required>
            </dev>
        </div>
        <div class="form-group">
            <div class="offset-sm-4 col-sm-4">
                <button type="submit" class="btn btn-warning">立即注册</button>
            </div>
        </div>
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