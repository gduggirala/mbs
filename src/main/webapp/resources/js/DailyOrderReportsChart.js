DailyOrderReportsChart = Ext.extend(Ext.Panel, {
    autoScroll: true,
    layoutConfig:{
        align:'middle'
    },
    id: 'dailyOrderReportsChartMasterId',
    frame: true,
    title: 'Daily Orders Report',
    flex: 1,
    collapsible:true,
    listeners: {
        render: function (thisPanel) {
            dailyOrderReporter();
        }
    },
    initComponent: function () {
        screenWidth = (Ext.getBody().getSize().width-50)/2;
        dailyOrderReportChartUrl = '<img src="./rest/charts/dailyOrderReportChart?width='+screenWidth+'"/>';
        dailyOrderRevenueTrendUrl = '<img src="./rest/charts/dailyOrderRevenueTrend?width='+screenWidth+'"/>';
        Ext.applyIf(this, {
            items: [
                {
                    xtype: 'panel',
                    layout: 'hbox',
                    items: [
                        {
                            xtype: 'panel',
                            autoScroll:true,
                            flex:1,
                            html: dailyOrderReportChartUrl
                        },
                        {
                            xtype: 'panel',
                            autoScroll:true,
                            flex:1,
                            html:dailyOrderRevenueTrendUrl
                        }/*,
                        {
                            xtype:'panel',
                            autoLoad:{
                                url:'./rest/google/charts/googleCharts',
                                scripts:true
                            }

                        }
*/                    ]
                },
                {
                    id: 'dailyOrderReportsChartId'
                }
            ]
        });
        function dailyOrderChanged(topic, messageFromPublisher, subscriberData) {
            dailyOrderReporter();
        }

        PageBus.subscribe("DailyOrderGrid.DailyOrder.modified", this, dailyOrderChanged, 'DailyOrderChart');
        DailyOrderReportsChart.superclass.initComponent.call(this);
    }
});

function dailyOrderReporter() {
    Ext.Ajax.request({
        url: './rest/report/dailyOrderReport',
        method: 'GET',
        headers: {'Content-Type': 'application/json; charset=utf-8'},
        success: function (response, opts) {
            var data = Ext.decode(response.responseText)
            var tpl = new Ext.XTemplate(
                '<table border="1" style="width:100%"><tr bgcolor="#C8C8C8"><td><b>Sector</b></td><td><b>BM Order</b></td><td><b>BM Revenue</b></td><td><b>CM Order</b></td><td><b>CM Revenue</b></td></tr>',
                '<tpl for="dailyOrderReport">',
                '<tr><td bgcolor="#C8C8C8"><b>{sector}</b></td><td>{totalBmOrder}</td><td>{bmRevenue}</td><td>{totalCmOrder}</td><td>{cmRevenue}</td></tr>',
                '</tpl>',
                '<tr bgcolor="#C8C8C8"><td><b>Total</b></td><td><b>{grandTotalBmOrder}</b></td><td><b>{totalBmRevenue}</b></td><td><b>{grandTotalCmOrder}</b></td><td><b>{totalCmRevenue}</b></td></tr>',
                '<tr bgcolor="#C8C8C8"><td><b>Grand Total Revenue</b></td><td colspan="4"><b>{totalMilkRevenue}</b></td></tr>',
                '</table> '
            );
            var panel = Ext.getCmp('dailyOrderReportsChartId');
            var panelBody = panel.body;
            panel.setTitle("Order for date "+data.forDate+" (Sector)");
            tpl.overwrite(panelBody, data);
        },
        failure: function (response, opts) {

        }
    });
}
/*dailyOrderReportStore = new Ext.data.JsonStore({
 autoLoad: false,
 restful: true,
 storeId: 'dailyOrderReportStoreId',
 url: './rest/report/dailyOrderReport', //Though nothing is mentioned here, the actual value will be given when processSelectedCustomer method is being executed.
 root: 'dailyOrderReport',
 fields: [
 {name: 'totalCmOrder', type: 'float'},
 {name: 'totalBmOrder', type: 'float'},
 {name: 'sector', type: 'string'}
 ],
 listeners: {
 load: function (store, records, options) {
 var tpl = new Ext.XTemplate(
 '<tpl for=".">',
 '<p>Sector:{values.data.sector}</p>',
 '<p>CM Order:{values.data.totalCmOrder}</p>',
 '<p>BM Order:{values.data.totalBmOrder}</p>',
 '<p>Total BM order:{values.parent.grandTotalCmOrder}</p>',
 '</tpl>'
 );

 var panelBody = Ext.getCmp('dailyOrderReportsChartId').body;
 tpl.overwrite(panelBody, store.data.items);
 Ext.getCmp('dailyOrderReportsChartId').body.highlight('#c3daf9', {block: true});
 },
 update: function (store, record, operation) {

 }
 }
 });*/


/**
 DailyOrderReportsChart = Ext.extend(Ext.Panel, {
    autoScroll: true,
    frame: true,
    width: 1000,
    title: 'Daily Orders Report',
    flex: 1,
    initComponent: function () {
        Ext.applyIf(this, {
            items: [
                {
                    xtype: 'columnchart',
                    store: dailyOrderReportStore,
                    yField: 'totalCmOrder',
                    xField: 'sector',
                    xAxis: new Ext.chart.CategoryAxis({
                        title: 'Sector'
                    }),
                    yAxis: new Ext.chart.NumericAxis({
                        title: 'Total CM Order'
                    }),
                    url: './resources/ext/resources/charts.swf'
                },
                {
                    xtype: 'columnchart',
                    store: dailyOrderReportStore,
                    yField: 'totalBmOrder',
                    xField: 'sector',
                    xAxis: new Ext.chart.CategoryAxis({
                        title: 'Sector'
                    }),
                    yAxis: new Ext.chart.NumericAxis({
                        title: 'Total BM Order'
                    }),
                    url: './resources/ext/resources/charts.swf'
                }
            ]
        });
        DailyOrderReportsChart.superclass.initComponent.call(this);
    }
});
 **/

/**
 items:[{
                    xtype: 'listview',
                    autoScroll: true,
                    frame:false,
                    store: dailyOrderReportStore,
                    columnResize: false,
                    columns: [
                        {
                            xtype: 'lvcolumn',
                            dataIndex: 'sector',
                            header: 'Sector'
                        },
                        {
                            xtype: 'lvcolumn',
                            dataIndex: 'totalCmOrder',
                            header: 'CM Order',
                            width: 0.25
                        },
                        {
                            xtype: 'lvcolumn',
                            dataIndex: 'totalBmOrder',
                            header: 'BM Order'
                        }
                    ]
                }]
 **/