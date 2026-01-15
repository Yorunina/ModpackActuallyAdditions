package net.yorunina.ftbqaa.client;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.Plane;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import org.joml.Matrix4f;

public class RenderLiquid {
    private static boolean isNeighborSameFluid(FluidState state, FluidState neighbor) {
        return neighbor.getType().equals(state.getType());
    }

    private static boolean isFaceOccludedByState(BlockGetter blockGetter, Direction direction, float i, BlockPos pos, BlockState state) {
        if (state.canOcclude()) {
            VoxelShape voxelshape = Shapes.create((double)0.0F, (double)0.0F, (double)0.0F, (double)1.0F, (double)i, (double)1.0F);
            VoxelShape voxelshape1 = state.getShape(blockGetter, pos);
            return Shapes.blockOccudes(voxelshape, voxelshape1, direction);
        } else {
            return false;
        }
    }

    private static boolean isFaceOccludedByNeighbor(BlockGetter blockGetter, BlockPos pos, Direction direction, float i, BlockState state) {
        return isFaceOccludedByState(blockGetter, direction, i, pos.relative(direction), state);
    }

    private static boolean isFaceOccludedBySelf(BlockGetter blockGetter, BlockPos pos, BlockState state, Direction direction) {
        return isFaceOccludedByState(blockGetter, direction.getOpposite(), 1.0F, pos, state);
    }
    
    public static boolean shouldRenderFace(BlockAndTintGetter blockGetter, BlockPos pos, FluidState state, BlockState blockState, Direction direction, FluidState neighbor) {
        return !isFaceOccludedBySelf(blockGetter, pos, blockState, direction) && !isNeighborSameFluid(state, neighbor);
    }

