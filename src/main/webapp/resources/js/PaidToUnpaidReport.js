PaidToUnpaidReport = Ext.extend(Ext.Panel, {
    autoScroll: true,
    layoutConfig:{
        align:'middle'
    },
    id: 'paidToUnpaidReportId',
    frame: true,
    title: 'Paid to Unpaid Reports',
    flex: 1,
    html: 'Loading...',
    listeners: {
        render: function (thisPanel) {
            paidToUnpaidBillReport();
        }
    },
    initComponent: function () {
        Ext.applyIf(this, {
            items: [

            ]
        });
        function billChanged(topic, messageFromPublisher, subscriberData) {
            paidToUnpaidBillReport();
        }

        PageBus.subscribe("CustomerBillGrid.Bill.modified", this, billChanged, 'BillGridChart');
        DailyOrderReportsChart.superclass.initComponent.call(this);
    }
});

function paidToUnpaidBillReport() {
    Ext.Ajax.request({
        url: '/rest/report/monthlyBills',
        method: 'GET',
        headers: {'Content-Type': 'application/json; charset=utf-8'},
        success: function (response, opts) {
            console.dir(response.responseText);
            var data = Ext.decode(response.responseText)
            var tpl = new Ext.XTemplate(
                '<table border="1" style="width:100%"><tr bgcolor="#C8C8C8">',
                '<td><b># Of Paid Bills</b></td><td><b>Total Paid amount</b></td><td><b># Of Unpaid Bills</b></td><td><b>Total Unpaid amount</b></td><td><b>Yet to Pay</b></td></tr>',
                '<tr bgcolor="#C8C8C8"><td><b>{paidBillsCount}</b></td><td><b>{paidAmount}</b></td><td><b>{unpaidBillsCount}</b></td><td><b>{unpaidAmount}</b></td><td><b>{amountYetToBePaid}</b></td></tr>',
                '</table>'
            );
            var panel = Ext.getCmp('paidToUnpaidReportId');
            var panelBody = panel.body;
            panel.setTitle("Bill for month "+data.forMonth);
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