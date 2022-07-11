package dev.twoz.compactdrops.commands;

import dev.twoz.compactdrops.managers.ActionbarManager;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ComandoCompactar implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage("Apenas jogadores podem executar este comando.");
        }

        Player player = (Player) sender;

        int compactados = compactarItens(player.getInventory().getContents(), player.getInventory(), player);

        if(compactados == 0) {
            ActionbarManager actionbar1 = new ActionbarManager("§cVocê não possui drops para compactar.");
            actionbar1.sendToPlayer(player);
            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 3, 5);
            return true;
        }

        compactados += compactarItens(player.getInventory().getContents(), player.getInventory(), player);
        ActionbarManager actionbar2 = new ActionbarManager("§aForam compactados " + String.valueOf(compactados) + " itens.");
        actionbar2.sendToPlayer(player);
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 3, 5);

        return false;
    }

    public int compactarItens(ItemStack[] itens, PlayerInventory inv, Player player) {
        int compactados = 0;
        List<ItemStack> devolver = new ArrayList<>();

        for(ItemStack item : itens) {
            if(item == null || item.getType() == Material.AIR) continue;
            if(item.getAmount() < 9 || item.hasItemMeta()) continue;
            if(item.getType() == Material.INK_SACK && item.getDurability() != 4) continue;

            try {
                Drops drops = Drops.valueOf(item.getType().name());
                int quantidade = item.getAmount();
                int give = (int) quantidade / 9;
                int resto = quantidade - (give * 9);
                ItemStack block = new ItemStack(drops.getBlock(), give);
                inv.remove(item);

                ItemMeta meta = block.getItemMeta();
                meta.addEnchant(Enchantment.DURABILITY, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

                if(block.getType() == Material.LEATHER) {
                    meta.setDisplayName("§aCouro Compactado");
                } else if(block.getType() == Material.BONE) {
                    meta.setDisplayName("§aOsso Compactado");
                } else if(block.getType() == Material.ROTTEN_FLESH) {
                    meta.setDisplayName("§aCarne Podre Compactada");
                } else if(block.getType() == Material.RAW_BEEF) {
                    meta.setDisplayName("§aCarne Compactada");
                }

                block.setItemMeta(meta);
                inv.addItem(block);

                if(resto > 0) {
                    devolver.add(new ItemStack(item.getType(), resto, item.getDurability()));
                }

                compactados += give * 9;

            } catch (Throwable ex) {
                continue;
            }
        }

        for(ItemStack item : devolver) {
            for(ItemStack rest : devolver) {
                player.getWorld().dropItem(player.getLocation(), rest);
            }
        }

        return compactados;
    }

    enum Drops {

        LEATHER(Material.LEATHER),
        RAW_BEEF(Material.RAW_BEEF),
        ROTTEN_FLESH(Material.ROTTEN_FLESH),
        BONE(Material.BONE);


        private Material item;

        Drops(Material item) {
            this.item = item;
        }

        private Material getBlock() {
            return item;
        }
    }
}
