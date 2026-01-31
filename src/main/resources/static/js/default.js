const YL  = YAHOO.lang,
      YUC = YAHOO.util.Connect,
      YUD = YAHOO.util.Dom,
      YUE = YAHOO.util.Event,
      YUG = YAHOO.util.Get,
      YUS = YAHOO.util.Selector;
const tabViewActiveTabCookie = 'YAHOO.page.tabView.activeIndex';

YAHOO.namespace('page');

YAHOO.page = {
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
    formatTag: function(el, oRecord, oColumn, oData, oDataTable) {
        //const oDT = oDataTable || this;
        const value = (YL.isValue(oData)) ? oData : '';
        el.innerHTML = YL.escapeHTML(value.toString());
        const className = oColumn.className || (oData ? oData.className : null);
        YAHOO.page.addClass(el.parentNode, className);
    },
    formatCurrency: function(el, oRecord, oColumn, oData, oDataTable) {
        const oDT = oDataTable || this;
        el.innerHTML = YAHOO.util.Number.format(oData, oColumn.currencyOptions || oDT.get('currencyOptions'));
        const className = oColumn.className || (oData ? oData.className : null);
        YAHOO.page.addClass(el.parentNode, className);
    },
    formatNumber: function(el, oRecord, oColumn, oData, oDataTable) {
        const oDT = oDataTable || this;
        el.innerHTML = YAHOO.util.Number.format(oData, oColumn.numberOptions || oDT.get("numberOptions"));
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
    openEditDialog: function(el, oConfig, fnSubmitHandler, oEventDef) {
        const dialog = new YAHOO.widget.SimpleDialog(el, {
            close: true,
            draggable: true,
            fixedcenter: oConfig && oConfig.fixedcenter !== undefined ? oConfig.fixedcenter : true,
            visible: false,
            modal: false,
            icon: null,
            height: oConfig && oConfig.height !== undefined ? oConfig.height : null,
            width: oConfig && oConfig.width !== undefined ? oConfig.width : null,
            autofillheight: 'body',
            constraintoviewport: oConfig && oConfig.constraintoviewport !== undefined ? oConfig.constraintoviewport : true,
            context: ['showbtn', 'tl', 'bl'],
            buttons: oConfig && oConfig.buttons !== undefined ? oConfig.buttons : [
                {text: 'Save', isDefault: true, handler: {
                    fn: fnSubmitHandler,
                    obj: el,
                    scope: this
                }},
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
            YAHOO.page.updateDataTables();
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
                const dataTable = this.initDataTable(YUD.get('data.' + id));
                YAHOO.page.dataTables.push(dataTable);
            });
            // via LayoutManager
            //YAHOO.page.optional.initSubSectionsLayout(section, elSubSections);
        }
    },
    initDataTable: function(elSubSection) {
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
            if (oState) {
                request += '?skipColumns=true';
            }
            return request;
        };
        const initialRequest = requestBuilder(null, null);
        const dataTableConfig = {
            caption: caption,
            //dynamicData: true,
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
        // 3. fired when the DataTable's DOM is rendered or dirty. 
        //dataTable.subscribe('renderEvent', function() {
        //    // TODO: init subSection context menu
        //});
        return dataTable;
    },
    updateDataTables: function() {
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
            YUE.onContentReady('pageDiv', function() {
                YAHOO.page.init(this);
            });
        }
    });
    loader.insert(); 
})();
