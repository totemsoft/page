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
        //const tabView = args[0].tabView;
        YAHOO.page.admin.initTabContextMenu();
        YAHOO.page.admin.initSectionContextMenu();
    },
    initTabContextMenu: function() {
        // li[@id=tab-menu.{id}]
        const elTabs = YUS.query('li[id^=tab-menu.]', 'pageDiv');
        const tabMenu = new YAHOO.widget.ContextMenu('tabMenu', {
            trigger: elTabs,
            zIndex: 3,
            lazyload: true,
            itemdata: [
                [
                    {text: 'Edit Tab', disabled: false},
                    {text: 'Add Tab', disabled: false}
                ],
                [
                    {text: 'Add Section', disabled: false}
                ]
            ]
        });
        tabMenu.subscribe('click', function(oType, oArgs) {
            const oEvent = oArgs[0];
            const oItem = oArgs[1];
            if (!oItem.cfg.getProperty('disabled')) {
                const oTarget = this.contextEventTarget;
                const tabId = YAHOO.page.getId(oTarget.parentNode.parentNode.id);
                const tabName = oTarget.innerText;
                switch (oItem.groupIndex) {
                case 0:
                    switch (oItem.index) {
                    case 0:
                        YAHOO.page.admin.editTab(tabId, tabName);
                        break;
                    case 1:
                        YAHOO.page.admin.addTab();
                        break;
                    }
                    break;
                case 1:
                    switch (oItem.index) {
                    case 0:
                        YAHOO.page.admin.addSection(tabId);
                        break;
                    }
                    break;
                }
            }
        });
    },
    initSectionContextMenu: function() {
        // div[@id=section.{id}_c]/div[@id=section.{id}]
        const elSections = YUS.query('div[id^=section.] div[id^=section.]', 'pageDiv');
        const sectionMenu = new YAHOO.widget.ContextMenu('sectionMenu', {
            trigger: elSections,
            zIndex: 3,
            lazyload: true,
            itemdata: [
                [
                    {text: 'Edit Section', disabled: false}
                ],
                [
                    {text: 'Add Sub-Section', disabled: false}
                ]
            ]
        });
        sectionMenu.subscribe('click', function(oType, oArgs) {
            const oEvent = oArgs[0];
            const oItem = oArgs[1];
            if (!oItem.cfg.getProperty('disabled')) {
                const oTarget = this.contextEventTarget;
                const sectionId = YAHOO.page.getId(oTarget.parentNode.id);
                const sectionName = oTarget.innerText;
                switch (oItem.groupIndex) {
                case 0:
                    switch (oItem.index) {
                    case 0:
                        YAHOO.page.admin.editSection(sectionId, sectionName);
                        break;
                    }
                    break;
                case 1:
                    switch (oItem.index) {
                    case 0:
                        YAHOO.page.admin.addSubSection(sectionId);
                        break;
                    }
                    break;
                }
            }
        });
    },
    editTab: function(tabId, tabName) {
        console.log('editTab: tabId=' + tabId + ', tabName=' + tabName);
        tabName = window.prompt('Edit the tab label:', tabName);
        if (tabName) {
            const tabDto = {id: tabId, name: tabName};
            YAHOO.page.admin.sendPostRequest('/page/tab', YAHOO.page.admin.reloadWindowCallback, tabDto);
        }
    },
    addTab: function() {
        console.log('addTab:');
        const tabName = window.prompt('Enter the new tab label:');
        if (tabName) {
            const pageId = YAHOO.page.admin.getPageId();
            const tabDto = {pageId: pageId, name: tabName};
            YAHOO.page.admin.sendPostRequest('/page/tab', YAHOO.page.admin.reloadWindowCallback, tabDto);
        }
    },
    editSection: function(sectionId, sectionName) {
        console.log('editSection: sectionId=' + sectionId + ', sectionName=' + sectionName);
        sectionName = window.prompt('Edit the section name:', sectionName);
        if (sectionName) {
            const sectionDto = {id: sectionId, name: sectionName};
            YAHOO.page.admin.sendPostRequest('/page/section', YAHOO.page.admin.reloadWindowCallback, sectionDto);
        }
    },
    addSection: function(tabId) {
        console.log('addSection: tabId=' + tabId);
        const sectionName = window.prompt('Enter the new section name:');
        if (sectionName) {
            const sectionDto = {tabId: tabId, name: sectionName};
            YAHOO.page.admin.sendPostRequest('/page/section', YAHOO.page.admin.reloadWindowCallback, sectionDto);
        }
    },
    editSubSection: function(subSectionId, subSectionName) {
        console.log('editSubSection: subSectionId=' + subSectionId + ', subSectionName=' + subSectionName);
        subSectionName = window.prompt('Edit the sub-section name:', subSectionName);
        if (subSectionName) {
            const subSectionDto = {id: subSectionId, name: subSectionName};
            YAHOO.page.admin.sendPostRequest('/page/subSection', YAHOO.page.admin.reloadWindowCallback, subSectionDto);
        }
    },
    addSubSection: function(sectionId) {
        console.log('addSubSection: sectionId=' + sectionId);
        const subSectionName = window.prompt('Enter the new sub-section name:');
        if (subSectionName) {
            const subSectionDto = {sectionId: sectionId, name: subSectionName};
            YAHOO.page.admin.sendPostRequest('/page/subSection', YAHOO.page.admin.reloadWindowCallback, subSectionDto);
        }
    }
};

(function() {
    YAHOO.page.pageReadyEvent.subscribe(YAHOO.page.admin.fnSubscriberPageReady);
})();
