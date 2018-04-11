<#-- @ftlvariable name="config" type="com.slyak.zzbid.model.Config" -->
<#-- @ftlvariable name="page" type="org.springframework.data.domain.Page<com.slyak.zzbid.model.Bid>" -->
<#-- @ftlvariable name="bid" type="com.slyak.zzbid.model.Bid" -->
<@layout.main tab=0>
<h4>自动投标记录</h4>
<hr>
<style>
    .modal-dialog{
        font-size: 14px !important;
    }
    .table th, .table td {
        text-align: center;
        vertical-align: middle!important;
    }

    .table td{
        font-size: 14px;
    }
</style>
<script>
    function showSnapshot(modal, btn) {
        return $("#snapshot" + btn.data("id")).html();
    }
    function showCurrent(modal, btn) {
        var result;
        $.get({
            url: "/snapshot?id=" + btn.data("id"),
            async: false,
            success: function (res) {
                result = res;
            }
        });
        return result;
    }
</script>
<table class="table">
    <thead class="thead-light">
    <tr>
        <th scope="col">分包编号</th>
        <th scope="col">类别</th>
        <th scope="col">单位</th>
        <th scope="col">入库时间</th>
        <th scope="col">投标状态</th>
        <th scope="col">投标快照</th>
        <th scope="col">目前情况</th>
    </tr>
    </thead>
    <tbody>
        <#list page.content as bid>
        <tr>
            <td>${bid.id}</td>
            <td>${bid.firstType?split(" ")?join("</br>")}</td>
            <td>${bid.dept}</td>
            <td>${bid.taskTime?number_to_date?string("hh:mm:ss:SSS")}</td>
            <td>
                <#if bid.bidTime gt 0>
                    已投
                    <#else >
                        未投
                </#if>
            </td>
            <td>
                <button type="button" class="btn btn-link btn-sm" data-toggle="modal" data-target="#snapshotModal"
                        data-id="${bid.id}">查看投标快照
                </button>
                <div class="d-none" id="snapshot${bid.id}">${bid.snapshot}</div>
            </td>
            <td><button type="button" class="btn btn-link btn-sm" data-toggle="modal" data-target="#currentModal"
                        data-id="${bid.id}">目前情况
            </button></td>
        </tr>
        </#list>
        <@bootstrap.model id="snapshotModal" title="投标快照" onShown='showSnapshot'/>
        <@bootstrap.model id="currentModal" title="目前情况" onShown='showCurrent'/>
    </tbody>
</table>
    <@bootstrap.pagination relativeUrl='/' showNumber=2 value=page classes=["justify-content-center"]/>
</@layout.main>