import imgui.*;
import imgui.flag.ImGuiCol;
import imgui.internal.ImGui;
import imgui.app.Application;
import imgui.app.Configuration;
import imgui.flag.ImGuiConfigFlags;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.internal.ImRect;
import imgui.type.ImBoolean;
import imgui.type.ImString;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main extends Application {
    private final ImString str = new ImString(5);
    private final float[] flt = new float[1];
    private int count = 0;

    @Override
    protected void configure(final Configuration config) {
        config.setTitle("Example Application");
    }

    @Override
    protected void initImGui(final Configuration config) {
        super.initImGui(config);

        final ImGuiIO io = ImGui.getIO();
        io.setIniFilename(null);                                // We don't want to save .ini file
        io.addConfigFlags(ImGuiConfigFlags.NavEnableKeyboard);  // Enable Keyboard Controls
        io.addConfigFlags(ImGuiConfigFlags.DockingEnable);      // Enable Docking
        //io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable);    // Enable Multi-Viewport / Platform Windows
        io.setConfigViewportsNoTaskBarIcon(true);

        initFonts(io);
    }

    /**
     * Example of fonts configuration
     * For more information read: https://github.com/ocornut/imgui/blob/33cdbe97b8fd233c6c12ca216e76398c2e89b0d8/docs/FONTS.md
     */
    private void initFonts(final ImGuiIO io) {
        // This enables FreeType font renderer, which is disabled by default.
        io.getFonts().setFreeTypeRenderer(true);

        // Add default font for latin glyphs
        io.getFonts().addFontDefault();

        // You can use the ImFontGlyphRangesBuilder helper to create glyph ranges based on text input.
        // For example: for a game where your script is known, if you can feed your entire script to it (using addText) and only build the characters the game needs.
        // Here we are using it just to combine all required glyphs in one place
        final ImFontGlyphRangesBuilder rangesBuilder = new ImFontGlyphRangesBuilder(); // Glyphs ranges provide
        rangesBuilder.addRanges(io.getFonts().getGlyphRangesDefault());
        rangesBuilder.addRanges(io.getFonts().getGlyphRangesCyrillic());
        rangesBuilder.addRanges(io.getFonts().getGlyphRangesJapanese());
        rangesBuilder.addRanges(FontAwesomeIcons._IconRange);

        // Font config for additional fonts
        // This is a natively allocated struct so don't forget to call destroy after atlas is built
        final ImFontConfig fontConfig = new ImFontConfig();
        fontConfig.setMergeMode(true);  // Enable merge mode to merge cyrillic, japanese and icons with default font

        final short[] glyphRanges = rangesBuilder.buildRanges();
        io.getFonts().addFontFromMemoryTTF(loadFromResources("Tahoma.ttf"), 14, fontConfig, glyphRanges); // cyrillic glyphs
        io.getFonts().addFontFromMemoryTTF(loadFromResources("NotoSansCJKjp-Medium.otf"), 14, fontConfig, glyphRanges); // japanese glyphs
        io.getFonts().addFontFromMemoryTTF(loadFromResources("fa-regular-400.ttf"), 14, fontConfig, glyphRanges); // font awesome
        io.getFonts().addFontFromMemoryTTF(loadFromResources("fa-solid-900.ttf"), 14, fontConfig, glyphRanges); // font awesome
        io.getFonts().build();

        fontConfig.destroy();
    }

    private boolean testCustomWidget() {
        String label = "Hello, imgui!";
        ImGuiStyle style = ImGui.getStyle();
        int id = ImGui.getID(label);
        ImVec2 windowPos = ImGui.getWindowPos();
        ImVec2 cursorPos = ImGui.getCursorPos();

        ImVec2 labelSize = ImGui.calcTextSize(label);
        ImVec2 size = ImGui.calcItemSize(new ImVec2(), labelSize.x + style.getFramePaddingX() * 2.0f, labelSize.y + style.getFramePaddingY() * 2.0f);

        ImRect bb = new ImRect(
            cursorPos.plus(windowPos),
            cursorPos.plus(windowPos).plus(size)
        );

        ImGui.itemSize(size, style.getFramePaddingY());
        if(!ImGui.itemAdd(bb, id)) {
            return false;
        }

        ImBoolean hovered = new ImBoolean();
        ImBoolean held = new ImBoolean();
        boolean pressed = ImGui.buttonBehavior(bb, id, hovered, held);
        boolean popupOpen = ImGui.isPopupOpen(label);

        if(pressed && !popupOpen)
        {
            ImGui.openPopup(label);
            popupOpen = true;
        }

        final ImVec2 framePadding = style.getFramePadding();
        final ImDrawList drawList = ImGui.getWindowDrawList();

        ImVec4 textColor = ImGui.getStyleColorVec4(ImGuiCol.Text);
        ImVec4 frameColor = ImGui.getStyleColorVec4(
            (held.get() && hovered.get()) ? ImGuiCol.ButtonActive :
            hovered.get() ? ImGuiCol.ButtonHovered : ImGuiCol.Button
        );

        ImGui.renderFrame(
            bb.min,
            bb.max,
            ImGui.getColorU32(frameColor)
        );

        ImGui.renderText(bb.min.plus(style.getFramePadding()), label);

        if (ImGui.beginPopup(label))
        {
            ImGui.text("Hello meow :3");
            ImGui.endPopup();
        }

        return popupOpen;
    }

    @Override
    public void process() {
        if (ImGui.begin("Demo")) {

            ImGui.text("OS: [" + System.getProperty("os.name") + "] Arch: [" + System.getProperty("os.arch") + "]");
            ImGui.text("Hello, World! " + FontAwesomeIcons.Smile);
            testCustomWidget();
            if (ImGui.button(FontAwesomeIcons.Save + " Save")) {
                count++;
            }
            ImGui.sameLine();
            ImGui.text(String.valueOf(count));
            ImGui.inputText("string", str, ImGuiInputTextFlags.CallbackResize);
            ImGui.text("Result: " + str.get());
            ImGui.sliderFloat("float", flt, 0, 1);
            ImGui.separator();
            ImGui.text("Extra");
            Extra.show(this);
        }
        ImGui.end();
    }

    private static byte[] loadFromResources(String name) {
        try {
            return Files.readAllBytes(Paths.get(Main.class.getResource(name).toURI()));
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(final String[] args) {
        launch(new Main());
        System.exit(0);
    }
}
