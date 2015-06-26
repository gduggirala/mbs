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
    {name: 'dailyCmOrder', type: 'float'},
    {name: 'customerId', type: 'int'}
]);

var rowClassFunction =  function (record, rowIndex, rowParams, store) {
    var c = record.get('balanceAmount');
    if (c <= 0) {
        return 'green';
    } else {
        return 'red';
    }
}
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
        {name: 'dailyCmOrder', type: 'float'},
        {name: 'customerId', type: 'int'}
    ],
    listeners: {
        load: function (store, records, options) {
            var gridPanel = Ext.getCmp('billsListGridId');
            gridPanel.store.data = store.data;
            gridPanel.getView().getRowClass = rowClassFunction;
            gridPanel.getView().refresh(true);
            gridPanel.setTitle("Bills")
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
        '<style type="text/css">',
        '.tg  {border-collapse:collapse;border-spacing:0;margin-left: 2cm;margin-top: .5cm; margin-bottom: .5cm}',
        '.tg td{font-family:Arial, sans-serif;font-size:14px;padding:10px 5px;border-style:solid;border-width:1px;overflow:hidden;word-break:normal;}',
        '.tg th{font-family:Arial, sans-serif;font-size:14px;font-weight:normal;padding:10px 5px;border-style:solid;border-width:1px;overflow:hidden;word-break:normal;}',
        '.tg .tg-e3zv{font-weight:bold;}',
        '</style>',
        '<table class="tg">',
        '<tr>',
        '<th class="tg-e3zv" colspan="4">To: {name} Customer Id:{customerId}<br>Phone:{phone}</th>',
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
        '<tpl if="previousMonthsBalanceAmount != 0">',
            '<tr>',
            '   <td class="tg-031e" colspan="4">Previous months Balance</td>',
            '   <td class="tg-e3zv">{previousMonthsBalanceAmount:inMoney}</td>',
            '</tr>',
        '</tpl>',
        '<tpl if="discount != 0">',
            '<tr>',
            '   <td class="tg-031e" colspan="4">Discount</td>',
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
        '   <td class="tg-031e" colspan="4">Grand Total</td>',
        '   <td class="tg-e3zv">{payableAmount:inMoney}</td>',
        '</tr>',

        '</table>'
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
                },
                // 'Show bills for the month:',
                {
                    xtype     : 'combo',
                    typeAhead: true,
                    triggerAction: 'all',
                    lazyRender:true,
                    hidden:true,
                    width     : 100,
                    mode: 'local',
                    store     : new  Ext.data.ArrayStore({
                        id:'monthFullNameStore',
                        fields: [
                            'monthFullName',
                            'displayText'
                        ],
                        data: [ ['January', 'January'], ['February', 'February'], ['March', 'March'], ['April', 'April'], ['May', 'May'],
                                ['June', 'June'], ['July', 'July'], ['August', 'August'], [ 'September', 'September'],
                                ['October', 'October'], ['November', 'November'], ['December', 'December']]
                    }),
                    listeners:{
                        'select':showMonthBill
                    },
                    valueField: 'monthId',
                    displayField: 'displayText'
                },'->',{
                    text: 'Excel',
                    iconCls: 'silk-page-excel',
                    handler: downloadBillsExcel
                },{
                      text: 'PDF',
                      iconCls: 'silk-pdf',
                      handler: downloadBillsPdf
                  },{
                    iconCls:'silk-table-refresh',
                    text:'Refresh',
                    handler:function(){
                        billsListStore.load();
                    }
                }
                ,'-',
                {
                    xtype: 'textfield',
                    emptyText:'Search by Name, Bill #, Phone and Customer Id and hit ENTER',
                    width:400,
                    label:'Search',
                    enableKeyEvents: true,
                    listeners: {
                        'specialkey': function (thisObj, eventObject) {
                            var billListGridMask = new Ext.LoadMask(Ext.get('billsListGridId'), {msg:"Please wait..."});
                            billListGridMask.show();
                            var grid = Ext.getCmp('billsListGridId');
                            var store = grid.getStore();
                            var searchString = thisObj.getValue();
                            // e.HOME, e.END, e.PAGE_UP, e.PAGE_DOWN,
                            // e.TAB, e.ESC, arrow keys: e.LEFT, e.RIGHT, e.UP, e.DOWN
                            var pressedKey = eventObject.getKey();
                            if(pressedKey !=  eventObject.ENTER){
                                if((pressedKey == eventObject.BACKSPACE || pressedKey == eventObject.DELETE) && searchString.length == 1){
                                    store.clearFilter();
                                }
                                if(eventObject.getKey() == eventObject.ESC){
                                    thisObj.setValue("");
                                    store.clearFilter();
                                }
                                billListGridMask.hide();
                                grid.selModel.selectFirstRow();
                                return;
                            }

                            if(searchString.length <= 0 ){
                                store.clearFilter();
                                billListGridMask.hide();
                                grid.selModel.selectFirstRow();
                                return;
                            }
                            store.clearFilter();
                            store.filterBy(function (record) {
                                var myRegExp = new RegExp(searchString,'i');
                                var recordName = record.get('name');
                                var customerId = record.get('customerId');
                                var recordPhone = record.get('phone');
                                var billId = record.get('id');
                                var recordReferredBy = record.get('referredBy');
                                if (myRegExp.test(recordName) || myRegExp.test(billId) || myRegExp.test(customerId) ||
                                    myRegExp.test(recordPhone) || myRegExp.test(recordReferredBy)) {
                                    billListGridMask.hide();
                                    return record;
                                }
                            });
                            grid.selModel.selectFirstRow();
                            billListGridMask.hide();
                        }
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
                {xtype: 'gridcolumn', dataIndex: 'id', header: 'Bill #', sortable: true},
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
            var localUrl = billsListStore.proxy.conn.url + '?month=June';
            billsListStore.proxy.conn.url = localUrl;
            billsListStore.load();
        }

        function showMonthBill(combo, record, index){
            var localUrl = billsListStore.proxy.url + '?month='+record.data.monthFullName;
            billsListStore.proxy.conn.url = localUrl;
            billsListStore.load();
        }

        function calculateCurrentMonthBill(combo, record, index){
            var myMask = new Ext.LoadMask(Ext.get('billsListGridId'), {msg:"Generating bills...", removeMask:true});
            Ext.Msg.alert('Caution', 'You are generating the bills for all the customers for '+record.data.displayText+' month as back ground job is executing \n as it will take more time so please be patient');
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

        function downloadBillsPdf(){
            window.open('./generate/billsPdf.pdf');
        }
        function downloadBillsExcel(){
            window.open('./generate/billsExcel.xls');
        }
        PageBus.subscribe("DailyOrderGrid.DailyOrder.modified", this,processBillChanged,'BillGrid');
        PageBus.subscribe("BillsList.Bill.modified", this, processBillChanged, 'BillGrid');
        BillsList.superclass.initComponent.call(this);
    }
});