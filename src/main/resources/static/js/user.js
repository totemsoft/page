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
            const dataTableConfig = {
                liveData: YAHOO.page.user.liveData,
                requestBuilder: requestBuilder
            }
            const dataTable = YAHOO.page.initDataTable(entityName, dataTableConfig);
            dataTable.subscribe('rowClickEvent', dataTable.onEventSelectRow);
            //dataTable.subscribe('cellClickEvent', dataTable.onEventSelectCell);
            dataTable.subscribe('checkboxClickEvent', function(oArgs) {
                const elCheckbox = oArgs.target;
                const row = this.getRecord(elCheckbox);
                if (!elCheckbox.checked) {
                    row.setData('role', null);
                }
                const data = row.getData();
                const userDto = {
                    email: data.email, // PK
                    role: data.role // authorities[i] add/remove
                };
                const callback = YAHOO.page.emptyCallback;
                callback.scope = YAHOO.page.user.usersDataTable;
                callback.argument = data;
                YAHOO.page.sendPostRequest(YAHOO.page.user.liveData + entityName, callback, userDto);
            });
            YAHOO.page.user.usersDataTable = dataTable;
        } else {
            YAHOO.page.updateDataTable(YAHOO.page.user.usersDataTable);
        }
        YAHOO.page.user.usersDialog.bringToTop();
        YAHOO.page.user.usersDialog.show();
    }
};
