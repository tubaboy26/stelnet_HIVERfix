package stelnet.commodity;

import java.util.Set;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.IntelUIAPI;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;

import stelnet.commodity.element.ButtonViewFactory;
import stelnet.commodity.element.CommodityViewFactory;
import stelnet.commodity.element.IntelSelectionFactory;
import stelnet.helper.IntelHelper;
import stelnet.commodity.ui.Callable;
import stelnet.commodity.ui.GridRenderer;
import stelnet.commodity.ui.Size;

public class CommodityBoard extends BaseIntelPlugin {

    public final static String TAG = "stelnetCommodity";

    public enum CommodityTab {
        BUY("Buy"), SELL("Sell");

        public String title;

        private CommodityTab(String title) {
            this.title = title;
        }
    }

    private String activeId;
    private CommodityTab activeTab;
    private ButtonViewFactory buttonViewFactory;
    private CommodityViewFactory commodityViewFactory;
    private IntelSelectionFactory intelSelectionFactory;

    public static CommodityBoard getInstance() {
        IntelInfoPlugin intel = IntelHelper.getFirstIntel(CommodityBoard.class);
        if (intel == null) {
            BaseIntelPlugin board = new CommodityBoard();
            IntelHelper.addIntel(board);
        }
        return (CommodityBoard) intel;
    }

    public CommodityBoard() {
        readResolve();
    }

    @Override
    public void buttonPressConfirmed(Object buttonId, IntelUIAPI ui) {
        Callable callable = (Callable) buttonId;
        callable.callback();
        ui.updateUIForItem(this);
    }

    @Override
    public void createIntelInfo(TooltipMakerAPI info, ListInfoMode mode) {
        info.addPara("Commodity Market", getTitleColor(mode), 0);
        info.addPara("Compare and track commodity prices among all known markets.", getBulletColorForMode(mode), 1f);
        info.addPara("", 1f);
    }

    @Override
    public void createLargeDescription(CustomPanelAPI panel, float width, float height) {
        float commodityViewWidth = width - 210;
        float commodityViewHeight = height - 35;
        GridRenderer renderer = new GridRenderer(new Size(width, height));
        renderer.setTopLeft(commodityViewFactory.get(activeId, activeTab, commodityViewWidth, commodityViewHeight));
        renderer.setTopRight(buttonViewFactory.get(activeId));
        renderer.setBottomLeft(intelSelectionFactory.get(activeId, activeTab, commodityViewWidth));
        renderer.render(panel);
    }

    @Override
    public String getIcon() {
        return Global.getSettings().getSpriteName("stelnet", "commodity");
    }

    @Override
    public Set<String> getIntelTags(SectorMapAPI map) {
        Set<String> tags = super.getIntelTags(map);
        tags.add(CommodityBoard.TAG);
        return tags;
    }

    @Override
    public IntelSortTier getSortTier() {
        return IntelSortTier.TIER_0;
    }

    @Override
    public boolean hasLargeDescription() {
        return true;
    }

    @Override
    public boolean hasSmallDescription() {
        return false;
    }

    public void setActiveId(String activeId) {
        this.activeId = activeId;
    }

    public void setActiveTab(CommodityTab activeTab) {
        this.activeTab = activeTab;
    }

    protected Object readResolve() {
        if (activeId == null) {
            activeId = Commodities.SUPPLIES;
        }
        if (activeTab == null) {
            activeTab = CommodityTab.BUY;
        }
        if (buttonViewFactory == null) {
            buttonViewFactory = new ButtonViewFactory();
        }
        if (intelSelectionFactory == null) {
            intelSelectionFactory = new IntelSelectionFactory();
        }
        if (commodityViewFactory == null) {
            commodityViewFactory = new CommodityViewFactory(intelSelectionFactory);
        }
        return this;
    }
}