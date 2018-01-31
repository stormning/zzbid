<#-- @ftlvariable name="config" type="com.slyak.zzbid.model.Config" -->
<#-- @ftlvariable name="page" type="org.springframework.data.domain.Page<com.slyak.zzbid.model.Bid>" -->
<#-- @ftlvariable name="bid" type="com.slyak.zzbid.model.Bid" -->
<@layout.main tab=0>
<h4>自动投标记录</h4>
<hr>
<table class="table">
    <thead class="thead-light">
    <tr>
        <th scope="col">分包编号</th>
        <th scope="col">类别</th>
        <th scope="col">单位</th>
        <th scope="col">开始时间</th>
        <th scope="col">结束时间</th>
        <th scope="col">自动投标时间</th>
    </tr>
    </thead>
    <tbody>
        <#list page.content as bid>
        <tr>
            <th scope="row">${bid.id}</th>
            <td>${bid.firstType}</td>
            <td>${bid.dept}</td>
            <td>${bid.startTime}</td>
            <td>${bid.endTime}</td>
            <td><#if bid.bidTime gt 0>${bid.bidTime?number_to_date?string("yyyy-MM-dd HH:mm:ss")}</#if></td>
        </tr>
        </#list>
    </tbody>
</table>
    <@bootstrap.pagination relativeUrl='/' showNumber=2 value=page classes=["justify-content-center"]/>
</@layout.main>