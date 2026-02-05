YAHOO.namespace('page.admin');

YAHOO.page.admin = {
    fnSubscriberPageReady: function(type, args) {
        YAHOO.page.admin.tabView = args[0].tabView;
        const context = [];
        YAHOO.page.admin.initPageContextMenu(context);
        YAHOO.page.admin.initTabContextMenu(context);
        YAHOO.page.admin.initSectionContextMenu(context);
        YAHOO.page.admin.initSubSectionContextMenu(context);
        setTimeout(function(context) {
            YAHOO.page.admin.initTooltip(context);
        }, 100, context);
    },
    initPageContextMenu: function(context) {
        // h2[@id=page.{id}]
        const triggerNode = YUS.query('h2[id^=page.]');
        const pageContextMenu = new YAHOO.widget.ContextMenu('pageContextMenu', {
            trigger: triggerNode,
            zIndex: 2,
            lazyload: true,
            itemdata: [
                [
                    {text: 'Edit Page', disabled: false},
                    {text: 'Add Page', disabled: false}
                ]
            ]
        });
        pageContextMenu.subscribe('click', function(oType, oArgs) {
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
        context.push(...triggerNode.map(el => el.id));
    },
    initTabContextMenu: function(context) {
        // li[@id=tab-menu.{id}]
        const triggerNodes = YUS.query('li[id^=tab-menu.]', 'pageDiv');
        const tabContextMenu = new YAHOO.widget.ContextMenu('tabContextMenu', {
            trigger: triggerNodes,
            zIndex: 2,
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
        tabContextMenu.subscribe('click', function(oType, oArgs) {
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
        context.push(...triggerNodes.map(el => el.id));
    },
    initSectionContextMenu: function(context) {
        // div[@id=section-menu.{id}]
        const triggerNodes = YUS.query('div[id^=section-menu.]', 'pageDiv');
        const sectionContextMenu = new YAHOO.widget.ContextMenu('sectionContextMenu', {
            trigger: triggerNodes,
            zIndex: 2,
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
        sectionContextMenu.subscribe('click', function(oType, oArgs) {
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
        context.push(...triggerNodes.map(el => el.id));
    },
    initSubSectionContextMenu: function(context) {
        // div[@id=subSection.{id}]/caption
        const triggerNodes = YUS.query('div[id^=data.subSection.] table caption', 'pageDiv');
        const subSectionContextMenu = new YAHOO.widget.ContextMenu('subSectionContextMenu', {
            trigger: triggerNodes,
            zIndex: 2,
            lazyload: true,
            itemdata: [
                [
                    {text: 'Edit Sub-Section', disabled: false},
                    {text: 'Map Sub-Section Keys', disabled: false}
                ]
            ]
        });
        subSectionContextMenu.subscribe('click', function(oType, oArgs) {
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
        context.push(...triggerNodes.map(el => el)); // caption[no @id]
    },
    initTooltip: function(context) {
        const tooltip = new YAHOO.widget.Tooltip('contextMenuTooltip', {
            context: context,
            text: 'Right-click here to open the context menu for edit.',
            zIndex: 3,
            //preventcontextoverlap: true,
            autodismissdelay: 3000,
            //hidedelay: 250,
            //xyoffset: [1,1],
            effect: {effect:YAHOO.widget.ContainerEffect.FADE, duration:0.50}
        });
        // Set the text for the tooltip just before we display it.
        //tooltip.contextTriggerEvent.subscribe(function(type, args) {
        //    const context = args[0];
        //    this.cfg.setProperty('text', 'Right-click here to open the context menu for edit.');
        //});
    },
    openEditPageDialog: function(pageId) {
        if (!YAHOO.page.admin.editPageDialog) {
            const fnSubmitHandler = function() {
                const pageDto = {
                    id: pageId, // PK
                    name: YUD.get('page.name').value
                };
                YAHOO.page.sendPostRequest('/page', YAHOO.page.reloadWindowCallback, pageDto);
            };
            YAHOO.page.admin.editPageDialog = YAHOO.page.openEditDialog('editPageDialog', {},
                fnSubmitHandler);
        }
        YAHOO.page.admin.editPageDialog.bringToTop();
        YAHOO.page.admin.editPageDialog.show();
        return YAHOO.page.admin.editPageDialog;
    },
    editPage: function(pageId, pageName) {
        console.log('editPage: pageId=' + pageId + ', pageName=' + pageName);
        YUD.get('page.name').value = pageName;
        YAHOO.page.admin.openEditPageDialog(pageId);
    },
    addPage: function() {
        console.log('addPage:');
        YUD.get('page.name').value = '';
        YAHOO.page.admin.openEditPageDialog(null);
    },
    openEditTabDialog: function(tabId) {
        if (!YAHOO.page.admin.editTabDialog) {
            const fnSubmitHandler = function() {
                const tabDto = {
                    pageId: YAHOO.page.getPageId(), // owner
                    id: tabId, // PK
                    name: YUD.get('tab.name').value,
                    index: YUD.get('tab.index').value
                };
                YAHOO.page.sendPostRequest('/page/tab', YAHOO.page.reloadWindowCallback, tabDto);
            };
            YAHOO.page.admin.editTabDialog = YAHOO.page.openEditDialog('editTabDialog', {},
                fnSubmitHandler);
        }
        YAHOO.page.admin.editTabDialog.bringToTop();
        YAHOO.page.admin.editTabDialog.show();
        return YAHOO.page.admin.editTabDialog;
    },
    editTab: function(tabId, tabName) {
        console.log('editTab: tabId=' + tabId + ', tabName=' + tabName);
        YUD.get('tab.name').value = tabName;
        YUD.get('tab.index').value = YUD.get('tab.index.' + tabId).value;
        YAHOO.page.admin.openEditTabDialog(tabId);
    },
    addTab: function() {
        console.log('addTab:');
        YUD.get('tab.name').value = '';
        YUD.get('tab.index').value = '1';
        YAHOO.page.admin.openEditTabDialog(null);
    },
    openEditSectionDialog: function() {
        if (!YAHOO.page.admin.editSectionDialog) {
            const fnSubmitHandler = function() {
                const elSplitRatio = YUD.get('section.splitRatio');
                YAHOO.page.sendPostRequest('/page/section', YAHOO.page.reloadWindowCallback, {
                    tabId: YUD.get('section.tabId').value, // owner
                    id: YUD.get('section.id').value, // PK
                    name: YUD.get('section.name').value,
                    index: YUD.get('section.index').value,
                    splitRatio: elSplitRatio.value
                });
            };
            YAHOO.page.admin.editSectionDialog = YAHOO.page.openEditDialog('editSectionDialog', {},
                fnSubmitHandler);
        }
        YAHOO.page.admin.editSectionDialog.bringToTop();
        YAHOO.page.admin.editSectionDialog.show();
        return YAHOO.page.admin.editSectionDialog;
    },
    editSection: function(sectionId, sectionName) {
        console.log('editSection: sectionId=' + sectionId + ', sectionName=' + sectionName);
        YUD.get('section.id').value = sectionId;
        YUD.get('section.name').value = sectionName;
        YUD.get('section.index').value = YUD.get('section.index.' + sectionId).value;
        const elSplitRatio = YUD.get('section.splitRatio');
        elSplitRatio.value = YUD.get('section.splitRatio.' + sectionId).value;
        YAHOO.page.admin.openEditSectionDialog();
    },
    addSection: function(tabId) {
        console.log('addSection: tabId=' + tabId);
        YUD.get('section.id').value = '';
        YUD.get('section.name').value = '';
        YUD.get('section.index').value = '1';
        const elSplitRatio = YUD.get('section.splitRatio');
        elSplitRatio.selectedIndex = 0;
        YUD.get('section.tabId').value = tabId;
        YAHOO.page.admin.openEditSectionDialog();
    },
    openEditSubSectionDialog: function() {
        if (!YAHOO.page.admin.editSubSectionDialog) {
            const fnSubmitHandler = function() {
                const elRowTagType = YUD.get('subSection.rowTagType');
                const elColumnTagType = YUD.get('subSection.columnTagType');
                YAHOO.page.sendPostRequest('/page/subSection', YAHOO.page.reloadWindowCallback, {
                    sectionId: YUD.get('subSection.sectionId').value, // owner
                    id: YUD.get('subSection.id').value,  // PK
                    name: YUD.get('subSection.name').value,
                    index: YUD.get('subSection.index').value,
                    rowTagTypeId: elRowTagType.value,
                    columnTagTypeId: elColumnTagType.value
                });
            };
            YAHOO.page.admin.editSubSectionDialog = YAHOO.page.openEditDialog('editSubSectionDialog', {},
                fnSubmitHandler);
        }
        YAHOO.page.admin.editSubSectionDialog.bringToTop();
        YAHOO.page.admin.editSubSectionDialog.show();
        return YAHOO.page.admin.editSubSectionDialog;
    },
    editSubSection: function(subSectionId, subSectionName) {
        console.log('editSubSection: subSectionId=' + subSectionId + ', subSectionName=' + subSectionName);
        YUD.get('subSection.id').value = subSectionId;
        YUD.get('subSection.name').value = subSectionName;
        YUD.get('subSection.index').value = YUD.get('subSection.index.' + subSectionId).value;
        YUD.get('subSection.sectionId').value = YUD.get('subSection.sectionId.' + subSectionId).value;
        const elRowTagType = YUD.get('subSection.rowTagType');
        elRowTagType.value = YUD.get('subSection.rowTagTypeId.' + subSectionId).value;
        const elColumnTagType = YUD.get('subSection.columnTagType');
        elColumnTagType.value = YUD.get('subSection.columnTagTypeId.' + subSectionId).value;
        YAHOO.page.admin.openEditSubSectionDialog();
    },
    addSubSection: function(sectionId) {
        console.log('addSubSection: sectionId=' + sectionId);
        YUD.get('subSection.id').value = '';
        YUD.get('subSection.name').value = '';
        YUD.get('subSection.index').value = '1';
        YUD.get('subSection.sectionId').value = sectionId;
        const elRowTagType = YUD.get('subSection.rowTagType');
        elRowTagType.selectedIndex = 0;
        const elColumnTagType = YUD.get('subSection.columnTagType');
        elColumnTagType.selectedIndex = 0;
        YAHOO.page.admin.openEditSubSectionDialog();
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
                YAHOO.page.sendPostRequest('/page/subSection/map', YAHOO.page.reloadWindowCallback, {
                    id: YUD.get('subSectionKeys.id').value,
                    rowTagTypeId: elRowTagType.value,
                    columnTagTypeId: elColumnTagType.value,
                    keys: keys
                });
            };
            const w = YUD.getViewportWidth();
            const h = YUD.getViewportHeight();
            YAHOO.page.admin.mapSubSectionKeysDialog = YAHOO.page.openEditDialog('mapSubSectionKeysDialog', {
                    fixedcenter: 'contained',
                    width: Math.floor(w / 3 * 2) + 'px',
                    height: Math.floor(h / 3 * 2) + 'px',
                    eventDefs: [
                        {
                            name: 'beforeShow',
                            handler: function(oType, oArgs) {
                                YAHOO.page.admin.initKeysDataTable([]);
                                YAHOO.page.admin.findSubSectionKeys();
                            }
                        }
                    ]
                },
                fnSubmitHandler
            );
            // preview
            YAHOO.page.admin.initPreviewDataTable();
        } else {
            // clear subSectionKeysSearch UI/data/rows
            YAHOO.page.admin.autoCompletes.forEach(ac => {
                const input = ac.getInputEl();
                input.value = '';
            });
            YAHOO.page.admin.tagTypeMap.clear();
            YAHOO.page.admin.clearKeys();
            // preview
            YAHOO.page.admin.updatePreviewDataTable();
        }
        YAHOO.page.admin.mapSubSectionKeysDialog.bringToTop();
        YAHOO.page.admin.mapSubSectionKeysDialog.show();
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
                const ac = args[0];
                //const query = args[1]; // <String> The query string
                //const request = args[2]; // <Object> The request
                const input = ac.getInputEl();
                const tagTypeId = YAHOO.page.getId(input.id);
                YAHOO.page.admin.tagTypeMap.set(tagTypeId, input.value);
            });
            ac.itemSelectEvent.subscribe(function(type, args) {
                const ac = args[0];
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
                {key: 'id', label: 'ID', width: 20},
                {key: 'name', label: 'Name', sortable: true, width: 50},
                {key: 'title', label: 'Title', width: 100},
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
                width: Math.floor(r.width * 0.66) + 'px',
                height: r.height + 'px'
            };
            const dataTable = new YAHOO.widget.ScrollingDataTable('subSectionKeysSearchResult',
                columnDefs, dataSource, dataTableConfig
            );
            dataTable.subscribe('rowClickEvent', dataTable.onEventSelectRow);
            dataTable.subscribe('postRenderEvent', function() {
                if (!YAHOO.page.admin.keysDataTableContextMenu) {
                    const contextMenu = new YAHOO.widget.ContextMenu('subSectionKeysSearchResultContextMenu', {
                        trigger: YAHOO.page.admin.keysDataTable.getTbodyEl(),
                        zIndex: 3,
                        lazyload: false,
                        itemdata: [
                            {text:'Remove Key(s)'}
                        ]
                    });
                    contextMenu.render('subSectionKeysSearchResult');
                    contextMenu.subscribe('click', function(oType, oArgs) {
                        const oItem = oArgs[1];
                        if (oItem) {
                            const trEls = YAHOO.page.admin.keysDataTable.getSelectedTrEls();
                            if (trEls && trEls.length != 0) {
                                switch (oItem.index) {
                                case 0:
                                    YAHOO.page.admin.clearKeysSelected(trEls);
                                }
                            }
                        }
                    });
                    YAHOO.page.admin.keysDataTableContextMenu = contextMenu;
                }
            });
            YAHOO.page.admin.keysDataTable = dataTable;
        }
    },
    initPreviewDataTable: function() {
        const oLiveData = '/subSection/';
        const dataSource = new YAHOO.util.XHRDataSource(oLiveData, {
            connXhrMode: 'queueRequests',
            maxCacheEntries: 0,
            responseType: YAHOO.util.XHRDataSource.TYPE_JSON,
            responseSchema: {
                resultsList: 'records',
                metaFields: {columns:'columns'}
            }
        });
        const elSubSection = YUD.get('preview.subSection');
        const r = YUD.getRegion(elSubSection);
        const requestBuilder = function(oState, oDataTable) {
            const subSectionId = YUD.get('subSectionKeys.id').value;
            const elRowTagType = YUD.get('subSectionKeys.rowTagType');
            const elColumnTagType = YUD.get('subSectionKeys.columnTagType');
            let request = '' + subSectionId + '?1=1';
            if (elRowTagType.value) {
                request += '&rowTagTypeId=' + elRowTagType.value;
            }
            if (elColumnTagType.value) {
                request += '&columnTagTypeId=' + elColumnTagType.value;
            }
            return request;
        };
        const subSectionName = YUD.get('subSectionKeys.name').value;
        const initialRequest = requestBuilder(null, null);
        const dataTableConfig = {
            caption: subSectionName,
            initialLoad: true,
            initialRequest: initialRequest,
            generateRequest: requestBuilder,
            width: (r.width - 2) + 'px'
        };
        const oColumnDefs = [];
        const dataTable = new YAHOO.widget.DataTable(elSubSection,
            oColumnDefs, dataSource, dataTableConfig);
        // 1. access data before it gets added to RecordSet and rendered to the TBODY
        dataTable.doBeforeLoadData = function(oRequest, oResponse, oPayload) {
            const meta = oResponse.meta;
            const columnDefs = meta.columns;
            if (columnDefs) {
                //this.disable();
                columnDefs.forEach((columnDef, index) => {
                    if (!this.getColumn(columnDef.key)) {
                        const column = this.insertColumn(columnDef, index);
                    }
                })
                this.getDataSource().responseSchema.fields = columnDefs.map(columnDef => columnDef.key);
                //this.undisable();
            }
            return true;
        };
        // 2. after each time the DataTable is updated with new data
        //dataTable.handleDataReturnPayload = function(oRequest, oResponse, oPayload) {
        //    return oPayload || {};
        //};
        // 3. fired when the DataTable's DOM is rendered or dirty. 
        //dataTable.subscribe('renderEvent', function() {
        //    
        //});
        YAHOO.page.admin.previewDataTable = dataTable;
        //
        const elRowTagType = YUD.get('subSectionKeys.rowTagType');
        YUE.addListener(elRowTagType, 'change', function(ev) {
            YAHOO.page.admin.updatePreviewDataTable();
        });
        const elColumnTagType = YUD.get('subSectionKeys.columnTagType');
        YUE.addListener(elColumnTagType, 'change', function(ev) {
            YAHOO.page.admin.updatePreviewDataTable();
        });
    },
    updatePreviewDataTable: function() {
        const subSectionId = YUD.get('subSectionKeys.id').value;
        const subSectionName = YUD.get('subSectionKeys.name').value;
        const elRowTagType = YUD.get('subSectionKeys.rowTagType');
        const elColumnTagType = YUD.get('subSectionKeys.columnTagType');
        const dataTable = YAHOO.page.admin.previewDataTable;
        // update caption
        dataTable.set('caption', subSectionName);
        // delete all rows
        const length = dataTable.getRecordSet().getLength();
        dataTable.deleteRows(0, length);
        // remove all columns (except for first one)
        const columns = dataTable.getColumnSet();
        columns.keys.reverse().forEach(column => {
            if (column.getKey() != 'TAG') {
                dataTable.removeColumn(column);
            }
        });
        //
        const state = dataTable.getState();
        const request = dataTable.get('generateRequest')(state, dataTable);
        const callback = {
            cache: false,
            success: dataTable.onDataReturnInsertRows,
            failure: dataTable.onDataReturnInsertRows,
            argument: state,
            scope: dataTable
        };
        dataTable.getDataSource().sendRequest(request, callback);
    },
    clearKeys: function() {
        const length = YAHOO.page.admin.keysDataTable.getRecordSet().getLength();
        YAHOO.page.admin.keysDataTable.deleteRows(0, length);
    },
    clearKeysSelected: function(rows) {
        if (confirm('Remove ' + rows.length + ' Key(s):\n - Are you sure?')) {
            rows.reverse().forEach(r => {
                YAHOO.page.admin.keysDataTable.deleteRow(r);
            });
        }
    },
    findSubSectionKeys: function() {
        const subSectionId = YUD.get('subSectionKeys.id').value;
        YAHOO.page.sendGetRequest('/page/key/' + subSectionId,
            YAHOO.page.admin.findKeysCallback);
    },
    findKeys: function() {
        YAHOO.page.sendPostRequest('/page/key',
            YAHOO.page.admin.findKeysCallback,
            YAHOO.page.admin.tagTypeMap);
    },
    findKeysCallback: {
        cache: false,
        success: function(oResponse) {
            const data = YAHOO.page.parseJsonData(oResponse.responseText, true);
            const dataTable = YAHOO.page.admin.keysDataTable;
            dataTable.addRows(data.records);
            dataTable.sortColumn(dataTable.getColumn('id'), YAHOO.widget.DataTable.CLASS_ASC);
        },
        failure: YAHOO.page.failureHandler
    },
    mapSubSectionKeys: function(subSectionId, subSectionName) {
        console.log('mapSubSectionKeys: subSectionId=' + subSectionId + ', subSectionName=' + subSectionName);
        YUD.get('subSectionKeys.id').value = subSectionId;
        YUD.get('subSectionKeys.name').value = subSectionName;
        const elRowTagType = YUD.get('subSectionKeys.rowTagType');
        elRowTagType.value = YUD.get('subSection.rowTagTypeId.' + subSectionId).value;
        const elColumnTagType = YUD.get('subSectionKeys.columnTagType');
        elColumnTagType.value = YUD.get('subSection.columnTagTypeId.' + subSectionId).value;
        YAHOO.page.admin.openMapSubSectionKeysDialog();
    }
};

(function() {
    YAHOO.page.pageReadyEvent.subscribe(YAHOO.page.admin.fnSubscriberPageReady);
})();
