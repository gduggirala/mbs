CustomerDetailsTabPanel = Ext.extend(Ext.TabPanel, {
    activeTab: 0,
    selectedCustomerRecord: null,
    id: 'customerDetailsTabPanelId',
    initComponent: function () {
        Ext.applyIf(this, {
            items: [
                {
                    xtype: 'panel',
                    layout: 'fit',
                    title: 'Info',
                    frame: true,
                    tbar: [
                        {xtype: 'button', text: 'Edit', iconCls: 'silk-user-edit', handler: editUser},
                        '-',
                        {xtype: 'button', text: 'Activate/Inactivate', id: 'activateOrInactivateId', handler: activateOrDeactivateUser},
                        '-'
                    ]
                },
                {
                    xtype: 'panel',
                    layout: 'fit',
                    title: 'Consumption',
                    items: [
                        new DailyOrdersGrid()
                    ]
                },
                {
                    xtype: 'panel',
                    title: 'Bills',
                    layout: 'fit',
                    items:[
                        new CustomerBillGrid()
                    ]
                }
            ]
        });
        var tpl = new Ext.XTemplate('' +
            '<p>Name: {name}</p>' +
            '<p>eMail: {email}</p>' +
            '<p>Phone: {phone}</p>' +
            '<p>Daily BM Order:{dailyBmOrder}</p>' +
            '<p>Daily CM Order:{dailyCmOrder}</p>' +
            '<p>BM Price:{bmPrice}</p>' +
            '<p>CM Price:{cmPrice}</p>' +
            '<p>Sector:{sector}</p>' +
            '<p>Is Active:{active}</p>');

        function processSelectedCustomer(topic, messageFromPublisher, subscriberData) {
            //this.selectedCustomerRecordId = messageFromPublisher.id;
            Ext.getCmp('customerDetailsTabPanelId').selectedCustomerRecord = messageFromPublisher.data;
            Ext.getCmp('customerDetailsTabPanelId').selectedCustomerRecord.id = messageFromPublisher.id;
            var infoPanel = this.getItem(0);
            tpl.overwrite(infoPanel.body, messageFromPublisher.data);
            var activateOrInactivateCmp = Ext.getCmp('activateOrInactivateId');
            if (messageFromPublisher.data.active === true) {
                activateOrInactivateCmp.setText("Inactivate");
                activateOrInactivateCmp.setIconClass("silk-user-gray")
            } else {
                activateOrInactivateCmp.setText("Activate");
                activateOrInactivateCmp.setIconClass("silk-user-green")
            }
        }

        function editUser() {
            PageBus.publish("CustomerDetailsTabPanel.customer.editCommand", Ext.getCmp('customerDetailsTabPanelId').selectedCustomerRecord);
        }

        function activateOrDeactivateUser() {
            var url = '/rest/user/status/' + Ext.getCmp('customerDetailsTabPanelId').selectedCustomerRecord.id;
            Ext.Ajax.request({
                url: url,
                method: 'POST',
                headers: {'Content-Type': 'application/json; charset=utf-8'},
                success: function (response, opts) {
                    var obj = Ext.decode(response.responseText);
                    console.dir(obj);
                    var messageFromPublisher = {};
                    messageFromPublisher.data = obj;
                    messageFromPublisher.id = obj.id;
                    PageBus.publish("CustomerDetailsTabPanel.customer.updated", messageFromPublisher);
                },
                failure: function (response, opts) {
                    console.log('server-side failure with status code ' + response.status);
                }
            });
        }

        //PageBus.publish("CustomerListGrid.customer.selected",record);
        PageBus.subscribe("CustomerListGrid.customer.selected", this, processSelectedCustomer, 'CustomerDetailsTabPanel');
        PageBus.subscribe("CustomerDetailsTabPanel.customer.updated", this, processSelectedCustomer, 'CustomerDetailsTabPanel');
        CustomerDetailsTabPanel.superclass.initComponent.call(this);
    }
});