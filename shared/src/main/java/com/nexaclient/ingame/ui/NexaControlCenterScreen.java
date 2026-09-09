package com.nexaclient.ingame.ui;

import com.nexaclient.ingame.config.NexaConfig;
import com.nexaclient.ingame.modules.ModuleCategory;
import com.nexaclient.ingame.modules.ModuleRegistry;
import com.nexaclient.ingame.modules.ModuleSettings;
import com.nexaclient.ingame.modules.NexaModule;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** NEXA In-Game V2 module library for 1.21.1 / 1.21.8. */
public final class NexaControlCenterScreen extends Screen {
    private final Screen parent;
    private final ModuleRegistry modules;
    private final Map<NexaModule, Rect> cards = new LinkedHashMap<>();
    private String section = "all";
    private NexaModule selected;
    private int scroll;
    private int wx, wy, ww, wh, rail;

    public NexaControlCenterScreen(Screen parent, ModuleRegistry modules) {
        super(Text.literal("NEXA Mods")); this.parent = parent; this.modules = modules;
    }

    @Override public void render(DrawContext c, int mx, int my, float delta) {
        NexaUi.backdrop(c, width, height);
        ww = Math.min(1030, Math.max(650, width - 54));
        wh = Math.min(650, Math.max(420, height - 54));
        wx = (width - ww) / 2; wy = (height - wh) / 2; rail = Math.min(158, Math.max(132, ww / 6));
        NexaUi.window(c, wx, wy, ww, wh);
        drawHeader(c, mx, my); drawRail(c, mx, my); drawLibrary(c, mx, my); drawFooter(c, mx, my);
        if (selected != null) drawInspector(c, mx, my);
    }

    private void drawHeader(DrawContext c, int mx, int my) {
        int h = 58; c.fill(wx, wy + h - 1, wx + ww, wy + h, NexaUi.BORDER);
        NexaUi.roundedRect(c, wx + 17, wy + 14, 30, 30, 9, NexaUi.PRIMARY);
        c.drawCenteredTextWithShadow(textRenderer, "N", wx + 32, wy + 25, 0xFFFFFFFF);
        c.drawTextWithShadow(textRenderer, "NEXA", wx + 58, wy + 18, NexaUi.TEXT);
        c.drawTextWithShadow(textRenderer, "IN-GAME", wx + 58, wy + 32, NexaUi.TEXT_3);
        int sx = wx + ww - 232; NexaUi.search(c, textRenderer, sx, wy + 15, 174, inside(mx,my,sx,wy+15,174,28));
        NexaUi.iconButton(c, textRenderer, wx + ww - 46, wy + 15, 28, "×", inside(mx,my,wx+ww-46,wy+15,28,28), false);
    }

    private void drawRail(DrawContext c, int mx, int my) {
        int top = wy + 58, bottom = wy + wh - 47;
        c.fill(wx, top, wx + rail, bottom, 0xFF0D1015); c.fill(wx + rail - 1, top, wx + rail, bottom, NexaUi.BORDER);
        NexaUi.sectionLabel(c, textRenderer, "MODS", wx + 15, top + 18);
        int y = top + 34;
        y = nav(c,mx,my,y,"favorites","★  Favoritos");
        y = nav(c,mx,my,y,"all","Todos");
        y = nav(c,mx,my,y,"hud","HUD");
        y = nav(c,mx,my,y,"visual","Visual");
        y = nav(c,mx,my,y,"gameplay","Gameplay");
        y = nav(c,mx,my,y,"camera","Camara");
        y = nav(c,mx,my,y,"other","Otros");
        NexaUi.sectionLabel(c, textRenderer, "PERFIL", wx + 15, bottom - 62);
        NexaUi.softPanel(c, wx + 12, bottom - 46, rail - 24, 32);
        c.drawTextWithShadow(textRenderer, NexaUi.abbreviate(textRenderer, modules.config().activeProfile, rail - 55), wx + 22, bottom - 34, NexaUi.TEXT_2);
        c.drawTextWithShadow(textRenderer, "›", wx + rail - 28, bottom - 34, NexaUi.TEXT_3);
    }

    private int nav(DrawContext c,int mx,int my,int y,String id,String label) {
        NexaUi.navItem(c,textRenderer,wx+10,y,rail-20,label,section.equals(id),inside(mx,my,wx+10,y,rail-20,28)); return y+31;
    }

