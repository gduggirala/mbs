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
        sw = (Ext.getBody().getSize().width-50);
        dailyOrderReportChartUrl = '<img src="./rest/charts/dailyOrderReportChart?width='+screenWidth+'"/>';
        dailyOrderRevenueTrendUrl = '<img src="./rest/charts/dailyRevenueTrend?width='+sw+'"/>';
        monthlyRevenueTrendUrl = '<img src="./rest/charts/monthlyRevenueTrend?width='+screenWidth+'"/>';
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
                            html:monthlyRevenueTrendUrl
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
                    xtype: 'panel',
                    autoScroll:true,
                    flex:1,
                    html:dailyOrderRevenueTrendUrl
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
                '<tr><td bgcolor="#C8C8C8"><b>{sector}</b></td><td>{totalBmOrder}</td><td>{bmRevenue:inMoney}</td><td>{totalCmOrder}</td><td>{cmRevenue:inMoney}</td></tr>',
                '</tpl>',
                '<tr bgcolor="#C8C8C8"><td><b>Total</b></td><td><b>{grandTotalBmOrder}</b></td><td><b>{totalBmRevenue:inMoney}</b></td><td><b>{grandTotalCmOrder}</b></td><td><b>{totalCmRevenue:inMoney}</b></td></tr>',
                '<tr bgcolor="#C8C8C8"><td><b>Grand Total Revenue</b></td><td colspan="4"><b>{totalMilkRevenue:inMoney}</b></td></tr>',
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