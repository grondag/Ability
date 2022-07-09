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

package grondag.ab.building;

import dev.architectury.networking.NetworkManager;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import grondag.ab.Ability;
import grondag.ab.building.gui.UpdateStackPaintC2S;
import grondag.xm.api.block.XmBlockRegistry;
import grondag.xm.api.item.XmItemRegistry;
import grondag.xm.api.texture.XmTextures;
import grondag.xm.api.texture.core.CoreTextures;
import grondag.xm.api.texture.tech.TechTextures;
import grondag.xm.api.texture.unstable.UnstableTextures;

public class Building {
	public static BlockEntityType<FormedBlockEntity> formedBlockEntityType;
	public static Block DEFAULT_ABILITY_BLOCK;

	public static FormedBlockEntity formedBlockEntity(BlockPos pos, BlockState state) {
		return new FormedBlockEntity(formedBlockEntityType, pos, state);
	}

	//	public static final PrimitiveState RC_DEFAULT_STATE = RoundedColumn.INSTANCE.newState().paintAll(DEFAULT_PAINT).releaseToImmutable();
	//	public static final HsBlock DURACRETE_ROUND_COLUMN = REG.blockNoItem("dcrc", new HsBlock(HsBlockSettings.duraCrete(), HsBlocks::roundedColumnBe, RC_DEFAULT_STATE));
	//	public static final BlockEntityType<HsBlockEntity> ROUND_COLUMN_BLOCK_ENTITY_TYPE = REG.blockEntityType("dcrc", HsBlocks::roundedColumnBe, DURACRETE_ROUND_COLUMN);
	//	public static final HsBlockItem DURACRETE_ROUNDED_COLUMN_ITEM = REG.item("dcrc_item", new HsBlockItem(DURACRETE_ROUND_COLUMN, REG.itemSettings()));
	//
	//	private static HsBlockEntity roundedColumnBe() {
	//		return new HsBlockEntity(ROUND_COLUMN_BLOCK_ENTITY_TYPE, RC_DEFAULT_STATE, PrimitiveStateMutator.builder().build());
	//	}

	private static void createBlockFamily(BuildingMaterial material) {
		final ObjectArrayList<Block> familyBlocks = new ObjectArrayList<>();
		FormedBlockShape.forEach(shape -> familyBlocks.add(createBlock(material, shape)));
		BLOCKS.add(familyBlocks);
	}

	private static Block createBlock(BuildingMaterial material, FormedBlockShape shape) {
		final String name = material.code() + "-" + shape.code;
		final Block block = Ability.blockNoItem(name, shape.createBlock(material));
		final BlockItem item = Ability.item(name, new FormedBlockItem(block, Ability.itemSettings()));
		item.registerBlocks(BlockItem.BY_BLOCK, item);

		return block;
	}

	private static final ObjectArrayList<ObjectArrayList<Block>> BLOCKS = new ObjectArrayList<>();

