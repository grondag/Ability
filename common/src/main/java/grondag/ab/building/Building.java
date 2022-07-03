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

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import grondag.ab.Ability;
import grondag.ab.building.gui.UpdateStackPaintC2S;
import grondag.ab.ux.client.color.BlockColors;
import grondag.xm.api.block.XmBlockRegistry;
import grondag.xm.api.connect.species.SpeciesProperty;
import grondag.xm.api.item.XmItemRegistry;
import grondag.xm.api.modelstate.primitive.PrimitiveState;
import grondag.xm.api.modelstate.primitive.PrimitiveStateMutator;
import grondag.xm.api.paint.XmPaint;
import grondag.xm.api.primitive.simple.Cube;
import grondag.xm.api.texture.XmTextures;
import grondag.xm.api.texture.core.CoreTextures;
import grondag.xm.api.texture.tech.TechTextures;
import grondag.xm.api.texture.unstable.UnstableTextures;

public class Building {
	public static final XmPaint DEFAULT_PAINT = XmPaint.finder().texture(0, XmTextures.TILE_NOISE_LIGHT).textureColor(0, BlockColors.DEFAULT_WHITE_RGB).find();

	public static final PrimitiveState CUBE_DEFAULT_STATE = Cube.INSTANCE.newState().paintAll(DEFAULT_PAINT).releaseToImmutable();
	public static final FormedBlock DURACRETE_CUBE = Ability.blockNoItem("dcc", new FormedSpeciesBlock(FormedBlockSettings.duraCrete(), Building::cubeBe, CUBE_DEFAULT_STATE));
	public static final BlockEntityType<FormedBlockEntity> CUBE_BLOCK_ENTITY_TYPE = Ability.blockEntityType("dcc", Building::cubeBe, DURACRETE_CUBE);

	public static final PrimitiveStateMutator CUBE_STATE_FUNC = PrimitiveStateMutator.builder()
			.withJoin(SpeciesProperty.matchBlockAndSpecies())
			.withUpdate(SpeciesProperty.SPECIES_MODIFIER)
			.build();

	public static final FormedBlockItem DURACRETE_CUBE_ITEM = Ability.item("dcc_item", new FormedBlockItem(DURACRETE_CUBE, Ability.itemSettings()));


	private static FormedBlockEntity cubeBe(BlockPos pos, BlockState state) {
		return new FormedBlockEntity(CUBE_BLOCK_ENTITY_TYPE, pos, state, CUBE_DEFAULT_STATE, CUBE_STATE_FUNC);
	}

	//	public static final PrimitiveState RC_DEFAULT_STATE = RoundedColumn.INSTANCE.newState().paintAll(DEFAULT_PAINT).releaseToImmutable();
	//	public static final HsBlock DURACRETE_ROUND_COLUMN = REG.blockNoItem("dcrc", new HsBlock(HsBlockSettings.duraCrete(), HsBlocks::roundedColumnBe, RC_DEFAULT_STATE));
	//	public static final BlockEntityType<HsBlockEntity> ROUND_COLUMN_BLOCK_ENTITY_TYPE = REG.blockEntityType("dcrc", HsBlocks::roundedColumnBe, DURACRETE_ROUND_COLUMN);
	//	public static final HsBlockItem DURACRETE_ROUNDED_COLUMN_ITEM = REG.item("dcrc_item", new HsBlockItem(DURACRETE_ROUND_COLUMN, REG.itemSettings()));
	//
	//	private static HsBlockEntity roundedColumnBe() {
	//		return new HsBlockEntity(ROUND_COLUMN_BLOCK_ENTITY_TYPE, RC_DEFAULT_STATE, PrimitiveStateMutator.builder().build());
	//	}

	public static void initialize() {
		NetworkManager.registerReceiver(NetworkManager.c2s(), UpdateStackPaintC2S.IDENTIFIER, UpdateStackPaintC2S::accept);

		XmBlockRegistry.addBlock(DURACRETE_CUBE, FormedBlockEntity.STATE_ACCESS_FUNC);
		XmItemRegistry.addItem(DURACRETE_CUBE_ITEM, FormedBlockItem.FORMED_BLOCK_ITEM_MODEL_FUNCTION);
		//		XmBlockRegistry.addBlock(DURACRETE_ROUND_COLUMN, HsBlockEntity.STATE_ACCESS_FUNC, HsBlockItem.HS_ITEM_MODEL_FUNCTION);

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
