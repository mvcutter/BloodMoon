package com.github.hexosse.bloodmoon.entity;

import com.github.hexosse.bloodmoon.BloodMoon;
import com.github.hexosse.bloodmoon.configuration.WorldConfig;
import net.minecraft.server.v1_10_R1.EntityMonster;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.inventory.ItemStack;

public abstract class BloodMoonEntityMonster extends BloodMoonEntityLiving
{

	protected EntityMonster nmsEntity;

	public BloodMoonEntityMonster(BloodMoon plugin, EntityMonster nmsEntity, BloodMoonEntityType type)
	{
		super(plugin, nmsEntity, type);

		this.nmsEntity = nmsEntity;
	}

	protected Block getBreakableTargetBlock()
	{
		Location direction = nmsEntity.getGoalTarget().getBukkitEntity().getLocation().subtract(bukkitEntity.getLocation());

		double dx = direction.getX();
		double dz = direction.getY();

		int bdx = 0;
		int bdz = 0;

		if(Math.abs(dx) > Math.abs(dz))
		{
			bdx = (dx > 0) ? 1 : -1;
		}
		else
		{
			bdz = (dx > 0) ? 1 : -1;
		}

		return nmsEntity.world.getWorld().getBlockAt((int) Math.floor(nmsEntity.locX + bdx), (int) Math.floor(nmsEntity.locY), (int) Math.floor(nmsEntity.locZ + bdz));
	}

	protected void attemptBreakBlock(WorldConfig worldConfig, Block block)
	{
		Material type = block.getType();

		if(type != Material.AIR && worldConfig.FEATURE_BREAK_BLOCKS_BLOCKS.getValue().contains(type.name()))
		{
			Location location = block.getLocation();

			if(this.rand.nextInt(100) < 80)
			{
				if(this.rand.nextInt(100) < 50)
				{
					nmsEntity.world.getWorld().playSound(location, Sound.ENTITY_ZOMBIE_ATTACK_DOOR_WOOD, Math.min(this.rand.nextFloat() + 0.2f, 1.0f), 1.0f);
				}
			}
			else
			{
				EntityChangeBlockEvent event = new EntityChangeBlockEvent(bukkitEntity, block, Material.AIR, (byte) 0);
				plugin.getServer().getPluginManager().callEvent(event);

				if(!event.isCancelled())
				{
					nmsEntity.world.getWorld().playEffect(location, Effect.ZOMBIE_DESTROY_DOOR, 0);

					if(worldConfig.FEATURE_BREAK_BLOCKS_REALISTIC_DROP.getValue())
					{
						block.breakNaturally();
					}
					else
					{
						block.setType(Material.AIR);

						if(worldConfig.FEATURE_BREAK_BLOCKS_DROP_ITEMS.getValue())
						{
							nmsEntity.world.getWorld().dropItemNaturally(location, new ItemStack(type, 1, block.getData()));
						}
					}
				}
			}
		}
	}

}
