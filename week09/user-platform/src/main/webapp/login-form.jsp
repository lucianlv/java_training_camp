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

    .sing-up-btn {
      color: #235ea7;
      margin-top: 1.5em;
      border: none;
      background-color: transparent;
    }

    .form-signUp {
         text-align:center;
    }

    .error-h {
        color: red;
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
	    <h1 class="h3 mb-3 font-weight-normal">登录</h1>
	    <br/>
	    <h2 class="error-h">${message}</h2>
	    <br/>
		<form class="form-signin" action="/login" method="POST">
			<label for="inputEmail" class="sr-only">请输出电子邮件</label>
			<input
				type="email" id="inputEmail" class="form-control" name="email"
				placeholder="请输入电子邮件" required autofocus />
			<br/>
			<label for="inputPassword" class="sr-only">Password</label>
			<input
				type="password" id="inputPassword" class="form-control" name="password"
				placeholder="请输入密码" required />
			<br/>
			<!-- <div class="checkbox mb-3">
				<label>
                    <input type="checkbox" value="remember-me">
                        Remember me
				</label>
			</div> -->
			<button class="btn btn-lg btn-primary btn-block" type="submit">登录</button>
		</form>

		<fieldset class="form-signUp">
            <form  action="/" method="GET">
                <span>还没有账号？</span>
                <button class="sing-up-btn"  type="submit">注册</button>
            </form>
        </fieldset>

        <p class="mt-5 mb-3 text-muted">&copy; 2017-2021</p>
	</div>
</body>