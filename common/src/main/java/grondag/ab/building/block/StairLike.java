/*
 * This file is part of Ability and is licensed to the project under
 * terms that are compatible with the GNU Lesser General Public License.
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership and licensing.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package grondag.ab.building.block;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.Vec3;

import grondag.ab.Ability;
import grondag.ab.building.block.base.FormedNonCubicBlock;
import grondag.ab.building.block.init.FormedBlockMaterial;
import grondag.ab.building.block.init.FormedBlockShape;
import grondag.ab.building.block.init.FormedBlockType;
import grondag.ab.building.block.init.ShapeType;
import grondag.xm.api.block.XmProperties;
import grondag.xm.api.modelstate.primitive.PrimitiveState;
import grondag.xm.api.modelstate.primitive.SimplePrimitiveStateMutator;
import grondag.xm.api.primitive.base.AbstractWedge;
import grondag.xm.api.primitive.simple.Wedge;
import grondag.xm.api.util.WorldHelper;
import grondag.xm.orientation.api.CubeRotation;
import grondag.xm.orientation.api.DirectionHelper;
import grondag.xm.orientation.api.FaceEdge;
import grondag.xm.orientation.api.HorizontalEdge;

public class StairLike extends FormedNonCubicBlock {
	public enum Shape {
		STRAIGHT,
		INSIDE_CORNER,
		OUTSIDE_CORNER;
	}

	public final Shape shape;

	public StairLike(FormedBlockType blockType, Shape shape) {
		super(blockType);
		this.shape = shape;
	}

	//UGLY: It was bad in the previous versions, too.  There must be a better model for this, but I haven't found it yet.
	//TODO: consider splitting this mess into a utility class for reuse - like it was in prior version
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockState result = super.getStateForPlacement(context);
		final Player player = context.getPlayer();
		final Direction onFace = context.getClickedFace().getOpposite();

		Direction bottomFace = Direction.DOWN;
		Direction backFace = Direction.SOUTH;

		if (player != null) {
			final Direction[] faces = context.getNearestLookingDirections();
			final int xIndex = faces[0].getAxis() == Axis.X ? 0 : (faces[1].getAxis() == Axis.X ? 1 : 2);
			final int yIndex = faces[0].getAxis() == Axis.Y ? 0 : (faces[1].getAxis() == Axis.Y ? 1 : 2);
			final int zIndex = faces[0].getAxis() == Axis.Z ? 0 : (faces[1].getAxis() == Axis.Z ? 1 : 2);

			final boolean modKey = Ability.modifyKey.isPressed(player);
			final boolean forceKey = Ability.forceKey.isPressed(player);

			final Vec3 hit = context.getClickLocation();

			if (shape == Shape.STRAIGHT) {
				if (modKey) {
					// horizontal stairs
					if (onFace.getAxis() != Axis.Y) {
						bottomFace = onFace;

						if (forceKey) {
							backFace = WorldHelper.closestAdjacentFace(onFace, hit.x, hit.y, hit.z);
						} else {
							if (onFace.getAxis() == Axis.X) {
								backFace = yIndex < zIndex ? faces[yIndex] : faces[zIndex];
							} else {
								backFace = yIndex < xIndex ? faces[yIndex] : faces[xIndex];
							}
						}
					} else {
						// placed on up or down
						backFace = onFace;
						bottomFace = forceKey
								? WorldHelper.closestAdjacentFace(onFace, hit.x, hit.y, hit.z)
										: player.getDirection();
					}
				} else {
					// vertical (normal)
					if (onFace.getAxis() == Axis.Y) {
						bottomFace = onFace;
						backFace = forceKey
								? WorldHelper.closestAdjacentFace(onFace, hit.x, hit.y, hit.z)
										: player.getDirection();
					} else {
						backFace = onFace;

						if (forceKey) {
							final Pair<Direction, Direction> pair = WorldHelper.closestAdjacentFaces(onFace, hit.x, hit.y, hit.z);
							bottomFace = pair.getLeft().getAxis() == Axis.Y ? pair.getLeft() : pair.getRight();
						} else {
							bottomFace = faces[yIndex];
						}
					}
				}
			} else {
				// CORNER
				if (modKey) {
					// Horizontal
					if (onFace.getAxis() == Axis.Y) {
						// placed on up or down
						if (forceKey) {
							final Pair<Direction, Direction> pair = WorldHelper.closestAdjacentFaces(onFace, (float) hit.x, (float) hit.y, (float) hit.z);
							bottomFace = pair.getLeft();
							final Direction rightFace = FaceEdge.fromWorld(onFace, bottomFace).counterClockwise().toWorld(bottomFace);
							backFace = rightFace == pair.getRight() ? onFace : pair.getRight();
						} else {
							bottomFace = player.getDirection();
							final int otherIndex = bottomFace.getAxis() == Axis.X ? zIndex : xIndex;
							final Direction otherFace = faces[otherIndex];
							final Direction rightFace = FaceEdge.fromWorld(onFace, bottomFace).counterClockwise().toWorld(bottomFace);
							backFace = rightFace == otherFace ? onFace : otherFace;
						}
					} else {
						// placed on bottom (horizontal) face directly
						bottomFace = onFace;

						if (forceKey) {
							final Pair<Direction, Direction> pair = WorldHelper.closestAdjacentFaces(onFace, (float) hit.x, (float) hit.y, (float) hit.z);
							boolean leftRightOrder = DirectionHelper.counterClockwise(pair.getLeft(), onFace.getAxis()) == pair.getRight();

							if (onFace.getAxisDirection() == AxisDirection.NEGATIVE) {
								leftRightOrder = !leftRightOrder;
							}

							backFace = leftRightOrder ? pair.getRight() : pair.getLeft();
						} else {
							final int firstIndex = onFace.getAxis() == Axis.X ? Math.min(yIndex, zIndex) : Math.min(yIndex, xIndex);
							final int secondIndex = onFace.getAxis() == Axis.X ? Math.max(yIndex, zIndex) : Math.max(yIndex, xIndex);
							final Direction firstFace = faces[firstIndex];
							final Direction secondFace = faces[secondIndex];
							final Direction rightFace = FaceEdge.fromWorld(firstFace, bottomFace).counterClockwise().toWorld(bottomFace);
							backFace = rightFace == secondFace ? firstFace : secondFace;
						}
					}
				} else {
					// vertical (normal)
					if (forceKey) {
						if (onFace.getAxis() == Axis.Y) {
							bottomFace = onFace;
							backFace = WorldHelper.closestAdjacentFace(onFace, hit.x, hit.y, hit.z);
						} else {
							final Pair<Direction, Direction> pair = WorldHelper.closestAdjacentFaces(onFace, hit.x, hit.y, hit.z);
							final boolean isLeftY = pair.getLeft().getAxis() == Axis.Y;
							bottomFace = isLeftY ? pair.getLeft() : pair.getRight();
							final Direction neighborFace = isLeftY ? pair.getRight() : pair.getLeft();
							final HorizontalEdge edge = HorizontalEdge.find(onFace, neighborFace);
							backFace = bottomFace == Direction.DOWN ? edge.left.face : edge.right.face;
						}
					} else {
						bottomFace = faces[yIndex];
						final HorizontalEdge edge = HorizontalEdge.fromRotation(player.getYRot());
						backFace = bottomFace == Direction.DOWN ? edge.left.face : edge.right.face;
					}
				}
			}
		}

		result = result.setValue(XmProperties.ROTATION, ObjectUtils.defaultIfNull(CubeRotation.find(bottomFace, backFace), CubeRotation.DOWN_WEST));
		return result;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(XmProperties.ROTATION);
	}

	@Deprecated
	@Override
	public BlockState rotate(BlockState state, Rotation rotation) {
		return state.setValue(XmProperties.ROTATION, state.getValue(XmProperties.ROTATION).rotate(rotation));
	}

	@Deprecated
	@Override
	public BlockState mirror(BlockState state, Mirror mirrir) {
		return state.rotate(Rotation.CLOCKWISE_180);
	}

	public static SimplePrimitiveStateMutator MODELSTATE_FROM_BLOCKSTATE = (modelState, blockState) -> {
		final Block rawBlock = blockState.getBlock();

		if (!(rawBlock instanceof final StairLike block)) {
			return modelState;
		}

		AbstractWedge.setCorner(block.shape != Shape.STRAIGHT, modelState);
		AbstractWedge.setInsideCorner(block.shape == Shape.INSIDE_CORNER, modelState);
		modelState.orientationIndex(blockState.getValue(XmProperties.ROTATION).ordinal());
		return modelState;
	};

	public static PrimitiveState createDefaultModelState(FormedBlockMaterial material, AbstractWedge primitive, StairLike.Shape shape, CubeRotation defaultRotation) {
		final var defaultState = primitive.newState().orientationIndex(defaultRotation.ordinal()).paintAll(material.paint());
		AbstractWedge.setCorner(shape != Shape.STRAIGHT, defaultState);
		AbstractWedge.setInsideCorner(shape == Shape.INSIDE_CORNER, defaultState);
		return defaultState;
	}

	public static FormedBlockShape createBlockShape(String name, AbstractWedge primitive, StairLike.Shape shape, CubeRotation defaultRotation) {
		return new FormedBlockShape("name",
			material -> StairLike.createDefaultModelState(material, Wedge.INSTANCE, shape, defaultRotation),
			StairLike.MODELSTATE_FROM_BLOCKSTATE,
			bt -> new StairLike(bt, shape), ShapeType.DYNAMIC_NON_CUBIC);
	}
}
