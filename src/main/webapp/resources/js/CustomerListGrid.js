var customerListStore = new Ext.data.JsonStore({
    autoLoad: true,
    restful: true,
    storeId: 'customersStore',
    url: './rest/user/',
    root: 'users',
    totalProperty: 'users.length',
    fields: [
        {name: 'name',type: 'string'},{name: 'email',type: 'string'},{name: 'sector',type: 'string'},
        {name: 'phone',type: 'string'},{name: 'address1',type: 'string'},{name: 'address2',type: 'string'},
        {name: 'address3',type: 'string'},{name: 'city',type: 'string'},{name: 'zip',type: 'string'},
        {name: 'dailyCmOrder',type: 'float'},{name: 'dailyBmOrder',type: 'float'},
        {name: 'cmPrice',type: 'float'},{name: 'bmPrice',type: 'float'},{name: 'active',type: 'boolean'}
    ]
});

CustomerListGrid = Ext.extend(Ext.grid.GridPanel, {
    title: 'Customer List',
    store: customerListStore,
    stripeRows:true,
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
            viewConfig: {
                forceFit: true
            },
            tbar: [{
                text: 'Add',
                iconCls: 'silk-user-add',
                handler: onAdd
            },'-'],
            columns: [
                new Ext.grid.RowNumberer(),
                {xtype: 'gridcolumn',dataIndex: 'name',header: 'Name',sortable: true},
                {xtype: 'gridcolumn',dataIndex: 'email',header: 'Email',sortable: true},
                {xtype: 'gridcolumn',dataIndex: 'sector',header: 'Sector',sortable: true},
                {xtype: 'gridcolumn',dataIndex: 'phone',header: 'Phone',sortable: true},
                {xtype: 'numbercolumn',dataIndex: 'dailyCmOrder',header: 'Daily CM Order',sortable: true},
                {xtype: 'numbercolumn',dataIndex: 'dailyBmOrder',header: 'Daily BM Order',sortable: true},
                {xtype: 'numbercolumn',dataIndex: 'cmPrice',header: 'CM Price',sortable: true},
                {xtype: 'numbercolumn',dataIndex: 'bmPrice',header: 'BM Price',sortable: true},
                {xtype: 'gridcolumn',dataIndex: 'address1',header: 'Address 1',sortable: true},
                {xtype: 'gridcolumn',dataIndex: 'address2',header: 'Address 2',sortable: true},
                {xtype: 'gridcolumn',dataIndex: 'address3',header: 'Address 3',sortable: true},
                {xtype: 'gridcolumn',dataIndex: 'city',header: 'City',sortable: true},
                {xtype: 'gridcolumn',dataIndex: 'zip',header: 'Zip',sortable: true}
            ],
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
            this.store.reload();
        }
        PageBus.subscribe("CustomerListGrid.CreateCustomerForm.Added",this,customerAdded,'createAddCustomerView');
        CustomerListGrid.superclass.initComponent.call(this);
    }
});

function createAddCustomerView(){
    Ext.QuickTips.init();
    Ext.form.Field.prototype.msgTarget = 'side';

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
    url: '/rest/user/',
    buttonAlign:'center',
    initComponent: function() {
        this.initialConfig = Ext.apply({
            url: '/rest/user/'
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
                        {xtype: 'textfield',id: 'sector',name: 'sector',allowBlank: false,fieldLabel: 'Sector'}
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
                        {xtype: 'textfield',id: 'zip',name: 'zip',fieldLabel: 'Zip'}
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
                        url: '/rest/user/',
                        headers:{'Content-Type':'application/json; charset=utf-8'},
                        success: function(response, opts){
                            PageBus.publish("CustomerListGrid.CreateCustomerForm.Added","Customer added");
                            PageBus.publish("CustomerListGrid.CreateCustomerForm.Cancel","My Message");
                            //alert("Success");
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

/*EditCustomerForm = Ext.extend(Ext.form.FormPanel,{
    autoScroll: true,
    width: 590,
    height:550,
    frame: true,
    id:'createCustomerForm',
    monitorValid:true,
    bodyStyle:'padding:5px 5px 0',
    buttonAlign:'center',
    initComponent: function() {
        this.initialConfig = Ext.apply({
            url: '/rest/user/'
        }, this.initialConfig);
    Ext.applyIf(this, {
        items:[

        ]
    })

})*/


