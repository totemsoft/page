YAHOO.namespace('page.admin');

YAHOO.page.admin = {
    getPageId: function() {
        const el = YUD.get('pageId');
        return el && el.value ? parseInt(el.value) : '';
    },
    sendGetRequest: function(action, callback) {
        YUC.asyncRequest('GET', action, callback); 
    },
    sendPostRequest: function(action, callback, postdata) {
        YUC.setDefaultPostHeader(true);
        if (postdata && YL.isObject(postdata)) {
            postdata = YL.JSON.stringify(postdata);
            YUC.setDefaultPostHeader(false);
            //YUC.initHeader('Accept', 'application/json');
            YUC.initHeader('Content-Type', 'application/json;charset=UTF-8');
        }
        YUC.asyncRequest('POST', action, callback, postdata); 
    },
    reloadWindowCallback: {
        cache: false,
        success: function(oResponse) {
            const win = window.parent ? window.parent : window;
            win.location.reload();
        },
        failure: function(oResponse) {
            const status = oResponse && oResponse.status ? oResponse.status : '';
            const message = oResponse && oResponse.responseText ? oResponse.responseText : oResponse.statusText;
            console.log('Failure: ' + status + '<br/>' + message);
        }
    },
    fnSubscriberPageReady: function(type, args) {
        // init context menu(s)
        //const tabView = args[0].tabView;
        // tab-menu.{id}
        const elTabs = YUS.query('li[id^=tab-menu.]', 'pageDiv');
        const tabViewMenu = new YAHOO.widget.ContextMenu('tabViewMenu', {
            trigger: elTabs,
            zIndex: 3,
            lazyload: true,
            itemdata: [
                {text: 'Edit Tab', disabled: false},
                {text: 'Add Tab', disabled: false}
            ]
        });
        tabViewMenu.subscribe('click', function(oType, oArgs) {
            const oEvent = oArgs[0];
            const oItem = oArgs[1];
            if (!oItem.cfg.getProperty('disabled')) {
                const oTarget = this.contextEventTarget;
                const tabId = YAHOO.page.getId(oTarget.parentNode.parentNode.id);
                const tabName = oTarget.innerText;
                switch (oItem.index) {
                case 0:
                    YAHOO.page.admin.editTab(tabId, tabName);
                    break;
                case 1:
                    YAHOO.page.admin.addTab();
                    break;
                }
            }
        });
    },
    editTab: function(tabId, tabName) {
        tabName = window.prompt('Edit the tab label:', tabName);
        if (tabName) {
            const pageId = YAHOO.page.admin.getPageId();
            const tabDto = {pageId: pageId, id: tabId, name: tabName};
            YAHOO.page.admin.sendPostRequest('/page/tab', YAHOO.page.admin.reloadWindowCallback, tabDto);
        }
    },
    addTab: function() {
        const tabName = window.prompt('Enter the new tab label:');
        if (tabName) {
            const pageId = YAHOO.page.admin.getPageId();
            const tabDto = {pageId: pageId, id: null, name: tabName};
            YAHOO.page.admin.sendPostRequest('/page/tab', YAHOO.page.admin.reloadWindowCallback, tabDto);
        }
    }
};

(function() {
    // subscribe our Custom Event handler
    YAHOO.page.pageReadyEvent.subscribe(YAHOO.page.admin.fnSubscriberPageReady);
})();
