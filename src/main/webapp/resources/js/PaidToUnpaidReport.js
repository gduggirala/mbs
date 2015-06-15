var reader = new Ext.data.JsonReader({
    fields: [
        {name: 'id', type: 'int'},
        {dateformat: 'Y-m-d', name: 'fromDate', type: 'date'},
        {dateformat: 'Y-m-d', name: 'toDate', type: 'date'},
        {dateformat: 'Y-m-d', name: 'generationDate', type: 'date'},
        {name: 'totalCmQty', type: 'float'},
        {name: 'totalBmQty', type: 'float'},
        {name: 'totalCmPrice', type: 'float'},
        {name: 'totalBmPrice', type: 'float'},
        {name: 'discount', type: 'float'},
        {name: 'otherCharges', type: 'float'},
        {name: 'totalAmount', type: 'float'},
        {name: 'discountReason', type: 'string'},
        {name: 'paidAmount', type: 'float'},
        {name: 'balanceAmount', type: 'float'},
        {name: 'bmPerQuantityPrice', type: 'float'},
        {name: 'cmPerQuantityPrice', type: 'float'},
        {name: 'comment', type: 'string'},
        {name: 'closed', type: 'boolean'},
        {name: 'month', type: 'string'},
        {name: 'previousMonthsBalanceAmount', type: 'float'},
        {name: 'billableAmount', type: 'float'},
        {name: 'payableAmount', type: 'float'},
        {name: 'isPaid', type: 'boolean'}
    ]
});


PaidToUnpaidReport = Ext.extend(Ext.Panel, {
    autoScroll: true,
    layout: 'vbox',
    viewConfig: {
        forceFit: true
    },
    layoutConfig:{
        align:'middle'
    },
    id: 'paidToUnpaidReportMaster',
    frame: true,
    title: 'Paid to Unpaid Reports',
    collapsible:true,
    listeners: {
        render: function (thisPanel) {
            paidToUnpaidBillReport();
        }
    },
    initComponent: function () {
        Ext.applyIf(this, {
            items: [
                {
                    frame:false,
                    flex:.1,
                    id:'paidToUnpaidReportId'
                }
            ]
        });
        function billChanged(topic, messageFromPublisher, subscriberData) {
            paidToUnpaidBillReport();
        }

        PageBus.subscribe("CustomerBillGrid.Bill.modified", this, billChanged, 'BillGridChart');
        DailyOrderReportsChart.superclass.initComponent.call(this);
    }
});

var paidAndUnpaidBillsStore= new Ext.data.Store({
    // explicitly create reader
    reader:reader,
    autoLoad:false
});

function paidToUnpaidBillReport() {
    Ext.Ajax.request({
        url: './rest/report/monthlyBills',
        method: 'GET',
        headers: {'Content-Type': 'application/json; charset=utf-8'},
        success: function (response, opts) {
            var data = Ext.decode(response.responseText)
            var i=0;
            for(i=0;i<data.paidBills.length;i++){
                data.paidBills[i].isPaid = true;
            }
            for(i=0;i<data.unpaidBills.length;i++){
                data.unpaidBills[i].isPaid = false;
            }
            paidAndUnpaidBillsStore.loadData(data.paidBills);
            paidAndUnpaidBillsStore.loadData(data.unpaidBills, true);
            createGrid();

            data.formattedPaidAmount = Ext.mf.format(data.paidAmount);
            data.formattedUnPaidAmount = Ext.mf.format(data.unpaidAmount);
            data.formattedAmountYetToBePaid = Ext.mf.format(data.amountYetToBePaid);
            data.formattedTotalRevenue = Ext.mf.format(data.totalRevenue);
            var tpl = new Ext.XTemplate(
                '<table border="1" style="width:100%"><tr bgcolor="#C8C8C8">',
                '<td><b># Of Paid Bills</b></td><td><b>Total revenue</b></td><td><b>Total Paid amount</b></td><td><b># Of Unpaid Bills</b></td><td><b>Yet to Pay</b></td></tr>',
                '<tr bgcolor="#C8C8C8"><td><b>{paidBillsCount}</b></td><td><b>{formattedTotalRevenue}</b></td><td><b>{formattedPaidAmount}</b></td><td><b>{unpaidBillsCount}</b></td><td><b>{formattedAmountYetToBePaid}</b></td></tr>',
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

function createGrid(){
    var grid = new Ext.grid.GridPanel({
        store: paidAndUnpaidBillsStore,
        flex:1,
        autoscroll:1,
        colModel: new Ext.grid.ColumnModel({
            defaults: {
                width: 120,
                sortable: true
            },
            columns: [
                /*
                 {xtype: 'datecolumn', dataIndex: 'fromDate', header: 'From Date', sortable: true, format: 'Y-m-d'},
                 {xtype: 'datecolumn', dataIndex: 'toDate', header: 'To Date', sortable: true, format: 'Y-m-d'},
                 {xtype: 'datecolumn', dataIndex: 'generationDate', header: 'Generated on', sortable: true, format: 'Y-m-d'},
                 */
                {xtype: 'numbercolumn', dataIndex: 'totalCmQty', header: 'Tot. CM Qty', sortable: true},
                {xtype: 'numbercolumn', dataIndex: 'totalBmQty', header: 'Tot. BM Qty', sortable: true},
                {dataIndex: 'totalCmPrice', header: 'Tot. CM Price', sortable: true,renderer: inMoney},
                {dataIndex: 'totalBmPrice', header: 'Tot. BM Price', sortable: true,renderer: inMoney},
                {dataIndex: 'discount', header: 'Discount', sortable: true,renderer: inMoney},
                {dataIndex: 'otherCharges', header: 'Other Charges', sortable: true,renderer: inMoney},
                {dataIndex: 'billableAmount', header: 'Billable Amt.', sortable: true,renderer: inMoney},
                {dataIndex: 'paidAmount', header: 'Paid Amt.', sortable: true,renderer: inMoney},
                {dataIndex: 'balanceAmount', header: 'Balance Amt.', sortable: true, renderer: inMoney},
                {dataIndex: 'bmPerQuantityPrice', header: 'BM Per Qty. Price', sortable: true,renderer: inMoney},
                {dataIndex: 'cmPerQuantityPrice', header: 'CM Per Qty. Price', sortable: true,renderer: inMoney},
                {xtype: 'gridcolumn', dataIndex: 'month', header: 'Month', sortable: true},
                {dataIndex:'isPaid', header:'Is Paid', sortable:true, renderer:function(val, rec, st){
                    return val?'Yes':'No'}
                }
            ]
        }),
        viewConfig: {
            forceFit: true,

//      Return CSS class to apply to rows depending upon data values
            getRowClass: function(record, rowIndex, rowParams, store) {
                var c = record.get('isPaid');
                if (c) {
                    return 'green';
                } else {
                    return 'red';
                }
            }
        },
        frame: false,
        iconCls: 'icon-grid'
    });
    Ext.getCmp('paidToUnpaidReportMaster').add(grid);
    Ext.getCmp('paidToUnpaidReportMaster').doLayout();
}