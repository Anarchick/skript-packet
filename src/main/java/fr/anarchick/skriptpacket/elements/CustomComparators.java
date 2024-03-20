package fr.anarchick.skriptpacket.elements;

import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.util.slot.Slot;
import fr.anarchick.skriptpacket.Logging;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.skriptlang.skript.lang.comparator.Comparators;
import org.skriptlang.skript.lang.comparator.Relation;

public class CustomComparators {

    static {

        Logging.info("Register Skript comparators");

        Comparators.registerComparator(Block.class, Material.class,
                (o1, o2) -> Relation.get(o1.getType().equals(o2)));

        Comparators.registerComparator(ItemStack.class, Material.class,
                (o1, o2) -> Relation.get(o1.getType().equals(o2)));

        Comparators.registerComparator(ItemType.class, Material.class,
                (o1, o2) -> Relation.get(o1.getMaterial().equals(o2)));

        Comparators.registerComparator(Slot.class, Material.class,
                (o1, o2) -> {

                    if (o1.getItem() == null) {
                        return Relation.get(Material.AIR.equals(o2));
                    } else {
                        return Relation.get(o1.getItem().getType().equals(o2));
                    }

                });

    }

}
