YAHOO.namespace('page.setup');

YAHOO.page.setup = {
    openTagTypesDialog: function() {
        if (!YAHOO.page.setup.tagTypesDialog) {
            const w = YUD.getViewportWidth();
            YAHOO.page.setup.tagTypesDialog = YAHOO.page.openEditDialog('tagTypesDialog', {
                fixedcenter: 'contained',
                width: (w / 3) + 'px',
                buttons: [
                    {text: 'OK', isDefault: true, handler: function() {
                        this.hide();
                    }}
                ]
            });
        }
        YAHOO.page.setup.tagTypesDialog.bringToTop();
        YAHOO.page.setup.tagTypesDialog.show();
        return YAHOO.page.setup.tagTypesDialog;
    },
    editTagTypes: function() {
        console.log('editTagTypes:');
        YAHOO.page.setup.openTagTypesDialog();
    }
};
