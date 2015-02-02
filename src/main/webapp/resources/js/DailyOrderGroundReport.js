var reader = new Ext.data.ArrayReader({}, [
    {name: 'id', type: 'int'},
    {name: 'cmOrder', type: 'float'},
    {name: 'bmOrder', type: 'float'},
    {name: 'orderDate', type: 'date', format: 'Y-m-d'},
    {name: 'name', type: 'string'},
    {name: 'sector', type: 'string'},
    {name: 'phone', type: 'string'},
    {name: 'givenSerialNumber', type: 'string'},
    {name: 'address1', type: 'string'}
]);

function dailyOrderGroundReport(){
    dailyOrderGroundReportStore.load();
}
dailyOrderGroundReportStore = new Ext.data.JsonStore({
    autoLoad: false,
    restful: true,
    storeId: 'dailyOrdersGroundReportStoreId',
    url: './rest/report/dailyOrderGroundReport', //Though nothing is mentioned here, the actual value will be given when processSelectedCustomer method is being executed.
    root: 'dailyOrderGrounds',
    fields: [
        {name: 'id'},
        {name: 'cmOrder'},
        {name: 'bmOrder'},
        {name: 'orderDate', type: 'date', dateFormat:'Y-m-d'},
        {name: 'name', type: 'string'},
        {name: 'sector', type: 'string'},
        {name: 'phone', type: 'string'},
        {name: 'givenSerialNumber', type: 'string'},
        {name: 'address1', type: 'string'}
    ],
    listeners: {
        load:function(store, records,options){
            var gridPanel = Ext.getCmp('dailyOrderGroundGridId');
            gridPanel.store.data = store.data;
            gridPanel.getView().refresh(true);
            gridPanel.setTitle("Today's Order ( "+Ext.util.Format.date(records[0].data.orderDate,'Y-m-d')+" )");
        },
        update:function(store, record, operation){
            record.data.orderDate = record.data.orderDate.format('Y-m-d');
            var values = record.data;
            var jsonValues = Ext.encode(values);
            Ext.Ajax.request({
                url: './rest/dailyOrders/update/',
                method: 'PUT',
                headers: {'Content-Type': 'application/json; charset=utf-8'},
                params: jsonValues,
                success: function (response, opts) {
                    PageBus.publish("DailyOrderGrid.DailyOrder.modified", record.data);
                },
                failure: function (response, opts) {
                    console.log('Server-side failure with status code ' + response.status);
                    console.dir(response);
                }
            });
        }
    }
});

var editor = new Ext.ux.grid.RowEditor({
    saveText: 'Update'
});

dailyOrderGroundGroupStore = new Ext.data.GroupingStore({
    reader: reader,
    data: dailyOrderGroundReportStore.data,
    sortInfo: {field: 'givenSerialNumber', direction: "ASC"},
    groupField:'sector'
});

var groupingView = new Ext.grid.GroupingView({
    forceFit: false,
    groupTextTpl: '{text} ({[values.rs.length]} {[values.rs.length > 1 ? "Items" : "Item"]})'
});

DailyOrderGroundReportGrid = Ext.extend(Ext.grid.GridPanel, {
    height: 471,
    width: 785,
    id: 'dailyOrderGroundGridId',
    title: "Today's Daily Orders",
    frame:true,
    plugins: [editor],
    store: dailyOrderGroundGroupStore,
    view: groupingView,
    listeners: {
        render: function (thisPanel) {
            dailyOrderGroundReport();
        }
    },
    initComponent: function () {
        Ext.applyIf(this, {
            tbar: ['Generate Daily orders for the month:',
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
                        'select':calculateDailyOrdersForMonth
                    },
                    valueField: 'monthId',
                    displayField: 'displayText'
                },'->',{
                text: 'PDF',
                iconCls: 'silk-pdf',
                handler: downloadPdf
            },'-',{
                text: 'Excel',
                iconCls: 'silk-page-excel',
                handler: downloadExcel
            }],
            columns: [
                new Ext.grid.RowNumberer(),
                {
                    xtype: 'gridcolumn', dataIndex: 'givenSerialNumber', header: '#', sortable: true, width: 100
                },
                {
                    xtype: 'gridcolumn', dataIndex: 'name', header: 'Name', sortable: true, width: 100
                },
                {
                    xtype: 'gridcolumn', dataIndex: 'sector', header: 'Sector', sortable: true, width: 100
                },
                {
                    xtype: 'gridcolumn', dataIndex: 'phone', header: 'Phone', sortable: true, width: 100
                },
                {
                    xtype: 'gridcolumn', dataIndex: 'cmOrder', header: 'CM Order', sortable: true, width: 100,
                    editor: {
                        xtype: 'numberfield',
                        allowBlank: false,
                        minValue: 0,
                        maxValue: 150000
                    }
                },
                {
                    xtype: 'gridcolumn', dataIndex: 'bmOrder', header: 'BM Order', sortable: true, width: 100,
                    editor: {
                        xtype: 'numberfield',
                        allowBlank: false,
                        minValue: 0,
                        maxValue: 150000
                    }
                },
                {
                    xtype: 'gridcolumn', dataIndex: 'address1', header: 'Address1', sortable: true, width: 100
                }
            ]
        });
        function downloadPdf(){
            window.open('./generate/dailyOrder.pdf');
        }
        function downloadExcel(){
            window.open('./generate/dailyOrder.xls');
        }
        function calculateDailyOrdersForMonth(combo, record, index){
            var myMask = new Ext.LoadMask(Ext.get('dailyOrderGroundGridId'), {msg:"Generating daily orders...", removeMask:true});
            //Ext.Msg.alert('Caution', 'You are generating the bills for all the customers for '+record.data.displayText+' month as back ground job is failed \n as it will take more time so please be patient');
            var urll = './rest/dailyOrders/generateAll?monthValue='+record.data.monthId+'&monthText='+record.data.displayText;
            myMask.show();
            Ext.Ajax.request({
                url: urll,
                method:'POST',
                timeout:1800000,
                headers:{'Content-Type':'application/json; charset=utf-8'},
                success: function(response, opts){
                    myMask.hide()
                    PageBus.publish("DailyOrderGrid.DailyOrder.modified", "DailyOrdersGroundReport");
                },
                failure: function(response, opts){
                    myMask.hide()
                    console.log('server-side failure with status code ' + response.status);
                }
            });
        }
        PageBus.subscribe("DailyOrderGrid.DailyOrder.modified", this, dailyOrderGroundReport, 'DailyOrderGroundReport');
        DailyOrderGroundReportGrid.superclass.initComponent.call(this);
    }
});