/*!
 * Ext JS Library 3.4.0
 * Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com
 * http://www.sencha.com/license
 */
// Application instance for showing user-feedback messages.
var App = new Ext.App({});

// Create a standard HttpProxy instance.
var proxy = new Ext.data.HttpProxy({
    url: '/rest/user/'
});

// Typical JsonReader.  Notice additional meta-data params for defining the core attributes of your json-response
var reader = new Ext.data.JsonReader({
    successProperty: 'success',
    idProperty: 'id',
    root: 'users',
    messageProperty: 'message'  // <-- New "messageProperty" meta-data
}, [
    {name: 'id'},
    {name: 'name',type: 'string',allowBlank:false},{name: 'email',type: 'string',allowBlank:false},{name: 'sector',type: 'string',allowBlank:false},
    {name: 'phone',type: 'string',allowBlank:false},{name: 'address1',type: 'string'},{name: 'address2',type: 'string'},
    {name: 'address3',type: 'string'},{name: 'city',type: 'string'},{name: 'zip',type: 'string'},
    {name: 'dailyCmOrder',type: 'float',allowBlank:false},{name: 'dailyBmOrder',type: 'float',allowBlank:false},
    {name: 'cmPrice',type: 'float',allowBlank:false},{name: 'bmPrice',type: 'float',allowBlank:false}
]);

// The new DataWriter component.
var writer = new Ext.data.JsonWriter({
    encode: false   // <-- don't return encoded JSON -- causes Ext.Ajax#request to send data using jsonData config rather than HTTP params
});

// Typical Store collecting the Proxy, Reader and Writer together.
var store = new Ext.data.Store({
    id: 'user',
    restful: true,     // <-- This Store is RESTful
    proxy: proxy,
    reader: reader,
    writer: writer    // <-- plug a DataWriter into the store just as you would a Reader
});

// load the store immeditately
store.load();

////
// ***New*** centralized listening of DataProxy events "beforewrite", "write" and "writeexception"
// upon Ext.data.DataProxy class.  This is handy for centralizing user-feedback messaging into one place rather than
// attaching listenrs to EACH Store.
//
// Listen to all DataProxy beforewrite events
//
Ext.data.DataProxy.addListener('beforewrite', function(proxy, action) {
    App.setAlert(App.STATUS_NOTICE, "Before " + action);
});

////
// all write events
//
Ext.data.DataProxy.addListener('write', function(proxy, action, result, res, rs) {
    App.setAlert(true, action + ':' + res.message);
});

////
// all exception events
//
Ext.data.DataProxy.addListener('exception', function(proxy, type, action, options, res) {
    App.setAlert(false, "Something bad happend while executing " + action);
});

// Let's pretend we rendered our grid-columns with meta-data from our ORM framework.
var userColumns =  [
    {header: "ID", width: 40, sortable: true, dataIndex: 'id'},
    {header: "Email", width: 100, sortable: true, dataIndex: 'email', editor: new Ext.form.TextField({})},
    {header: "Name", width: 50, sortable: true, dataIndex: 'name', editor: new Ext.form.TextField({})},
    {header: "Sector", width: 50, sortable: true, dataIndex: 'sector', editor: new Ext.form.TextField({})},
    {header: "Phone", width: 50, sortable: true, dataIndex: 'phone', editor: new Ext.form.TextField({})},
    {header: "Daily CM Order", width: 50, sortable: true, dataIndex: 'dailyCmOrder', editor: new Ext.form.TextField({})},
    {header: "Daily BM Order", width: 50, sortable: true, dataIndex: 'dailyBmOrder', editor: new Ext.form.TextField({})},
    {header: "CM Price", width: 50, sortable: true, dataIndex: 'cmPrice', editor: new Ext.form.TextField({})},
    {header: "BM Price", width: 50, sortable: true, dataIndex: 'bmPrice', editor: new Ext.form.TextField({})},
    {header: "Address 1", width: 50, sortable: true, dataIndex: 'address1', editor: new Ext.form.TextField({})},
    {header: "Address 2", width: 50, sortable: true, dataIndex: 'address2', editor: new Ext.form.TextField({})},
    {header: "Address 3", width: 50, sortable: true, dataIndex: 'address3', editor: new Ext.form.TextField({})},
    {header: "City", width: 50, sortable: true, dataIndex: 'city', editor: new Ext.form.TextField({})},
    {header: "Zip", width: 50, sortable: true, dataIndex: 'zip', editor: new Ext.form.TextField({})}
];


Ext.onReady(function() {
    Ext.QuickTips.init();

    // use RowEditor for editing
    var editor = new Ext.ux.grid.RowEditor({
        saveText: 'Update'
    });

    // Create a typical GridPanel with RowEditor plugin
    var userGrid = new Ext.grid.GridPanel({
        renderTo: 'user-grid',
        iconCls: 'icon-grid',
        frame: true,
        title: 'Users',
        height: 300,
        store: store,
        plugins: [editor],
        columns : userColumns,
        tbar: [{
            text: 'Add',
            iconCls: 'silk-add',
            handler: onAdd
        }, '-', {
            text: 'Delete',
            iconCls: 'silk-delete',
            handler: onDelete
        }, '-'],
        viewConfig: {
            forceFit: true
        }
    });

    /**
     * onAdd
     */
    function onAdd(btn, ev) {
        var u = new userGrid.store.recordType({
            first : '',
            last: '',
            email : ''
        });
        editor.stopEditing();
        userGrid.store.insert(0, u);
        editor.startEditing(0);
    }
    /**
     * onDelete
     */
    function onDelete() {
        var rec = userGrid.getSelectionModel().getSelected();
        if (!rec) {
            return false;
        }
        userGrid.store.remove(rec);
    }

});
