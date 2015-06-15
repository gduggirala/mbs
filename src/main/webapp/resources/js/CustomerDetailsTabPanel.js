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
                        /*{xtype: 'button', text: 'Edit', iconCls: 'silk-user-edit', handler: editUser},
                        '-',*/
                        {xtype: 'button', text: 'Activate/Inactivate', id: 'activateOrInactivateId', handler: activateOrDeactivateUser},
                        '-'
                    ]
                },
                {
                    xtype: 'panel',
                    layout: 'fit',
                    title: 'Consumption',
                    id:'dailyOrderConsumptionPanel',
                    items: [
                        new DailyOrdersGrid()
                    ]
                },
                {
                    xtype: 'panel',
                    title: 'Bills',
                    layout: 'fit',
                    id:'customerBillPanel',
                    items:[
                        new CustomerBillGrid()
                    ]
                }
            ]
        });
        var tpl = new Ext.XTemplate(
         '<style type="text/css">',
                 '.tg  {border-collapse:collapse;border-spacing:0;margin-left: 2cm;margin-top: .5cm; margin-bottom: .5cm}',
                 '.tg td{font-family:Arial, sans-serif;font-size:14px;padding:10px 5px;border-style:solid;border-width:1px;overflow:hidden;word-break:normal;}',
                 '.tg th{font-family:Arial, sans-serif;font-size:14px;font-weight:normal;padding:10px 5px;border-style:solid;border-width:1px;overflow:hidden;word-break:normal;}',
                 '.tg .tg-e3zv{font-weight:bold;}',
                 '</style>',
                 '<table class="tg">',
            '<tr><td class="tg-e3zv">Name</td> <td class="tg-e3zv">{name}</td></tr>' ,
            '<tr><td class="tg-e3zv">eMail</td> <td class="tg-e3zv">{email}</td></tr>' ,
            '<tr><td class="tg-e3zv">Phone</td> <td class="tg-e3zv">{phone}</td></tr>' ,
            '<tr><td class="tg-e3zv">Daily BM Order</td> <td class="tg-e3zv">{dailyBmOrder}</td></tr>' ,
            '<tr><td class="tg-e3zv">Daily CM Order</td> <td class="tg-e3zv">{dailyCmOrder}</td></tr>' ,
            '<tr><td class="tg-e3zv">BM Price</td> <td class="tg-e3zv">{bmPrice}</td></tr>' ,
            '<tr><td class="tg-e3zv">CM Price</td> <td class="tg-e3zv">{cmPrice}</td></tr>' ,
            '<tr><td class="tg-e3zv">Sector</td> <td class="tg-e3zv">{sector}</td></tr>' ,
            '<tr><td class="tg-e3zv">Referred by</td class="tg-e3zv"> <td>{referredBy}</td></tr>' ,
            '<tr><td class="tg-e3zv">Is Active</td> <td class="tg-e3zv">{active}</td></tr>' ,
            '</table>');

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
            infoPanel.setTitle(messageFromPublisher.data.name+"'s info");
        }

        function editUser() {
            PageBus.publish("CustomerDetailsTabPanel.customer.editCommand", Ext.getCmp('customerDetailsTabPanelId').selectedCustomerRecord);
        }

        function activateOrDeactivateUser() {
            var url = './rest/user/status/' + Ext.getCmp('customerDetailsTabPanelId').selectedCustomerRecord.id;
            Ext.Ajax.request({
                url: url,
                method: 'POST',
                headers: {'Content-Type': 'application/json; charset=utf-8'},
                success: function (response, opts) {
                    var obj = Ext.decode(response.responseText);
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