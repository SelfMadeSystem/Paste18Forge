package uwu.smsgamer.pasteclient.gui.clickgui.block;

import net.minecraft.client.gui.*;
import org.lwjgl.input.Keyboard;
import uwu.smsgamer.pasteclient.gui.clickgui.block.blockguis.CategoryGUI;
import uwu.smsgamer.pasteclient.gui.clickgui.utils.*;
import uwu.smsgamer.pasteclient.utils.fontRenderer.GlyphPageFontRenderer;
import uwu.smsgamer.pasteclient.values.FancyColorValue;

import java.awt.*;
import java.io.IOException;

public class BlockClickGUI extends GuiScreen { // TODO: Keybind
    public static FancyColorValue GUI_BACKGROUND = new FancyColorValue("GUIBG", "Background for the GUI.", new Color(0F, 0F, 0F, 0.5F));
    public static FancyColorValue GUI_COLOR = new FancyColorValue("GUIColor", "Color for the GUI.", Color.WHITE);
    public static FancyColorValue GUI_BORDER = new FancyColorValue("GUIBorder", "Border for the GUI.", Color.RED);

    public static FancyColorValue CATEGORY = new FancyColorValue("Category", "Color for category.", new Color(200, 200, 200));
    public static FancyColorValue CATEGORY_HOVER = new FancyColorValue("CategoryHover", "Color when hovering of category.", new Color(170, 170, 170));

    public static FancyColorValue MODULE_ON = new FancyColorValue("ModuleOn", "Color when module is on.", new Color(160, 200, 160));
    public static FancyColorValue MODULE_ON_HOVER = new FancyColorValue("ModuleOnHover", "Color when module is on and hovering.", new Color(120, 170, 120));
    public static FancyColorValue MODULE_OFF = CATEGORY.clone("ModuleOff", "Color when module is off.");
    public static FancyColorValue MODULE_OFF_HOVER = CATEGORY_HOVER.clone("ModuleOffHover", "Color when module is off and hovering.");

    public static FancyColorValue DEFAULT = new FancyColorValue("Default", "Color for default values.", new Color(180, 180, 180));
    public static FancyColorValue DEFAULT_HOVER = new FancyColorValue("DefaultHover", "Color for default values when hovering.", new Color(100, 100, 100));

    public static FancyColorValue BACK = new FancyColorValue("Back", "Color for back button.", new Color(230, 20, 20));
    public static FancyColorValue BACK_HOVER = new FancyColorValue("BackHover", "Color for back button when hovering.", new Color(180, 0, 0));

    public static FancyColorValue CHOICE = CATEGORY.clone("Choice", "Color for choice values.");
    public static FancyColorValue CHOICE_HOVER = CATEGORY_HOVER.clone("ChoiceHover", "Color for choice values when hovering.");

    public static FancyColorValue CHOICE_UNSELECT = MODULE_OFF.clone("ChoiceUnselect", "Color for unselected choice.");
    public static FancyColorValue CHOICE_UNSELECT_HOVER = MODULE_OFF_HOVER.clone("ChoiceUnselectHover", "Color for unselected choice when hovering.");
    public static FancyColorValue CHOICE_SELECT = MODULE_ON.clone("ChoiceSelect", "Color for selected choice.");
    public static FancyColorValue CHOICE_SELECT_HOVER = MODULE_ON_HOVER.clone("ChoiceSelectHover", "Color for selected choice when hovering.");

    public static FancyColorValue NUM_BG = CATEGORY.clone("NumBG", "BackGround for number slider.");
    public static FancyColorValue NUM_BG_HOVER = CATEGORY_HOVER.clone("NumBGHover", "BackGround for number slider when hovering.");
    public static FancyColorValue NUM_BG_SELECT = new FancyColorValue("NumBGSelect", "BackGround for number slider when selected.", new Color(120, 120, 120));
    public static FancyColorValue NUM_SLIDER = new FancyColorValue("NumSlider", "Color for slider.", new Color(130, 200, 130));
    public static FancyColorValue NUM_SLIDER_HOVER = new FancyColorValue("NumSliderHover", "Color for slider when hovering.", new Color(80, 150, 80));
    public static FancyColorValue NUM_SLIDER_SELECT = new FancyColorValue("NumSliderSelect", "Color for slider when selected.", new Color(80, 120, 80));

