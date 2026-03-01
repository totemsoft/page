YAHOO.namespace('page.user');

YAHOO.page.user = {
    liveData: '/users/',
    editUsers: function() {
        console.log('editUsers:');
        const entityName = 'user';
        if (!YAHOO.page.user.usersDialog) {
            const w = YUD.getViewportWidth();
            YAHOO.page.user.usersDialog = YAHOO.page.openEditDialog(entityName + 'Dialog', {
                fixedcenter: 'contained',
                width: Math.floor(w / 3) + 'px',
                buttons: [
                    {text: 'Close', isDefault: true, handler: function() {
                        this.cancel();
                    }}
                ]
            });
            const requestBuilder = function(oState, oDataTable) {
                let request = entityName;
                return request;
            };
            const saveEvent = function(oArgs) {
                //const el = oArgs.target; // radio/checkbox, el.checked
                const key = this.getColumn().field;
                const data = this.getRecord().getData();
                const userDto = {
                    email: data.email, // PK
                    authorities: oArgs.newData
                };
                const callback = YAHOO.page.emptyCallback;
                //const callback = YAHOO.page.user.saveCallback;
                callback.scope = YAHOO.page.user.usersDataTable;
                callback.argument = data;
                YAHOO.page.sendPostRequest(YAHOO.page.user.liveData + entityName, callback, userDto);
            };
            const dataTableConfig = {
                liveData: YAHOO.page.user.liveData,
                requestBuilder: requestBuilder,
                saveEvent: saveEvent
            }
            const dataTable = YAHOO.page.initDataTable(entityName, dataTableConfig);
            dataTable.subscribe('rowClickEvent', dataTable.onEventSelectRow);
            //dataTable.subscribe('cellClickEvent', dataTable.onEventSelectCell);
            YAHOO.page.user.usersDataTable = dataTable;
        } else {
            YAHOO.page.updateDataTable(YAHOO.page.user.usersDataTable);
        }
        YAHOO.page.user.usersDialog.bringToTop();
        YAHOO.page.user.usersDialog.show();
    }
};
