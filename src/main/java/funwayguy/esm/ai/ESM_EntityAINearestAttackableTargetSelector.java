package funwayguy.esm.ai;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

class ESM_EntityAINearestAttackableTargetSelector implements IEntitySelector
{
    final IEntitySelector field_111103_c;

    final ESM_EntityAINearestAttackableTarget field_111102_d;

    ESM_EntityAINearestAttackableTargetSelector(ESM_EntityAINearestAttackableTarget par1EntityAINearestAttackableTarget, IEntitySelector par2IEntitySelector)
    {
        this.field_111102_d = par1EntityAINearestAttackableTarget;
        this.field_111103_c = par2IEntitySelector;
    }

    /**
     * Return whether the specified entity is applicable to this filter.
     */
    public boolean isEntityApplicable(Entity par1Entity)
    {
        return !(par1Entity instanceof EntityLivingBase) ? false : (this.field_111103_c != null && !this.field_111103_c.isEntityApplicable(par1Entity) ? false : this.field_111102_d.isSuitableTarget((EntityLivingBase)par1Entity, false));
    }
}