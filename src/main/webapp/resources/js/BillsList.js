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
    {name: 'name', type: 'string'},
    {name: 'sector', type: 'string'},
    {name: 'address1', type: 'string'},
    {name: 'givenSerialNumber', type: 'integer'},
    {name: 'phone', type: 'string'},
    {name: 'dailyBmOrder', type: 'float'},
    {name: 'dailyCmOrder', type: 'float'}
]);

billsListStore = new Ext.data.JsonStore({
    restful: true,
    autoLoad: false,
    storeId: 'billStoreId',
    url: './rest/report/listBills',
    root: 'billListReports',
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
        {name: 'name', type: 'string'},
        {name: 'sector', type: 'string'},
        {name: 'address1', type: 'string'},
        {name: 'givenSerialNumber', type: 'integer'},
        {name: 'phone', type: 'string'},
        {name: 'dailyBmOrder', type: 'float'},
        {name: 'dailyCmOrder', type: 'float'}
    ],
    listeners: {
        load: function (store, records, options) {
            var gridPanel = Ext.getCmp('billsListGridId');
            gridPanel.store.data = store.data;
            gridPanel.getView().refresh(true);
            gridPanel.setTitle("Bills")
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
    data: billsListStore.data,
    sortInfo: {field: 'month', direction: "ASC"},
    groupField: 'month'
});

var editor = new Ext.ux.grid.RowEditor({
    saveText: 'Update'
});

billListGridRowExpander = new Ext.ux.grid.RowExpander({
    tpl: new Ext.XTemplate(
        '<br/><p><b>Start Date:</b>&nbsp;&nbsp;{fromDate:date("d M  Y")}&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>End Date:</b>&nbsp;&nbsp;{toDate:date("d M  Y")}',
        '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>Generated on:</b>&nbsp;&nbsp;{generationDate:date("d M Y")}</p> <br/>',
        '<table padding="15px;" border="1px solid black;"><tr><th><b>Charge type</b></th><th><b>Total Milk delivered</b></th><th><b>Price/Liter</b></th><th><b>Total amount</b></th></tr>',
        '<tr><td><b>BM</b></td><td>{totalBmQty}</td><td>{bmPerQuantityPrice:inMoney}</td><td><b>{totalBmPrice:inMoney}</b></td></tr>',
        '<tr><td><b>CM</b></td><td>{totalCmQty}</td><td>{cmPerQuantityPrice:inMoney}</td><td><b>{totalCmPrice:inMoney}</b></td></tr>',
        '<tr><td><b>Others</b></td><td></td><td></td><td>{otherCharges:inMoney}</td></tr>',
        '<tr><td></td><td></td><td><b>Billable Amount</b></td><td><b>{billableAmount:inMoney}</b></td></tr></table>',
        '<br />',
        '<p><b>Discount:</b>{discount:inMoney}</p>',
        '<p><b>Paid Amount:</b>{paidAmount:inMoney}</p>',
        '<p><b>Previous months balance:</b>{previousMonthsBalanceAmount:inMoney}</p>',
        '<p><b>Payable Amount:</b>{payableAmount:inMoney}</p>'
    )
});

function billsListStoreLoader(){
    billsListStore.load();
}
BillsList = Ext.extend(Ext.grid.GridPanel, {
    height: 471,
    width: 785,
    autoScroll: true,
    forceFit: true,
    frame:true,
    id: 'billsListGridId',
    store: billsListStore,
    viewConfig: {
        forceFit: true
    },
    plugins: [billListGridRowExpander],
    listeners: {
        render: function (thisPanel) {
            var billsListMask = new Ext.LoadMask(Ext.get('billsListGridId'), {msg:"Please wait...", store:billsListStore});
            billsListStoreLoader();
        }
    },
    initComponent: function () {
        Ext.applyIf(this, {
            tbar:[
                'Generate bills for the month:',
                {
                    xtype     : 'combo',
                    typeAhead: true,
                    triggerAction: 'all',
                    lazyRender:true,
                    width     : 100,
                    mode: 'local',
                    store     : new  Ext.data.ArrayStore({
                        id:'monthsStore',
                        fields: [
                            'monthId',
                            'displayText'
                        ],
                        data: [ [1, 'January'], [2, 'February'], [3, 'March'], [4, 'April'], [5, 'May'],
                                [6, 'June'], [7, 'July'], [8, 'August'], [9, 'September'],
                                [10, 'October'], [11, 'November'], [12, 'December']]
                    }),
                    listeners:{
                        'select':calculateCurrentMonthBill
                    },
                    valueField: 'monthId',
                    displayField: 'displayText'
                },'->',{
                    iconCls:'silk-table-refresh',
                    text:'Refresh',
                    handler:function(){
                        billsListStore.load();
                    }
                }
            ],
            columns: [
                new Ext.grid.RowNumberer(),
                billListGridRowExpander,
                /*
                 {xtype: 'datecolumn', dataIndex: 'fromDate', header: 'From Date', sortable: true, format: 'Y-m-d'},
                 {xtype: 'datecolumn', dataIndex: 'toDate', header: 'To Date', sortable: true, format: 'Y-m-d'},
                 {xtype: 'datecolumn', dataIndex: 'generationDate', header: 'Generated on', sortable: true, format: 'Y-m-d'},
                 */
                {xtype: 'numbercolumn', dataIndex: 'totalCmQty', header: 'Tot. CM Qty', sortable: true},
                {xtype: 'numbercolumn', dataIndex: 'totalBmQty', header: 'Tot. BM Qty', sortable: true},
                {dataIndex: 'totalCmPrice', header: 'Tot. CM Price', sortable: true,renderer:inMoney},
                {dataIndex: 'totalBmPrice', header: 'Tot. BM Price', sortable: true,renderer:inMoney},
                {dataIndex: 'discount', header: 'Discount', sortable: true,renderer:inMoney},
                {dataIndex: 'otherCharges', header: 'Other Charges', sortable: true,renderer:inMoney},
                {dataIndex: 'billableAmount', header: 'Billable Amt.', sortable: true,renderer:inMoney},
                {dataIndex: 'paidAmount', header: 'Paid Amt.', sortable: true,renderer:inMoney},
                {dataIndex: 'balanceAmount', header: 'Balance Amt.', sortable: true,renderer:inMoney},
                {dataIndex: 'bmPerQuantityPrice', header: 'BM Per Qty. Price', sortable: true,renderer:inMoney},
                {dataIndex: 'cmPerQuantityPrice', header: 'CM Per Qty. Price', sortable: true,renderer:inMoney},
                /*{xtype: 'checkcolumn', dataIndex: 'closed', header: 'Paid?', sortable: true
                 },*/
                {xtype: 'gridcolumn', dataIndex: 'month', header: 'Month', sortable: true},
                {xtype: 'gridcolumn', dataIndex:  'name', header: 'Name', sortable: true},
                {xtype: 'gridcolumn', dataIndex: 'sector', header: 'Sector', sortable: true},
                {xtype: 'gridcolumn', dataIndex: 'address1', header: 'Address1', sortable: true},
                {xtype: 'gridcolumn', dataIndex: 'givenSerialNumber', header: 'Serail #', sortable: true},
                {xtype: 'gridcolumn', dataIndex: 'phone', header: 'Phone', sortable: true},
                {xtype: 'gridcolumn', dataIndex: 'dailyBmOrder', header: 'Daily BM Order', sortable: true},
                {xtype: 'gridcolumn', dataIndex: 'dailyCmOrder', header: 'Daily CM Order', sortable: true}

            ]
        });

        function processBillChanged(topic, messageFromPublisher, subscriberData) {
          //  billsListStore.proxy.conn.url = './rest/bill/user/' + selectedUserId;
            billsListStore.load();
        }

        function calculateCurrentMonthBill(combo, record, index){
            var myMask = new Ext.LoadMask(Ext.get('billsListGridId'), {msg:"Generating bills...", removeMask:true});
            Ext.Msg.alert('Caution', 'You are generating the bills for all the customers for '+record.data.displayText+' month as back ground job is failed \n as it will take more time so please be patient');
            var urll = './rest/bill/generateAll?monthValue='+record.data.monthId+'&monthText='+record.data.displayText;
            myMask.show();
            Ext.Ajax.request({
                url: urll,
                method:'POST',
                timeout:1800000,
                headers:{'Content-Type':'application/json; charset=utf-8'},
                success: function(response, opts){
                    myMask.hide()
                    PageBus.publish("BillsList.Bill.modified","Bill Generated");
                },
                failure: function(response, opts){
                    myMask.hide()
                    console.log('server-side failure with status code ' + response.status);
                }
            });
        }

        PageBus.subscribe("DailyOrderGrid.DailyOrder.modified", this,processBillChanged,'BillGrid');
        PageBus.subscribe("BillsList.Bill.modified", this, processBillChanged, 'BillGrid');
        BillsList.superclass.initComponent.call(this);
    }
});