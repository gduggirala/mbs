MasterViewUi = Ext.extend(Ext.Viewport, {

    initComponent: function () {
        var customerListGrid = new CustomerListGrid({flex: 2});
        var customerDetailsPanel = new CustomerDetailsTabPanel({flex:3});
        Ext.applyIf(this, {
            items: [
                {
                    xtype: 'tabpanel',
                    id:'masterTabPanelId',
                    autoScroll:false,
                    height: Ext.getBody().getViewSize().height,
                    width: Ext.getBody().getViewSize().width,
                    activeTab:0,
                    items: [
                        {
                            xtype: 'panel',
                            layout: 'vbox',
                            layoutConfig: {
                                align: 'stretch',
                                pack: 'start'
                            },
                            title: 'Overview',
                            frame:false,
                            items:[
                                    new DailyOrderReportsChart(),
                                    new PaidToUnpaidReport()
                            ]
                        },
                        {
                            xtype: 'panel',
                            frame: true,
                            title: 'Customer',
                            layout: 'vbox',
                            layoutConfig: {
                                align: 'stretch',
                                pack: 'start'
                            },
                            items: [
                                customerListGrid,
                                customerDetailsPanel
                            ]
                        }
                    ]
                } //Tab panel closed.
            ]
        }); //Close apply
        MasterViewUi.superclass.initComponent.call(this);
    } //Close initComponent
}); //Close MasterViewUI, Ext.extend

Ext.onReady(function () {
    console.info('woohoo!!!');
    //Ext.Msg.alert("First alert", "First alert box, if everything is good");
    master = new MasterViewUi();
    master.render(Ext.getBody());
}); //end onReady