YAHOO.namespace('page.admin');

YAHOO.page.admin = {
    parseJsonData: function(oData, ignoreErrors) {
        if (!oData) {
            return null;
        }
        // encode Back Slashes
        oData = oData.replace(/\\/g, '\\\\');
        try {
            return YAHOO.lang.JSON.parse(oData);
        } catch (e) {
            if (ignoreErrors) {
                return null;
            }
            throw e;
        }
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
            const data = YAHOO.page.admin.parseJsonData(oResponse.responseText, true);
            if (data && data.pageId)  {
                const url = new URL(win.location.href);
                url.searchParams.set('pageId', data.pageId);
                win.location.href = url.toString();
            } else {
                win.location.reload();
            }
        },
        failure: function(oResponse) {
            const status = oResponse && oResponse.status ? oResponse.status : '';
            const message = oResponse && oResponse.responseText ? oResponse.responseText : oResponse.statusText;
            console.log('Failure: ' + status + '<br/>' + message);
        }
    },
    fnSubscriberPageReady: function(type, args) {
        YAHOO.page.admin.tabView = args[0].tabView;
        YAHOO.page.admin.initPageContextMenu();
        YAHOO.page.admin.initTabContextMenu();
        YAHOO.page.admin.initSectionContextMenu();
        YAHOO.page.admin.initSubSectionContextMenu();
    },
    initPageContextMenu: function() {
        // h2[@id=page.{id}]
        const triggerNode = YUS.query('h2[id^=page.]');
        const pageMenu = new YAHOO.widget.ContextMenu('pageMenu', {
            trigger: triggerNode,
            zIndex: 3,
            lazyload: true,
            itemdata: [
                [
                    {text: 'Edit Page', disabled: false},
                    {text: 'Add Page', disabled: false}
                ]
            ]
        });
        pageMenu.subscribe('click', function(oType, oArgs) {
            const oEvent = oArgs[0];
            const oItem = oArgs[1];
            if (!oItem.cfg.getProperty('disabled')) {
                const oTarget = this.contextEventTarget;
                const pageId = YAHOO.page.getPageId();
                const pageName = oTarget.innerText;
                switch (oItem.groupIndex) {
                case 0:
                    switch (oItem.index) {
                    case 0:
                        YAHOO.page.admin.editPage(pageId, pageName);
                        break;
                    case 1:
                        YAHOO.page.admin.addPage();
                        break;
                    }
                    break;
                }
            }
        });
    },
    initTabContextMenu: function() {
        // li[@id=tab-menu.{id}]
        const triggerNodes = YUS.query('li[id^=tab-menu.]', 'pageDiv');
        const tabMenu = new YAHOO.widget.ContextMenu('tabMenu', {
            trigger: triggerNodes,
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
        // div[@id=section-menu.{id}]
        const triggerNodes = YUS.query('div[id^=section-menu.]', 'pageDiv');
        const sectionMenu = new YAHOO.widget.ContextMenu('sectionMenu', {
            trigger: triggerNodes,
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
    initSubSectionContextMenu: function() {
        // div[@id=subSection.{id}]/caption
        const triggerNodes = YUS.query('div[id^=subSection.] caption', 'pageDiv');
        const subSectionMenu = new YAHOO.widget.ContextMenu('subSectionMenu', {
            trigger: triggerNodes,
            zIndex: 3,
            lazyload: true,
            itemdata: [
                [
                    {text: 'Edit Sub-Section', disabled: false}
                ]
            ]
        });
        subSectionMenu.subscribe('click', function(oType, oArgs) {
            const oEvent = oArgs[0];
            const oItem = oArgs[1];
            if (!oItem.cfg.getProperty('disabled')) {
                const oTarget = this.contextEventTarget;
                const subSectionId = YAHOO.page.getId(oTarget.parentNode.id);
                const subSectionName = oTarget.innerText;
                switch (oItem.groupIndex) {
                case 0:
                    switch (oItem.index) {
                    case 0:
                        YAHOO.page.admin.editSubSection(subSectionId, subSectionName);
                        break;
                    }
                    break;
                }
            }
        });
    },
    openEditDialog: function(el, fnSubmitHandler) {
        const dialog = new YAHOO.widget.SimpleDialog(el, {
            close: true,
            draggable: true,
            fixedcenter: true,
            visible: false,
            modal: false,
            icon: null,
            autofillheight: 'body',
            constraintoviewport: true,
            context: ['showbtn', 'tl', 'bl'],
            buttons: [
                {text: 'Save', isDefault: true, handler: fnSubmitHandler},
                {text: 'Cancel', handler: function() {
                    this.cancel();
                }}
            ]
        });
        dialog.cfg.queueProperty('keylisteners', new YAHOO.util.KeyListener(
            document,
            { keys: 27 },
            { fn: function() {this.cancel();}, scope: dialog, correctScope: true }
        ));
        dialog.render(document.body);
        return dialog;
    },
    editPage: function(pageId, pageName) {
        console.log('editPage: pageId=' + pageId + ', pageName=' + pageName);
        if (!YAHOO.page.admin.editPageDialog) {
            const fnSubmitHandler = function() {
                const pageDto = {
                    id: pageId,
                    name: YUD.get('page.name').value
                };
                YAHOO.page.admin.sendPostRequest('/page', YAHOO.page.admin.reloadWindowCallback, pageDto);
            };
            YAHOO.page.admin.editPageDialog = YAHOO.page.admin.openEditDialog('editPageDialog', fnSubmitHandler);
        }
        YUD.get('page.name').value = pageName;
        YAHOO.page.admin.editPageDialog.bringToTop();
        YAHOO.page.admin.editPageDialog.show();
    },
    addPage: function() {
        console.log('addPage:');
        if (!YAHOO.page.admin.editPageDialog) {
            const fnSubmitHandler = function() {
                const pageDto = {
                    name: YUD.get('page.name').value
                };
                YAHOO.page.admin.sendPostRequest('/page', YAHOO.page.admin.reloadWindowCallback, pageDto);
            };
            YAHOO.page.admin.editPageDialog = YAHOO.page.admin.openEditDialog('editPageDialog', fnSubmitHandler);
        }
        YUD.get('page.name').value = '';
        YAHOO.page.admin.editPageDialog.bringToTop();
        YAHOO.page.admin.editPageDialog.show();
    },
    editTab: function(tabId, tabName) {
        console.log('editTab: tabId=' + tabId + ', tabName=' + tabName);
        if (!YAHOO.page.admin.editTabDialog) {
            const fnSubmitHandler = function() {
                const tabDto = {
                    id: tabId,
                    name: YUD.get('tab.name').value
                };
                YAHOO.page.admin.sendPostRequest('/page/tab', YAHOO.page.admin.reloadWindowCallback, tabDto);
            };
            YAHOO.page.admin.editTabDialog = YAHOO.page.admin.openEditDialog('editTabDialog', fnSubmitHandler);
        }
        YUD.get('tab.name').value = tabName;
        YAHOO.page.admin.editTabDialog.bringToTop();
        YAHOO.page.admin.editTabDialog.show();
    },
    addTab: function() {
        console.log('addTab:');
        if (!YAHOO.page.admin.editTabDialog) {
            const fnSubmitHandler = function() {
                const tabDto = {
                    pageId: YAHOO.page.getPageId(),
                    name: YUD.get('tab.name').value
                };
                YAHOO.page.admin.sendPostRequest('/page/tab', YAHOO.page.admin.reloadWindowCallback, tabDto);
            };
            YAHOO.page.admin.editTabDialog = YAHOO.page.admin.openEditDialog('editTabDialog', fnSubmitHandler);
        }
        YUD.get('tab.name').value = '';
        YAHOO.page.admin.editTabDialog.bringToTop();
        YAHOO.page.admin.editTabDialog.show();
    },
    openEditSectionDialog: function() {
        if (!YAHOO.page.admin.editSectionDialog) {
            const fnSubmitHandler = function() {
                const elSplitRatio = YUD.get('section.splitRatio');
                YAHOO.page.admin.sendPostRequest('/page/section', YAHOO.page.admin.reloadWindowCallback, {
                    id: YUD.get('section.id').value,
                    name: YUD.get('section.name').value,
                    index: YUD.get('section.index').value,
                    splitRatio: elSplitRatio.value,
                    tabId: YUD.get('section.tabId').value
                });
            };
            YAHOO.page.admin.editSectionDialog = YAHOO.page.admin.openEditDialog('editSectionDialog', fnSubmitHandler);
        }
        return YAHOO.page.admin.editSectionDialog;
    },
    editSection: function(sectionId, sectionName) {
        console.log('editSection: sectionId=' + sectionId + ', sectionName=' + sectionName);
        YUD.get('section.id').value = sectionId;
        YUD.get('section.name').value = sectionName;
        YUD.get('section.index').value = YUD.get('section.index.' + sectionId).value;
        const elSplitRatio = YUD.get('section.splitRatio');
        elSplitRatio.value = YUD.get('section.splitRatio.' + sectionId).value;
        const dialog = YAHOO.page.admin.openEditSectionDialog();
        dialog.bringToTop();
        dialog.show();
    },
    addSection: function(tabId) {
        console.log('addSection: tabId=' + tabId);
        YUD.get('section.id').value = '';
        YUD.get('section.name').value = '';
        YUD.get('section.index').value = '1';
        const elSplitRatio = YUD.get('section.splitRatio');
        elSplitRatio.selectedIndex = 0;
        YUD.get('section.tabId').value = tabId;
        const dialog = YAHOO.page.admin.openEditSectionDialog();
        dialog.bringToTop();
        dialog.show();
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
