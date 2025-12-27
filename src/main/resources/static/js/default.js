const YL  = YAHOO.lang,
      YUC = YAHOO.util.Connect,
      YUD = YAHOO.util.Dom,
      YUE = YAHOO.util.Event,
      YUG = YAHOO.util.Get,
      YUS = YAHOO.util.Selector;

YAHOO.page = {
    sectionMap: new Map(),
    init: function(oContainer) {
        const tabView = YAHOO.page.initTabView(oContainer);
        // init tab section(s)
        const tabs = tabView.get('tabs');
        tabs.forEach(tab => {
            const elTab = tab.get('contentEl');
            const sections = YAHOO.page.initSections(elTab);
            YAHOO.page.sectionMap.set(tab, sections);
        });
        // show first tab
        tabView.set('activeIndex', 0, false);
    },
    initTabView: function(oContainer) {
        const tabView = new YAHOO.widget.TabView(oContainer);
        tabView.on('activeIndexChange', function(e) {
            const tab = this.getTab(e.newValue);
            const elTab = tab.get('contentEl');
            var r = YUD.getRegion(elTab.parentNode);
            var y = r.top;
            const sections = YAHOO.page.sectionMap.get(tab);
            sections.forEach(section => {
                section.moveTo(0, y);
                const elSection = section.element;
                r = YUD.getRegion(elSection);
                y = r.bottom;
            });
        });
        return tabView;
    },
    initSections: function(elTab) {
        const nodes = YUS.query('div[id^=section.]', elTab);
        const sections = [];
        for (var i = 0; i < nodes.length; i++) {
            const node = nodes[i];
            const section = YAHOO.page.initSection(elTab, node);
            sections.push(section);
        }
        return sections;
    },
    initSection: function(elTab, elSection) {
        const w = YUD.getViewportWidth();
        const section = new YAHOO.widget.Panel(elSection,
            { width: w + 'px', visible:true, draggable:!false, close:false } );
        section.render(elTab);
        return section;
    }
};

(function() {
    var loader = new YAHOO.util.YUILoader({
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
