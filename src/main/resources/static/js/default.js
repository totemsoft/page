const YL  = YAHOO.lang,
      YUC = YAHOO.util.Connect,
      YUD = YAHOO.util.Dom,
      YUE = YAHOO.util.Event,
      YUG = YAHOO.util.Get,
      YUS = YAHOO.util.Selector;

YAHOO.page = {
    init: function(oContainer) {
        const tabView = YAHOO.page.initTabView(oContainer);
        // init tab section(s)
        const tabs = tabView.get('tabs');
        tabs.forEach(tab => {
            YAHOO.page.initSections(tab.get('contentEl'));
        });
    },
    initTabView: function(oContainer) {
        const tabView = new YAHOO.widget.TabView(oContainer);
        tabView.on('activeIndexChange', function(e) {
            const tab = this.getTab(e.newValue);
        });
        tabView.set('activeIndex', 0, false);
        return tabView;
    },
    initSections: function(oContainer) {
        const sectionNodes = YUS.query('div[id^=section.]', oContainer);
        for (var i = 0; i < sectionNodes.length; i++) {
            YAHOO.page.initSection(oContainer, sectionNodes[i], i);
        }
    },
    initSection: function(oContainer, sectionNode, index) {
        const w = YUD.getViewportWidth();
        const section = new YAHOO.widget.Panel(sectionNode,
            { width: w + 'px', visible:true, draggable:!false, close:false } );
        section.render(oContainer);
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
