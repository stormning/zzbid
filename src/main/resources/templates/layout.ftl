<#ftl strip_whitespace=true>
<#-- @ftlvariable name="config" type="com.slyak.zzbid.model.Config" -->
<#macro main tab=0>
<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Document</title>
    <@slyak.js url=['/jquery/jquery-3.2.1.min.js','/jquery/jquery-migrate-3.0.1.min.js','/popper.js','/bootstrap/js/bootstrap.min.js']/>
    <@slyak.css url=['/bootstrap/css/bootstrap.min.css','/custom.css']/>
    <#if tab==1>
        <@slyak.js url=['/validation/js/jquery.validationEngine-zh_CN.js','/validation/js/jquery.validationEngine.min.js']/>
        <@slyak.css url='/validation/css/validationEngine.jquery.css'/>
    </#if>
</head>
<body>
<nav class="navbar navbar-expand-lg navbar-dark bg-dark bd-navbar">
    <div class="container">
        <a class="navbar-brand mr-5" href="#">投标E</a>
        <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent"
                aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse">
            <ul class="navbar-nav mr-auto">
                <li class="nav-item<#if tab==0> active</#if>">
                    <a class="nav-link" href="<@spring.url relativeUrl='/'/>">自动投标记录 <span
                            class="sr-only">(current)</span></a>
                </li>
                <li class="nav-item<#if tab==1> active</#if>">
                    <a class="nav-link" href="<@spring.url relativeUrl='/config'/>">系统设置</a>
                </li>
            </ul>
        </div>
    </div>
</nav>
<div class="container">
    <main class="pt-5 pb-5 position-relative">
        <#nested />
    </main>
</div>
<div class="modal" tabindex="-1" role="dialog" id="captchaDialog">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">填写登陆验证码</h5>
            </div>
            <div class="modal-body">
                <form class="form-inline" id="captchaForm" method="post"
                      action="<@spring.url relativeUrl='/startBid'/>">
                    <label class="mr-sm-2" for="inlineFormCustomSelectPref">验证码</label>
                    <input type="text" class="form-control mb-2 mr-sm-2 mb-sm-0" id="captcha" name="captcha"
                           placeholder="验证码">
                    <input type="hidden" name="sessionId" id="sessionId">
                    <div class="mb-2" id="captchaHolder"></div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-primary" id="startBid">开始自动投标</button>
                <button type="button" class="btn btn-secondary" data-dismiss="modal" id="dismiss">暂不处理</button>
            </div>
        </div>
    </div>
</div>
<script>
        <#if config??&&config.isValid()&&config.start>
        var processing = false;
        var dismiss = false;
        var dialog = $("#captchaDialog").on("hide.bs.modal", function () {
            processing = false;
        });
        dialog.find("#dismiss").click(function () {
            dismiss = true;
        });
        dialog.find("#startBid").click(function () {
            $("#captchaForm").submit();
        });

        var checkSession = function () {
            if (!dismiss && !processing) {
                processing = true;
                $.get("<@spring.url relativeUrl='/nextSessionId'/>", function (sessionId) {
                    if (sessionId) {
                        $("#sessionId").val(sessionId);
                        var captchaUrl = "<@spring.url relativeUrl='/captcha'/>" + "/" + sessionId;
                        dialog.find("#captchaHolder").html("<img src='" + captchaUrl + "'/>");
                        dialog.modal("show");
                    }
                }).fail(function () {
                    alert("投标网站暂时无法访问，请核实！");
                });
            }
        };
        checkSession();
        window.setInterval(checkSession, 5000);
        </#if>
</script>
</body>
</html>
</#macro>