    public static boolean tesselate(Matrix4f matrix, BlockAndTintGetter blockAndTintGetter, BlockPos pos, VertexConsumer vertexConsumer, FluidState fluidState) {
        BlockState state = blockAndTintGetter.getBlockState(pos);
        TextureAtlasSprite[] atextureatlassprite = ForgeHooksClient.getFluidSprites(blockAndTintGetter, pos, fluidState);
        int i = IClientFluidTypeExtensions.of(fluidState).getTintColor(fluidState, blockAndTintGetter, pos);
        float alpha = 0.55F * ((float)(i >> 24 & 255) / 255.0F);
        float f = (float)(i >> 16 & 255) / 255.0F;
        float f1 = (float)(i >> 8 & 255) / 255.0F;
        float f2 = (float)(i & 255) / 255.0F;
        BlockState blockstate = blockAndTintGetter.getBlockState(pos.relative(Direction.DOWN));
        FluidState fluidstate = blockstate.getFluidState();
        BlockState blockstate1 = blockAndTintGetter.getBlockState(pos.relative(Direction.UP));
        FluidState fluidstate1 = blockstate1.getFluidState();
        BlockState blockstate2 = blockAndTintGetter.getBlockState(pos.relative(Direction.NORTH));
        FluidState fluidstate2 = blockstate2.getFluidState();
        BlockState blockstate3 = blockAndTintGetter.getBlockState(pos.relative(Direction.SOUTH));
        FluidState fluidstate3 = blockstate3.getFluidState();
        BlockState blockstate4 = blockAndTintGetter.getBlockState(pos.relative(Direction.WEST));
        FluidState fluidstate4 = blockstate4.getFluidState();
        BlockState blockstate5 = blockAndTintGetter.getBlockState(pos.relative(Direction.EAST));
        FluidState fluidstate5 = blockstate5.getFluidState();
        boolean flag1 = !isNeighborSameFluid(fluidState, fluidstate1);
        boolean flag2 = shouldRenderFace(blockAndTintGetter, pos, fluidState, state, Direction.DOWN, fluidstate) && !isFaceOccludedByNeighbor(blockAndTintGetter, pos, Direction.DOWN, 0.8888889F, blockstate);
        boolean flag3 = shouldRenderFace(blockAndTintGetter, pos, fluidState, state, Direction.NORTH, fluidstate2);
        boolean flag4 = shouldRenderFace(blockAndTintGetter, pos, fluidState, state, Direction.SOUTH, fluidstate3);
        boolean flag5 = shouldRenderFace(blockAndTintGetter, pos, fluidState, state, Direction.WEST, fluidstate4);
        boolean flag6 = shouldRenderFace(blockAndTintGetter, pos, fluidState, state, Direction.EAST, fluidstate5);
        if (!flag1 && !flag2 && !flag6 && !flag5 && !flag3 && !flag4) {
            return false;
        } else {
            boolean flag7 = false;
            float f3 = blockAndTintGetter.getShade(Direction.DOWN, true);
            float f4 = blockAndTintGetter.getShade(Direction.UP, true);
            float f5 = blockAndTintGetter.getShade(Direction.NORTH, true);
            float f6 = blockAndTintGetter.getShade(Direction.WEST, true);
            Fluid fluid = fluidState.getType();
            float f11 = getHeight(blockAndTintGetter, fluid, pos, state, fluidState);
            float f7;
            float f8;
            float f9;
            float f10;
            if (f11 >= 1.0F) {
                f7 = 1.0F;
                f8 = 1.0F;
                f9 = 1.0F;
                f10 = 1.0F;
            } else {
                float f12 = getHeight(blockAndTintGetter, fluid, pos.m_122012_(), blockstate2, fluidstate2);
                float f13 = getHeight(blockAndTintGetter, fluid, pos.m_122019_(), blockstate3, fluidstate3);
                float f14 = getHeight(blockAndTintGetter, fluid, pos.m_122029_(), blockstate5, fluidstate5);
                float f15 = getHeight(blockAndTintGetter, fluid, pos.m_122024_(), blockstate4, fluidstate4);
                f7 = calculateAverageHeight(blockAndTintGetter, fluid, f11, f12, f14, pos.relative(Direction.NORTH).relative(Direction.EAST));
                f8 = calculateAverageHeight(blockAndTintGetter, fluid, f11, f12, f15, pos.relative(Direction.NORTH).relative(Direction.WEST));
                f9 = calculateAverageHeight(blockAndTintGetter, fluid, f11, f13, f14, pos.relative(Direction.SOUTH).relative(Direction.EAST));
                f10 = calculateAverageHeight(blockAndTintGetter, fluid, f11, f13, f15, pos.relative(Direction.SOUTH).relative(Direction.WEST));
            }

            float f17 = flag2 ? 0.001F : 0.0F;
            if (flag1 && !isFaceOccludedByNeighbor(blockAndTintGetter, pos, Direction.UP, Math.min(Math.min(f8, f10), Math.min(f9, f7)), blockstate1)) {
                flag7 = true;
                f8 -= 0.001F;
                f10 -= 0.001F;
                f9 -= 0.001F;
                f7 -= 0.001F;
                Vec3 vec3 = fluidState.getFlow(blockAndTintGetter, pos);
                float f20;
                float f21;
                float f22;
                float f23;
                float f24;
                float f25;
                float f18;
                float f19;
                if (vec3.x == 0.0F && vec3.y == 0.0F) {
                    TextureAtlasSprite textureatlassprite1 = atextureatlassprite[0];
                    f18 = textureatlassprite1.getU(0.0F);
                    f22 = textureatlassprite1.getV(0.0F);
                    f19 = f18;
                    f23 = textureatlassprite1.getV(16.0F);
                    f20 = textureatlassprite1.getU(16.0F);
                    f24 = f23;
                    f21 = f20;
                    f25 = f22;
                } else {
                    TextureAtlasSprite textureatlassprite = atextureatlassprite[1];
                    float f26 = (float)Mth.m_14136_(vec3.f_82481_, vec3.f_82479_) - ((float)Math.PI / 2F);
                    float f27 = Mth.m_14031_(f26) * 0.25F;
                    float f28 = Mth.m_14089_(f26) * 0.25F;
                    f18 = textureatlassprite.m_118367_((double)(8.0F + (-f28 - f27) * 16.0F));
                    f22 = textureatlassprite.m_118393_((double)(8.0F + (-f28 + f27) * 16.0F));
                    f19 = textureatlassprite.m_118367_((double)(8.0F + (-f28 + f27) * 16.0F));
                    f23 = textureatlassprite.m_118393_((double)(8.0F + (f28 + f27) * 16.0F));
                    f20 = textureatlassprite.m_118367_((double)(8.0F + (f28 + f27) * 16.0F));
                    f24 = textureatlassprite.m_118393_((double)(8.0F + (f28 - f27) * 16.0F));
                    f21 = textureatlassprite.m_118367_((double)(8.0F + (f28 - f27) * 16.0F));
                    f25 = textureatlassprite.m_118393_((double)(8.0F + (-f28 - f27) * 16.0F));
                }

                float f49 = (f18 + f19 + f20 + f21) / 4.0F;
                float f50 = (f22 + f23 + f24 + f25) / 4.0F;
                float f51 = (float)atextureatlassprite[0].m_174743_() / (atextureatlassprite[0].m_118410_() - atextureatlassprite[0].m_118409_());
                float f52 = (float)atextureatlassprite[0].m_174744_() / (atextureatlassprite[0].m_118412_() - atextureatlassprite[0].m_118411_());
                float f53 = 4.0F / Math.max(f52, f51);
                f18 = Mth.m_14179_(f53, f18, f49);
                f19 = Mth.m_14179_(f53, f19, f49);
                f20 = Mth.m_14179_(f53, f20, f49);
                f21 = Mth.m_14179_(f53, f21, f49);
                f22 = Mth.m_14179_(f53, f22, f50);
                f23 = Mth.m_14179_(f53, f23, f50);
                f24 = Mth.m_14179_(f53, f24, f50);
                f25 = Mth.m_14179_(f53, f25, f50);
                float f30 = f4 * f;
                float f31 = f4 * f1;
                float f32 = f4 * f2;
                vertex(matrix, vertexConsumer, 0.0F, f8, 0.0F, f30, f31, f32, alpha, f18, f22);
                vertex(matrix, vertexConsumer, 0.0F, f10, 1.0F, f30, f31, f32, alpha, f19, f23);
                vertex(matrix, vertexConsumer, 1.0F, f9, 1.0F, f30, f31, f32, alpha, f20, f24);
                vertex(matrix, vertexConsumer, 1.0F, f7, 0.0F, f30, f31, f32, alpha, f21, f25);
                if (fluidState.m_76171_(blockAndTintGetter, pos.m_7494_())) {
                    vertex(matrix, vertexConsumer, 0.0F, f8, 0.0F, f30, f31, f32, alpha, f18, f22);
                    vertex(matrix, vertexConsumer, 1.0F, f7, 0.0F, f30, f31, f32, alpha, f21, f25);
                    vertex(matrix, vertexConsumer, 1.0F, f9, 1.0F, f30, f31, f32, alpha, f20, f24);
                    vertex(matrix, vertexConsumer, 0.0F, f10, 1.0F, f30, f31, f32, alpha, f19, f23);
                }
            }

            if (flag2) {
                float f40 = atextureatlassprite[0].m_118409_();
                float f41 = atextureatlassprite[0].m_118410_();
                float f42 = atextureatlassprite[0].m_118411_();
                float f43 = atextureatlassprite[0].m_118412_();
                float f46 = f3 * f;
                float f47 = f3 * f1;
                float f48 = f3 * f2;
                vertex(matrix, vertexConsumer, 0.0F, f17, 1.0F, f46, f47, f48, alpha, f40, f43);
                vertex(matrix, vertexConsumer, 0.0F, f17, 0.0F, f46, f47, f48, alpha, f40, f42);
                vertex(matrix, vertexConsumer, 1.0F, f17, 0.0F, f46, f47, f48, alpha, f41, f42);
                vertex(matrix, vertexConsumer, 1.0F, f17, 1.0F, f46, f47, f48, alpha, f41, f43);
                flag7 = true;
            }

            for(Direction direction : Plane.HORIZONTAL) {
                float f44;
                float f45;
                float d3;
                float d4;
                float d5;
                float d6;
                boolean flag8;
                switch (direction) {
                    case NORTH:
                        f44 = f8;
                        f45 = f7;
                        d3 = 0.0F;
                        d5 = 1.0F;
                        d4 = 0.001F;
                        d6 = 0.001F;
                        flag8 = flag3;
                        break;
                    case SOUTH:
                        f44 = f9;
                        f45 = f10;
                        d3 = 1.0F;
                        d5 = 0.0F;
                        d4 = 0.999F;
                        d6 = 0.999F;
                        flag8 = flag4;
                        break;
                    case WEST:
                        f44 = f10;
                        f45 = f8;
                        d3 = 0.001F;
                        d5 = 0.001F;
                        d4 = 1.0F;
                        d6 = 0.0F;
                        flag8 = flag5;
                        break;
                    default:
                        f44 = f7;
                        f45 = f9;
                        d3 = 0.999F;
                        d5 = 0.999F;
                        d4 = 0.0F;
                        d6 = 1.0F;
                        flag8 = flag6;
                }

                if (flag8 && !isFaceOccludedByNeighbor(blockAndTintGetter, pos, direction, Math.max(f44, f45), blockAndTintGetter.getBlockState(pos.relative(direction)))) {
                    flag7 = true;
                    BlockPos blockpos = pos.relative(direction);
                    TextureAtlasSprite textureatlassprite2 = atextureatlassprite[1];
                    if (atextureatlassprite[2] != null && blockAndTintGetter.getBlockState(blockpos).shouldDisplayFluidOverlay(blockAndTintGetter, blockpos, fluidState)) {
                        textureatlassprite2 = atextureatlassprite[2];
                    }

                    float f54 = textureatlassprite2.m_118367_((double)0.0F);
                    float f55 = textureatlassprite2.m_118367_((double)8.0F);
                    float f33 = textureatlassprite2.m_118393_((double)((1.0F - f44) * 16.0F * 0.5F));
                    float f34 = textureatlassprite2.m_118393_((double)((1.0F - f45) * 16.0F * 0.5F));
                    float f35 = textureatlassprite2.m_118393_((double)8.0F);
                    float f36 = direction.m_122434_() == Axis.Z ? f5 : f6;
                    float f37 = f4 * f36 * f;
                    float f38 = f4 * f36 * f1;
                    float f39 = f4 * f36 * f2;
                    vertex(matrix, vertexConsumer, d3, f44, d4, f37, f38, f39, alpha, f54, f33);
                    vertex(matrix, vertexConsumer, d5, f45, d6, f37, f38, f39, alpha, f55, f34);
                    vertex(matrix, vertexConsumer, d5, f17, d6, f37, f38, f39, alpha, f55, f35);
                    vertex(matrix, vertexConsumer, d3, f17, d4, f37, f38, f39, alpha, f54, f35);
                    if (textureatlassprite2 != atextureatlassprite[2]) {
                        vertex(matrix, vertexConsumer, d3, f17, d4, f37, f38, f39, alpha, f54, f35);
                        vertex(matrix, vertexConsumer, d5, f17, d6, f37, f38, f39, alpha, f55, f35);
                        vertex(matrix, vertexConsumer, d5, f45, d6, f37, f38, f39, alpha, f55, f34);
                        vertex(matrix, vertexConsumer, d3, f44, d4, f37, f38, f39, alpha, f54, f33);
                    }
                }
            }

            return flag7;
        }
    }

