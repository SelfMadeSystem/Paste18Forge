/*
package uwu.smsgamer.pasteclient.values;

import com.google.gson.*;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.*;
import uwu.smsgamer.pasteclient.utils.StringHashMap;

public class PacketValue extends ChoiceValue<Class<? extends Packet<?>>> {
    public static final StringHashMap<Class<? extends Packet<?>>> packetChoices = new StringHashMap<>();
    public static final StringHashMap<Class<? extends Packet<?>>> cPacketChoices = StringHashMap.reverse(
      "CPacketAnimation", CPacketAnimation.class,
      "CPacketChatMessage", CPacketChatMessage.class,
      "CPacketClickWindow", CPacketClickWindow.class,
      "CPacketClientSettings", CPacketClientSettings.class,
      "CPacketClientStatus", CPacketClientStatus.class,
      "CPacketCloseWindow", CPacketCloseWindow.class,
      "CPacketConfirmTeleport", CPacketConfirmTeleport.class,
      "CPacketConfirmTransaction", CPacketConfirmTransaction.class,
      "CPacketCreativeInventoryAction", CPacketCreativeInventoryAction.class,
      "CPacketCustomPayload", CPacketCustomPayload.class,
      "CPacketEnchantItem", CPacketEnchantItem.class,
      "CPacketEntityAction", CPacketEntityAction.class,
      "CPacketHeldItemChange", CPacketHeldItemChange.class,
      "CPacketInput", CPacketInput.class,
      "CPacketKeepAlive", CPacketKeepAlive.class,
      "CPacketPlaceRecipe", CPacketPlaceRecipe.class,
      "CPacketPlayer", CPacketPlayer.class,
      "CPacketPlayerTryUseItem", CPacketPlayerTryUseItem.class,
      "CPacketPlayerTryUseItemOnBlock", CPacketPlayerTryUseItemOnBlock.class,
      "CPacketRecipeInfo", CPacketRecipeInfo.class,
      "CPacketResourcePackStatus", CPacketResourcePackStatus.class,
      "CPacketSeenAdvancements", CPacketSeenAdvancements.class,
      "CPacketSpectate", CPacketSpectate.class,
      "CPacketSteerBoat", CPacketSteerBoat.class,
      "CPacketTabComplete", CPacketTabComplete.class,
      "CPacketUpdateSign", CPacketUpdateSign.class,
      "CPacketUseEntity", CPacketUseEntity.class,
      "CPacketVehicleMove", CPacketVehicleMove.class
    );
    public static final StringHashMap<Class<? extends Packet<?>>> sPacketChoices = StringHashMap.reverse(
      "SPacketAdvancementInfo", SPacketAdvancementInfo.class,
      "SPacketAnimation", SPacketAnimation.class,
      "SPacketBlockAction", SPacketBlockAction.class,
      "SPacketBlockAction", SPacketBlockAction.class,
      "SPacketBlockBreakAnim", SPacketBlockBreakAnim.class,
      "SPacketBlockChange", SPacketBlockChange.class,
      "SPacketCamera", SPacketCamera.class,
      "SPacketChangeGameState", SPacketChangeGameState.class,
      "SPacketChat", SPacketChat.class,
      "SPacketChunkData", SPacketChunkData.class,
      "SPacketCloseWindow", SPacketCloseWindow.class,
      "SPacketCollectItem", SPacketCollectItem.class,
      "SPacketCombatEvent", SPacketCombatEvent.class,
      "SPacketConfirmTransaction", SPacketConfirmTransaction.class,
      "SPacketCooldown", SPacketCooldown.class,
      "SPacketCustomPayload", SPacketCustomPayload.class,
      "SPacketCustomSound", SPacketCustomSound.class,
      "SPacketDestroyEntities", SPacketDestroyEntities.class,
      "SPacketDisconnect", SPacketDisconnect.class,
      "SPacketDisplayObjective", SPacketDisplayObjective.class,
      "SPacketEffect", SPacketEffect.class,
      "SPacketEntity", SPacketEntity.class,
      "SPacketEntityAttach", SPacketEntityAttach.class,
      "SPacketEntityEquipment", SPacketEntityEquipment.class,
      "SPacketEntityHeadLook", SPacketEntityHeadLook.class,
      "SPacketEntityMetadata", SPacketEntityMetadata.class,
      "SPacketEntityProperties", SPacketEntityProperties.class,
      "SPacketEntityStatus", SPacketEntityStatus.class,
      "SPacketEntityTeleport", SPacketEntityTeleport.class,
      "SPacketEntityVelocity", SPacketEntityVelocity.class,
      "SPacketExplosion", SPacketExplosion.class,
      "SPacketHeldItemChange", SPacketHeldItemChange.class,
      "SPacketJoinGame", SPacketJoinGame.class,
      "SPacketKeepAlive", SPacketKeepAlive.class,
      "SPacketMaps", SPacketMaps.class,
      "SPacketMoveVehicle", SPacketMoveVehicle.class,
      "SPacketMultiBlockChange", SPacketMultiBlockChange.class,
      "SPacketOpenWindow", SPacketOpenWindow.class,
      "SPacketParticles", SPacketParticles.class,
      "SPacketPlaceGhostRecipe", SPacketPlaceGhostRecipe.class,
      "SPacketPlayerAbilities", SPacketPlayerAbilities.class,
      "SPacketPlayerListHeaderFooter", SPacketPlayerListHeaderFooter.class,
      "SPacketPlayerListItem", SPacketPlayerListItem.class,
      "SPacketPlayerPosLook", SPacketPlayerPosLook.class,
      "SPacketRecipeBook", SPacketRecipeBook.class,
      "SPacketRemoveEntityEffect", SPacketRemoveEntityEffect.class,
      "SPacketResourcePackSend", SPacketResourcePackSend.class,
      "SPacketRespawn", SPacketRespawn.class,
      "SPacketScoreboardObjective", SPacketScoreboardObjective.class,
      "SPacketSelectAdvancementsTab", SPacketSelectAdvancementsTab.class,
      "SPacketServerDifficulty", SPacketServerDifficulty.class,
      "SPacketSetExperience", SPacketSetExperience.class,
      "SPacketSetPassengers", SPacketSetPassengers.class,
      "SPacketSetSlot", SPacketSetSlot.class,
      "SPacketSignEditorOpen", SPacketSignEditorOpen.class,
      "SPacketSoundEffect", SPacketSoundEffect.class,
      "SPacketSpawnExperienceOrb", SPacketSpawnExperienceOrb.class,
      "SPacketSpawnGlobalEntity", SPacketSpawnGlobalEntity.class,
      "SPacketSpawnMob", SPacketSpawnMob.class,
      "SPacketSpawnObject", SPacketSpawnObject.class,
      "SPacketSpawnPainting", SPacketSpawnPainting.class,
      "SPacketSpawnPlayer", SPacketSpawnPlayer.class,
      "SPacketSpawnPosition", SPacketSpawnPosition.class,
      "SPacketStatistics", SPacketStatistics.class,
      "SPacketTabComplete", SPacketTabComplete.class,
      "SPacketTeams", SPacketTeams.class,
      "SPacketTimeUpdate", SPacketTimeUpdate.class,
      "SPacketTitle", SPacketTitle.class,
      "SPacketUnloadChunk", SPacketUnloadChunk.class,
      "SPacketUpdateBossInfo", SPacketUpdateBossInfo.class,
      "SPacketUpdateHealth", SPacketUpdateHealth.class,
      "SPacketUpdateScore", SPacketUpdateScore.class,
      "SPacketUpdateTileEntity", SPacketUpdateTileEntity.class,
      "SPacketUseBed", SPacketUseBed.class,
      "SPacketWindowItems", SPacketWindowItems.class,
      "SPacketWindowProperty", SPacketWindowProperty.class,
      "SPacketWorldBorder", SPacketWorldBorder.class
    );

    static {
        packetChoices.putAll(cPacketChoices);
        packetChoices.putAll(sPacketChoices);
    }

    public PacketValue(String name, String description, Class<? extends Packet<?>> val, Value<?>... children) {
        this(name, description, val, packetChoices, children);
    }

    public PacketValue(String name, String description, Class<? extends Packet<?>> val, StringHashMap<Class<? extends Packet<?>>> choices, Value<?>... children) {
        super(name, description, val, choices, children);
    }

    @Override
    public Class<? extends Packet<?>> getCommandT(String arg) {
        return packetChoices.getReversedMap().get(arg);
    }

    @Override
    public JsonElement toElement() {
        return new JsonPrimitive(choices.get(value));
    }

    @Override
    public void fromElement(JsonElement ele) {
        value = choices.getReversedMap().get(ele.getAsString());
    }
}
*/