    public static FancyColorValue STR = NUM_BG.clone("Str", "Color for string.");
    public static FancyColorValue STR_HOVER = NUM_BG_HOVER.clone("StrHover", "Color for string when hovering.");
    public static FancyColorValue STR_SELECT = NUM_BG_SELECT.clone("StrSelect", "Color for string when selected.");

    public static FancyColorValue ADD = new FancyColorValue("Add", "Color for add.", new Color(80, 230, 80));
    public static FancyColorValue ADD_HOVER = new FancyColorValue("AddHover", "Color for add when hovering.", new Color(60, 150, 60));

    public static final IRenderer renderer;

    static {
        GlyphPageFontRenderer consolas = GlyphPageFontRenderer.create("Montserrat", 15, false, false, false);
        renderer = new ClientBaseRendererImpl(consolas);
    }

    public String moduleName = "";
    public String currentDescription = "";

    private final BlockGUI blockGUI = new CategoryGUI();

    private static BlockClickGUI instance;

    public static BlockClickGUI getInstance() {
        if (instance == null) instance = new BlockClickGUI();
        return instance;
    }

    private BlockClickGUI() {
    }

    public int getMidScreenX() {
        ScaledResolution res = new ScaledResolution(mc);
        return res.getScaledWidth();
    }

    public int getMidScreenY() {
        ScaledResolution res = new ScaledResolution(mc);
        return res.getScaledHeight();
    }

    private long lastRender = System.currentTimeMillis();

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        long now = System.currentTimeMillis();
        blockGUI.render(mouseX, mouseY, (int) (now - lastRender));
        if (currentDescription != null && !currentDescription.isEmpty()) {
            int height = renderer.getStringHeight(currentDescription);
            int width = renderer.getStringWidth(currentDescription);
            renderer.drawRect(0, getMidScreenY() * 2 - height - 6, width + 10, height + 6, Color.BLACK);
            renderer.drawOutline(0, getMidScreenY() * 2 - height - 6, width + 10, height + 6, 2, Color.WHITE);
            renderer.drawString(4, getMidScreenY() * 2 - height - 4, currentDescription, Color.WHITE);
        }
        if (moduleName != null && !moduleName.isEmpty()) {
            final String text = "Internal Name: " + moduleName;
            int height = renderer.getStringHeight(text);
            int width = renderer.getStringWidth(text);
            renderer.drawRect(getMidScreenX() * 2 - width - 12, getMidScreenY() * 2 - height - 6, width + 10, height + 6, Color.BLACK);
            renderer.drawOutline(getMidScreenX() * 2 - width - 9, getMidScreenY() * 2 - height - 6, width + 10, height + 6, 2, Color.WHITE);
            renderer.drawString(getMidScreenX() * 2 - width - 6, getMidScreenY() * 2 - height - 4, text, Color.WHITE);
        }
        lastRender = now;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        blockGUI.click(mouseX, mouseY, mouseButton, 0);
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int mouseButton, long timeSinceLastClick) {
        blockGUI.click(mouseX, mouseY, mouseButton, 1);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        super.mouseReleased(mouseX, mouseY, mouseButton);
        blockGUI.click(mouseX, mouseY, mouseButton, 2);
    }

    @Override
    public void handleKeyboardInput() {
        char typedChar = Keyboard.getEventCharacter();
        int keyCode = Keyboard.getEventKey();
        if (keyCode == 1 && Keyboard.getEventKeyState() && !Keyboard.isRepeatEvent()) {
            close();
        } else blockGUI.keyPress(typedChar, keyCode, Keyboard.getEventKeyState() ?
          (Keyboard.isRepeatEvent() ? 1 : 0) : 2);

        this.mc.dispatchKeypresses();
    }

    public void close() {
        blockGUI.setChild(null); // just to make sure
        this.mc.displayGuiScreen(null);
        if (this.mc.currentScreen == null) {
            this.mc.setIngameFocus();
        }
    }
}
