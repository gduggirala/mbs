customerBillListStore = new Ext.data.JsonStore({
            restful: true,
            autoLoad:false,
            storeId: 'billStore',
            url: '/rest/bill/',
            root: 'bills',
            fields: [
                {name: 'id',type: 'int'},
                {dateFormdat: 'Y-m-d',name: 'fromDate',type: 'date'},
                {dateFormat: 'Y-m-d',name: 'toDate',type: 'date'},
                {dateFormat: 'Y-m-d',name: 'generationDate',type: 'date'},
                {name: 'totalCmQty',type: 'float'},
                {name: 'totalBmQty',type: 'float'},
                {name: 'totalCmPrice',type: 'float'},
                {name: 'totalBmPrice',type: 'float'},
                {name: 'discount',type: 'float'},
                {name: 'otherCharges',type: 'float'},
                {name: 'totalAmount',type: 'float'},
                {name: 'discountReason',type: 'string'},
                {name: 'paidAmount',type: 'float'},
                {name: 'balanceAmount',type: 'float'},
                {name: 'bmPerQuantityPrice',type: 'float'},
                {name: 'cmPerQuantityPrice',type: 'float'},
                {name: 'comment',type: 'string'},
                {name: 'closed',type: 'boolean'}
            ]
});


CustomerBillGrid = Ext.extend(Ext.grid.GridPanel, {
    height: 471,
    width: 785,
    autoScroll: true,
    store: customerBillListStore,
    initComponent: function() {
        Ext.applyIf(this, {
            columns: [
                new Ext.grid.RowNumberer(),
                {xtype: 'datecolumn',dataIndex: 'fromDate',header: 'From Date',sortable: true},
                {xtype: 'datecolumn',dataIndex: 'toDate',header: 'To Date',sortable: true},
                {xtype: 'datecolumn',dataIndex: 'generationDate',header: 'Generation Date',sortable: true},
                {xtype: 'numbercolumn',align: 'right',dataIndex: 'totalCmQty',header: 'Total CM Qty',sortable: true},
                {xtype: 'numbercolumn',align: 'right',dataIndex: 'totalBmQty',header: 'Total BM Qty',sortable: true},
                {xtype: 'numbercolumn',align: 'right',dataIndex: 'totalCmPrice',header: 'Total CM Price',sortable: true},
                {xtype: 'numbercolumn',align: 'right',dataIndex: 'totalBmPrice',header: 'Total BM Price',sortable: true},
                {xtype: 'numbercolumn',align: 'right',dataIndex: 'discount',header: 'Discount',sortable: true},
                {xtype: 'numbercolumn',align: 'right',dataIndex: 'otherCharges',header: 'Other Charges',sortable: true},
                {xtype: 'numbercolumn',align: 'right',dataIndex: 'totalAmount',header: 'Total Amount',sortable: true},
                {xtype: 'gridcolumn',dataIndex: 'discountReason',header: 'Discount Reason',sortable: true},
                {xtype: 'numbercolumn',align: 'right',dataIndex: 'paidAmount',header: 'Paid Amount',sortable: true},
                {xtype: 'numbercolumn',align: 'right',dataIndex: 'balanceAmount',header: 'Balance Amount',sortable: true},
                {xtype: 'numbercolumn',align: 'right',dataIndex: 'bmPerQuantityPrice',header: 'BM Per Qty. Price',sortable: true},
                {xtype: 'numbercolumn',align: 'right',dataIndex: 'cmPerQuantityPrice',header: 'CMPer Qty. Price',sortable: true},
                {xtype: 'gridcolumn',dataIndex: 'comment',header: 'Comment',sortable: true},
                {xtype: 'booleancolumn',dataIndex: 'closed',header: 'Closed',sortable: true}
            ]
        });
        function processSelectedCustomer(topic, messageFromPublisher,subscriberData){
            customerBillListStore.proxy.conn.url ='/rest/bill/user/'+messageFromPublisher.id;
            customerBillListStore.load();
        }
        PageBus.subscribe("CustomerListGrid.customer.selected",this,processSelectedCustomer,'BillGrid');
        CustomerBillGrid.superclass.initComponent.call(this);
    }
});