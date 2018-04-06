<#-- @ftlvariable name="config" type="com.slyak.zzbid.model.Config" -->
<#-- @ftlvariable name="page" type="org.springframework.data.domain.Page<com.slyak.zzbid.model.Bid>" -->
<#-- @ftlvariable name="bid" type="com.slyak.zzbid.model.Bid" -->
<@layout.main tab=0>
<h4>自动投标记录</h4>
<hr>
<table class="table" style="font-size: 12px">
    <thead class="thead-light">
    <tr>
        <th scope="col">分包编号</th>
        <th scope="col">类别</th>
        <th scope="col">单位</th>
        <th scope="col">任务开始时间</th>
        <th scope="col">自动投标时间</th>
        <th scope="col">投标快照</th>
        <th scope="col">目前情况</th>
    </tr>
    </thead>
    <tbody>
        <#list page.content as bid>
        <tr>
            <th scope="row">${bid.id}</th>
            <td>${bid.firstType}</td>
            <td>${bid.dept}</td>
            <td><#if bid.taskTime gt 0>${bid.taskTime?number_to_date?string("HH:mm:ss")}</#if></td>
            <td><#if bid.bidTime gt 0>${bid.bidTime?number_to_date?string("HH:mm:ss")}</#if></td>
            <td><#--${bid.snapshot}-->建设中</td>
            <td><#--${bid.snapshot}-->建设中</td>
        </tr>
        </#list>
    </tbody>
</table>
    <@bootstrap.pagination relativeUrl='/' showNumber=2 value=page classes=["justify-content-center"]/>
</@layout.main>