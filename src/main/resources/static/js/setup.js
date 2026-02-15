YAHOO.namespace('page.setup');

YAHOO.page.setup = {
    initDataTable: function(entityName, requestBuilder, saveEvent) {
        const oLiveData = '/setup/';
        const dataSource = new YAHOO.util.XHRDataSource(oLiveData, {
            connXhrMode: 'queueRequests',
            maxCacheEntries: 0,
            responseType: YAHOO.util.XHRDataSource.TYPE_JSON,
            responseSchema: {
                resultsList: 'records',
                metaFields: {columns:'columns'}
            }
        });
        const elContainer = YUD.get(entityName);
        const r = YUD.getRegion(elContainer);
        const h = YUD.getViewportHeight();
        const initialRequest = requestBuilder(null, null);
        const dataTableConfig = {
            //caption: caption,
            initialLoad: true,
            initialRequest: initialRequest,
            generateRequest: requestBuilder,
            width: (r.width - 2) + 'px',
            height: h / 3 * 2 + 'px'
        };
        const oColumnDefs = [];
        const dataTable = new YAHOO.widget.ScrollingDataTable(elContainer,
            oColumnDefs, dataSource, dataTableConfig);
        // 1. access data before it gets added to RecordSet and rendered to the TBODY
        dataTable.doBeforeLoadData = function(oRequest, oResponse, oPayload) {
            const meta = oResponse.meta;
            const columnDefs = meta.columns;
            if (columnDefs) {
                //this.disable();
                let editable = false;
                columnDefs.forEach((columnDef, index) => {
                    if (!this.getColumn(columnDef.key)) {
                        const oConfigs = {
                            disableBtns: columnDef.disableBtns
                        };
                        if (columnDef.editor === 'dropdown' && columnDef.dropdownOptions !== undefined) {
                            oConfigs.dropdownOptions = columnDef.dropdownOptions;
                            oConfigs.multiple = columnDef.multiple;
                            columnDef.editor = new YAHOO.widget.DropdownCellEditor(oConfigs);
                        } else if (columnDef.editor === 'textbox') {
                            columnDef.editor = new YAHOO.widget.TextboxCellEditor(oConfigs);
                        }
                        const column = this.insertColumn(columnDef, index);
                        if (column.editor) {
                            editable = true;
                            if (saveEvent !== undefined) {
                                column.editor.subscribe('saveEvent', saveEvent);
                            }
                        }
                    }
                })
                this.getDataSource().responseSchema.fields = columnDefs.map(columnDef => columnDef.key);
                if (editable) {
                    this.subscribe('cellMouseoverEvent', this.onEventHighlightCell);
                    this.subscribe('cellMouseoutEvent', this.onEventUnhighlightCell);
                    this.subscribe('cellClickEvent', this.onEventShowCellEditor);
                }
                //this.undisable();
            }
            return true;
        };
        return dataTable;
    },
    updateDataTable: function(dataTable) {
        // delete all rows
        const length = dataTable.getRecordSet().getLength();
        dataTable.deleteRows(0, length);
        // remove all columns (except for first one)
        const columns = dataTable.getColumnSet();
        columns.keys.reverse().forEach(column => {
            dataTable.removeColumn(column);
        });
        //
        const state = dataTable.getState();
        const request = dataTable.get('generateRequest')(state, dataTable);
        const callback = {
            cache: false,
            success: dataTable.onDataReturnInsertRows,
            failure: dataTable.onDataReturnInsertRows,
            scope: dataTable,
            argument: state
        };
        dataTable.getDataSource().sendRequest(request, callback);
    },
    saveCallback: {
        cache: false,
        success: function(oResponse) {
            const id = parseInt(oResponse.responseText);
            const self = YAHOO.page.setup.saveCallback;
            const data = self.argument;
            if (!data.id) {
                data.id = id;
                this.updateRow(this.getState().totalRecords - 1, data);
            }
        },
        failure: function(oResponse) {
            YAHOO.page.failureHandler(oResponse);
        },
        scope: null,   // dataTable
        argument: null // data
    },
    editTagTypes: function() {
        console.log('editTagTypes:');
        const entityName = 'tagType';
        if (!YAHOO.page.setup.tagTypesDialog) {
            const w = YUD.getViewportWidth();
            YAHOO.page.setup.tagTypesDialog = YAHOO.page.openEditDialog(entityName + 'Dialog', {
                fixedcenter: 'contained',
                width: Math.floor(w / 3) + 'px',
                buttons: [
                    {text: 'Add', title: 'Add new Tag Type', handler: function() {
                        YAHOO.page.setup.tagTypesDataTable.addRow({id: null, name: '', title: ''});
                    }},
                    {text: 'Close', isDefault: true, handler: function() {
                        this.cancel();
                    }}
                ]
            });
            const requestBuilder = function(oState, oDataTable) {
                let request = '' + entityName;
                return request;
            };
            const saveEvent = function(oArgs) {
                //const el = oArgs.target; // radio/checkbox, el.checked
                const key = this.getColumn().field;
                const data = this.getRecord().getData();
                const value = oArgs.newData;
                const tagTypeDto = {
                    id: data.id, // PK
                    name: key === 'name' ? value : data.name,
                    title: key === 'title' ? value : data.title
                };
                if (tagTypeDto.name && tagTypeDto.title) {
                    const callback = YAHOO.page.setup.saveCallback;
                    callback.scope = YAHOO.page.setup.tagTypesDataTable;
                    callback.argument = data;
                    YAHOO.page.sendPostRequest('/setup/' + entityName, callback, tagTypeDto);
                }
            };
            const dataTable = YAHOO.page.setup.initDataTable(entityName, requestBuilder, saveEvent);
            dataTable.subscribe('rowClickEvent', dataTable.onEventSelectRow);
            dataTable.subscribe('cellClickEvent', dataTable.onEventSelectCell);
            YAHOO.page.setup.tagTypesDataTable = dataTable;
        } else {
            YAHOO.page.setup.updateDataTable(YAHOO.page.setup.tagTypesDataTable);
        }
        YAHOO.page.setup.tagTypesDialog.bringToTop();
        YAHOO.page.setup.tagTypesDialog.show();
    },
    editTags: function() {
        console.log('editTags:');
        const entityName = 'tag';
        if (!YAHOO.page.setup.tagsDialog) {
            const w = YUD.getViewportWidth();
            YAHOO.page.setup.tagsDialog = YAHOO.page.openEditDialog(entityName + 'Dialog', {
                fixedcenter: 'contained',
                width: Math.floor(w / 2) + 'px',
                buttons: [
                    {text: 'Add', title: 'Add new Tag', handler: function() {
                        YAHOO.page.setup.tagsDataTable.addRow({id: null, name: '', title: ''});
                    }},
                    {text: 'Close', isDefault: true, handler: function() {
                        this.cancel();
                    }}
                ]
            });
            const requestBuilder = function(oState, oDataTable) {
                let request = '' + entityName + '?1=1';
                const elTagType = YUD.get(entityName + '.tagType');
                if (elTagType.value) {
                    request += '&tagTypeId=' + elTagType.value;
                }
                return request;
            };
            const saveEvent = function(oArgs) {
                //const el = oArgs.target; // radio/checkbox, el.checked
                const key = this.getColumn().field;
                const data = this.getRecord().getData();
                const value = oArgs.newData;
                const tagTypeId = parseInt(YUD.get(entityName + '.tagType').value);
                const tagDto = {
                    id: data.id, // PK
                    name: key === 'name' ? value : data.name,
                    title: key === 'title' ? value : data.title,
                    tagTypeId: tagTypeId
                };
                if (tagDto.name && tagDto.title) {
                    const callback = YAHOO.page.setup.saveCallback;
                    callback.scope = YAHOO.page.setup.tagsDataTable;
                    callback.argument = data;
                    YAHOO.page.sendPostRequest('/setup/' + entityName, callback, tagDto);
                }
            };
            const dataTable = YAHOO.page.setup.initDataTable(entityName, requestBuilder, saveEvent);
            dataTable.subscribe('rowClickEvent', dataTable.onEventSelectRow);
            dataTable.subscribe('cellClickEvent', dataTable.onEventSelectCell);
            YAHOO.page.setup.tagsDataTable = dataTable;
            // enable/disable add button
            const configAddButton = function(elTagType) {
                const buttons = YAHOO.page.setup.tagsDialog.getButtons();
                buttons[0].set('disabled', !elTagType.value);
            };
            const elTagType = YUD.get(entityName + '.tagType');
            configAddButton(elTagType);
            YUE.addListener(elTagType, 'change', function(ev) {
                YAHOO.page.setup.updateDataTable(YAHOO.page.setup.tagsDataTable);
                configAddButton(this);
            });
        } else {
            YAHOO.page.setup.updateDataTable(YAHOO.page.setup.tagsDataTable);
        }
        YAHOO.page.setup.tagsDialog.bringToTop();
        YAHOO.page.setup.tagsDialog.show();
    },
    editKeys: function() {
        console.log('editKeys:');
        const entityName = 'key';
        if (!YAHOO.page.setup.keysDialog) {
            const w = YUD.getViewportWidth();
            //const h = YUD.getViewportHeight();
            YAHOO.page.setup.keysDialog = YAHOO.page.openEditDialog(entityName + 'Dialog', {
                fixedcenter: 'contained',
                width: Math.floor(w / 2) + 'px',
                //height: Math.floor(h / 2) + 'px',
                buttons: [
                    {text: 'Add', handler: function() {
                        YAHOO.page.setup.keysDataTable.addRow({id: null, name: '', title: ''});
                    }},
                    {text: 'Close', isDefault: true, handler: function() {
                        this.cancel();
                    }}
                ]
            });
            const requestBuilder = function(oState, oDataTable) {
                let request = '' + entityName;
                return request;
            };
            const saveEvent = function(oArgs) {
                //const el = oArgs.target; // radio/checkbox, el.checked
                const key = this.getColumn().field;
                const data = this.getRecord().getData();
                const value = oArgs.newData;
                const keyDto = {
                    id: data.id, // PK
                    name: key === 'name' ? value : data.name,
                    title: key === 'title' ? value : data.title
                };
                if (keyDto.name && keyDto.title) {
                    const callback = YAHOO.page.setup.saveCallback;
                    callback.scope = YAHOO.page.setup.keysDataTable;
                    callback.argument = data;
                    YAHOO.page.sendPostRequest('/setup/' + entityName, callback, keyDto);
                }
            };
            const dataTable = YAHOO.page.setup.initDataTable(entityName, requestBuilder, saveEvent);
            dataTable.subscribe('rowClickEvent', dataTable.onEventSelectRow);
            dataTable.subscribe('cellClickEvent', dataTable.onEventSelectCell);
            // 3. fired when the DataTable's DOM is rendered or dirty (or postRenderEvent)
            dataTable.subscribe('renderEvent', function() {
                const triggerNodes = [];
                const actionColumn = this.getColumn('action');
                const rows = this.getRecordSet().getRecords();
                rows.forEach(row => {
                    const data = row.getData();
                    const tdLinerEl = this.getTdLinerEl({record:row, column:actionColumn});
                    triggerNodes.push(tdLinerEl);
                }, this);
                //this.subscribe('cellClickEvent', function(oArgs) {});
                YUE.addListener(triggerNodes, 'click', function(oEvent) {
                    const tdLinerEl = YUE.getTarget(oEvent); // tdLinerEl
                    const tdEl = tdLinerEl.parentNode; // td
                    if (YUD.hasClass(tdEl, 'collapsed')) {
                        setTimeout(function() {
                            const dataTable = YAHOO.page.setup.keysDataTable;
                            const rowId = dataTable.getLastSelectedRecord();
                            const row = dataTable.getRecord(rowId);
                            const data = row.getData();
                            const keyId = data.id;
                            if (keyId) {
                                YAHOO.page.removeClass(tdEl, 'collapsed');
                                YAHOO.page.addClass(tdEl, 'expanded');
                                YAHOO.page.setup.editKeyTags(keyId);
                            }
                        }, 0);
                    } else {
                        YAHOO.page.removeClass(tdEl, 'expanded');
                        YAHOO.page.addClass(tdEl, 'collapsed');
                    }
                });
            });
            YAHOO.page.setup.keysDataTable = dataTable;
        } else {
            YAHOO.page.setup.updateDataTable(YAHOO.page.setup.keysDataTable);
        }
        YAHOO.page.setup.keysDialog.bringToTop();
        YAHOO.page.setup.keysDialog.show();
    },
    editKeyTags: function(keyId) {
        console.log('editKeyTags: ' + keyId);
        const entityName = 'tagByKey';
        if (!YAHOO.page.setup.keyTagsDialog) {
            const submitCallback = {
                cache: false,
                success: function(oResponse) {
                    YAHOO.page.setup.updateDataTable(YAHOO.page.setup.keysDataTable);
                    YAHOO.page.setup.keyTagsDialog.cancel();
                },
                failure: function(oResponse) {
                    YAHOO.page.failureHandler(oResponse);
                }
            };
            const fnSubmitHandler = function() {
                const tagIds = [];
                const rows = YAHOO.page.setup.keyTagsDataTable.getRecordSet().getRecords();
                rows.forEach(row => {
                    const data = row.getData();
                    const tagId = data.id;
                    if (tagId) {
                        tagIds.push(tagId);
                    }
                }, YAHOO.page.setup.keyTagsDataTable);
                YAHOO.page.sendPostRequest('/setup/key/' + keyId + '/tags', submitCallback, tagIds);
            };
            const w = YUD.getViewportWidth();
            YAHOO.page.setup.keyTagsDialog = YAHOO.page.openEditDialog(entityName + 'Dialog', {
                fixedcenter: 'contained',
                width: Math.floor(w / 3) + 'px',
                //modal: true, // prevent typing in AutoComplete
                buttons: [
                    {text: 'Add', handler: function() {
                        YAHOO.page.setup.keyTagsDataTable.addRow({id: null, name: '', tagTypeId: null, title: ''});
                    }},
                    {text: 'Save', handler: fnSubmitHandler},
                    {text: 'Close', isDefault: true, handler: function() {
                        this.cancel();
                    }}
                ],
                eventDefs: [
                    {name: 'show', handler: function(oType, oArgs) {
                        const tdEl = YAHOO.page.setup.keysDataTable.getSelectedTdEls()[0];
                        YAHOO.page.removeClass(tdEl, 'collapsed');
                        YAHOO.page.addClass(tdEl, 'expanded');
                    }},
                    {name: 'hide', handler: function(oType, oArgs) {
                        const tdEl = YAHOO.page.setup.keysDataTable.getSelectedTdEls()[0];
                        YAHOO.page.removeClass(tdEl, 'expanded');
                        YAHOO.page.addClass(tdEl, 'collapsed');
                    }}
                ]
            });
            //
            const requestBuilder = function(oState, oDataTable) {
                let request = '' + entityName;
                request += '/' + (oDataTable ? oDataTable.keyId : keyId);
                return request;
            };
            const dataTable = YAHOO.page.setup.initDataTable(entityName, requestBuilder);
            dataTable.subscribe('dropdownChangeEvent', function(oArgs) {
                const elDropdown = oArgs.target;
                const oRecord = this.getRecord(elDropdown);
                oRecord.setData('tagTypeId', elDropdown.value);
            });
            // 3. fired when the DataTable's DOM is rendered or dirty (renderEvent or postRenderEvent)
            dataTable.subscribe('renderEvent', function() {
                // edit tag (name column) for selected tagType (tagTypeId column)
                const editor = this.getColumn('name').editor;
                editor.doAfterRender = function() {
                    if (!this.autoComplete) {
                        YAHOO.page.setup.initTagByKeyAutoComplete(this);
                    }
                };
            });
            YAHOO.page.setup.keyTagsDataTable = dataTable;
        } else {
            YAHOO.page.setup.tagByKeyAutoComplete = null;
            YAHOO.page.setup.keyTagsDataTable.keyId = keyId;
            YAHOO.page.setup.updateDataTable(YAHOO.page.setup.keyTagsDataTable);
        }
        YAHOO.page.setup.keyTagsDialog.bringToTop();
        YAHOO.page.setup.keyTagsDialog.show();
    },
    initTagByKeyAutoComplete: function(editor) {
        console.log('initTagByKeyAutoComplete:');
        const input = editor.textbox;
        const container = YUD.get('container.tagByKey');
        const data = editor.getRecord().getData();
        const keyId = data.id;
        const tagTypeId = data.tagTypeId;
        const value = input.value;
        //
        const requestBuilder = function(sQuery) {
            const data = editor.getRecord().getData();
            return data.tagTypeId + '?query=' + sQuery;
        };
        // /page/tag/{tagTypeId}?query=sQuery
        const ds = new YAHOO.util.XHRDataSource('/page/tag/');
        ds.responseType = YAHOO.util.XHRDataSource.TYPE_JSON;
        ds.responseSchema = {
            resultsList: 'records',
            fields: ['name', 'id']
        };
        const ac = new YAHOO.widget.AutoComplete(input, container, ds, {
            prehighlightClassName: 'yui-ac-prehighlight',
            generateRequest: requestBuilder,
            autoHighlight: false,
            allowBrowserAutocomplete: false,
            forceSelection: true,
            typeAheadDelay: 0.5,
            queryDelay: 0.4,
            minQueryLength: 0,
            maxResultsDisplayed: 100
        });
        // to fix input cleared
        ac.textboxFocusEvent.subscribe(function(type, args) {
            const ac = args[0];
            const row = editor.getRecord();
            const data = row.getData();
            ac.sendQuery('' + data.name);
        });
        // update dataTable with new tag data
        ac.itemSelectEvent.subscribe(function(type, args) {
            const ac = args[0];
            //const elLI = args[1]; // The selected <li> element item
            const oData = args[2]; // The data returned for the item, either as an object, or mapped from the schema into an array
            // update with the selected item's tagId
            const row = editor.getRecord();
            const tagDto = row.getData();
            tagDto.id = oData[1];
            tagDto.name = oData[0];
            const dataTable = editor.getDataTable();
            dataTable.updateRow(row, tagDto); // clone {...tagDto}
        });
        editor.autoComplete = ac;
    },
    editCurrency: function() {
        console.log('editCurrency:');
        const entityName = 'currency';
        if (!YAHOO.page.setup.currencyDialog) {
            const w = YUD.getViewportWidth();
            //const h = YUD.getViewportHeight();
            YAHOO.page.setup.currencyDialog = YAHOO.page.openEditDialog(entityName + 'Dialog', {
                fixedcenter: 'contained',
                width: Math.floor(w / 3) + 'px',
                //height: Math.floor(h / 2) + 'px',
                buttons: [
                    {text: 'Close', isDefault: true, handler: function() {
                        this.cancel();
                    }}
                ]
            });
            const requestBuilder = function(oState, oDataTable) {
                let request = '' + entityName;
                return request;
            };
            const dataTable = YAHOO.page.setup.initDataTable(entityName, requestBuilder);
            dataTable.subscribe('rowClickEvent', dataTable.onEventSelectRow);
            //dataTable.subscribe('cellClickEvent', dataTable.onEventSelectCell);
            dataTable.subscribe('checkboxClickEvent', function(oArgs) {
                const elCheckbox = oArgs.target;
                const base = elCheckbox.checked ? true : null;
                const row = this.getRecord(elCheckbox);
                row.setData('base', base);
                const data = row.getData();
                const currencyDto = {
                    code: data.code, // PK
                    base: data.base
                };
                const callback = YAHOO.page.setup.saveCallback;
                callback.scope = YAHOO.page.setup.currencyDataTable;
                callback.argument = data;
                YAHOO.page.sendPostRequest('/setup/' + entityName, callback, currencyDto);
            });
            YAHOO.page.setup.currencyDataTable = dataTable;
        } else {
            YAHOO.page.setup.updateDataTable(YAHOO.page.setup.currencyDataTable);
        }
        YAHOO.page.setup.currencyDialog.bringToTop();
        YAHOO.page.setup.currencyDialog.show();
    }
};
