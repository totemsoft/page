const YL  = YAHOO.lang,
      YUC = YAHOO.util.Connect,
      YUD = YAHOO.util.Dom,
      YUE = YAHOO.util.Event,
      YUG = YAHOO.util.Get,
      YUS = YAHOO.util.Selector;

YAHOO.page = {
    tabView: null,
    pageMap: new Map(), // <Tab, [Section]>
    getId: function(s) {
        return s.substring(s.lastIndexOf('.') + 1);
    },
    formatTag: function(elLiner, oRecord, oColumn, oData) {
        const el = elLiner.parentNode;
        if (!YUD.hasClass(el, 'page-tag')) {
            YUD.addClass(el, 'page-tag');
        }
    },
    init: function(oContainer) {
        // Add the custom formatter
        YAHOO.widget.DataTable.formatTag = this.formatTag;
        //
        this.tabView = YAHOO.page.initTabView(oContainer);
        // init tab section(s)
        this.tabView.get('tabs').forEach(tab => {
            const elTab = tab.get('contentEl');
            const sections = YAHOO.page.initSections(elTab);
            YAHOO.page.pageMap.set(tab, sections);
        });
        // TODO: show second tab (final step after all data loaded in DataTable(s))
        //this.tabView.selectTab(1);
    },
    initTabView: function(oContainer) {
        const tabView = new YAHOO.widget.TabView(oContainer/*, {activeIndex: 0}*/);
        tabView.on('activeIndexChange', function(e) {
            const tab = this.getTab(e.newValue);
            const elTab = tab.get('contentEl');
            let r = YUD.getRegion(elTab.parentNode);
            let y = r.top;
            const sections = YAHOO.page.pageMap.get(tab);
            sections.forEach(section => {
                section.moveTo(0, y);
                r = YUD.getRegion(section.element);
                y = r.bottom;
            });
        });
        return tabView;
    },
    initSections: function(elTab) {
        const elSections = YUS.query('div[id^=section.]', elTab);
        const sections = [];
        elSections.forEach(elSection => {
            const section = YAHOO.page.initSection(elTab, elSection);
            sections.push(section);
        });
        return sections;
    },
    initSection: function(elTab, elSection) {
        const w = YUD.getViewportWidth();
        const section = new YAHOO.widget.Panel(elSection, { 
            width: w + 'px',
            autofillheight: 'body',
            //constraintoviewport: true,
            visible: true,
            draggable: !false,
            close: false
        });
        YAHOO.page.initSubSections(section);
        section.render(elTab);
        return section;
    },
    initSubSections: function(section) {
        const elSection = section.element;
        const elSubSections = YUS.query('div[id^=subSection.]', elSection);
        const size = elSubSections.length;
        // Layout max 3 subSection(s)
        if (size > 0) {
            // via grid.css
            elSubSections.forEach(elSubSection => {
                const id = elSubSection.id; // subSection.{id}
                const subSectionId = YAHOO.page.getId(id);
                const dataTable = YAHOO.page.initDataTable(YUD.get('data.' + id), '/subSection/' + subSectionId);
            });
            // via LayoutManager
            //YAHOO.page.initSubSectionsLayout(section, elSubSections);
            // via Panel
            //YAHOO.page.initSubSectionsPanel(section, elSubSections);
        }
    },
    initDataTable: function(elSubSection, oLiveData) {
        const id = elSubSection.id; // data.subSection.{id}
        const subSectionId = YAHOO.page.getId(id);
        const headerEl = YUD.get('hd.subSection.' + subSectionId); // hd.subSection.{id}
        const caption = headerEl.innerText;
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
            //initialLoad: true,
            //initialRequest: '/columns', // oLiveData + initialRequest
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
                    if (columnDef.formatter && typeof columnDef.formatter === 'string') {
                        columnDef.formatter = eval(columnDef.formatter); // string -> function
                    }
                    const column = this.insertColumn(columnDef, index);
                })
                this.getDataSource().responseSchema.fields = columnDefs.map(columnDef => columnDef.key);
                //this.undisable();
            }
            return true;
        };
        // 2. after each time the DataTable is updated with new data
        dataTable.handleDataReturnPayload = function(oRequest, oResponse, oPayload) {
            return oPayload;
        };
        // 3. fired when the DataTable's DOM is rendered or dirty. 
        dataTable.subscribe('renderEvent', function() {
            // TODO: init subSection context menu
        });
        return dataTable;
    },
    initSubSectionsLayout: function(section, elSubSections) {
        // LayoutUnit positions
        const positions = [
            ['center'],
            ['center', 'right'],
            ['left', 'center', 'right']
        ];
        const width = YUD.getViewportWidth();
        const units = [];
        // max 3 subSection(s) [left, center, right]
        const size = elSubSections.length;
        elSubSections.forEach((elSubSection, index) => {
            const id = elSubSection.id;
            const headerEl = YUD.get('hd.' + id);
            const header = headerEl.innerHTML;
            setTimeout(function(el) {
                el.parentNode.removeChild(el);
            }, 100, headerEl.parentNode);
            units.push({
                position: positions[size - 1][index],
                // The html to use as the Header of the Unit (sets via innerHTML)
                header: header,
                // The content for the footer. If we find an element in the page with an id that matches the passed option we will move that element into the footer of this unit. (sets via innerHTML)
                //footer: 'ft.' + id,
                // The content for the body. If we find an element in the page with an id that matches the passed option we will move that element into the body of this unit. (sets via innerHTML)
                body: 'bd.' + id,
                //grids: true,
                width: width / size,
                gutter: '2'
            });
        });
        const subSectionLayout = new YAHOO.widget.Layout('bd.' + section.id, {
            height: section.body.offsetHeight, //clientHeight,
            width: width,
            units: units
        });
        subSectionLayout.render();
    },
    initSubSectionsPanel: function(section, elSubSections) {
        const width = YUD.getViewportWidth();
        const size = elSubSections.length;
        elSubSections.forEach((elSubSection) => {
            const w = width / size;
            const subSection = new YAHOO.widget.Panel(elSubSection,
                { width: w + 'px', autofillheight: 'body', constraintoviewport: true, visible:true, draggable:!false, close:false } );
            subSection.render(section.body);
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