    private void drawLibrary(DrawContext c, int mx, int my) {
        int left = wx + rail + 20, top = wy + 78, right = wx + ww - 20;
        List<NexaModule> visible = visible();
        c.drawTextWithShadow(textRenderer, title(), left, top, NexaUi.TEXT);
        int enabled = (int)visible.stream().filter(m -> modules.state(m).enabled).count();
        c.drawTextWithShadow(textRenderer, enabled + " activos · " + visible.size() + " modulos", left, top + 15, NexaUi.TEXT_3);
        cards.clear(); int available = right-left; int gap=10; int min=112; int cols=Math.max(2,Math.min(5,(available+gap)/(min+gap))); int cw=(available-gap*(cols-1))/cols; int ch=100;
        int start=Math.min(scroll,Math.max(0,visible.size()-1));
        for(int i=start;i<visible.size();i++) {
            int local=i-start, x=left+(local%cols)*(cw+gap), y=top+39+(local/cols)*(ch+gap); if(y+ch>wy+wh-62) break;
            NexaModule m=visible.get(i); cards.put(m,new Rect(x,y,cw,ch)); drawCard(c,mx,my,m,x,y,cw,ch);
        }
        if(visible.isEmpty()) { c.drawCenteredTextWithShadow(textRenderer,"No hay modulos en esta seccion",(left+right)/2,top+105,NexaUi.TEXT_3); }
    }

    private void drawCard(DrawContext c,int mx,int my,NexaModule m,int x,int y,int w,int h) {
        NexaConfig.ModuleConfig s=modules.state(m); boolean hover=inside(mx,my,x,y,w,h), active=m==selected;
        NexaUi.moduleCard(c,x,y,w,h,hover,active,s.enabled);
        String glyph=icon(m); NexaUi.moduleIcon(c,textRenderer,x+(w-34)/2,y+13,glyph,s.enabled,hover);
        c.drawCenteredTextWithShadow(textRenderer,NexaUi.abbreviate(textRenderer,m.name(),w-16),x+w/2,y+55,NexaUi.TEXT);
        c.drawCenteredTextWithShadow(textRenderer,s.enabled?"ENABLED":m.implemented()?"DISABLED":"COMING SOON",x+w/2,y+72,m.implemented()?(s.enabled?NexaUi.PRIMARY:NexaUi.TEXT_3):NexaUi.WARNING);
        c.drawTextWithShadow(textRenderer,isFavorite(s)?"★":"☆",x+8,y+8,isFavorite(s)?NexaUi.PRIMARY:NexaUi.TEXT_3);
        if(hover) c.drawTextWithShadow(textRenderer,"⚙",x+w-18,y+8,NexaUi.TEXT_2);
    }

    private void drawFooter(DrawContext c,int mx,int my) {
        int y=wy+wh-47; c.fill(wx,y,wx+ww,y+1,NexaUi.BORDER);
        int bx=wx+16; NexaUi.button(c,textRenderer,bx,y+9,114,28,"HUD EDITOR",inside(mx,my,bx,y+9,114,28),true);
        c.drawTextWithShadow(textRenderer,"NEXA UI V2",wx+ww-76,y+19,NexaUi.TEXT_3);
    }

    private void drawInspector(DrawContext c,int mx,int my) {
        int iw=Math.min(250,ww/3), ix=wx+ww-iw-10, iy=wy+68, ih=wh-126;
        NexaUi.panel(c,ix,iy,iw,ih); NexaConfig.ModuleConfig s=modules.state(selected);
        c.drawTextWithShadow(textRenderer,selected.name(),ix+16,iy+17,NexaUi.TEXT);
        c.drawTextWithShadow(textRenderer,NexaUi.abbreviate(textRenderer,selected.description(),iw-32),ix+16,iy+34,NexaUi.TEXT_3);
        NexaUi.iconButton(c,textRenderer,ix+iw-32,iy+10,22,"×",inside(mx,my,ix+iw-32,iy+10,22,22),false);
        NexaUi.divider(c,ix+14,iy+54,iw-28);
        c.drawTextWithShadow(textRenderer,"Estado",ix+16,iy+72,NexaUi.TEXT_2); NexaUi.toggle(c,ix+iw-52,iy+66,s.enabled,selected.implemented(),inside(mx,my,ix+iw-52,iy+66,34,18));
        NexaUi.sectionLabel(c,textRenderer,"AJUSTE",ix+16,iy+106);
        NexaUi.softPanel(c,ix+14,iy+120,iw-28,38); c.drawTextWithShadow(textRenderer,ModuleSettings.summary(selected,s),ix+26,iy+134,NexaUi.TEXT_2);
        NexaUi.button(c,textRenderer,ix+14,iy+170,iw-28,30,"SIGUIENTE OPCION",inside(mx,my,ix+14,iy+170,iw-28,30),false);
        if(selected.editableHud()) NexaUi.button(c,textRenderer,ix+14,iy+210,iw-28,30,"EDITAR EN HUD",inside(mx,my,ix+14,iy+210,iw-28,30),true);
        c.drawTextWithShadow(textRenderer,isFavorite(s)?"★ Favorito":"☆ Agregar a favoritos",ix+16,iy+ih-24,isFavorite(s)?NexaUi.PRIMARY:NexaUi.TEXT_3);
    }

