<#-- @ftlvariable name="config" type="com.slyak.zzbid.model.Config" -->
<@layout.main tab=1>
<h4>配置</h4>
<hr>
    <#if !config.isValid()>
    <div class="alert alert-danger" role="alert">自动投标运行前，请先完善配置信息！</div>
    </#if>
<form id="configForm" method="post" action="<@spring.url relativeUrl='/config'/>" autocomplete="off">
    <div class="form-group row">
        <@spring.formHiddenInput path="config.id"/>
        <input type="hidden" name="id" value="${config.id}">
        <label for="name" class="col-sm-2 col-form-label">用户名</label>
        <div class="col-sm-6">
            <input id="name" type="text" class="form-control validate[required]" placeholder="请输入用户名" name="name" value="${config.name}" autocomplete="off">
        </div>
    </div>
    <div class="form-group row">
        <label for="password" class="col-sm-2 col-form-label">密码</label>
        <div class="col-sm-6">
            <input id="password" type="password" class="form-control validate[required]" placeholder="请输入密码" name="password" value="${config.password}" autocomplete="off">
        </div>
    </div>
    <div class="form-group row">
        <label for="interval" class="col-sm-2 col-form-label">投标时间间隔(秒)</label>
        <div class="col-sm-6">
            <input id="interval" type="text" class="form-control validate[required,custom[integer],min[1]]" placeholder="间隔：不得小于1秒" name="interval" value="${config.interval}">
        </div>
    </div>
    <div class="form-group row">
        <label for="money" class="col-sm-2 col-form-label">投标金额(元)</label>
        <div class="col-sm-6">
            <input id="money" type="text" class="form-control validate[required,custom[number],min[0]]" placeholder="金额：大于等于0" name="money" value="${config.money}">
        </div>
    </div>
    <div class="form-group row">
        <label for="retry" class="col-sm-2 col-form-label">失败重试次数</label>
        <div class="col-sm-6">
            <input id="retry" type="text" class="form-control validate[required,custom[integer]]" placeholder="次数：小于等于0则不重试" name="retry" value="${config.retry}">
        </div>
    </div>
    <div class="form-group row">
        <label for="retry" class="col-sm-2 col-form-label">自动投标时间段</label>
        <div class="col-sm-6">
            <div class="form-control-plaintext">
                <input id="retry" type="text" class="form-control validate[custom[condition]]" placeholder="格式08:45-09:20" name="condition" value="${config.condition}">
            </div>
        </div>
    </div>
    <div class="form-group row">
        <label for="retry" class="col-sm-2 col-form-label">自动投标开关</label>
        <div class="col-sm-6">
            <div class="form-control-plaintext">
                <span class="form-text d-inline mr-2">开启</span><input name="start" type="radio" value="true" <#if config.start>checked</#if>>&nbsp;&nbsp;&nbsp;&nbsp;<span class="form-text d-inline mr-2">关闭</span><input name="start" type="radio" value="false" <#if !config.start>checked</#if>>
            </div>
        </div>
    </div>
    <button class="btn btn-primary" type="submit">保存配置信息</button>
</form>
<script>
    $(function () {
        $.validationEngineLanguage.allRules.condition = {
            "regex":/^(([0-1]\d|2[0-3]):[0-5]\d)-(([0-1]\d|2[0-3]):[0-5]\d)$/,
            'alertText': '格式08:45-09:20'
        };
        $('#configForm').validationEngine('attach', {
            promptPosition: 'centerRight',
            scroll: false
        }).andSelf().validationEngine('validate');
    })
</script>
</@layout.main>