<!DOCTYPE html>
<%@page import="com.sivalabs.springapp.web.controllers.UserController" %>
<%@include file="taglib.jsp" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>Welcome</title>
    <link rel="stylesheet" type="text/css" href="${rootURL}resources/ext/resources/css/ext-all.css"/>
    <link rel="stylesheet" type="text/css" href="${rootURL}resources/ext/ux/css/ux-all.css"/>
    <link rel="stylesheet" type="text/css" href="${rootURL}resources/ext/shared/icons/silk.css"/>
    <script type="text/javascript" src="${rootURL}resources/ext/adapter/ext/ext-base-debug.js"></script>
    <script type="text/javascript" src="${rootURL}resources/ext/ext-all-debug-w-comments.js"></script>
    <script type="text/javascript" src="${rootURL}resources/ext/ux/ux-all-debug.js"></script>

    <script type="text/javascript" src="${rootURL}resources/js/pageBus.js"></script>

    <script type="text/javascript" src="${rootURL}resources/js/CustomerListGrid.js"></script>
    <script type="text/javascript" src="${rootURL}resources/js/CustomerDetailsTabPanel.js"></script>
    <script type="text/javascript" src="${rootURL}resources/js/DailyOrderGrid.js"></script>
    <script type="text/javascript" src="${rootURL}resources/js/BillGrid.js"></script>
    <script type="text/javascript" src="${rootURL}resources/js/DailyOrderReportsChart.js"></script>
    <script type="text/javascript" src="${rootURL}resources/js/app.js"></script>

    <script type="text/javascript">
        Ext.onReady(function () {
            Ext.rootUrl = '${rootURL}';
            Ext.BLANK_IMAGE_URL = '${rootURL}resources/ext/resources/images/default/s.gif';
            //Ext.chart.Chart.CHART_URL = 'http://localhost:8080/resources/ext/resources/charts.swf'
        }); //end onReady
    </script>