    private static void vertex(Matrix4f matrix, VertexConsumer vertexBuilderIn, float x, float y, float z, float red, float green, float blue, float alpha, float u, float v) {
        vertexBuilderIn.m_252986_(matrix, x, y, z).m_85950_(red, green, blue, alpha).m_7421_(u, v).m_85969_(15728880).m_5601_(0.0F, 1.0F, 0.0F).m_5752_();
    }

    private static float calculateAverageHeight(BlockAndTintGetter p_203150_, Fluid p_203151_, float p_203152_, float p_203153_, float p_203154_, BlockPos p_203155_) {
        if (!(p_203154_ >= 1.0F) && !(p_203153_ >= 1.0F)) {
            float[] afloat = new float[2];
            if (p_203154_ > 0.0F || p_203153_ > 0.0F) {
                float f = getHeight(p_203150_, p_203151_, p_203155_);
                if (f >= 1.0F) {
                    return 1.0F;
                }

                addWeightedHeight(afloat, f);
            }

            addWeightedHeight(afloat, p_203152_);
            addWeightedHeight(afloat, p_203154_);
            addWeightedHeight(afloat, p_203153_);
            return afloat[0] / afloat[1];
        } else {
            return 1.0F;
        }
    }

    private static void addWeightedHeight(float[] p_203189_, float p_203190_) {
        if (p_203190_ >= 0.8F) {
            p_203189_[0] += p_203190_ * 10.0F;
            p_203189_[1] += 10.0F;
        } else if (p_203190_ >= 0.0F) {
            p_203189_[0] += p_203190_;
            int var10002 = p_203189_[1]++;
        }

    }

    private static float getHeight(BlockAndTintGetter p_203157_, Fluid p_203158_, BlockPos p_203159_) {
        BlockState blockstate = p_203157_.getBlockState(p_203159_);
        return getHeight(p_203157_, p_203158_, p_203159_, blockstate, blockstate.getFluidState());
    }

    private static float getHeight(BlockAndTintGetter p_203161_, Fluid p_203162_, BlockPos p_203163_, BlockState p_203164_, FluidState p_203165_) {
        if (p_203162_.m_6212_(p_203165_.getType())) {
            BlockState blockstate = p_203161_.getBlockState(p_203163_.m_7494_());
            return p_203162_.m_6212_(blockstate.getFluidState().getType()) ? 1.0F : p_203165_.m_76182_();
        } else {
            return !p_203164_.m_280296_() ? 0.0F : -1.0F;
        }
    }
}
