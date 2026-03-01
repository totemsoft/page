const YL  = YAHOO.lang,
      YUC = YAHOO.util.Connect,
      YUD = YAHOO.util.Dom,
      YUE = YAHOO.util.Event,
      YUG = YAHOO.util.Get,
      YUS = YAHOO.util.Selector;
const tabViewActiveTabCookie = 'YAHOO.page.tabView.activeIndex';

YAHOO.namespace('page');

YAHOO.page = {
    locale: 'en-US',
    pageReadyEvent: new YAHOO.util.CustomEvent('pageReady'),
    pageMap: new Map(), // <Tab, [Section]>
    getId: function(s) {
        return parseInt(s.substring(s.lastIndexOf('.') + 1)); // ##.##.id
    },
    getPageId: function() {
        const el = YUD.get('pageId');
        return el && el.value ? parseInt(el.value) : '';
    },
    addClass: function(el, className) {
        if (className && !YUD.hasClass(el, className)) {
            YUD.addClass(el, className);
        }
    },
    removeClass: function(el, className) {
        if (className && YUD.hasClass(el, className)) {
            YUD.removeClass(el, className);
        }
    },
    toggleClass: function(el, className) {
        if (YUD.hasClass(el, className)) {
            YUD.removeClass(el, className);
        } else {
            YUD.addClass(el, className);
        }
    },
    updateNumberFormat: function() {
        YAHOO.page.numberFormat = new Intl.NumberFormat(YAHOO.page.locale, {
            minimumFractionDigits: 0,
            maximumFractionDigits: 4
        });
    },
    updateCurrencyFormat: function(currency) {
        YAHOO.page.currencyFormat = new Intl.NumberFormat(YAHOO.page.locale, {
            style: 'currency',
            currency: currency,
            currencyDisplay: 'symbol', // default
            minimumFractionDigits: 2,
            maximumFractionDigits: 2
        });
    },
    formatTag: function(el, oRecord, oColumn, oData, oDataTable) {
        //const oDT = oDataTable || this;
        const value = YL.isValue(oData) ? oData : '';
        el.innerHTML = YL.escapeHTML(value.toString());
        const className = oColumn.className || (oData ? oData.className : null);
        YAHOO.page.addClass(el.parentNode, className);
    },
    formatCurrency: function(el, oRecord, oColumn, oData, oDataTable) {
        //const oDT = oDataTable || this;
        //const currencyOptions = oColumn.currencyOptions || oDT.get('currencyOptions');
        //el.innerHTML = YAHOO.util.Number.format(oData, currencyOptions);
        el.innerHTML = YL.isValue(oData) ? YAHOO.page.currencyFormat.format(oData) : '';
        const className = oColumn.className || (oData ? oData.className : null);
        YAHOO.page.addClass(el.parentNode, className);
    },
    formatNumber: function(el, oRecord, oColumn, oData, oDataTable) {
        //const oDT = oDataTable || this;
        //el.innerHTML = YAHOO.util.Number.format(oData, oColumn.numberOptions || oDT.get("numberOptions"));
        el.innerHTML = YL.isValue(oData) ? YAHOO.page.numberFormat.format(oData) : '';
        const className = oColumn.className || (oData ? oData.className : null);
        YAHOO.page.addClass(el.parentNode, className);
    },
    formatText: function(el, oRecord, oColumn, oData, oDataTable) {
        const value = (YL.isValue(oData)) ? oData : '';
        el.innerHTML = YL.escapeHTML(value.toString());
        const className = oColumn.className || (oData ? oData.className : null);
        YAHOO.page.addClass(el.parentNode, className);
    },
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
    failureHandler: function(oResponse) {
        const status = oResponse && oResponse.status ? oResponse.status : '';
        const message = oResponse && oResponse.responseText ? oResponse.responseText : oResponse.statusText;
        console.log('Failure: ' + status + '<br/>' + message);
    },
    isElementObscured: function(element) {
        const r = YUD.getRegion(element);
        // Check a point near the center of the element
        const x = r.left + r.width / 2;
        const y = r.top + r.height / 2;
        // Get the top-most element at that point
        const topElement = document.elementFromPoint(x, y);
        // Check if the top element is the original element or a descendant of it
        return !element.contains(topElement);
    },
    openEditDialog: function(el, oConfig, fnSubmitHandler) {
        const dialog = new YAHOO.widget.SimpleDialog(el, {
            zIndex: oConfig && oConfig.zIndex !== undefined ? oConfig.zIndex : undefined,
            close: true,
            draggable: true,
            fixedcenter: oConfig && oConfig.fixedcenter !== undefined ? oConfig.fixedcenter : true,
            visible: false,
            modal: oConfig && oConfig.modal !== undefined ? oConfig.modal : false,
            icon: null,
            width: oConfig && oConfig.width !== undefined ? oConfig.width : null,
            height: oConfig && oConfig.height !== undefined ? oConfig.height : null,
            autofillheight: 'body',
            constraintoviewport: oConfig && oConfig.constraintoviewport !== undefined ? oConfig.constraintoviewport : true,
            context: ['showbtn', 'tl', 'bl'],
            buttons: oConfig && oConfig.buttons !== undefined ? oConfig.buttons : [
                {text: 'Save', handler: {
                    fn: fnSubmitHandler,
                    obj: el,
                    scope: this
                }},
                {text: 'Cancel', isDefault: true, handler: function() {
                    this.cancel();
                }}
            ]
        });
        const escapeKeyListener = function() {
            if (!YAHOO.page.isElementObscured(this.body)) {
                this.cancel();
            }
        };
        dialog.cfg.queueProperty('keylisteners', new YAHOO.util.KeyListener(
            document,
            { keys: 27 },
            { fn: escapeKeyListener, scope: dialog, correctScope: true }
        ));
        if (oConfig && oConfig.eventDefs !== undefined) {
            oConfig.eventDefs.forEach(oEventDef => {
                dialog.subscribe(oEventDef.name, oEventDef.handler);
            });
        }
        dialog.render(document.body);
        return dialog;
    },
    emptyCallback: {
        cache: false,
        success: function(oResponse) {
            // do nothing
        },
        failure: function(oResponse) {
            YAHOO.page.failureHandler(oResponse);
        }
    },
    reloadWindowCallback: {
        cache: false,
        success: function(oResponse) {
            const data = YAHOO.page.parseJsonData(oResponse.responseText, true);
            const pageId = data && data.pageId ? data.pageId : YAHOO.page.getPageId();
            const date = YUD.get('pageDate').value;
            YAHOO.page.reloadWindow(pageId, date);
        },
        failure: function(oResponse) {
            YAHOO.page.failureHandler(oResponse);
        }
    },
    reloadWindow: function(pageId, pageDate) {
        const win = window.parent ? window.parent : window;
        if (pageId)  {
            const url = new URL(win.location.href);
            url.searchParams.set('pageId', pageId);
            if (pageDate) {
                url.searchParams.set('pageDate', pageDate);
            }
            win.location.href = url.toString();
        } else {
            win.location.reload();
        }
    },
    paginatorDefaultTemplate: '{FirstPageLink} {PreviousPageLink} {PageLinks} {NextPageLink} {LastPageLink}',
    paginationRequestBuilder: function(oState) {
        oState = oState || { pagination: null, sortedBy: null };
        let request = '';
        const page = oState.pagination ? oState.pagination.page : 1; // 1 based
        request += 'page=' + (page - 1); // 0 based
        const offset = oState.pagination ? oState.pagination.recordOffset : 0;
        request += '&offset=' + offset;
        const limit = oState.pagination ? oState.pagination.rowsPerPage : 100;
        request += '&limit=' + limit;
        const total = oState.pagination ? oState.pagination.totalRecords : '';
        request += '&total=' + total;
        //const sort = oState.sortedBy ? oState.sortedBy.key : 'id'; 
        //request += '&sort=' + sort;
        //const dir = oState.sortedBy && oState.sortedBy.dir === YAHOO.widget.DataTable.CLASS_DESC ? 'desc' : 'asc'; 
        //request += '&dir=' + dir;
        return request;
    },
    initDataTable: function(entityName, oConfig) {
        const oLiveData = oConfig.liveData;
        const dataSource = new YAHOO.util.XHRDataSource(oLiveData, {
            connXhrMode: 'queueRequests',
            maxCacheEntries: 0,
            responseType: YAHOO.util.XHRDataSource.TYPE_JSON,
            responseSchema: {
                resultsList: 'records',
                // Access to values in the server response
                metaFields: {
                    columns: 'columns',
                    totalRecords: 'total',
                    startIndex: 'offset'
                }
            }
        });
        const elContainer = YUD.get(entityName);
        const r = YUD.getRegion(elContainer);
        const h = YUD.getViewportHeight();
        const dataTableConfig = {
            caption: oConfig.caption !== undefined ? oConfig.caption : null,
            dynamicData: !!oConfig.dynamicData,
            //sortedBy : {key: 'mic', dir: YAHOO.widget.DataTable.CLASS_ASC},
            paginator: oConfig.paginator !== undefined ? oConfig.paginator : null,
            initialLoad: true,
            initialRequest: oConfig.requestBuilder(),
            generateRequest: oConfig.requestBuilder,
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
                        if (columnDef.editor === 'checkbox' && columnDef.checkboxOptions !== undefined) {
                            oConfigs.checkboxOptions = columnDef.checkboxOptions;
                            columnDef.editor = new YAHOO.widget.CheckboxCellEditor(oConfigs);
                        } else if (columnDef.editor === 'dropdown' && columnDef.dropdownOptions !== undefined) {
                            oConfigs.dropdownOptions = columnDef.dropdownOptions;
                            oConfigs.multiple = columnDef.multiple;
                            columnDef.editor = new YAHOO.widget.DropdownCellEditor(oConfigs);
                        } else if (columnDef.editor === 'textbox') {
                            columnDef.editor = new YAHOO.widget.TextboxCellEditor(oConfigs);
                        }
                        const column = this.insertColumn(columnDef, index);
                        if (column.editor) {
                            editable = true;
                            if (oConfig.saveEvent !== undefined) {
                                column.editor.subscribe('saveEvent', oConfig.saveEvent);
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
            if (oPayload.pagination) {
                oPayload.totalRecords = oResponse.meta.totalRecords;
                oPayload.pagination.recordOffset = oResponse.meta.startIndex;
            }
            return oPayload;
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
    init: function(oContainer) {
        // Registry of cell formatting functions
        // custom 'tag' column formatter
        YAHOO.widget.DataTable.Formatter.tag = this.formatTag;
        // customise builtin column formatter(s)
        YAHOO.widget.DataTable.Formatter.currency = this.formatCurrency;
        YAHOO.widget.DataTable.Formatter.number = this.formatNumber;
        YAHOO.widget.DataTable.Formatter.text = this.formatText;
        //
        YAHOO.page.tabView = this.initTabView(oContainer);
        // init tab section(s)
        YAHOO.page.dataTables = [];
        YAHOO.page.tabView.get('tabs').forEach(tab => {
            const elTab = tab.get('contentEl');
            const sections = this.initSections(elTab);
            this.pageMap.set(tab, sections);
        });
        //
        YAHOO.page.initMenu();
        //
        const pageId = YAHOO.page.getPageId();
        const subName = '' + pageId;
        var activeIndex = parseInt(YAHOO.util.Cookie.getSub(tabViewActiveTabCookie, subName));
        activeIndex = !activeIndex || isNaN(activeIndex) ? 0 : activeIndex;
        YAHOO.page.tabView.selectTab(activeIndex);
        //
        YUE.addListener('pageDate', 'change', function(e) {
            //const date = YUE.getTarget(e).value;
            //YAHOO.page.reloadWindow(pageId, date);
            YAHOO.page.updateSubSectionDataTables();
        });
        //
        YAHOO.page.updateNumberFormat();
        YAHOO.page.updateCurrencyFormat(YUD.get('pageCurrency').value);
        YUE.addListener('pageCurrency', 'change', function(e) {
            const currency = YUE.getTarget(e).value;
            YAHOO.page.updateCurrencyFormat(currency);
            YAHOO.page.updateSubSectionDataTables();
        });
        // fire the custom event
        YAHOO.page.pageReadyEvent.fire({tabView: YAHOO.page.tabView});
    },
    initMenu: function() {
        const menuBar = new YAHOO.widget.MenuBar('pageMenu', {
            zIndex: 2,
            lazyload: true
        });
        menuBar.render(document.body);
    },
    initTabView: function(oContainer) {
        const tabView = new YAHOO.widget.TabView(oContainer/*, {activeIndex: 0}*/);
        tabView.on('activeIndexChange', function(e) {
            const activeIndex = e.newValue;
            const subName = '' + YAHOO.page.getPageId();
            YAHOO.util.Cookie.setSub(tabViewActiveTabCookie, subName, '' + activeIndex, {expires: new Date(Date.now() + 30*60*1000)});
            setTimeout(function() {
                YAHOO.page.moveSections();
            }, 100);
        });
        return tabView;
    },
    initSections: function(elTab) {
        const elSections = YUS.query('div[id^=section.]', elTab);
        const sections = [];
        elSections.forEach((elSection, index, array) => {
            const section = this.initSection(elTab, elSection);
            sections.push(section);
        });
        return sections;
    },
    initSection: function(elTab, elSection) {
        const w = YUD.getViewportWidth();
        const section = new YAHOO.widget.Panel(elSection, {
            zIndex: 1,
            width: w + 'px',
            autofillheight: 'body',
            constraintoviewport: false,
            visible: true,
            draggable: false,
            close: false
        });
        this.initSubSections(section);
        section.render(elTab);
        return section;
    },
    moveSections: function(tab) {
        if (!tab) {
            tab = YAHOO.page.tabView.get('activeTab');
        }
        const elTab = tab.get('contentEl');
        let r = YUD.getRegion(elTab.parentNode);
        let y = r.top;
        const sections = this.pageMap.get(tab);
        sections.forEach(section => {
            section.moveTo(0, y);
            r = YUD.getRegion(section.element);
            y = r.bottom;
        });
    },
    initSubSections: function(section) {
        const elSection = section.element;
        const elSubSections = YUS.query('div[id^=subSection.]', elSection);
        const size = elSubSections.length;
        // Layout max 3 subSection(s)
        if (size > 0) {
            // via grid.css
            elSubSections.forEach((elSubSection, index, array) => {
                const id = elSubSection.id; // subSection.{id}
                const dataTable = this.initSubSectionDataTable(YUD.get('data.' + id));
                YAHOO.page.dataTables.push(dataTable);
            });
            // via LayoutManager
            //YAHOO.page.optional.initSubSectionsLayout(section, elSubSections);
        }
    },
    initSubSectionDataTable: function(elSubSection) {
        const id = elSubSection.id; // data.subSection.{id}
        const subSectionId = this.getId(id);
        const headerId = 'hd.subSection.' + subSectionId; // hd.subSection.{id}
        const headerEl = YUD.get(headerId);
        const caption = headerEl.innerHTML; //innerText;
        setTimeout(function(el) {
            el.parentNode.removeChild(el);
        }, 100, headerEl);
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
        const r = YUD.getRegion(elSubSection);
        const requestBuilder = function(oState, oDataTable) {
            const date = YUD.get('pageDate').value;
            let request = subSectionId + '/' + date;
            const currency = YUD.get('pageCurrency').value;
            request += '/' + currency;
            if (oState) {
                request += '?skipColumns=true';
            }
            return request;
        };
        const initialRequest = requestBuilder(null, null);
        const dataTableConfig = {
            caption: caption,
            dynamicData: false,
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
                    const column = this.insertColumn(columnDef, index);
                })
                this.getDataSource().responseSchema.fields = columnDefs.map(columnDef => columnDef.key);
                //this.undisable();
            }
            return true;
        };
        // 2. after each time the DataTable is updated with new data
        dataTable.handleDataReturnPayload = function(oRequest, oResponse, oPayload) {
            YAHOO.page.moveSections();
            return oPayload || {};
        };
        // 3. fired when the DataTable's DOM is rendered or dirty (or postRenderEvent)
        //dataTable.subscribe('renderEvent', function() {
        //    // TODO: init subSection context menu
        //});
        return dataTable;
    },
    updateSubSectionDataTables: function() {
        YAHOO.page.dataTables.forEach(dataTable => {
            const state = dataTable.getState();
            const request = dataTable.get('generateRequest')(state, dataTable);
            const callback = {
                cache: false,
                success: dataTable.onDataReturnReplaceRows,
                failure: dataTable.onDataReturnReplaceRows,
                argument: state,
                scope: dataTable
            };
            dataTable.getDataSource().sendRequest(request, callback);
        });
    }
};

(function() {
    const loader = new YAHOO.util.YUILoader({
        base: 'js/yui/',
        skin: {
            base: 'assets/skins/',
            defaultSkin: 'sam'
        },
        onSuccess: function() {
            YUE.onDOMReady(function() {
                const win = window.parent ? window.parent : window;
                const url = new URL(win.location.href);
                const pageDate = url.searchParams.get('pageDate');
                if (pageDate) {
                    url.searchParams.delete('pageDate');
                    history.replaceState(history.state, '', url.href);
                }
            });
            YUE.onContentReady('pageDiv', function() {
                YAHOO.page.init(this);
            });
        }
    });
    loader.insert(); 
})();
