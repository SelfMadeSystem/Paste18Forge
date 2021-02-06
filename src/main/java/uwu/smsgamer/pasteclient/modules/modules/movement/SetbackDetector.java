/*
 * Copyright (c) 2018 superblaubeere27
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package uwu.smsgamer.pasteclient.modules.modules.movement;

import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import uwu.smsgamer.pasteclient.PasteClient;
import uwu.smsgamer.pasteclient.events.PacketEvent;
import uwu.smsgamer.pasteclient.modules.*;
import uwu.smsgamer.pasteclient.notifications.*;
import uwu.smsgamer.pasteclient.values.BoolValue;

public class SetbackDetector extends PasteModule {
    private final BoolValue disableModules = addBool("Disable Modules",
      "Whether or not to disable movement modules when getting flagged", true);

    public SetbackDetector() {
        super("FlagDetector", "Detects flags", ModuleCategory.MOVEMENT);
    }

    @EventTarget
    private void onPacket(PacketEvent event) {
        if (!getState()) return;
        // TODO: 2020-11-27 Make it so that it doesn't "flag" when joining server.
        if (event.getPacket() instanceof S08PacketPlayerPosLook) {
            NotificationManager.show(new Notification(NotificationType.WARNING, getName(), "Flag detected", 1));
            if (disableModules.getValue()) {
                PasteClient.INSTANCE.moduleManager.getModule("Fly", false).setState(false);
                PasteClient.INSTANCE.moduleManager.getModule("Speed", false).setState(false);
                PasteClient.INSTANCE.moduleManager.getModule("Velocity", false).setState(false);
            }
        }
    }
}
