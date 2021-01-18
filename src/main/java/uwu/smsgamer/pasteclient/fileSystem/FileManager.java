/*
 * Copyright (c) 2018 superblaubeere27
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package uwu.smsgamer.pasteclient.fileSystem;

import com.google.common.io.Files;
import com.google.gson.*;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.NotNull;
import uwu.smsgamer.pasteclient.PasteClient;
import uwu.smsgamer.pasteclient.modules.PasteModule;
import uwu.smsgamer.pasteclient.values.Value;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.*;

public class FileManager {
    private final File clientDir = new File(Minecraft.getMinecraft().mcDataDir, PasteClient.CLIENT_NAME);
    private final File backupDir = new File(clientDir, "backups");
    private final File scriptsDir = new File(clientDir, "scripts");
    private final File saveFile = new File(clientDir, "client.json");

    public void save() throws Exception {
        //noinspection ResultOfMethodCallIgnored
        clientDir.mkdirs();

        if (!saveFile.exists() && !saveFile.createNewFile())
            throw new IOException("Failed to create " + saveFile.getAbsolutePath());

        Files.write(toJsonObject().toString().getBytes(StandardCharsets.UTF_8), saveFile);
    }

    @NotNull
    private JsonObject toJsonObject() {
        LogManager.getLogger().info("Saving settings");

        JsonObject obj = new JsonObject();


        {
            JsonObject metadata = new JsonObject();

            metadata.addProperty("clientVersion", PasteClient.CLIENT_VERSION_NUMBER);

            obj.add("metadata", metadata);
        }

        {
            JsonObject modules = new JsonObject();

            for (PasteModule module : PasteClient.INSTANCE.moduleManager.getModules()) {
                JsonObject moduleObject = new JsonObject();

                moduleObject.addProperty("state", module.getState());
                moduleObject.addProperty("keybind", module.getKeybind());

                modules.add(module.getName(), moduleObject);
            }

            obj.add("modules", modules);
        }
        {
            JsonObject values = new JsonObject();

            for (PasteModule module : PasteClient.INSTANCE.moduleManager.getModules()) {
                JsonObject json = new JsonObject();
                for (Value<?> value : module.getValues().values()) {
                    json.add(value.getName(), value.toJSON().get(value.getName()));
                }
                values.add(module.getName(), json);
            }

            obj.add("values", values);
        }

        return obj;
    }

    public void load() {
        if (!saveFile.exists()) return;

        List<String> backupReasons = new ArrayList<>();

        try {
            JsonObject object = (JsonObject) new JsonParser().parse(new InputStreamReader(new FileInputStream(saveFile)));

            //<editor-fold desc="metadata">
            if (object.has("metadata")) {
                JsonElement metadataElement = object.get("metadata");

                if (metadataElement instanceof JsonObject) {
                    JsonObject metadata = (JsonObject) metadataElement;

                    JsonElement clientVersion = metadata.get("clientVersion");

                    if (clientVersion != null && clientVersion.isJsonPrimitive() && ((JsonPrimitive) clientVersion).isNumber()) {
                        double version = clientVersion.getAsDouble();

                        if (version > PasteClient.CLIENT_VERSION_NUMBER) {
                            backupReasons.add("Version number of save file (" + version + ") is higher than " + PasteClient.CLIENT_VERSION_NUMBER);
                        }
                        if (version < PasteClient.CLIENT_VERSION_NUMBER) {
                            backupReasons.add("Version number of save file (" + version + ") is lower than " + PasteClient.CLIENT_VERSION_NUMBER);
                        }
                    } else {
                        backupReasons.add("'clientVersion' object is not valid.");
                    }
                } else {
                    backupReasons.add("'metadata' object is not valid.");
                }

            } else {
                backupReasons.add("Save file has no metadata");
            }
            //</editor-fold>

            //<editor-fold desc="modules">
            JsonElement modulesElement = object.get("modules");

            if (modulesElement instanceof JsonObject) {
                JsonObject modules = (JsonObject) modulesElement;

                for (Map.Entry<String, JsonElement> stringJsonElementEntry : modules.entrySet()) {
                    PasteModule module = PasteClient.INSTANCE.moduleManager.getModule(stringJsonElementEntry.getKey(), true);

                    if (module == null) {
                        backupReasons.add("Module '" + stringJsonElementEntry.getKey() + "' doesn't exist");
                        continue;
                    }

                    if (stringJsonElementEntry.getValue() instanceof JsonObject) {
                        JsonObject moduleObject = (JsonObject) stringJsonElementEntry.getValue();

                        JsonElement state = moduleObject.get("state");

                        if (state instanceof JsonPrimitive && ((JsonPrimitive) state).isBoolean()) {
                            module.setState(state.getAsBoolean());
                        } else {
                            backupReasons.add("'" + stringJsonElementEntry.getKey() + "/state' isn't valid");
                        }

                        JsonElement keybind = moduleObject.get("keybind");

                        if (keybind instanceof JsonPrimitive && ((JsonPrimitive) keybind).isNumber()) {
                            module.setKeybind(keybind.getAsInt());
                        } else {
                            backupReasons.add("'" + stringJsonElementEntry.getKey() + "/keybind' isn't valid");
                        }
                    } else {
                        backupReasons.add("Module object '" + stringJsonElementEntry.getKey() + "' isn't valid");
                    }
                }
            } else {
                backupReasons.add("'modules' object is not valid");
            }
            //</editor-fold>

            //<editor-fold desc="values">
            JsonElement valuesElement = object.get("values");

            if (valuesElement instanceof JsonObject) {
                for (Map.Entry<String, JsonElement> stringJsonElementEntry : ((JsonObject) valuesElement).entrySet()) {
                    PasteModule module = PasteClient.INSTANCE.moduleManager.getModule(stringJsonElementEntry.getKey(), false);

                    if (module == null) {
                        backupReasons.add("Module '" + stringJsonElementEntry.getKey() + "' doesn't exist");
                        continue;
                    }

                    if (!stringJsonElementEntry.getValue().isJsonObject()) {
                        backupReasons.add("'module/" + stringJsonElementEntry.getKey() + "' is not valid");
                        continue;
                    }

                    JsonObject valueObject = (JsonObject) stringJsonElementEntry.getValue();

                    for (Value<?> value : module.getValues().values()) {
                        if (valueObject.has(value.getName())) {
                            try {
                                value.fromJSON(valueObject);
                            } catch (Exception e) {
                                e.printStackTrace();
                                backupReasons.add("Error while applying 'values/" + stringJsonElementEntry.getKey() + "' " + e.toString());
                            }
                        } else {
                            backupReasons.add("'value/" + stringJsonElementEntry.getKey() + "' does not exist!");
                        }
                    }
                }
            } else {
                backupReasons.add("'values' is not valid");
            }
            //</editor-fold>

            if (backupReasons.size() > 0) {
                backup(backupReasons);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void backup(@NotNull List<String> backupReasons) {
        System.out.println("Creating backup " + backupReasons);

        try {
            backupDir.mkdirs();

            File out = new File(backupDir, "backup_" + System.currentTimeMillis() + ".zip");
            out.createNewFile();

            StringBuilder reason = new StringBuilder();

            for (String backupReason : backupReasons) {
                reason.append("- ").append(backupReason).append("\n");
            }

            ZipOutputStream outputStream = new ZipOutputStream(new FileOutputStream(out));

            outputStream.putNextEntry(new ZipEntry("client.json"));
            Files.copy(saveFile, outputStream);
            outputStream.closeEntry();

            outputStream.putNextEntry(new ZipEntry("reason.txt"));
            outputStream.write(reason.toString().getBytes(StandardCharsets.UTF_8));
            outputStream.closeEntry();

            outputStream.close();
        } catch (Exception e) {
            System.out.println("Failed to backup");
            e.printStackTrace();
        }

    }

    /*public void loadScripts() {
        if (!scriptsDir.exists()) scriptsDir.mkdirs();

        File[] files = scriptsDir.listFiles(pathname -> pathname.getName().endsWith("zip") || pathname.getName().endsWith("cbs"));

        if (files != null) {
            for (File file : files) {
                try {
                    PasteClient.INSTANCE.scriptManager.load(file);
                } catch (Exception e) {
                    System.err.println("Failed to load script " + file.getName());
                    e.printStackTrace();
                }
            }
        }
    }*/
}