    @Override public boolean mouseClicked(double mx,double my,int button) {
        if(button!=0) return super.mouseClicked(mx,my,button);
        if(inside(mx,my,wx+ww-46,wy+15,28,28)){close();return true;}
        int top=wy+58,y=top+34; String[] ids={"favorites","all","hud","visual","gameplay","camera","other"}; for(String id:ids){if(inside(mx,my,wx+10,y,rail-20,28)){section=id;scroll=0;selected=null;return true;}y+=31;}
        int profileBottom=wy+wh-47;if(inside(mx,my,wx+12,profileBottom-46,rail-24,32)){modules.config().nextBuiltInProfile();modules.save();return true;}
        if(inside(mx,my,wx+16,wy+wh-38,114,28)){if(client!=null)client.setScreen(new NexaHudEditorScreen(this,modules));return true;}
        if(selected!=null){int iw=Math.min(250,ww/3),ix=wx+ww-iw-10,iy=wy+68,ih=wh-126;if(inside(mx,my,ix+iw-32,iy+10,22,22)){selected=null;return true;}if(inside(mx,my,ix+iw-52,iy+66,34,18))return toggle();if(inside(mx,my,ix+14,iy+170,iw-28,30)){ModuleSettings.cycle(selected,modules.state(selected));modules.save();return true;}if(selected.editableHud()&&inside(mx,my,ix+14,iy+210,iw-28,30)){if(client!=null)client.setScreen(new NexaHudEditorScreen(this,modules));return true;}if(inside(mx,my,ix+14,iy+ih-38,iw-28,34)){favorite(selected);return true;}}
        for(var e:cards.entrySet()){Rect r=e.getValue();if(!r.contains(mx,my))continue;selected=e.getKey();if(inside(mx,my,r.x,r.y,25,25)){favorite(selected);return true;}if(inside(mx,my,r.x,r.y+r.h-30,r.w,30))return toggle();return true;}
        return super.mouseClicked(mx,my,button);
    }

    private boolean toggle(){if(selected==null||!selected.implemented())return true;var s=modules.state(selected);s.enabled=!s.enabled;modules.save();return true;}
    private void favorite(NexaModule m){var s=modules.state(m);s.settings.put("favorite",Boolean.toString(!isFavorite(s)));modules.save();}
    private static boolean isFavorite(NexaConfig.ModuleConfig s){return s.booleanSetting("favorite",false);}
    @Override public boolean mouseScrolled(double mx,double my,double h,double v){int max=Math.max(0,visible().size()-1);scroll=Math.max(0,Math.min(max,scroll+(v<0?1:-1)));return true;}
    private List<NexaModule> visible(){return ModuleRegistry.MODULES.stream().filter(this::matches).toList();}
    private boolean matches(NexaModule m){return switch(section){case"favorites"->isFavorite(modules.state(m));case"hud"->m.category()==ModuleCategory.HUD;case"visual"->m.category()==ModuleCategory.VISUAL;case"gameplay"->m.category()==ModuleCategory.GAMEPLAY;case"camera"->m.category()==ModuleCategory.CAMERA;case"other"->m.category()==ModuleCategory.WORLD||m.category()==ModuleCategory.UTILITY||m.category()==ModuleCategory.PERFORMANCE;default->true;};}
    private String title(){return switch(section){case"favorites"->"Favoritos";case"hud"->"HUD";case"visual"->"Visual";case"gameplay"->"Gameplay";case"camera"->"Camara";case"other"->"Otros";default->"Todos los mods";};}
    private static String icon(NexaModule m){return switch(m.id()){case"fps"->"F";case"ping"->"P";case"memory"->"M";case"clock"->"C";case"coordinates"->"XYZ";case"armor"->"A";case"inventory"->"I";case"keystrokes"->"W";case"cps"->"CPS";case"crosshair"->"+";case"zoom"->"Z";case"perspective"->"360";case"freecam"->"FC";case"fov_changer"->"FOV";default->m.name().substring(0,Math.min(2,m.name().length())).toUpperCase();};}
    private static boolean inside(double mx,double my,int x,int y,int w,int h){return mx>=x&&mx<x+w&&my>=y&&my<y+h;}
    private record Rect(int x,int y,int w,int h){boolean contains(double mx,double my){return inside(mx,my,x,y,w,h);}}
    @Override public void close(){modules.save();if(client!=null)client.setScreen(parent);} @Override public boolean shouldPause(){return false;}
}
