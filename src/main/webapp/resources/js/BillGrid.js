var selectedUserId = null;
function  inMoney(val, p, record){
    return Ext.util.Format.inMoney(val);
}
var reader = new Ext.data.ArrayReader({}, [
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
    {name: 'customerName', type: 'string'},
    {name: 'customerId', type: 'int'},
    {name: 'customerPhone', type: 'string'}
]);

customerBillListStore = new Ext.data.JsonStore({
    restful: true,
    autoLoad: false,
    storeId: 'billStoreId',
    url: './rest/bill/',
    root: 'bills',
    fields: [
        {name: 'id', type: 'int'},
        {format: 'Y-m-d', name: 'fromDate', type: 'date'},
        {format: 'Y-m-d', name: 'toDate', type: 'date'},
        {format: 'Y-m-d', name: 'generationDate', type: 'date'},
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
        {name: 'payableAmount', type: 'float'},
        {name: 'customerName', type: 'string'},
        {name: 'customerId', type: 'int'},
        {name: 'customerPhone', type: 'string'}
    ],
    listeners: {
        load: function (store, records, options) {
            var gridPanel = Ext.getCmp('customerBillGridId');
            gridPanel.store.data = store.data;
            gridPanel.getView().refresh(true);
        },
        update: function (store, record, operation) {
            var values = record.data;
            var jsonValues = Ext.encode(values);
            Ext.Ajax.request({
                url: './rest/bill/',
                method: 'PUT',
                headers: {'Content-Type': 'application/json; charset=utf-8'},
                success: function (response, opts) {
                    PageBus.publish("CustomerBillGrid.Bill.modified", record.data);
                },
                failure: function (response, opts) {
                    console.log('server-side failure with status code ' + response.status);
                },
                params: jsonValues
            });
        }
    }
});

billGroupStore = new Ext.data.GroupingStore({
    reader: reader,
    data: customerBillListStore.data,
    sortInfo: {field: 'month', direction: "ASC"},
    groupField: 'month'
});

var editor = new Ext.ux.grid.RowEditor({
    saveText: 'Update'
});

billGridRowExpander = new Ext.ux.grid.RowExpander({
    tpl: new Ext.XTemplate(
        '<style type="text/css">',
        '.tg  {border-collapse:collapse;border-spacing:0;margin-left: 2cm;margin-top: .5cm; margin-bottom: .5cm}',
        '.tg td{font-family:Arial, sans-serif;font-size:14px;padding:10px 5px;border-style:solid;border-width:1px;overflow:hidden;word-break:normal;}',
        '.tg th{font-family:Arial, sans-serif;font-size:14px;font-weight:normal;padding:10px 5px;border-style:solid;border-width:1px;overflow:hidden;word-break:normal;}',
        '.tg .tg-e3zv{font-weight:bold;}',
        '</style>',
        '<table class="tg">',
        '<tr>',
        '<th class="tg-e3zv" colspan="4">To: {customerName} Customer Id: {customerId}<br>Phone:{customerPhone}</th>',
        '<th class="tg-e3zv">Bill no: {id}<br>Date:{generationDate:date("d-M-y")}</th>',
        '</tr>',
        '<tr>',
        '<td class="tg-e3zv">Milk Type</td>',
        '<td class="tg-e3zv">Period</td>',
        '<td class="tg-e3zv">Quantity</td>',
        '<td class="tg-e3zv">Rate</td>',
        '       <td class="tg-e3zv">Total</td>',
        '               </tr>',
        '<tr>',
        '   <td class="tg-031e">B.M</td>',
        '<td class="tg-031e">From: {fromDate:date("d-M-y")}<br>To: {toDate:date("d-M-y")}</td>',
        '   <td class="tg-031e">{totalBmQty}</td>',
        '   <td class="tg-031e">{bmPerQuantityPrice:inMoney}</td>',
        '   <td class="tg-e3zv">{totalBmPrice:inMoney}</td>',
        '   </tr>',
        '<tr>',
        '   <td class="tg-031e">C.M</td>',
        '<td class="tg-031e">From: {fromDate:date("d-M-y")}<br>To: {toDate:date("d-M-y")}</td>',
        '   <td class="tg-031e">{totalCmQty}</td>',
        '   <td class="tg-031e">{cmPerQuantityPrice:inMoney}</td>',
        '   <td class="tg-e3zv">{totalCmPrice:inMoney}</td>',
        '   </tr>',
        '<tpl if="otherCharges != 0">',
        '<tr>',
        '   <td class="tg-031e" colspan="4">Other Charges</td>',
        '   <td class="tg-e3zv">{otherCharges:inMoney}</td>',
        '</tr>',
        '</tpl>',
        '<tr>',
        '   <td class="tg-031e" colspan="4">Monthly Total</td>',
        '   <td class="tg-e3zv">{billableAmount:inMoney}</td>',
        '</tr>',
        '<tpl if="previousMonthsBalanceAmount != 0">',
        '<tr>',
        '   <td class="tg-031e" colspan="4">Previous months Balance</td>',
        '   <td class="tg-e3zv">{previousMonthsBalanceAmount:inMoney}</td>',
        '</tr>',
        '</tpl>',
        '<tpl if="discount != 0">',
        '<tr>',
        '   <td class="tg-031e" colspan="4">Adjustment</td>',
        '   <td class="tg-e3zv">{discount:inMoney}</td>',
        '</tr>',
        '</tpl>',
        '<tpl if="paidAmount != 0">',
        '<tr>',
        '   <td class="tg-031e" colspan="4">Paid amount</td>',
        '   <td class="tg-e3zv">{paidAmount:inMoney}</td>',
        '</tr>',
        '</tpl>',
        '<tr>',
            '   <td class="tg-031e" colspan="4">Payable total</td>',
            '   <td class="tg-e3zv">{balanceAmount:inMoney}</td>',
            '</tr>',
        '</table>'
    )
});

