const YL  = YAHOO.lang,
      YUC = YAHOO.util.Connect,
      YUD = YAHOO.util.Dom,
      YUE = YAHOO.util.Event,
      YUG = YAHOO.util.Get,
      YUS = YAHOO.util.Selector;

// LayoutUnit positions
const positions = [
    ['center'],
    ['center', 'right'],
    ['left', 'center', 'right']
];

YAHOO.page = {
    pageMap: new Map(), // <Tab, [Section]>
    init: function(oContainer) {
        const tabView = YAHOO.page.initTabView(oContainer);
        // init tab section(s)
        const tabs = tabView.get('tabs');
        tabs.forEach(tab => {
            const elTab = tab.get('contentEl');
            const sections = YAHOO.page.initSections(elTab);
            YAHOO.page.pageMap.set(tab, sections);
        });
        // show first tab
        //setTimeout(function() {
            tabView.set('activeIndex', 0, false);
        //}, 100);
    },
    initTabView: function(oContainer) {
        const tabView = new YAHOO.widget.TabView(oContainer);
        tabView.on('activeIndexChange', function(e) {
//*
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
//*/
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
        const section = new YAHOO.widget.Panel(elSection,
            { width: w + 'px', autofillheight: 'body', constraintoviewport: true, visible:true, draggable:!false, close:false } );
        section.beforeRenderEvent.subscribe(function() {
            YUE.onAvailable(section.id, function() {
                YAHOO.page.initSubSections(section);
            });
        });
        section.render(elTab);
        return section;
    },
    initSubSections: function(section) {
        const elSection = section.element;
        const elSubSections = YUS.query('div[id^=subSection.]', elSection);
        const size = elSubSections.length;
        if (size > 0) {
            const width = YUD.getViewportWidth();
            // max 3 subSection(s)
            const units = [];
            elSubSections.forEach((elSubSection, index) => {
                //const subSection = YAHOO.page.initSubSection(section, elSubSection);
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
                    width: width / size
                });
            });
            const subSectionLayout = new YAHOO.widget.Layout('bd.' + section.id, {
                //height: section.body.offsetHeight - 20,
                //width: width,
                units: units
            });
            subSectionLayout.render();
        }
    },
    initSubSection: function(section, elSubSection) {
        const w = YUD.getViewportWidth() / 2;
        const subSection = new YAHOO.widget.Panel(elSubSection,
            { width: w + 'px', autofillheight: 'body', constraintoviewport: true, visible:true, draggable:!false, close:false } );
        subSection.render(section.body);
        return subSection;
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
