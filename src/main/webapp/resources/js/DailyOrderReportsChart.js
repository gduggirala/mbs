DailyOrderReportsChart = Ext.extend(Ext.Panel, {
    autoScroll: true,
    layoutConfig:{
        align:'middle'
    },
    id: 'dailyOrderReportsChartId',
    frame: true,
    title: 'Daily Orders Report',
    flex: 1,
    html: 'Loading...',
    listeners: {
        render: function (thisPanel) {
            dailyOrderReporter();
        }
    },
    initComponent: function () {
        Ext.applyIf(this, {
            items: [

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
        url: '/rest/report/dailyOrderReport',
        method: 'GET',
        headers: {'Content-Type': 'application/json; charset=utf-8'},
        success: function (response, opts) {
            console.dir(response.responseText);
            var data = Ext.decode(response.responseText)
            var tpl = new Ext.XTemplate(
                '<table border="1" style="width:100%"><tr><td><b>Sector</b></td><td><b>BM Order</b></td><td><b>CM Order</b></td></tr>',
                '<tpl for="dailyOrderReport">',
                '<tr><td><b>{sector}</b></td><td>{totalBmOrder}</td><td>{totalCmOrder}</td></tr>',
                '</tpl>',
                '<tr><td><b>Total</b></td><td><b>{grandTotalBmOrder}</b></td><td><b>{grandTotalCmOrder}</b></td></tr>',
                '</table> '
            );
            var panel = Ext.getCmp('dailyOrderReportsChartId');
            var panelBody = panel.body;
            panel.setTitle("Order for date "+data.forDate);
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