sectorStore = {};
function  inMoney(val, p, record){
    return Ext.util.Format.inMoney(val);
}
var formSectorCombo = {};
function getSectorStore(){
    Ext.Ajax.request({
        url: './rest/user/sectors',
        method: 'GET',
        headers: {'Content-Type': 'application/json; charset=utf-8'},
        success: function (response, opts) {
            var temp =  Ext.decode(response.responseText);
            sectorStore = temp.sectors;
        },
        failure: function (response, opts) {
            console.log('server-side failure with status code ' + response.status);
        }
    });
}

customerListStore = new Ext.data.JsonStore({
    autoLoad: true,
    restful: true,
    storeId: 'customersStore',
    url: './rest/user/',
    root: 'users',
    totalProperty: 'total',
    fields: [
        {name: 'id',type: 'int'},{name: 'name',type: 'string'},{name: 'email',type: 'string'},{name: 'sector',type: 'string'},{name:'orderStartDate',type:'date',format:'Y-m-d'},
        {name: 'phone',type: 'string'},{name: 'address1',type: 'string'},{name: 'address2',type: 'string'},
        {name: 'address3',type: 'string'},{name: 'city',type: 'string'},{name: 'zip',type: 'string'},
        {name: 'dailyCmOrder',type: 'float'},{name: 'dailyBmOrder',type: 'float'},
        {name: 'cmPrice',type: 'float'},{name: 'bmPrice',type: 'float'},{name: 'active',type: 'boolean'},{name: 'givenSerialNumber',type: 'int'},
        {name: 'referredBy',type: 'string'}
    ],
    listeners:{
        load:function(store, records,options){
            var gridPanel = Ext.getCmp('customerListGridId');
            gridPanel.store.data = store.data;
            gridPanel.getView().refresh(true);
        },
        update: function (store, record, operation) {
            var values = record.data;
            var jsonValues = Ext.encode(values);
            Ext.Ajax.request({
                url: './rest/user/',
                method: 'PUT',
                headers: {'Content-Type': 'application/json; charset=utf-8'},
                success: function (response, opts) {
                    PageBus.publish("CustomerListGrid.CustomerList.modified", record.data);
                },
                failure: function (response, opts) {
                    console.log('server-side failure with status code ' + response.status);
                },
                params: jsonValues
            });
        }
    }
});

var reader = new Ext.data.ArrayReader({}, [
    {name: 'name',type: 'string'},{name: 'email',type: 'string'},{name: 'sector',type: 'string'},
    {name: 'phone',type: 'string'},{name: 'address1',type: 'string'},{name: 'address2',type: 'string'},
    {name: 'address3',type: 'string'},{name: 'city',type: 'string'},{name: 'zip',type: 'string'},
    {name: 'dailyCmOrder',type: 'float'},{name: 'dailyBmOrder',type: 'float'},
    {name:'orderStartDate',type:'date',format:'Y-m-d'},
    {name: 'cmPrice',type: 'float'},{name: 'bmPrice',type: 'float'},{name: 'active',type: 'boolean'},{name: 'givenSerialNumber',type: 'int'},
    {name: 'referredBy',type: 'string'}
]);

var groupingView = new Ext.grid.GroupingView({
    forceFit: false,
    groupTextTpl: '{text} ({[values.rs.length]} {[values.rs.length > 1 ? "Items" : "Item"]})'
});

var editor = new Ext.ux.grid.RowEditor({
    saveText: 'Update',

});

customerListGroupStore = new Ext.data.GroupingStore({
    reader: reader,
    data: customerListStore.data,
    sortInfo: {field: 'sector', direction: "ASC"},
    groupField:'sector'
});

