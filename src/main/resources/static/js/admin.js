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
            if (postdata instanceof Map) {
                // Convert the Map to a plain object
                postdata = Object.fromEntries(postdata);
            }
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
        failure: YAHOO.page.admin.failureHandler
    },
    failureHandler: function(oResponse) {
        const status = oResponse && oResponse.status ? oResponse.status : '';
        const message = oResponse && oResponse.responseText ? oResponse.responseText : oResponse.statusText;
        console.log('Failure: ' + status + '<br/>' + message);
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
        const triggerNodes = YUS.query('div[id^=data.subSection.] table caption', 'pageDiv');
        const subSectionMenu = new YAHOO.widget.ContextMenu('subSectionMenu', {
            trigger: triggerNodes,
            zIndex: 3,
            lazyload: true,
            itemdata: [
                [
                    {text: 'Edit Sub-Section', disabled: false},
                    {text: 'Map Sub-Section Keys', disabled: false}
                ]
            ]
        });
        subSectionMenu.subscribe('click', function(oType, oArgs) {
            const oEvent = oArgs[0];
            const oItem = oArgs[1];
            if (!oItem.cfg.getProperty('disabled')) {
                const oTarget = this.contextEventTarget;
                const subSectionId = YAHOO.page.getId(oTarget.parentNode.parentNode.id);
                const subSectionName = oTarget.innerText;
                switch (oItem.groupIndex) {
                case 0:
                    switch (oItem.index) {
                    case 0:
                        YAHOO.page.admin.editSubSection(subSectionId, subSectionName);
                        break;
                    case 1:
                        YAHOO.page.admin.mapSubSectionKeys(subSectionId, subSectionName);
                        break;
                    }
                    break;
                }
            }
        });
    },
    openEditDialog: function(el, fnSubmitHandler, oEventDef, oConfig) {
        const dialog = new YAHOO.widget.SimpleDialog(el, {
            close: true,
            draggable: true,
            fixedcenter: true,
            visible: false,
            modal: false,
            icon: null,
            height: oConfig && oConfig.height ? oConfig.height : null,
            width: oConfig && oConfig.width ? oConfig.width : null,
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
        if (oEventDef) {
            dialog.subscribe(oEventDef.name, oEventDef.handler);
        }
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
            YAHOO.page.admin.editPageDialog = YAHOO.page.admin.openEditDialog('editPageDialog',
                fnSubmitHandler);
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
            YAHOO.page.admin.editPageDialog = YAHOO.page.admin.openEditDialog('editPageDialog',
                fnSubmitHandler);
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
            YAHOO.page.admin.editTabDialog = YAHOO.page.admin.openEditDialog('editTabDialog',
                fnSubmitHandler);
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
            YAHOO.page.admin.editTabDialog = YAHOO.page.admin.openEditDialog('editTabDialog',
                fnSubmitHandler);
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
            YAHOO.page.admin.editSectionDialog = YAHOO.page.admin.openEditDialog('editSectionDialog',
                fnSubmitHandler);
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
    openEditSubSectionDialog: function() {
        if (!YAHOO.page.admin.editSubSectionDialog) {
            const fnSubmitHandler = function() {
                const elRowTagType = YUD.get('subSection.rowTagType');
                const elColumnTagType = YUD.get('subSection.columnTagType');
                YAHOO.page.admin.sendPostRequest('/page/subSection', YAHOO.page.admin.reloadWindowCallback, {
                    id: YUD.get('subSection.id').value,
                    name: YUD.get('subSection.name').value,
                    sectionId: YUD.get('subSection.sectionId').value,
                    rowTagTypeId: elRowTagType.value,
                    columnTagTypeId: elColumnTagType.value
                });
            };
            YAHOO.page.admin.editSubSectionDialog = YAHOO.page.admin.openEditDialog('editSubSectionDialog',
                fnSubmitHandler);
        }
        return YAHOO.page.admin.editSubSectionDialog;
    },
    editSubSection: function(subSectionId, subSectionName) {
        console.log('editSubSection: subSectionId=' + subSectionId + ', subSectionName=' + subSectionName);
        YUD.get('subSection.id').value = subSectionId;
        YUD.get('subSection.name').value = subSectionName;
        YUD.get('subSection.sectionId').value = YUD.get('subSection.sectionId.' + subSectionId).value;
        const elRowTagType = YUD.get('subSection.rowTagType');
        elRowTagType.value = YUD.get('subSection.rowTagTypeId.' + subSectionId).value;
        const elColumnTagType = YUD.get('subSection.columnTagType');
        elColumnTagType.value = YUD.get('subSection.columnTagTypeId.' + subSectionId).value;
        const dialog = YAHOO.page.admin.openEditSubSectionDialog();
        dialog.bringToTop();
        dialog.show();
    },
    addSubSection: function(sectionId) {
        console.log('addSubSection: sectionId=' + sectionId);
        YUD.get('subSection.id').value = '';
        YUD.get('subSection.name').value = '';
        YUD.get('subSection.sectionId').value = sectionId;
        const elRowTagType = YUD.get('subSection.rowTagType');
        elRowTagType.selectedIndex = 0;
        const elColumnTagType = YUD.get('subSection.columnTagType');
        elColumnTagType.selectedIndex = 0;
        const dialog = YAHOO.page.admin.openEditSubSectionDialog();
        dialog.bringToTop();
        dialog.show();
    },
    openMapSubSectionKeysDialog: function(subSectionId) {
        if (!YAHOO.page.admin.mapSubSectionKeysDialog) {
            YAHOO.page.admin.initSearchKeys();
            // dialog
            const fnSubmitHandler = function() {
                const elRowTagType = YUD.get('subSectionKeys.rowTagType');
                const elColumnTagType = YUD.get('subSectionKeys.columnTagType');
                const rs = YAHOO.page.admin.keysDataTable.getRecordSet();
                const keyIds = new Set();
                for (var r = 0; r < rs.getLength(); r++) {
                    const row = rs.getRecord(r);
                    const data = row.getData();
                    keyIds.add(data.id);
                }
                const keys = Array.from(keyIds).map(keyId => ({id: keyId}));
                YAHOO.page.admin.sendPostRequest('/page/subSection/map', YAHOO.page.admin.reloadWindowCallback, {
                    id: YUD.get('subSectionKeys.id').value,
                    rowTagTypeId: elRowTagType.value,
                    columnTagTypeId: elColumnTagType.value,
                    keys: keys
                });
            };
            const oEventDef = {
                name:'beforeShow',
                handler: function(oType, oArgs) {
                    YAHOO.page.admin.initKeysDataTable([]);
                    YAHOO.page.admin.findSubSectionKeys(subSectionId);
                }
            };
            const w = YUD.getViewportWidth();
            YAHOO.page.admin.mapSubSectionKeysDialog = YAHOO.page.admin.openEditDialog('mapSubSectionKeysDialog',
                fnSubmitHandler, oEventDef, {width: (w / 3) + 'px'});
        } else {
            // clear subSectionKeysSearch UI/data/rows
            YAHOO.page.admin.autoCompletes.forEach(ac => {
                const input = ac.getInputEl();
                input.value = '';
            });
            YAHOO.page.admin.tagTypeMap.clear();
            YAHOO.page.admin.clearKeys();
        }
        return YAHOO.page.admin.mapSubSectionKeysDialog;
    },
    initSearchKeys: function(subSectionId) {
        YAHOO.page.admin.tagTypeMap = new Map(); // <tagTypeId, tagName/tagId>
        YAHOO.page.admin.autoCompletes = [];
        // div[@id=subSectionKeys.tagType.{id}]
        const elAutoCompletes = YUS.query('div[id^=subSectionKeys.tagType.]');
        elAutoCompletes.forEach(elAutoComplete => {
            const id = elAutoComplete.id; // subSectionKeys.tagType.{id}
            const tagTypeId = YAHOO.page.getId(id);
            const input = YUD.get('input.' + id);
            const hidden = YUD.get('hidden.' + id);
            const container = YUD.get('container.' + id);
            const ds = new YAHOO.util.XHRDataSource('/page/tag/' + tagTypeId);
            ds.responseType = YAHOO.util.XHRDataSource.TYPE_JSON;
            ds.responseSchema = {
                resultsList: 'records',
                fields: ['name', 'id']
            };
            const ac = new YAHOO.widget.AutoComplete(input, container, ds, {
                prehighlightClassName: 'yui-ac-prehighlight',
                autoHighlight: false,
                allowBrowserAutocomplete: false,
                forceSelection: false,
                queryDelay: .5,
                minQueryLength: 1,
                maxResultsDisplayed: 100,
                animVert: true,
                animSpeed: 0.4
            });
            ac.dataRequestEvent.subscribe(function(type, args) {
                const ac = args[0]; // The AutoComplete instance
                //const query = args[1] <String> The query string
                //const request = args[2] <Object> The request
                const input = ac.getInputEl();
                const tagTypeId = YAHOO.page.getId(input.id);
                YAHOO.page.admin.tagTypeMap.set(tagTypeId, input.value);
            });
            ac.itemSelectEvent.subscribe(function(type, args) {
                const ac = args[0]; // The AutoComplete instance
                //const elLI = args[1]; // The selected <li> element item
                const oData = args[2]; // The data returned for the item, either as an object, or mapped from the schema into an array
                // update with the selected item's ID
                const input = ac.getInputEl();
                const tagTypeId = YAHOO.page.getId(input.id);
                YAHOO.page.admin.tagTypeMap.set(tagTypeId, oData[1]);
            });
            YAHOO.page.admin.autoCompletes.push(ac);
        });
        //
        new YAHOO.widget.Button('subSectionKeysClearButton').on('click', function(e) {
            YAHOO.page.admin.clearKeys();
        });
        new YAHOO.widget.Button('subSectionKeysSearchButton').on('click', function(e) {
            YAHOO.page.admin.findKeys();
        });
    },
    initKeysDataTable: function(records) {
        if (!YAHOO.page.admin.keysDataTable) {
            const columnDefs = [
                {key: 'id', label: 'ID', sortable: true, width: 20},
                {key: 'name', label: 'Name', sortable: true, width: 50},
                {key: 'title', label: 'Title', sortable: true, width: 100},
                {key: 'tagSummary', label: 'Tag Summary', width: 200}
            ];
            const fields = columnDefs.map(columnDef => columnDef.key);
            const dataSource = new YAHOO.util.DataSource(records, {
                responseType: YAHOO.util.XHRDataSource.TYPE_JSARRAY,
                responseSchema: {fields: fields}
            });
            const r = YUD.getRegion('subSectionKeys'); // yui-gd 1/3 - 2/3, 32% - 66%
            const dataTableConfig = {
                caption: '',
                height: r.height + 'px',
                width: Math.floor(r.width * 0.66) + 'px'
            };
            YAHOO.page.admin.keysDataTable = new YAHOO.widget.ScrollingDataTable('subSectionKeysSearchResult',
                columnDefs, dataSource, dataTableConfig
            );
        }
    },
    clearKeys: function() {
        const length = YAHOO.page.admin.keysDataTable.getRecordSet().getLength();
        YAHOO.page.admin.keysDataTable.deleteRows(0, length);
    },
    findSubSectionKeys: function(subSectionId) {
        YAHOO.page.admin.sendGetRequest('/page/key/' + subSectionId,
            YAHOO.page.admin.findKeysCallback);
    },
    findKeys: function() {
        YAHOO.page.admin.sendPostRequest('/page/key',
            YAHOO.page.admin.findKeysCallback,
            YAHOO.page.admin.tagTypeMap);
    },
    findKeysCallback: {
        cache: false,
        success: function(oResponse) {
            const data = YAHOO.page.admin.parseJsonData(oResponse.responseText, true);
            const dataTable = YAHOO.page.admin.keysDataTable;
            dataTable.addRows(data.records);
            dataTable.sortColumn(dataTable.getColumn('id'), YAHOO.widget.DataTable.CLASS_ASC);
            //dataTable.validateColumnWidths(null);
        },
        failure: YAHOO.page.admin.failureHandler
    },
    mapSubSectionKeys: function(subSectionId, subSectionName) {
        console.log('mapSubSectionKeys: subSectionId=' + subSectionId + ', subSectionName=' + subSectionName);
        // TODO:
        YUD.get('subSectionKeys.id').value = subSectionId;
        const elRowTagType = YUD.get('subSectionKeys.rowTagType');
        elRowTagType.value = YUD.get('subSection.rowTagTypeId.' + subSectionId).value;
        const elColumnTagType = YUD.get('subSectionKeys.columnTagType');
        elColumnTagType.value = YUD.get('subSection.columnTagTypeId.' + subSectionId).value;
        const dialog = YAHOO.page.admin.openMapSubSectionKeysDialog(subSectionId);
        dialog.bringToTop();
        dialog.show();
    }
};

(function() {
    YAHOO.page.pageReadyEvent.subscribe(YAHOO.page.admin.fnSubscriberPageReady);
})();
