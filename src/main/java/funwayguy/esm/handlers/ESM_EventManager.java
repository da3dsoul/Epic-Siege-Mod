package funwayguy.esm.handlers;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import cpw.mods.fml.common.FMLCommonHandler;
import funwayguy.esm.core.ESM_Settings;
import funwayguy.esm.handlers.entities.ESM_BlazeHandler;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIArrowAttack;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITaskEntry;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumStatus;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.ChunkCoordinates;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.event.world.WorldEvent.Unload;

public class ESM_EventManager
{	
	@ForgeSubscribe
	public void onEntityJoinWorld(EntityJoinWorldEvent event)
	{
		if(!FMLCommonHandler.instance().getEffectiveSide().isServer())
		{
			return;
		}
		
		if(event.world.provider.worldObj == null || (ESM_Settings.Apocalypse && !(event.entity instanceof EntityZombie || event.entity instanceof EntityPlayer || (event.entity instanceof EntityEnderman && ESM_Settings.EndermanMode == "Slender"))))
		{
			event.setCanceled(true);
			return;
		}
		
		if(event.entity instanceof EntityLiving)
		{
			updateEntityAwareness((EntityLiving)event.entity);
			if(event.entity.getEntityData().getBoolean("ESM_MODIFIED"))
			{
				return;
			} else
			{
				event.entity.getEntityData().setBoolean("ESM_MODIFIED", true);
			}
		}
		
		if(event.entity instanceof EntityCreeper)
		{
			if(ESM_Settings.CreeperPowered && ESM_Settings.CreeperPoweredRarity <= 0)
			{
				((EntityCreeper)event.entity).getDataWatcher().updateObject(17, Byte.valueOf((byte)1));
				return;
			} else if(ESM_Settings.CreeperPowered && ESM_Settings.CreeperPoweredRarity > 0)
			{
				if(event.world.rand.nextInt(ESM_Settings.CreeperPoweredRarity) == 0)
				{
					((EntityCreeper)event.entity).getDataWatcher().updateObject(17, Byte.valueOf((byte)1));
					return;
				} else
				{
					return;
				}
			}
		} else if(event.entity instanceof EntitySpider)
		{
			if(event.entity.riddenByEntity == null)
			{
				if(ESM_Settings.SpiderBombs && ESM_Settings.SpiderBombRarity <= 0)
				{
					EntityCreeper passenger = new EntityCreeper(event.entity.worldObj);
					passenger.setLocationAndAngles(event.entity.posX, event.entity.posY, event.entity.posZ, event.entity.rotationYaw, 0.0F);
					passenger.onSpawnWithEgg((EntityLivingData)null);
					event.entity.worldObj.spawnEntityInWorld(passenger);
					passenger.mountEntity(event.entity);
				} else if(ESM_Settings.SpiderBombs && ESM_Settings.SpiderBombRarity > 0)
				{
					if(event.world.rand.nextInt(ESM_Settings.SpiderBombRarity) == 0)
					{
						EntityCreeper passenger = new EntityCreeper(event.entity.worldObj);
						passenger.setLocationAndAngles(event.entity.posX, event.entity.posY, event.entity.posZ, event.entity.rotationYaw, 0.0F);
						passenger.onSpawnWithEgg((EntityLivingData)null);
						event.entity.worldObj.spawnEntityInWorld(passenger);
						passenger.mountEntity(event.entity);
					}
				}
	            return;
			}
		} else if(event.entity instanceof EntitySkeleton)
		{
			if(((EntitySkeleton)event.entity).getSkeletonType() == 0)
			{
				if(ESM_Settings.WitherSkeletons && ESM_Settings.WitherSkeletonRarity <= 0)
				{
					event.setCanceled(true);
					EntitySkeleton newSkeleton = new EntitySkeleton(event.world);
					newSkeleton.setLocationAndAngles(event.entity.posX, event.entity.posY, event.entity.posZ, event.entity.rotationYaw, 0.0F);
					newSkeleton.setSkeletonType(1);
					newSkeleton.setCurrentItemOrArmor(0, new ItemStack(Item.swordStone));
					newSkeleton.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(4.0D);
					newSkeleton.setCombatTask();
					newSkeleton.getEntityData().setBoolean("ESM_MODIFIED", true);
					event.world.spawnEntityInWorld(newSkeleton);
				} else if(ESM_Settings.WitherSkeletons && ESM_Settings.WitherSkeletonRarity > 0)
				{
					if(event.world.rand.nextInt(ESM_Settings.WitherSkeletonRarity) == 0)
					{
						event.setCanceled(true);
						EntitySkeleton newSkeleton = new EntitySkeleton(event.world);
						newSkeleton.setLocationAndAngles(event.entity.posX, event.entity.posY, event.entity.posZ, event.entity.rotationYaw, 0.0F);
						newSkeleton.setSkeletonType(1);
						newSkeleton.setCurrentItemOrArmor(0, new ItemStack(Item.swordStone));
						newSkeleton.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(4.0D);
						newSkeleton.setCombatTask();
						newSkeleton.getEntityData().setBoolean("ESM_MODIFIED", true);
						event.world.spawnEntityInWorld(newSkeleton);
					}
				} else
				{
					event.entity.getEntityData().setString("ESM_TASK_ID", event.entity.getUniqueID().toString() + ",NULL");
				}
			}
		} else if(event.entity instanceof EntityZombie && !ESM_Settings.Apocalypse)
		{
			switch(event.world.rand.nextInt(3))
			{
				case 0:
				{
					if(ESM_Settings.GhastSpawn && ESM_Settings.GhastRarity <= 0 && event.world.canBlockSeeTheSky((int)event.entity.posX, (int)event.entity.posY, (int)event.entity.posZ) && event.entity.posY >= 64)
					{
						event.setCanceled(true);
						EntityGhast newGhast = new EntityGhast(event.world);
						newGhast.setLocationAndAngles(event.entity.posX, event.entity.posY + 32, event.entity.posZ, event.entity.rotationYaw, 0.0F);
						event.world.spawnEntityInWorld(newGhast);
					} else if(ESM_Settings.GhastSpawn && ESM_Settings.GhastRarity > 0 && event.world.canBlockSeeTheSky((int)event.entity.posX, (int)event.entity.posY, (int)event.entity.posZ) && event.entity.posY >= 64)
					{
						if(event.world.rand.nextInt(ESM_Settings.GhastRarity) == 0)
						{
							event.setCanceled(true);
							EntityGhast newGhast = new EntityGhast(event.world);
							newGhast.setLocationAndAngles(event.entity.posX, event.entity.posY, event.entity.posZ, event.entity.rotationYaw, 0.0F);
							event.world.spawnEntityInWorld(newGhast);
						}
					}
					break;
				}
				
				case 1:
				{
					if(ESM_Settings.BlazeSpawn && ESM_Settings.BlazeRarity <= 0)
					{
						event.setCanceled(true);
						EntityBlaze newBlaze = new EntityBlaze(event.world);
						newBlaze.setLocationAndAngles(event.entity.posX, event.entity.posY, event.entity.posZ, event.entity.rotationYaw, 0.0F);
						newBlaze.getEntityData().setBoolean("ESM_MODIFIED", true);
						event.world.spawnEntityInWorld(newBlaze);
					} else if(ESM_Settings.BlazeSpawn && ESM_Settings.BlazeRarity > 0)
					{
						if(event.world.rand.nextInt(ESM_Settings.BlazeRarity) == 0)
						{
							event.setCanceled(true);
							EntityBlaze newBlaze = new EntityBlaze(event.world);
							newBlaze.setLocationAndAngles(event.entity.posX, event.entity.posY, event.entity.posZ, event.entity.rotationYaw, 0.0F);
							newBlaze.getEntityData().setBoolean("ESM_MODIFIED", true);
							event.world.spawnEntityInWorld(newBlaze);
						}
					}
					break;
				}
			}
		} else if(event.entity instanceof EntityArrow)
		{
			EntityArrow arrow = (EntityArrow)event.entity;
			if(arrow.shootingEntity instanceof EntitySkeleton && !arrow.getEntityData().getBoolean("ESM_TAGGED"))
			{
				EntitySkeleton shooter = (EntitySkeleton)arrow.shootingEntity;
				EntityLivingBase target = shooter.getAttackTarget();
				replaceArrowAttack(shooter, target, arrow.getDamage());
				event.setCanceled(true);
			}
		} else if(event.entity instanceof EntityBlaze)
		{
			ESM_BlazeHandler.onEntityJoinWorld((EntityBlaze)event.entity);
		} else if(event.entity instanceof EntitySmallFireball)
		{
			EntitySmallFireball fireball = (EntitySmallFireball)event.entity;
			if(fireball.shootingEntity instanceof EntityBlaze)
			{
				fireball.shootingEntity.getEntityData().setInteger("ESM_FIREBALLS", fireball.shootingEntity.getEntityData().getInteger("ESM_FIREBALLS") + 1);
			}
		}
	}
	