CustomerListGrid = Ext.extend(Ext.grid.GridPanel, {
    title: 'Customer List',
    store: customerListGroupStore,
    id:'customerListGridId',
    stripeRows:true,
    view: groupingView,
    plugins: [editor],
    initComponent: function() {
        Ext.applyIf(this, {
            selModel: new Ext.grid.RowSelectionModel({
                singleSelect: true,
                listeners:{
                    'rowselect':function(rowSelection, rowIndex,record){
                        PageBus.publish("CustomerListGrid.customer.selected",record);
                    }
                }
            }),
            tbar: [
                {
                    text: 'Add',
                    iconCls: 'silk-user-add',
                    handler: onAdd
                },
                '->',
                {
                    xtype: 'textfield',
                    emptyText:'Search by Name, eMail, Sector, Phone and Serial # and hit ENTER',
                    width:400,
                    label:'Search',
                    enableKeyEvents: true,
                    listeners: {
                        'specialkey': function (thisObj, eventObject) {
                            var customerListGridMask = new Ext.LoadMask(Ext.get('customerListGridId'), {msg:"Please wait..."});
                            customerListGridMask.show();
                            var grid = Ext.getCmp('customerListGridId');
                            var store = Ext.getCmp('customerListGridId').getStore();
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
                                customerListGridMask.hide();
                                grid.selModel.selectFirstRow();
                                return;
                            }

                            if(searchString.length <= 0 ){
                                store.clearFilter();
                                customerListGridMask.hide();
                                grid.selModel.selectFirstRow();
                                return;
                            }
                            store.clearFilter();
                            store.filterBy(function (record) {
                                var myRegExp = new RegExp(searchString);
                                var recordName = record.get('name');
                                var recordEmail = record.get('email')
                                var recordSector = record.get('sector');
                                var recordPhone = record.get('phone');
                                var recordGivenSerailNumber = record.get('givenSerialNumber');
                                var recordReferredBy = record.get('referredBy');
                                if (myRegExp.test(recordName) || myRegExp.test(recordEmail) || myRegExp.test(recordSector) ||
                                    myRegExp.test(recordPhone) || myRegExp.test(recordGivenSerailNumber) || myRegExp.test(recordReferredBy)) {
                                    customerListGridMask.hide();
                                    return record;
                                }
                            });
                            grid.selModel.selectFirstRow();
                            customerListGridMask.hide();
                        }
                    }
                }
            ],
            columns: [
                new Ext.grid.RowNumberer(),
                {xtype: 'numbercolumn',dataIndex: 'id',header: 'Customer Id',format:'0', sortable: true},
                {xtype: 'numbercolumn',dataIndex: 'givenSerialNumber',header: 'Serial #',format:'0', sortable: true,
                    editor: {
                        xtype: 'numberfield',
                        allowBlank: false,
                        allowDecimals:false,
                        minValue: 0,
                        maxValue: 150000
                    }
                },
                {xtype: 'gridcolumn',dataIndex: 'name',header: 'Name',sortable: true,
                    editor: {xtype: 'textfield',allowBlank: false}
                },
                {xtype: 'gridcolumn',dataIndex: 'email',header: 'Email',sortable: true,
                    editor: {xtype: 'textfield',allowBlank: false}
                },
                {xtype: 'gridcolumn',dataIndex: 'sector',header: 'Sector',sortable: true,
                    editor: {xtype: 'textfield',allowBlank: false}
                },
                {xtype: 'gridcolumn',dataIndex: 'phone',header: 'Phone',sortable: true,
                    editor: {xtype: 'textfield',allowBlank: false}
                },
                {xtype: 'numbercolumn',dataIndex: 'dailyCmOrder',header: 'Daily CM Order',sortable: true,
                    editor: {
                        xtype: 'numberfield',
                        allowBlank: false,
                        minValue: 0,
                        maxValue: 150000
                    }
                },
                {xtype: 'numbercolumn',dataIndex: 'dailyBmOrder',header: 'Daily BM Order',sortable: true,
                    editor: {
                        xtype: 'numberfield',
                        allowBlank: false,
                        minValue: 0,
                        maxValue: 150000
                    }
                },
                {dataIndex: 'cmPrice',header: 'CM Price',sortable: true,
                    editor: {
                        xtype: 'numberfield',
                        allowBlank: false,
                        minValue: 0,
                        maxValue: 150000
                    }, renderer:inMoney
                },
                {dataIndex: 'bmPrice',header: 'BM Price',sortable: true,
                    editor: {
                        xtype: 'numberfield',
                        allowBlank: false,
                        minValue: 0,
                        maxValue: 150000
                    }, renderer:inMoney
                },
                {
                    xtype: 'datecolumn', dataIndex: 'orderStartDate', header: 'Order Start Date', sortable: true, width: 100, format: 'Y-m-d',
                    editor: {
                        xtype: 'datefield',
                        allowBlank: false,
                        minValue: '2012-12-01',
                        minText: 'Can\'t have a start date before the company existed!'
                    }
                },
                {xtype: 'gridcolumn',dataIndex: 'referredBy',header: 'Referred by',sortable: true,
                    editor: {xtype: 'textfield',allowBlank: true}
                }
            ],
            viewConfig: {
                forceFit: true,
                getRowClass: function(record, rowIndex, rowParams, store) {
                    var c = record.get('active');
                    if (c) {
                        return 'green';
                    } else {
                        return 'red';
                    }
                }
            },
            listeners:{
                'viewready':function(grid){
                    grid.selModel.selectFirstRow();
                }
            }
        });
        function onAdd(){
            //Ext.Msg.alert("Clicked on ","Add");
            createAddCustomerView();
        }
        function onDelete(){
            Ext.Msg.alert("Clicked on", "Delete");
        }
        function customerAdded(){
            customerListStore.load();
        }
        PageBus.subscribe("CustomerListGrid.CreateCustomerForm.Added",this,customerAdded,'createAddCustomerView');
        PageBus.subscribe("CustomerListGrid.CustomerList.modified",this,customerAdded,'createAddCustomerView');
        getSectorStore();
        CustomerListGrid.superclass.initComponent.call(this);
    }
});

