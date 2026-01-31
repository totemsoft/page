YAHOO.namespace('page.setup');

YAHOO.page.setup = {
    initDataTable: function(entitiesName, requestBuilder) {
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
        const elContainer = YUD.get(entitiesName);
        const r = YUD.getRegion(elContainer);
        const initialRequest = requestBuilder(null, null);
        const dataTableConfig = {
            //caption: caption,
            initialLoad: true,
            initialRequest: initialRequest,
            generateRequest: requestBuilder,
            width: (r.width - 2) + 'px'
        };
        const oColumnDefs = [];
        const dataTable = new YAHOO.widget.DataTable(elContainer,
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
        const entitiesName = 'tagTypes';
        if (!YAHOO.page.setup.tagTypesDialog) {
            const w = YUD.getViewportWidth();
            YAHOO.page.setup.tagTypesDialog = YAHOO.page.openEditDialog(entitiesName + 'Dialog', {
                fixedcenter: 'contained',
                width: (w / 3) + 'px',
                buttons: [
                    {text: 'OK', isDefault: true, handler: function() {
                        this.hide();
                    }}
                ]
            });
            const requestBuilder = function(oState, oDataTable) {
                let request = '' + entitiesName;
                return request;
            };
            YAHOO.page.setup.tagTypesDataTable = YAHOO.page.setup.initDataTable(entitiesName, requestBuilder);
        } else {
            YAHOO.page.setup.updateDataTable(YAHOO.page.setup.tagTypesDataTable);
        }
        YAHOO.page.setup.tagTypesDialog.bringToTop();
        YAHOO.page.setup.tagTypesDialog.show();
    },
    editTags: function() {
        console.log('editTags:');
        const entitiesName = 'tags';
        if (!YAHOO.page.setup.tagsDialog) {
            const w = YUD.getViewportWidth();
            YAHOO.page.setup.tagsDialog = YAHOO.page.openEditDialog(entitiesName + 'Dialog', {
                fixedcenter: 'contained',
                width: (w / 2) + 'px',
                buttons: [
                    {text: 'OK', isDefault: true, handler: function() {
                        this.hide();
                    }}
                ]
            });
            const requestBuilder = function(oState, oDataTable) {
                let request = '' + entitiesName + '?1=1';
                const elTagType = YUD.get(entitiesName + '.tagType');
                if (elTagType.value) {
                    request += '&tagTypeId=' + elTagType.value;
                }
                return request;
            };
            YAHOO.page.setup.tagsDataTable = YAHOO.page.setup.initDataTable(entitiesName, requestBuilder);
            const elTagType = YUD.get(entitiesName + '.tagType');
            YUE.addListener(elTagType, 'change', function(ev) {
                YAHOO.page.setup.updateDataTable(YAHOO.page.setup.tagsDataTable);
            });
        } else {
            YAHOO.page.setup.updateDataTable(YAHOO.page.setup.tagsDataTable);
        }
        YAHOO.page.setup.tagsDialog.bringToTop();
        YAHOO.page.setup.tagsDialog.show();
    },
};
