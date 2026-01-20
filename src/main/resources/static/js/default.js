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
        const tabView = this.initTabView(oContainer);
        // init tab section(s)
        tabView.get('tabs').forEach(tab => {
            const elTab = tab.get('contentEl');
            const sections = this.initSections(elTab);
            this.pageMap.set(tab, sections);
        });
        const pageId = YAHOO.page.getPageId();
        const subName = '' + pageId;
        var activeIndex = parseInt(YAHOO.util.Cookie.getSub(tabViewActiveTabCookie, subName));
        activeIndex = !activeIndex || isNaN(activeIndex) ? 0 : activeIndex;
        tabView.selectTab(activeIndex);
        //
        YUE.addListener('pageDate', 'change', function(e) {
            const date = YUE.getTarget(e).value;
            YAHOO.page.reloadWindow(pageId, date);
        });
        // fire the custom event
        YAHOO.page.pageReadyEvent.fire({tabView: tabView});
    },
    initTabView: function(oContainer) {
        const tabView = new YAHOO.widget.TabView(oContainer/*, {activeIndex: 0}*/);
        tabView.on('activeIndexChange', function(e) {
            const activeIndex = e.newValue;
            const subName = '' + YAHOO.page.getPageId();
            YAHOO.util.Cookie.setSub(tabViewActiveTabCookie, subName, '' + activeIndex, {expires: new Date(Date.now() + 30*60*1000)});
            const tab = this.getTab(activeIndex);
            YAHOO.page.moveSections(tab);
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
            zIndex: 2,
            width: w + 'px',
            autofillheight: 'body',
            constraintoviewport: true,
            visible: true,
            draggable: false,
            close: false
        });
        this.initSubSections(section);
        section.render(elTab);
        return section;
    },
    moveSections: function(tab) {
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
                const subSectionId = this.getId(id);
                const date = YUD.get('pageDate').value;
                const dataTable = this.initDataTable(YUD.get('data.' + id), '/subSection/' + subSectionId + '/' + date);
            });
            // via LayoutManager
            //YAHOO.page.optional.initSubSectionsLayout(section, elSubSections);
        }
    },
    initDataTable: function(elSubSection, oLiveData) {
        const id = elSubSection.id; // data.subSection.{id}
        const subSectionId = this.getId(id);
        const headerId = 'hd.subSection.' + subSectionId; // hd.subSection.{id}
        const headerEl = YUD.get(headerId);
        const caption = headerEl.innerHTML; //innerText;
        setTimeout(function(el) {
            el.parentNode.removeChild(el);
        }, 100, headerEl);
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
            return '';
        };
        const dataTableConfig = {
            caption: caption,
            //dynamicData: true,
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
        //dataTable.handleDataReturnPayload = function(oRequest, oResponse, oPayload) {
        //    return oPayload || {};
        //};
        // 3. fired when the DataTable's DOM is rendered or dirty. 
        //dataTable.subscribe('renderEvent', function() {
        //    // TODO: init subSection context menu
        //});
        return dataTable;
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
