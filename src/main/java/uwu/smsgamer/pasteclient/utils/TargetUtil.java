package uwu.smsgamer.pasteclient.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.*;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.*;
import uwu.smsgamer.pasteclient.values.*;

import java.util.*;

public class TargetUtil {
    private final static Minecraft mc = Minecraft.getMinecraft();

    public static BoolValue players = new BoolValue("Players", "Allows targeting of players.", true);
    public static BoolValue mobs = new BoolValue("Mobs", "Allows targeting of mobs.", true);
    public static BoolValue animals = new BoolValue("Animals", "Allows targeting of animals.", true);
    public static BoolValue others = new BoolValue("Others", "Allows targeting of other entities.", true);
    public static BoolValue invisible = new BoolValue("Invisible", "Allows targeting of invisible entities.", true);
    public static BoolValue air = new BoolValue("Air", "Excludes entities in air (packet wise).", true);
    public static BoolValue ground = new BoolValue("Ground", "Excludes entities on ground (packet wise).", true);
    public static BoolValue tab = new BoolValue("Tab", "Excludes entities that are not in the tablist.", true);
    public static BoolValue tabEquals = new BoolValue("TabEquals", "Only allows equals strings instead of contains.", true) {
        @Override
        public boolean isVisible() {
            return tab.getValue();
        }
    };
    public static BoolValue tabCase = new BoolValue("TabCase", "Is case sensitive.", true) {
        @Override
        public boolean isVisible() {
            return tab.getValue();
        }
    };

    public static boolean isValid(Entity entity) {
        if (!(entity instanceof EntityLivingBase)) return false;
        if (entity.equals(mc.thePlayer)) return false;
        if (entity instanceof EntityPlayer) return players.getValue() && isInvisAllowed(entity) && checkBot(entity);
        else if (entity instanceof EntityAnimal) return animals.getValue() && isInvisAllowed(entity) && checkBot(entity);
        else if (entity instanceof EntityMob) return mobs.getValue() && isInvisAllowed(entity) && checkBot(entity);
        else return others.getValue() && isInvisAllowed(entity) && checkBot(entity);
    }

    public static boolean isInvisAllowed(Entity entity) {
        return invisible.getValue() || !entity.isInvisible();
    }

    public static boolean checkBot(Entity entity) {
        String entityName = entity.getName();
        return !(entity instanceof EntityPlayer) || (!air.getValue() || entity.onGround) && (!ground.getValue() || !entity.onGround) &&
          (!tab.getValue() || mc.thePlayer.sendQueue.getPlayerInfoMap().stream().anyMatch(info -> {
              if (tabEquals.getValue() && tabCase.getValue())
                return info.getGameProfile().getName().equals(entityName);
              else if (tabEquals.getValue() && !tabCase.getValue())
                  return info.getGameProfile().getName().equalsIgnoreCase(entityName);
              else if (!tabEquals.getValue() && tabCase.getValue())
                  return info.getGameProfile().getName().contains(entityName);
              else return info.getGameProfile().getName().toLowerCase().contains(entityName.toLowerCase());
          }));
    }

    public static Entity getClosestEntity(double maxRange, double maxAngle) {
        double lowestDistance = maxRange;
        Entity returnEntity = null;
        for (Entity entity : mc.theWorld.loadedEntityList) {
            if (!isValid(entity)) continue;
            double dist = entity.getDistanceToEntity(mc.thePlayer);
            if (dist < lowestDistance && Math.abs(new RotationUtil(entity).getRotation().playerYawDiff()) <= maxAngle) {
                returnEntity = entity;
                lowestDistance = dist;
            }
        }
        return returnEntity;
    }

    public static Entity getLowestHealthEntity(double maxRange, double maxAngle) {
        double lowestHealth = 20000;
        Entity returnEntity = null;
        for (Entity entity : mc.theWorld.loadedEntityList) {
            if (!isValid(entity)) continue;
            double dist = entity.getDistanceToEntity(mc.thePlayer);
            if (((EntityLivingBase) entity).getHealth() < lowestHealth && dist < maxRange &&
              Math.abs(new RotationUtil(entity).getRotation().playerYawDiff()) <= maxAngle) {
                returnEntity = entity;
                lowestHealth = ((EntityLivingBase) entity).getHealth();
            }
        }
        return returnEntity;
    }

    public static Entity getLowestAngleEntity(double maxRange, double maxAngle) {
        double lowestAngle = maxAngle;
        Entity returnEntity = null;
        for (Entity entity : mc.theWorld.loadedEntityList) {
            if (!isValid(entity)) continue;
            double dist = entity.getDistanceToEntity(mc.thePlayer);
            double angle = Math.abs(new RotationUtil(entity).getRotation().playerYawDiff());
            if (dist < maxRange && angle <= lowestAngle) {
                returnEntity = entity;
                lowestAngle = angle;
            }
        }
        return returnEntity;
    }

    public static List<Entity> getEntities(double maxRange, double maxAngle) {
        List<Entity> entities = new ArrayList<>();;
        for (Entity entity : mc.theWorld.loadedEntityList) {
            if (!isValid(entity)) continue;
            double dist = entity.getDistanceToEntity(mc.thePlayer);
            double angle = Math.abs(new RotationUtil(entity).getRotation().playerYawDiff());
            if (dist < maxRange && angle <= maxAngle) {
                entities.add(entity);
            }
        }
        return entities;
    }

    public static List<Entity> sortEntitiesByDistance(List<Entity> entities) {
        entities.sort((o1, o2) -> {
            float f = o1.getDistanceToEntity(mc.thePlayer) - o2.getDistanceToEntity(mc.thePlayer);
            return f == 0 ? 0 : f < 0 ? -1 : 1;
        });
        return entities;
    }

    public static List<Entity> sortEntitiesByHealth(List<Entity> entities) {
        entities.sort((o1, o2) -> {
            float f = ((EntityLivingBase) o1).getHealth() - ((EntityLivingBase) o2).getHealth();
            return f == 0 ? 0 : f < 0 ? -1 : 1;
        });
        return entities;
    }

    public static List<Entity> sortEntitiesByAngle(List<Entity> entities, Rotation rotation) {
        entities.sort((o1, o2) -> {
            double d = Math.abs(new RotationUtil(o1).getRotation().getDiffS(rotation)) -
              Math.abs(new RotationUtil(o2).getRotation().getDiffS(rotation));
            return d == 0 ? 0 : d < 0 ? -1 : 1;
        });
        return entities;
    }

    public static boolean isInRange(Entity entity, double maxRange, double maxAngle){
        if (entity == null) return false;
        double dist = entity.getDistanceToEntity(mc.thePlayer);
        double angle = Math.abs(new RotationUtil(entity).getRotation().playerYawDiff());
        return dist < maxRange && angle < maxAngle;
    }
}
