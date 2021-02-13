package com.domochevsky.quiverbow.weapons.base.fireshape;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.domochevsky.quiverbow.config.WeaponProperties;
import com.domochevsky.quiverbow.renderer.RenderBeam;
import com.domochevsky.quiverbow.util.Raytrace;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BeamFireShape implements FireShape
{
    public static final Pair<String, String>
        PIERCING = Pair.of("piercing", "How many entities and blocks the beam can pierce through"),
        MAX_RANGE = Pair.of("maxRange", "Maximum beam length");
    // RGB color encoded as an integer
    private final int colour;
    // Applies the effect of the beam
    private final IBeamEffect effect;

    public BeamFireShape(IBeamEffect effect, int colour)
    {
        this.colour = colour;
        this.effect = effect;
    }

    @Override
    public boolean fire(World world, EntityLivingBase shooter, ItemStack stack, WeaponProperties properties)
    {
        double maxRange = properties.getFloat(MAX_RANGE);
        Vec3d eyeVec = shooter.getPositionVector().addVector(0.0D, shooter.getEyeHeight(), 0.0D);
        Vec3d rayEndVec = eyeVec.add(shooter.getLookVec().scale(maxRange));

        int pierceCount = properties.has(BeamFireShape.PIERCING)
            ? properties.getInt(BeamFireShape.PIERCING) : 1;
        if (pierceCount > 1) // Piercing
        {
            List<RayTraceResult> results = Raytrace.all(new ArrayList<>(pierceCount),
                world, shooter, eyeVec, rayEndVec);
            if (results.isEmpty()) return RenderBeam.updateOrCreateBeam(shooter, maxRange, colour);
            // Sort the list in ascending order of distance from the shooter
            results.sort((resultA, resultB) ->
            {
                double distanceA = resultA.hitVec.squareDistanceTo(shooter.getPositionVector());
                double distanceB = resultB.hitVec.squareDistanceTo(shooter.getPositionVector());
                return Double.compare(distanceA, distanceB);
            });
            // Truncate the list to contain only the results that will be pierced/hit
            results = results.subList(0, Math.min(pierceCount, results.size()));
            for (RayTraceResult result : results)
                effect.apply(stack, world, shooter, result, properties);
            RayTraceResult finalResult = results.get(results.size() - 1);
            return RenderBeam.updateOrCreateBeam(shooter, finalResult.hitVec.lengthVector(), colour);
        }
        else // Non-piercing
        {
            RayTraceResult result = Raytrace.closest(world, shooter, eyeVec, rayEndVec);
            if (result != null)
            {
                effect.apply(stack, world, shooter, result, properties);
                return RenderBeam.updateOrCreateBeam(shooter, result.hitVec.lengthVector(), colour);
            }
            else
                return RenderBeam.updateOrCreateBeam(shooter, maxRange, colour);
        }
    }

    public static interface IBeamEffect
    {
        public void apply(ItemStack stack, World world, EntityLivingBase shooter, RayTraceResult target, WeaponProperties properties);
    }
}
