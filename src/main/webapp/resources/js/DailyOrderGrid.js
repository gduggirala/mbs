var selectedUserId = null;

var reader = new Ext.data.ArrayReader({}, [
    {name: 'id', type: 'int'},
    {name: 'cmOrder', type: 'float'},
    {name: 'bmOrder', type: 'float'},
    {name: 'orderDate', type: 'date', format: 'Y-m-d'},
    {name: 'month', type: 'string'}
]);

dailyOrderStore = new Ext.data.JsonStore({
    autoLoad: false,
    restful: true,
    storeId: 'dailyOrdersStoreId',
    url: './rest/dailyOrders/search?userId=', //Though nothing is mentioned here, the actual value will be given when processSelectedCustomer method is being executed.
    root: 'dailyOrders',
    fields: [
        {name: 'id'},
        {name: 'cmOrder'},
        {name: 'bmOrder'},
        {name: 'orderDate'},
        {name: 'month'}
    ],
    listeners: {
        load:function(store, records,options){
            var gridPanel = Ext.getCmp('dailyOrderGridId');
            gridPanel.store.data = store.data;
            gridPanel.getView().refresh(true);
        },
        update: function (store, record, operation) {
            var changedOrderDate = record.data.orderDate.format('Y-m-d');
            record.data.orderDate = changedOrderDate;
            var values = record.data;
            var jsonValues = Ext.encode(values);
            Ext.Ajax.request({
                url: '/rest/dailyOrders/',
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

dailyOrderGroupStore = new Ext.data.GroupingStore({
    reader: reader,
    data: dailyOrderStore.data,
    sortInfo: {field: 'month', direction: "ASC"},
    groupField:'month'
});

var editor = new Ext.ux.grid.RowEditor({
    saveText: 'Update'
});

var groupingView = new Ext.grid.GroupingView({
    forceFit: false,
    groupTextTpl: '{text} ({[values.rs.length]} {[values.rs.length > 1 ? "Items" : "Item"]})'
});

DailyOrdersGrid = Ext.extend(Ext.grid.GridPanel, {
    height: 471,
    width: 785,
    selectedUserId: null,
    id: 'dailyOrderGridId',
    title: 'Daily Orders',
    store: dailyOrderGroupStore,
    plugins: [editor],
    view: groupingView,
    initComponent: function () {
        Ext.applyIf(this, {
            tbar: [{
                text: 'Rerun DailyOrders',
                iconCls: 'silk-table-refresh',
                handler: onRerun
            },'-'],
            columns: [
                new Ext.grid.RowNumberer(),
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
                    xtype: 'datecolumn', dataIndex: 'orderDate', header: 'OrderDate', sortable: true, width: 100, format: 'Y-m-d',
                    editor: {
                        xtype: 'datefield',
                        allowBlank: false,
                        minValue: '2012-12-01',
                        minText: 'Can\'t have a start date before the company existed!'
                    }
                },
                {
                    xtype: 'gridcolumn', dataIndex: 'month', header: 'Month', sortable: true, width: 100
                }
            ]
        });
        function onRerun(){
            var jsonValues={userId:selectedUserId};
            var urll = './rest/dailyOrders/rerun?userId='+selectedUserId
            Ext.Ajax.request({
                url: urll,
                method: 'POST',
                headers: {'Content-Type': 'application/json; charset=utf-8'},
                success: function (response, opts) {
                    PageBus.publish("DailyOrderGrid.DailyOrder.modified", response.responseText);
                },
                failure: function (response, opts) {
                    console.log('server-side failure with status code ' + response.status);
                },
                params: jsonValues
            });
        }

        function processSelectedCustomer(topic, messageFromPublisher, subscriberData) {
            dailyOrderStore.proxy.conn.url = './rest/dailyOrders/search?userId=' + messageFromPublisher.id;
            selectedUserId = messageFromPublisher.id;
            dailyOrderStore.load();
            Ext.getCmp('dailyOrderGridId').setTitle(messageFromPublisher.data.name+"'s daily orders")
        }

        function processDailyOrderChanged(topic, messageFromPublisher, subscriberData) {
            dailyOrderStore.proxy.conn.url = './rest/dailyOrders/search?userId=' + selectedUserId;
            dailyOrderStore.load();
        }

        PageBus.subscribe("CustomerListGrid.customer.selected", this, processSelectedCustomer, 'DailyOrderGrid');
        PageBus.subscribe("DailyOrderGrid.DailyOrder.modified", this, processDailyOrderChanged, 'DailyOrderGrid');
        DailyOrdersGrid.superclass.initComponent.call(this);
    }
});