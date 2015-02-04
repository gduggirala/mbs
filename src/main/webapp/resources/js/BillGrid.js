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
    {name: 'payableAmount', type: 'float'}
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
        {name: 'payableAmount', type: 'float'}
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
        '<br/><p class=ex1><b>Start Date:</b>&nbsp;&nbsp;{fromDate:date("d M  Y")}&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>End Date:</b>&nbsp;&nbsp;{toDate:date("d M  Y")}',
        '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>Generated on:</b>&nbsp;&nbsp;{generationDate:date("d M Y")}</p> <br/>',
        '<table class=ex2 border="1px solid black;"><tr><th><b>Charge type</b></th><th><b>Total Milk delivered</b></th><th><b>Price/Liter</b></th><th><b>Total amount</b></th></tr>',
        '<tr><td><b>BM</b></td><td>{totalBmQty}</td><td>{bmPerQuantityPrice:inMoney}</td><td><b>{totalBmPrice:inMoney}</b></td></tr>',
        '<tr><td><b>CM</b></td><td>{totalCmQty}</td><td>{cmPerQuantityPrice:inMoney}</td><td><b>{totalCmPrice:inMoney}</b></td></tr>',
        '<tr><td><b>Others</b></td><td></td><td></td><td>{otherCharges:inMoney}</td></tr>',
        '<tr><td><b>Previous months balance:</b></td><td></td><td></td><td>{previousMonthsBalanceAmount:inMoney}</td></tr>',
        '<tr><td><b>Discount:</b></td><td></td><td></td><td>{discount:inMoney}</td></tr>',
        '<tr><td><b>Paid amount:</b></td><td></td><td></td><td>{paidAmount:inMoney}</td></tr>',
        '<tr><td></td><td></td><td><b>Billable Amount</b></td><td><b>{payableAmount:inMoney}</b></td></tr></table>'
        /*,'<br />',
        '<p class=ex1><b>Discount:</b>{discount:inMoney}</p>',
        '<p class=ex1><b>Paid Amount:</b>{paidAmount:inMoney}</p>',
        '<p class=ex1><b>Payable Amount:</b>{payableAmount:inMoney}</p>'*/
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
            Ext.getCmp('customerBillGridId').setTitle(messageFromPublisher.data.name + "'s bill");

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