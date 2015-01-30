PaidToUnpaidReport = Ext.extend(Ext.Panel, {
    autoScroll: true,
    layoutConfig:{
        align:'middle'
    },
    id: 'paidToUnpaidReportId',
    frame: true,
    title: 'Paid to Unpaid Reports',
    flex: 1,
    collapsible:true,
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
        url: './rest/report/monthlyBills',
        method: 'GET',
        headers: {'Content-Type': 'application/json; charset=utf-8'},
        success: function (response, opts) {
            var data = Ext.decode(response.responseText)
            var tpl = new Ext.XTemplate(
                '<table border="1" style="width:100%"><tr bgcolor="#C8C8C8">',
                '<td><b># Of Paid Bills</b></td><td><b>Total Paid amount</b></td><td><b># Of Unpaid Bills</b></td><td><b>Total Unpaid amount</b></td><td><b>Yet to Pay</b></td></tr>',
                '<tr bgcolor="#C8C8C8"><td><b>{paidBillsCount}</b></td><td><b>{paidAmount}</b></td><td><b>{unpaidBillsCount}</b></td><td><b>{unpaidAmount}</b></td><td><b>{amountYetToBePaid}</b></td></tr>',
                '</table>'
            );
            var panel = Ext.getCmp('paidToUnpaidReportId');
            var panelBody = panel.body;
            panel.setTitle("Paid to Unpaid report for month "+data.forMonth);
            tpl.overwrite(panelBody, data);
        },
        failure: function (response, opts) {
            console.dir(response);
        }
    });
}