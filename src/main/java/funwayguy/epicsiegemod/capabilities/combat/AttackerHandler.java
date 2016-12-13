package funwayguy.epicsiegemod.capabilities.combat;

import java.util.ArrayList;
import java.util.Collections;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;

public class AttackerHandler implements IAttackerHandler
{
	private ArrayList<EntityLiving> attackers = new ArrayList<EntityLiving>();
	
	@Override
	public boolean canAttack(EntityLivingBase target, EntityLiving attacker)
	{
		return this.getAttackers() < 8;
	}
	
	@Override
	public void addAttacker(EntityLivingBase target, EntityLiving attacker)
	{
		if(attacker != null && !attacker.isDead && attacker.getAttackTarget() == target && !attackers.contains(attacker))
		{
			attackers.add(attacker);
		}
	}
	
	@Override
	public int getAttackers()
	{
		return attackers.size();
	}
	
	@Override
	public void updateAttackers(EntityLivingBase target)
	{
		Collections.sort(attackers, new EntityAINearestAttackableTarget.Sorter(target));
		
		for(int i = attackers.size() - 1; i >= 0; i--)
		{
			if(attackers.size() > 8)
			{
				attackers.remove(i);
				continue;
			}
			
			EntityLiving att = attackers.get(i);
			
			if(att == null || att.isDead || att.getAttackTarget() != target)
			{
				attackers.remove(i);
			}
		}
	}
}