function createAddCustomerView(){
    Ext.QuickTips.init();
    Ext.form.Field.prototype.msgTarget = 'side';
    formSectorCombo = new Ext.form.ComboBox({
        // all of your config options
        store: sectorStore,
        emptyText: 'Select Sector',
        id: 'sector',
        name: 'sector',
        fieldLabel: 'Sector',
        forceSelection: false,
        typeAhead: true
    });
    var customerWindow = new Ext.Window({
        title: "Add customer",
        layout:'fit',
        autoScroll:true,
        closeAction:'close',
        plain: false,
        items:[
            new CreateCustomerForm()
        ]
    });
    customerWindow.show();

    PageBus.subscribe("CustomerListGrid.CreateCustomerForm.Cancel",this,windowClose,'createAddCustomerView');
    //topic=CustomerListGrid.CreateCustomerForm.Cancel
    //message=MyWindow -> The data which is sent by the Publisher.
    //subscriberData=createAddCustomerView
    function windowClose(topic, message, subscriberData){
        customerWindow.close();
    }
}

CreateCustomerForm = Ext.extend(Ext.form.FormPanel, {
    autoScroll: true,
    width: 590,
    height:550,
    frame: true,
    id:'createCustomerForm',
    monitorValid:true,
    bodyStyle:'padding:5px 5px 0',
    //title: 'Customer Information',
    url: './rest/user/',
    buttonAlign:'center',
    initComponent: function() {
        this.initialConfig = Ext.apply({
            url: './rest/user/'
        }, this.initialConfig);

        Ext.applyIf(this, {
            items: [
                {
                  xtype:'hidden',
                  name:'active',
                  value:true
                },
                {
                    xtype: 'fieldset',
                    title: 'Personal Information',
                    items: [
                        {xtype: 'textfield',id: 'name',name: 'name',allowBlank: false,fieldLabel: 'Name'},
                        {xtype: 'textfield',id: 'phone',name: 'phone',allowBlank: false,fieldLabel: 'Phone'},
                        {xtype: 'textfield',id: 'email',name: 'email',allowBlank: false,fieldLabel: 'eMail'},
                        formSectorCombo
                    ]
                },
                {
                    xtype: 'fieldset',
                    title: 'Order Information',
                    items: [
                        {xtype: 'numberfield',id: 'dailyBmOrder',name: 'dailyBmOrder',allowBlank: false,value: 1,fieldLabel: 'BM Order'},
                        {xtype: 'numberfield',id: 'dailyCmOrder',name: 'dailyCmOrder',allowBlank: false,value: 1,fieldLabel: 'CM Order'},
                        {xtype: 'numberfield',id: 'bmPrice',name: 'bmPrice',allowBlank: false,fieldLabel: 'BM Price'},
                        {xtype: 'numberfield',id: 'cmPrice',name: 'cmPrice',allowBlank: false,fieldLabel: 'CM Price'},
                        {xtype: 'datefield',id: 'orderStartDate',name: 'orderStartDate',fieldLabel: 'Start Date',format:'Y-m-d'}
                    ]
                },
                {
                    xtype: 'fieldset',
                    title: 'Correspondance',
                    items: [
                        {xtype: 'textfield',id: 'address1',name: 'address1',fieldLabel: 'Address 1'},
                        {xtype: 'textfield',id: 'address2',name: 'address2',fieldLabel: 'Address 2'},
                        {xtype: 'textfield',id: 'address3',name: 'address3',fieldLabel: 'Address 3'},
                        {xtype: 'textfield',id: 'city',name: 'city',fieldLabel: 'City'},
                        {xtype: 'numberfield',id: 'zip',name: 'zip',fieldLabel: 'Zip'},
                        {xtype: 'numberfield',id: 'givenSerialNumber',name: 'givenSerialNumber',fieldLabel: 'Serial #'},
                        {xtype: 'textfield',id: 'referredBy',name: 'referredBy',fieldLabel: 'Reference'}
                    ]
                }
            ],
            buttons:[{
                text:'OK',
                formBind: true,
                handler:function(){
                    var formValues = Ext.getCmp('createCustomerForm').getForm().getValues();
                    var jsonValues = Ext.encode(formValues);
                    Ext.Ajax.request({
                        url: './rest/user/',
                        headers:{'Content-Type':'application/json; charset=utf-8'},
                        success: function(response, opts){
                            PageBus.publish("CustomerListGrid.CreateCustomerForm.Added","Customer added");
                            PageBus.publish("CustomerListGrid.CreateCustomerForm.Cancel","My Message");
                        },
                        failure: function(response, opts){
                            console.log('server-side failure with status code ' + response.status);
                        },
                        params: jsonValues
                    });
                }
            },{
                text:'Cancel',
                handler:function(){
                    PageBus.publish("CustomerListGrid.CreateCustomerForm.Cancel","My Message");
                }
            }]
        });

        CreateCustomerForm.superclass.initComponent.call(this);
    }
});