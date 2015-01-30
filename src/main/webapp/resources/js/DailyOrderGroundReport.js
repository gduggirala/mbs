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
        {name: 'orderDate', type: 'date', format: 'Y-m-d'},
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
            gridPanel.setTitle("Today's Order");
        }
    }
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
    store: dailyOrderGroundGroupStore,
    view: groupingView,
    listeners: {
        render: function (thisPanel) {
            dailyOrderGroundReport();
        }
    },
    initComponent: function () {
        Ext.applyIf(this, {
            tbar: ['->',{
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
                    xtype: 'gridcolumn', dataIndex: 'cmOrder', header: 'CM Order', sortable: true, width: 100
                },
                {
                    xtype: 'gridcolumn', dataIndex: 'bmOrder', header: 'BM Order', sortable: true, width: 100
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
        PageBus.subscribe("DailyOrderGrid.DailyOrder.modified", this, dailyOrderGroundReport, 'DailyOrderGroundReport');
        DailyOrderGroundReportGrid.superclass.initComponent.call(this);
    }
});