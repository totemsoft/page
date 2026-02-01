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
            height: h / 3 * 2 + 'px',
            width: (r.width - 2) + 'px'
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
                        const column = this.insertColumn(columnDef, index);
                        if (columnDef.editor) {
                            editable = true;
                            column.editor.subscribe('saveEvent', saveEvent);
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
            argument: state,
            scope: dataTable
        };
        dataTable.getDataSource().sendRequest(request, callback);
    },
    editTagTypes: function() {
        console.log('editTagTypes:');
        const entityName = 'tagType';
        if (!YAHOO.page.setup.tagTypesDialog) {
            const w = YUD.getViewportWidth();
            YAHOO.page.setup.tagTypesDialog = YAHOO.page.openEditDialog(entityName + 'Dialog', {
                fixedcenter: 'contained',
                width: (w / 3) + 'px',
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
                const row = this.getRecord()._oData;
                const value = oArgs.newData;
                const tagTypeDto = {
                    id: row.id, // PK
                    name: key === 'name' ? value : row.name,
                    title: key === 'title' ? value : row.title
                };
                if (tagTypeDto.name && tagTypeDto.title) {
                    YAHOO.page.sendPostRequest('/setup/' + entityName, YAHOO.page.emptyCallback, tagTypeDto);
                }
            };
            YAHOO.page.setup.tagTypesDataTable = YAHOO.page.setup.initDataTable(entityName, requestBuilder, saveEvent);
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
                width: (w / 2) + 'px',
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
                const row = this.getRecord()._oData;
                const value = oArgs.newData;
                const tagTypeId = parseInt(YUD.get(entityName + '.tagType').value);
                const tagDto = {
                    id: row.id, // PK
                    name: key === 'name' ? value : row.name,
                    title: key === 'title' ? value : row.title,
                    tagTypeId: tagTypeId
                };
                if (tagDto.name && tagDto.title) {
                    YAHOO.page.sendPostRequest('/setup/' + entityName, YAHOO.page.emptyCallback, tagDto);
                }
            };
            YAHOO.page.setup.tagsDataTable = YAHOO.page.setup.initDataTable(entityName, requestBuilder, saveEvent);
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
            YAHOO.page.setup.keysDialog = YAHOO.page.openEditDialog(entityName + 'Dialog', {
                fixedcenter: 'contained',
                width: (w / 3) + 'px',
                buttons: [
                    {text: 'Add', title: 'Add new Key', handler: function() {
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
                const row = this.getRecord()._oData;
                const value = oArgs.newData;
                const keyDto = {
                    id: row.id, // PK
                    name: key === 'name' ? value : row.name,
                    title: key === 'title' ? value : row.title
                };
                if (keyDto.name && keyDto.title) {
                    YAHOO.page.sendPostRequest('/setup/' + entityName, YAHOO.page.emptyCallback, keyDto);
                }
            };
            YAHOO.page.setup.keysDataTable = YAHOO.page.setup.initDataTable(entityName, requestBuilder, saveEvent);
        } else {
            YAHOO.page.setup.updateDataTable(YAHOO.page.setup.keysDataTable);
        }
        YAHOO.page.setup.keysDialog.bringToTop();
        YAHOO.page.setup.keysDialog.show();
    }
};
