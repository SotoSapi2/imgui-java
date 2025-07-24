package imgui.flag;

import imgui.binding.annotation.BindingAstEnum;
import imgui.binding.annotation.BindingSource;

@BindingSource
public final class ImGuiNavRenderCursorFlags
{
    private ImGuiNavRenderCursorFlags() {
    }

    @BindingAstEnum(file = "ast-imgui_internal.json", qualType = "ImGuiNavRenderCursorFlags", sanitizeName = "ImGuiNavRenderCursorFlags_")
    public Void __;
}
