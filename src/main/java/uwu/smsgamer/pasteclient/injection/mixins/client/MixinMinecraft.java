/*
 * Copyright (c) 2018 superblaubeere27
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package uwu.smsgamer.pasteclient.injection.mixins.client;

import com.darkmagician6.eventapi.EventManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.main.GameConfiguration;
import net.minecraft.util.*;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.lwjgl.opengl.Display;
import org.spongepowered.asm.mixin.*;
import uwu.smsgamer.pasteclient.PasteClient;
import uwu.smsgamer.pasteclient.events.KeyEvent;
import uwu.smsgamer.pasteclient.injection.interfaces.IMixinMinecraft;
import uwu.smsgamer.pasteclient.modules.modules.fun.DemoModeModule;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.*;
import java.nio.ByteBuffer;

@Mixin(Minecraft.class)
@SideOnly(Side.CLIENT)
public class MixinMinecraft implements IMixinMinecraft {
    @Shadow
    public GuiScreen currentScreen;
    @Shadow
    private Timer timer;

    @Shadow
    @Mutable
    @Final
    private Session session;

    @Shadow
    private ByteBuffer readImageToBuffer(InputStream p_readImageToBuffer_1_) {
        return null;
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void minecraftConstructor(GameConfiguration gameConfig, CallbackInfo ci) {
        new PasteClient();
    }

    @Inject(method = "startGame", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;ingameGUI:Lnet/minecraft/client/gui/GuiIngame;", shift = At.Shift.AFTER))
    private void startGame(CallbackInfo ci) {
        PasteClient.INSTANCE.startClient();
    }

    @Inject(method = "runTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;dispatchKeypresses()V", shift = At.Shift.AFTER))
    private void onKey(CallbackInfo ci) {
        if (Keyboard.getEventKeyState() && currentScreen == null)
            EventManager.call(new KeyEvent(Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() + 256 : Keyboard.getEventKey()));
    }

    @Inject(method = "shutdown", at = @At("HEAD"))
    private void onShutdown(CallbackInfo ci) {
        PasteClient.INSTANCE.stopClient();
    }

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "isDemo", at = @At("HEAD"), cancellable = true)
    private void isDemo(CallbackInfoReturnable<Boolean> cir) {
        if (PasteClient.INSTANCE == null || PasteClient.INSTANCE.moduleManager == null) return;

        DemoModeModule mod = PasteClient.INSTANCE.moduleManager.getModule(DemoModeModule.class);

        if (mod != null && mod.getState()) {
            cir.setReturnValue(true);
            cir.cancel();
        }
    }

    /**
     * @author Sms_Gamer_3808
     */
    @Overwrite
    private void setWindowIcon() {
        Util.EnumOS util$enumos = Util.getOSType();
        LogManager.getLogger().info("Setting icon!");
        if (util$enumos != Util.EnumOS.OSX) {
            InputStream image16 = null;
            InputStream image32 = null;

            try {
                image16 = getClass().getResourceAsStream("/assets/PasteForgeICO-16.png");
                image32 = getClass().getResourceAsStream("/assets/PasteForgeICO-32.png");
                if (image16 != null && image32 != null) {
                    Display.setIcon(new ByteBuffer[]{this.readImageToBuffer(image16), this.readImageToBuffer(image32)});
                }
            } finally {
                IOUtils.closeQuietly(image16);
                IOUtils.closeQuietly(image32);
            }
        }
    }

    @Override
    public Session getSession() {
        return session;
    }

    @Override
    public void setSession(Session session) {
        this.session = session;
    }

    @Override
    public Timer getTimer() {
        return timer;
    }
}
