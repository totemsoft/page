YAHOO.namespace('page.admin');

//(function() {
    YAHOO.page.admin = {
        tabViewMenu: null,
        fnSubscriberPageReady: function(type, args) {
            //const tabView = args[0].tabView;
            // tab-menu.{id}
            const elTabs = YUS.query('li[id^=tab-menu.]', 'pageDiv');
            YAHOO.page.admin.tabViewMenu = new YAHOO.widget.ContextMenu('pageMenu', {
                trigger: elTabs,
                zIndex: 3,
                lazyload: true,
                itemdata: [
                    {text: 'Edit Tab', disabled: false},
                    {text: 'Add Tab', disabled: false}
                ]
            });
        }
    };

(function() {
    // subscribe our Custom Event handler
    YAHOO.page.pageReadyEvent.subscribe(YAHOO.page.admin.fnSubscriberPageReady);
})();
