package me.dave.followers.item;

import me.dave.chatcolorhandler.ChatColorHandler;
import me.dave.followers.Followers;
import me.dave.followers.data.FollowerHandler;
import me.dave.followers.exceptions.ObjectNameLockedException;
import me.dave.followers.gui.custom.BuilderGui;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class FollowerCreator implements Listener {
    private static final ItemStack creatorItem = getOrLoadCreatorItem();

    @EventHandler
    public void onPlayerInteract(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        if (!(event.getRightClicked() instanceof ArmorStand armorStand)) return;
        if (!heldItem.isSimilar(creatorItem)) return;
        event.setCancelled(true);
        if (!player.hasPermission("follower.admin.create")) {
            ChatColorHandler.sendMessage(player, Followers.configManager.getLangMessage("no-permissions"));
            return;
        }
        if (Followers.dataManager.getActiveArmorStandsSet().contains(armorStand.getUniqueId())) return;
        String armorStandName = armorStand.getCustomName();
        FollowerHandler.Builder followerBuilder = new FollowerHandler.Builder();
        if (armorStandName != null) {
            try {
                followerBuilder.setName(armorStandName);
            } catch (ObjectNameLockedException ignored) {}
        }

        EntityEquipment armorStandEquipment = armorStand.getEquipment();
        if (armorStandEquipment != null) {
            for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
                followerBuilder.setSlot(equipmentSlot, armorStandEquipment.getItem(equipmentSlot));
            }
        }

        BuilderGui builderGui = new BuilderGui(player, BuilderGui.Mode.CREATE, followerBuilder);
        builderGui.openInventory();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (player.isSneaking() && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            ItemStack heldItem = player.getInventory().getItemInMainHand();
            if (!heldItem.isSimilar(creatorItem)) return;
            event.setCancelled(true);

            if (!player.hasPermission("follower.admin.create")) {
                ChatColorHandler.sendMessage(player, Followers.configManager.getLangMessage("no-permissions"));
                return;
            }

            BuilderGui builderGui = new BuilderGui(player, BuilderGui.Mode.CREATE, new FollowerHandler.Builder());
            builderGui.openInventory();
        }
    }

    @EventHandler
    public void onPlayerManipulateArmorStand(PlayerArmorStandManipulateEvent event) {
        ItemStack item = event.getPlayerItem();
        if (!item.isSimilar(creatorItem)) return;
        event.setCancelled(true);
    }

    public static ItemStack getOrLoadCreatorItem() {
        if (creatorItem != null) return creatorItem;

        ItemStack item = new ItemStack(Material.TOTEM_OF_UNDYING);
        ItemMeta creatorMeta = item.getItemMeta();
        creatorMeta.setDisplayName(ChatColorHandler.translateAlternateColorCodes("<gradient:#FBDA00:#EEFDEA>Follower Creator"));
        creatorMeta.setLore(ChatColorHandler.translateAlternateColorCodes(Arrays.asList("&7Right Click an Armor Stand", "&7to turn it into a new Follower!")));
        creatorMeta.addEnchant(Enchantment.DURABILITY, 1, false);
        creatorMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(creatorMeta);
        return item;
    }
}