	public static void replaceArrowAttack(EntitySkeleton shooter, EntityLivingBase par1EntityLivingBase, double par2)
	{
    	EntityArrow entityarrow;
        double targetDist = shooter.getDistance(par1EntityLivingBase.posX, par1EntityLivingBase.boundingBox.minY, par1EntityLivingBase.posZ);
    	
    	if(ESM_Settings.SkeletonDistance == 0)
    	{
    		entityarrow = new EntityArrow(shooter.worldObj, shooter, par1EntityLivingBase, 1.6F, ESM_Settings.SkeletonAccuracy);
    	} else
    	{
    		entityarrow = new EntityArrow(shooter.worldObj, shooter, par1EntityLivingBase, (float)((0.00013*(targetDist)*(targetDist)) + (0.02*targetDist) + 1.25), ESM_Settings.SkeletonAccuracy);
    	}
    	
    	entityarrow.getEntityData().setBoolean("ESM_TAGGED", true);
    	
        //EntityArrow entityarrow = new EntityArrow(shooter.worldObj, shooter, par1EntityLivingBase, 1.6F, (float)(14 - shooter.worldObj.difficultySetting * 4));
        int i = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, shooter.getHeldItem());
        int j = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, shooter.getHeldItem());
        entityarrow.setDamage(par2);

        if (i > 0)
        {
            entityarrow.setDamage(entityarrow.getDamage() + (double)i * 0.5D + 0.5D);
        }

        if (j > 0)
        {
            entityarrow.setKnockbackStrength(j);
        }

        if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, shooter.getHeldItem()) > 0 || shooter.getSkeletonType() == 1)
        {
            entityarrow.setFire(100);
        }

        shooter.playSound("random.bow", 1.0F, 1.0F / (shooter.getRNG().nextFloat() * 0.4F + 0.8F));
        shooter.worldObj.spawnEntityInWorld(entityarrow);
	}
	
	@ForgeSubscribe
	public void onEntityDeath(LivingDeathEvent event)
	{
		if(event.entity.worldObj.isRemote)
		{
			return;
		}
		
		if(event.entity instanceof EntityPlayer)
		{
			if(event.source.getSourceOfDamage() instanceof EntityZombie && ESM_Settings.ZombieInfectious)
			{
				EntityZombie zombie = new EntityZombie(event.entity.worldObj);
				zombie.setPosition(event.entity.posX, event.entity.posY, event.entity.posZ);
				zombie.setCanPickUpLoot(true);
				zombie.setCustomNameTag(event.entity.getEntityName());
				zombie.getEntityData().setBoolean("ESM_MODIFIED", true);
				event.entity.worldObj.spawnEntityInWorld(zombie);
				
				System.out.println(event.entity.getEntityName() + " was infected!");
			}
		}
	}
	
	public static void updateEntityAwareness(EntityLivingBase entityLivingBase)
	{
		EntityLiving entityLiving;
		
		if(entityLivingBase instanceof EntityLiving)
		{
			entityLiving = (EntityLiving)entityLivingBase;
		} else
		{
			return;
		}
		
		if(entityLiving.getNavigator() != null)
		{
			if(entityLiving instanceof EntityZombie && ESM_Settings.Awareness < 40)
			{
				entityLiving.getEntityAttribute(SharedMonsterAttributes.followRange).setAttribute(40);
			} else
			{
				entityLiving.getEntityAttribute(SharedMonsterAttributes.followRange).setAttribute(ESM_Settings.Awareness);
			}
		} else
		{
			//entityLiving.setAttackTarget(ESM_Settings.GetNearestValidTarget(entityLiving));
		}
	}
	
	@ForgeSubscribe
	public void onLivingUpdate(LivingUpdateEvent event)
	{
		if(event.entity.worldObj.isRemote)
		{
			return;
		}
		
		if(ESM_Settings.Apocalypse && !(event.entityLiving instanceof EntityPlayer || event.entityLiving instanceof EntityZombie || (event.entityLiving instanceof EntityEnderman && ESM_Settings.EndermanMode.equals("Slender"))))
		{
			event.entityLiving.setDead();
			return;
		}
		
		updateEntityAwareness(event.entityLiving);
		
		if(event.entityLiving instanceof EntityCreeper && ESM_Settings.CreeperBreaching)
		{
			EntityCreeper creeper = (EntityCreeper)event.entity;
			double detDist = 3.0D;
			if(creeper.getPowered())
			{
				detDist = 6.0D;
			}
			
			List targetList = creeper.worldObj.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getAABBPool().getAABB(creeper.posX - detDist, creeper.posY - detDist, creeper.posZ - detDist, creeper.posX + detDist, creeper.posY + detDist, creeper.posZ + detDist));
			
			if(!targetList.isEmpty())
			{
				Iterator targets = targetList.iterator();
				EntityPlayer closestTarget = null;
				float dist = 6.0F;
				
				while(targets.hasNext())
				{
					EntityPlayer testing = (EntityPlayer)targets.next();
					if(creeper.getAttackTarget() == testing && !creeper.canEntityBeSeen(testing))
					{
						closestTarget = testing;
					}
					/*EntityPlayer testing = (EntityPlayer)targets.next();
					if(testing.capabilities.isCreativeMode)
					{
						continue;
					} else if(closestTarget == null && !creeper.canEntityBeSeen(testing))
					{
						closestTarget = testing;
						dist = creeper.getDistanceToEntity(testing);
					} else
					{
						if(creeper.getDistanceSqToEntity(testing) < dist && !creeper.canEntityBeSeen(testing))
						{
							closestTarget = testing;
							dist = creeper.getDistanceToEntity(testing);
						}
					}*/
				}
				
				if(closestTarget != null && dist < (float)detDist)
				{
					ESM_ServerScheduledTickHandler.registerNewBreach(creeper);
					
				}
			}
		} else if(event.entityLiving instanceof EntitySkeleton)
		{
			EntitySkeleton skeleton = (EntitySkeleton)event.entityLiving;
			
			List<EntityAITaskEntry> taskList = skeleton.tasks.taskEntries;
			
			if(!skeleton.getEntityData().getString("ESM_TASK_ID").equals(skeleton.getUniqueID().toString() + "," + ESM_Settings.SkeletonDistance) && skeleton.getSkeletonType() == 0)
			{
				for(int i = 0; i < taskList.size(); i++)
				{
					EntityAIBase entry = taskList.get(i).action;
					if(entry instanceof EntityAIArrowAttack)
					{
						//taskList.remove(i);
						skeleton.tasks.removeTask(entry);
						skeleton.tasks.addTask(4, new EntityAIArrowAttack(skeleton, 1.0D, 20, 60, (float)ESM_Settings.SkeletonDistance));
						skeleton.getEntityData().setString("ESM_TASK_ID", skeleton.getUniqueID().toString() + "," + ESM_Settings.SkeletonDistance);
						break;
					}
				}
			}
		} else if(event.entity instanceof EntityBlaze)
		{
			EntityBlaze blaze = (EntityBlaze)event.entity;
			
			ESM_BlazeHandler.onLivingUpdate(blaze);
		}
		return;
	}

	@ForgeSubscribe
	public void onPlayerSleepInBed(PlayerSleepInBedEvent event)
	{
		if(ESM_Settings.AllowSleep)
		{
			return;
		}
		
		if (!event.entityPlayer.worldObj.isRemote)
        {
            if (event.entityPlayer.isPlayerSleeping() || !event.entityPlayer.isEntityAlive())
            {
                return;
            }
            
            if (!event.entityPlayer.worldObj.provider.canRespawnHere())
            {
                return;
            }
            
            if (event.entityPlayer.worldObj.isDaytime())
            {
                return;
            }
            
            if (Math.abs(event.entityPlayer.posX - (double)event.x) > 3.0D || Math.abs(event.entityPlayer.posY - (double)event.y) > 2.0D || Math.abs(event.entityPlayer.posZ - (double)event.z) > 3.0D)
            {
                return;
            }
            double d0 = 8.0D;
            double d1 = 5.0D;
            List list = event.entityPlayer.worldObj.getEntitiesWithinAABB(EntityMob.class, AxisAlignedBB.getAABBPool().getAABB((double)event.x - d0, (double)event.y - d1, (double)event.z - d0, (double)event.x + d0, (double)event.y + d1, (double)event.z + d0));
            
	        if (!list.isEmpty())
            {
                return;
            }
        }
	    
	    event.result = EnumStatus.OTHER_PROBLEM;
		
	    if (event.entityPlayer.isRiding())
	    {
	        event.entityPlayer.mountEntity((Entity)null);
	    }
	    
		event.entityPlayer.setSpawnChunk(new ChunkCoordinates(event.x,event.y,event.z), false);
		event.entityPlayer.sendChatToPlayer(ChatMessageComponent.createFromText("Spawnpoint set"));
	}
	
	@ForgeSubscribe
	public void onWorldLoad(Load event)
	{
		if(!event.world.isRemote && ESM_Settings.currentWorlds == null)
		{
			MinecraftServer mc = MinecraftServer.getServer();
			ESM_Settings.currentWorlds = mc.worldServers;
			ESM_Settings.currentWorldConfig = new File(mc.getFile("ESM_Options").getAbsolutePath(), mc.getFolderName() + ".ESM.cfg");
			ESM_Settings.LoadConfig();
		}
	}
	
	@ForgeSubscribe
	public void onWorldUnload(Unload event)
	{
		ESM_Settings.currentWorlds = null;
	}
}