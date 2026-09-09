package com.nexaclient.ingame.modern;

import com.nexaclient.ingame.config.NexaConfig;
import com.nexaclient.ingame.hud.HudRenderer;
import com.nexaclient.ingame.modules.ModuleRegistry;
import com.nexaclient.ingame.modules.NexaModule;
import com.nexaclient.ingame.ui.NexaUi;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.LinkedHashMap;
import java.util.Map;

/** Floating HUD editor used by the NEXA UI V2 shell on 1.21.10. */
public final class ModernNexaHudEditorScreen extends Screen {
    private final Screen parent;
    private final ModuleRegistry modules;
    private final Map<NexaModule, HudRenderer.Bounds> bounds = new LinkedHashMap<>();
    private NexaModule dragging, focused;
    private double dragOffsetX, dragOffsetY;
    private boolean grid = true, snap = true;

    public ModernNexaHudEditorScreen(Screen parent, ModuleRegistry modules) { super(Text.literal("NEXA HUD Editor")); this.parent=parent; this.modules=modules; }

    @Override public void render(DrawContext c,int mx,int my,float delta){NexaUi.backdrop(c,width,height);c.fill(0,0,width,height,0x4A000000);if(grid)drawGrid(c);c.drawTextWithShadow(textRenderer,"HUD EDITOR",18,18,NexaUi.TEXT);c.drawTextWithShadow(textRenderer,"Arrastra · rueda escala · Shift+rueda opacidad",18,33,NexaUi.TEXT_3);bounds.clear();if(client!=null)for(NexaModule module:ModuleRegistry.MODULES){NexaConfig.ModuleConfig state=modules.state(module);if(!state.enabled||!module.editableHud())continue;HudRenderer.Bounds box=HudRenderer.renderModule(c,client,modules,module,state,true);bounds.put(module,box);boolean active=module==dragging||module==focused||box.contains(mx,my);c.drawStrokedRectangle(box.x()-3,box.y()-3,box.width()+6,box.height()+6,active?NexaUi.PRIMARY:0x66788292);}drawToolbar(c,mx,my);if(focused!=null)drawInspector(c,mx,my);}
    private void drawGrid(DrawContext c){for(int x=16;x<width;x+=32)c.fill(x,0,x+1,height,0x122F7BC7);for(int y=16;y<height;y+=32)c.fill(0,y,width,y+1,0x122F7BC7);c.fill(width/2,0,width/2+1,height,0x66438BFF);c.fill(0,height/2,width,height/2+1,0x66438BFF);}
    private void drawToolbar(DrawContext c,int mx,int my){int w=356,h=42,x=(width-w)/2,y=height-58;NexaUi.toolbar(c,x,y,w,h);button(c,mx,my,x+8,y+7,72,"GRID "+(grid?"ON":"OFF"),grid);button(c,mx,my,x+84,y+7,72,"SNAP "+(snap?"ON":"OFF"),snap);button(c,mx,my,x+160,y+7,84,"RESET HUD",false);button(c,mx,my,x+248,y+7,100,"GUARDAR",true);} private void button(DrawContext c,int mx,int my,int x,int y,int w,String label,boolean primary){NexaUi.button(c,textRenderer,x,y,w,28,label,inside(mx,my,x,y,w,28),primary);}
    private void drawInspector(DrawContext c,int mx,int my){var state=modules.state(focused);int w=220,h=142,x=width-w-18,y=18;NexaUi.panel(c,x,y,w,h);c.drawTextWithShadow(textRenderer,focused.name(),x+14,y+15,NexaUi.TEXT);c.drawTextWithShadow(textRenderer,"Scale  "+Math.round(state.scale*100)+"%",x+14,y+39,NexaUi.TEXT_2);c.drawTextWithShadow(textRenderer,"Opacity  "+Math.round(state.opacity*100)+"%",x+14,y+56,NexaUi.TEXT_2);c.drawTextWithShadow(textRenderer,"X "+Math.round(state.x*100)+"%   Y "+Math.round(state.y*100)+"%",x+14,y+73,NexaUi.TEXT_3);NexaUi.button(c,textRenderer,x+14,y+96,w-28,28,"RESTABLECER POSICION",inside(mx,my,x+14,y+96,w-28,28),false);c.drawTextWithShadow(textRenderer,"×",x+w-25,y+14,NexaUi.TEXT_3);}
    @Override public boolean mouseClicked(Click click,boolean doubled){if(click.button()!=0)return super.mouseClicked(click,doubled);double mx=click.x(),my=click.y();int tw=356,tx=(width-tw)/2,ty=height-58;if(inside(mx,my,tx+8,ty+7,72,28)){grid=!grid;return true;}if(inside(mx,my,tx+84,ty+7,72,28)){snap=!snap;return true;}if(inside(mx,my,tx+160,ty+7,84,28)){for(NexaModule m:bounds.keySet())reset(m);return true;}if(inside(mx,my,tx+248,ty+7,100,28)){close();return true;}if(focused!=null){int ix=width-238,iy=18;if(inside(mx,my,ix+14,iy+96,192,28)){reset(focused);return true;}if(inside(mx,my,ix+190,iy+6,28,28)){focused=null;return true;}}for(var e:bounds.entrySet())if(e.getValue().contains(mx,my)){dragging=e.getKey();focused=dragging;dragOffsetX=mx-e.getValue().x();dragOffsetY=my-e.getValue().y();return true;}return super.mouseClicked(click,doubled);}
    @Override public boolean mouseDragged(Click click,double dx,double dy){if(dragging==null||click.button()!=0)return super.mouseDragged(click,dx,dy);var state=modules.state(dragging);var box=bounds.get(dragging);double px=click.x()-dragOffsetX,py=click.y()-dragOffsetY;if(snap){if(Math.abs(px+box.width()/2.0-width/2.0)<8)px=(width-box.width())/2.0;if(Math.abs(py+box.height()/2.0-height/2.0)<8)py=(height-box.height())/2.0;}px=Math.max(0,Math.min(width-box.width(),px));py=Math.max(0,Math.min(height-box.height(),py));state.x=clamp((float)(px/Math.max(1,width-box.width())));state.y=clamp((float)(py/Math.max(1,height-box.height())));return true;}
    @Override public boolean mouseReleased(Click click){if(dragging!=null){dragging=null;modules.save();return true;}return super.mouseReleased(click);}
    @Override public boolean mouseScrolled(double mx,double my,double h,double v){for(var e:bounds.entrySet())if(e.getValue().contains(mx,my)){focused=e.getKey();var state=modules.state(focused);long window=client.getWindow().getHandle();boolean shift=GLFW.glfwGetKey(window,GLFW.GLFW_KEY_LEFT_SHIFT)==GLFW.GLFW_PRESS||GLFW.glfwGetKey(window,GLFW.GLFW_KEY_RIGHT_SHIFT)==GLFW.GLFW_PRESS;if(shift)state.opacity=Math.max(.15f,Math.min(1f,state.opacity+(v>0?.05f:-.05f)));else state.scale=Math.max(.5f,Math.min(2f,state.scale+(v>0?.1f:-.1f)));return true;}return super.mouseScrolled(mx,my,h,v);} private void reset(NexaModule m){var s=modules.state(m);s.x=m.defaultX();s.y=m.defaultY();s.scale=1f;s.opacity=.92f;modules.save();} private static float clamp(float v){return Math.max(0f,Math.min(1f,v));}private static boolean inside(double mx,double my,int x,int y,int w,int h){return mx>=x&&mx<x+w&&my>=y&&my<y+h;}@Override public void close(){modules.save();if(client!=null)client.setScreen(parent);}@Override public boolean shouldPause(){return false;}
}