	public static void initialize() {
		BuildingMaterial.ALL.forEach(Building::createBlockFamily);

		DEFAULT_ABILITY_BLOCK = BLOCKS.get(0).get(0);

		final ObjectArrayList<Block> allBlocks = new ObjectArrayList<>();
		BLOCKS.forEach(allBlocks::addAll);
		formedBlockEntityType = Ability.blockEntityType("fbe", Building::formedBlockEntity, allBlocks.toArray(new FormedBlock[allBlocks.size()]));
		allBlocks.forEach(b -> XmBlockRegistry.addBlock(b, FormedBlockEntity.STATE_ACCESS_FUNC, FormedBlockItem.FORMED_BLOCK_ITEM_MODEL_FUNCTION));

		final Item BLOCK_PLACEMENT_TOOL = Ability.item("bpt", new BlockPlacementTool(Ability.itemSettings().stacksTo(1)));
		NetworkManager.registerReceiver(NetworkManager.c2s(), UpdateStackPaintC2S.IDENTIFIER, UpdateStackPaintC2S::accept);
		XmItemRegistry.addItem(BLOCK_PLACEMENT_TOOL, BlockPlacementTool.ITEM_MODEL_FUNCTION);

		XmTextures.TILE_NOISE_STRONG.use();
		XmTextures.TILE_NOISE_MODERATE.use();
		XmTextures.TILE_NOISE_LIGHT.use();
		XmTextures.TILE_NOISE_SUBTLE.use();
		XmTextures.TILE_NOISE_EXTREME.use();

		XmTextures.WHITE.use();
		XmTextures.BORDER_SINGLE_LINE.use();
		XmTextures.TILE_NOISE_BLUE_A.use();
		XmTextures.TILE_NOISE_BLUE_B.use();
		XmTextures.TILE_NOISE_BLUE.use();
		XmTextures.TILE_NOISE_EXTREME.use();

		TechTextures.DECAL_PLUS.use();
		TechTextures.DECAL_MINUS.use();


		UnstableTextures.BIGTEX_ASPHALT.use();
		UnstableTextures.BIGTEX_CRACKED_EARTH.use();
		UnstableTextures.BIGTEX_MARBLE.use();
		UnstableTextures.BIGTEX_ROUGH_ROCK.use();
		UnstableTextures.BIGTEX_WEATHERED_STONE.use();
		UnstableTextures.BIGTEX_WOOD.use();
		UnstableTextures.BIGTEX_WOOD_FLIP.use();
		UnstableTextures.BIGTEX_WORN_ASPHALT.use();
		UnstableTextures.BORDER_CAUTION.use();
		UnstableTextures.BORDER_CHECKERBOARD.use();
		UnstableTextures.BORDER_FILMSTRIP.use();
		UnstableTextures.BORDER_GROOVY_PINSTRIPES.use();
		UnstableTextures.BORDER_SIGNAL.use();
		UnstableTextures.BORDER_LOGIC.use();
		UnstableTextures.BORDER_GRITTY_CHECKERBOARD.use();
		UnstableTextures.BORDER_GRITTY_PINSTRIPE_GROOVES.use();
		UnstableTextures.BORDER_GRITTY_CHECKERBOARD.use();
		UnstableTextures.BORDER_GRITTY_SIGNAL.use();
		UnstableTextures.DECAL_DIAGONAL_BARS.use();

		UnstableTextures.DECAL_SKINNY_DIAGONAL_RIDGES.use();
		UnstableTextures.DECAL_THICK_DIAGONAL_CROSS_RIDGES.use();
		UnstableTextures.DECAL_THICK_DIAGONAL_RIDGES.use();
		UnstableTextures.DECAL_THIN_DIAGONAL_CROSS_RIDGES.use();
		UnstableTextures.DECAL_THIN_DIAGONAL_RIDGES.use();
		UnstableTextures.DECAL_THIN_DIAGONAL_CROSS_BARS.use();
		UnstableTextures.DECAL_THIN_DIAGONAL_BARS.use();
		UnstableTextures.DECAL_SKINNY_DIAGNAL_CROSS_BARS.use();
		UnstableTextures.DECAL_SKINNY_DIAGONAL_BARS.use();
		UnstableTextures.DECAL_DIAGONAL_CROSS_BARS.use();
		UnstableTextures.DECAL_DIAGONAL_BARS.use();
		UnstableTextures.DECAL_FAT_DIAGONAL_CROSS_BARS.use();
		UnstableTextures.DECAL_FAT_DIAGONAL_BARS.use();
		UnstableTextures.DECAL_DIAGONAL_CROSS_RIDGES.use();
		UnstableTextures.DECAL_DIAGONAL_RIDGES.use();
		UnstableTextures.DECAL_SKINNY_BARS.use();
		UnstableTextures.DECAL_FAT_BARS.use();
		UnstableTextures.DECAL_THICK_BARS.use();
		UnstableTextures.DECAL_THIN_BARS.use();
		UnstableTextures.DECAL_SKINNY_DIAGONAL_RIDGES_90.use();
		UnstableTextures.DECAL_THICK_DIAGONAL_RIDGES_90.use();
		UnstableTextures.DECAL_THIN_DIAGONAL_RIDGES_90.use();
		UnstableTextures.DECAL_THIN_DIAGONAL_BARS_90.use();
		UnstableTextures.DECAL_SKINNY_DIAGONAL_BARS_90.use();
		UnstableTextures.DECAL_DIAGONAL_BARS_90.use();
		UnstableTextures.DECAL_FAT_DIAGONAL_BARS_90.use();
		UnstableTextures.DECAL_DIAGONAL_RIDGES_90.use();
		UnstableTextures.DECAL_SKINNY_BARS_90.use();
		UnstableTextures.DECAL_FAT_BARS_90.use();
		UnstableTextures.DECAL_THICK_BARS_90.use();
		UnstableTextures.DECAL_THIN_BARS_90.use();
		UnstableTextures.DECAL_SKINNY_DIAGONAL_RIDGES_RANDOM.use();
		UnstableTextures.DECAL_THICK_DIAGONAL_RIDGES_RANDOM.use();
		UnstableTextures.DECAL_THIN_DIAGONAL_RIDGES_RANDOM.use();
		UnstableTextures.DECAL_THIN_DIAGONAL_BARS_RANDOM.use();
		UnstableTextures.DECAL_SKINNY_DIAGONAL_BARS_RANDOM.use();
		UnstableTextures.DECAL_DIAGONAL_BARS_RANDOM.use();
		UnstableTextures.DECAL_FAT_DIAGONAL_BARS_RANDOM.use();
		UnstableTextures.DECAL_DIAGONAL_RIDGES_RANDOM.use();
		UnstableTextures.DECAL_SKINNY_BARS_RANDOM.use();
		UnstableTextures.DECAL_FAT_BARS_RANDOM.use();
		UnstableTextures.DECAL_THICK_BARS_RANDOM.use();
		UnstableTextures.DECAL_THIN_BARS_RANDOM.use();

		UnstableTextures.DECAL_SOFT_SKINNY_DIAGONAL_RIDGES.use();
		UnstableTextures.DECAL_SOFT_THICK_DIAGONAL_CROSS_RIDGES.use();
		UnstableTextures.DECAL_SOFT_THICK_DIAGONAL_RIDGES.use();
		UnstableTextures.DECAL_SOFT_THIN_DIAGONAL_CROSS_RIDGES.use();
		UnstableTextures.DECAL_SOFT_THIN_DIAGONAL_RIDGES.use();
		UnstableTextures.DECAL_SOFT_THIN_DIAGONAL_CROSS_BARS.use();
		UnstableTextures.DECAL_SOFT_THIN_DIAGONAL_BARS.use();
		UnstableTextures.DECAL_SOFT_SKINNY_DIAGNAL_CROSS_BARS.use();
		UnstableTextures.DECAL_SOFT_SKINNY_DIAGONAL_BARS.use();
		UnstableTextures.DECAL_SOFT_DIAGONAL_CROSS_BARS.use();
		UnstableTextures.DECAL_SOFT_DIAGONAL_BARS.use();
		UnstableTextures.DECAL_SOFT_FAT_DIAGONAL_CROSS_BARS.use();
		UnstableTextures.DECAL_SOFT_FAT_DIAGONAL_BARS.use();
		UnstableTextures.DECAL_SOFT_DIAGONAL_CROSS_RIDGES.use();
		UnstableTextures.DECAL_SOFT_DIAGONAL_RIDGES.use();
		UnstableTextures.DECAL_SOFT_SKINNY_DIAGONAL_RIDGES_90.use();
		UnstableTextures.DECAL_SOFT_THICK_DIAGONAL_RIDGES_90.use();
		UnstableTextures.DECAL_SOFT_THIN_DIAGONAL_RIDGES_90.use();
		UnstableTextures.DECAL_SOFT_THIN_DIAGONAL_BARS_90.use();
		UnstableTextures.DECAL_SOFT_SKINNY_DIAGONAL_BARS_90.use();
		UnstableTextures.DECAL_SOFT_DIAGONAL_BARS_90.use();
		UnstableTextures.DECAL_SOFT_FAT_DIAGONAL_BARS_90.use();
		UnstableTextures.DECAL_SOFT_DIAGONAL_RIDGES_90.use();

		UnstableTextures.DECAL_SOFT_SKINNY_DIAGONAL_RIDGES_RANDOM.use();
		UnstableTextures.DECAL_SOFT_THICK_DIAGONAL_RIDGES_RANDOM.use();
		UnstableTextures.DECAL_SOFT_THIN_DIAGONAL_RIDGES_RANDOM.use();
		UnstableTextures.DECAL_SOFT_THIN_DIAGONAL_BARS_RANDOM.use();
		UnstableTextures.DECAL_SOFT_SKINNY_DIAGONAL_BARS_RANDOM.use();
		UnstableTextures.DECAL_SOFT_DIAGONAL_BARS_RANDOM.use();
		UnstableTextures.DECAL_SOFT_FAT_DIAGONAL_BARS_RANDOM.use();
		UnstableTextures.DECAL_SOFT_DIAGONAL_RIDGES_RANDOM.use();

		UnstableTextures.TILE_DOTS.use();
		UnstableTextures.TILE_DOTS_SUBTLE.use();
		UnstableTextures.TILE_DOTS_INVERSE.use();
		UnstableTextures.TILE_DOTS_INVERSE_SUBTLE.use();

		CoreTextures.TILE_COBBLE.use();
		CoreTextures.BORDER_COBBLE.use();
		CoreTextures.BORDER_SMOOTH_BLEND.use();
		CoreTextures.BORDER_WEATHERED_BLEND.use();
		CoreTextures.BORDER_BEVEL.use();
		CoreTextures.BORDER_WEATHERED_LINE.use();
		CoreTextures.BIGTEX_SANDSTONE.use();
		CoreTextures.BIGTEX_RAMMED_EARTH.use();
		CoreTextures.BIGTEX_COBBLE_SQUARES.use();
		CoreTextures.BIGTEX_GRANITE.use();
		CoreTextures.BIGTEX_SNOW.use();
	}
}