CustomerBillGrid = Ext.extend(Ext.grid.GridPanel, {
    height: 471,
    width: 785,
    autoScroll: true,
    forceFit: true,
    id: 'customerBillGridId',
    store: customerBillListStore,
    viewConfig: {
        forceFit: true
    },
    plugins: [billGridRowExpander, editor],
    initComponent: function () {
        Ext.applyIf(this, {
            tbar:[
                {
                    text:'Calculate current month bill',
                    iconCls:'silk-calculator',
                    handler:calculateCurrentMonthBill
                },'->',
                {
                   iconCls:'silk-table-refresh',
                   text:'Refresh',
                   handler:function(){
                       customerBillListStore.load();
                   }
                }
            ],
            columns: [
                new Ext.grid.RowNumberer(),
                billGridRowExpander,
/*
                {xtype: 'datecolumn', dataIndex: 'fromDate', header: 'From Date', sortable: true, format: 'Y-m-d'},
                {xtype: 'datecolumn', dataIndex: 'toDate', header: 'To Date', sortable: true, format: 'Y-m-d'},
                {xtype: 'datecolumn', dataIndex: 'generationDate', header: 'Generated on', sortable: true, format: 'Y-m-d'},
*/
                {xtype: 'gridcolumn', dataIndex: 'id', header: 'Bill #', sortable: true},
                {xtype: 'numbercolumn', dataIndex: 'totalCmQty', header: 'Tot. CM Qty', sortable: true},
                {xtype: 'numbercolumn', dataIndex: 'totalBmQty', header: 'Tot. BM Qty', sortable: true},
                {dataIndex: 'totalCmPrice', header: 'Tot. CM Price', sortable: true,renderer: inMoney},
                {dataIndex: 'totalBmPrice', header: 'Tot. BM Price', sortable: true,renderer: inMoney},
                {dataIndex: 'discount', header: 'Discount', sortable: true,
                    editor: {
                        xtype: 'numberfield',
                        allowBlank: false,
                        minValue: 0,
                        maxValue: 150000
                    },renderer: inMoney
                },
                {dataIndex: 'otherCharges', header: 'Other Charges', sortable: true,
                    editor: {
                        xtype: 'numberfield',
                        allowBlank: false,
                        minValue: 0,
                        maxValue: 150000
                    }, renderer: inMoney
                },
                {
                    dataIndex: 'billableAmount', header: 'Billable Amt.', sortable: true,
                    renderer: inMoney
                },
                {dataIndex: 'paidAmount', header: 'Paid Amt.', sortable: true,
                    editor: {
                        xtype: 'numberfield',
                        allowBlank: false,
                        minValue: 0,
                        maxValue: 150000
                    }, renderer: inMoney
                },
                {dataIndex: 'balanceAmount', header: 'Balance Amt.', sortable: true, renderer: inMoney},
                {dataIndex: 'bmPerQuantityPrice', header: 'BM Per Qty. Price', sortable: true,
                    editor: {
                        xtype: 'numberfield',
                        allowBlank: false,
                        minValue: 0,
                        maxValue: 150000
                    }, renderer: inMoney
                },
                {dataIndex: 'cmPerQuantityPrice', header: 'CM Per Qty. Price', sortable: true,
                    editor: {
                        xtype: 'numberfield',
                        allowBlank: false,
                        minValue: 0,
                        maxValue: 150000
                    },renderer: inMoney
                },
                /*{xtype: 'checkcolumn', dataIndex: 'closed', header: 'Paid?', sortable: true
                },*/
                {xtype: 'gridcolumn', dataIndex: 'month', header: 'Month', sortable: true}
            ]
        });
        function processSelectedCustomer(topic, messageFromPublisher, subscriberData) {
            customerBillListStore.proxy.conn.url = './rest/bill/user/' + messageFromPublisher.id;
            selectedUserId = messageFromPublisher.id;
            customerBillListStore.load();
           // Ext.getCmp('customerBillGridId').setTitle(messageFromPublisher.data.name + "'s bill");
            Ext.getCmp('customerBillPanel').setTitle(messageFromPublisher.data.name+"'s Bills")

        }

        function processBillChanged(topic, messageFromPublisher, subscriberData) {
            customerBillListStore.proxy.conn.url = './rest/bill/user/' + selectedUserId;
            customerBillListStore.load();
        }

        function calculateCurrentMonthBill(){
            var urll = './rest/bill/generate/user/' + selectedUserId;
            Ext.Ajax.request({
                url: urll,
                headers:{'Content-Type':'application/json; charset=utf-8'},
                success: function(response, opts){
                    PageBus.publish("CustomerBillGrid.Bill.modified","Bill Generated");
                },
                failure: function(response, opts){
                    console.log('server-side failure with status code ' + response.status);
                }
            });
        }
        PageBus.subscribe("CustomerListGrid.customer.selected", this, processSelectedCustomer, 'BillGrid');
        PageBus.subscribe("DailyOrderGrid.DailyOrder.modified", this,processBillChanged,'BillGrid');
        PageBus.subscribe("CustomerBillGrid.Bill.modified", this, processBillChanged, 'BillGrid');
        CustomerBillGrid.superclass.initComponent.call(this);
    }
});