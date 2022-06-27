/*
 * This file is part of Wit and is licensed to the project under
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

package grondag.wit.client;

import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import dev.architectury.registry.menu.MenuRegistry;
import dev.architectury.registry.menu.MenuRegistry.ScreenFactory;
import org.lwjgl.glfw.GLFW;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import io.vram.frex.api.world.BlockEntityRenderData;
import io.vram.modkeys.api.client.ModKeyBinding;

import grondag.fluidity.base.synch.DiscreteStorageServerDelegate;
import grondag.wit.Wit;
import grondag.wit.init.BinBlocks;
import grondag.wit.init.CrateBlocks;
import grondag.wit.init.MenuTypes;
import grondag.wit.init.TankBlocks;
import grondag.wit.ux.WitBaseContainerMenu;

public abstract class WitClient {
	private WitClient() { }

	public static void initialize() {
		registerBeType(CrateBlocks.slottedCrateBlockEntityType(), d -> new StorageBlockRenderer<>(d));
		registerBeType(CrateBlocks.crateBlockEntityType(), d -> new StorageBlockRenderer<>(d));
		registerBeType(BinBlocks.binBlockEntityTypeX1(), d -> new BinBlockRenderer(d, 1));
		registerBeType(BinBlocks.binBlockEntityTypeX2(), d -> new BinBlockRenderer(d, 2));
		registerBeType(BinBlocks.binBlockEntityTypeX4(), d -> new BinBlockRenderer(d, 4));
		registerBeType(BinBlocks.creativeBinBlockEntityTypeX1(), d -> new BinBlockRenderer(d, 1));
		registerBeType(BinBlocks.creativeBinBlockEntityTypeX2(), d -> new BinBlockRenderer(d, 2));
		registerBeType(BinBlocks.creativeBinBlockEntityTypeX4(), d -> new BinBlockRenderer(d, 4));
		registerBeType(TankBlocks.tankBlockEntityType(), d -> new TankBlockRenderer(d));

		// Generic inference gets confused without
		final ScreenFactory<WitBaseContainerMenu<DiscreteStorageServerDelegate>, ItemStorageScreen> itemScreenFactory = (h, i, t) -> new ItemStorageScreen(h, i, t);
		MenuRegistry.registerScreenFactory(MenuTypes.crateBlockMenuType(), itemScreenFactory);
		MenuRegistry.registerScreenFactory(MenuTypes.crateItemMenuType(), itemScreenFactory);

		final var forceKey = new KeyMapping("key.wit.force", GLFW.GLFW_KEY_LEFT_CONTROL, "key.wit.category");
		KeyMappingRegistry.register(forceKey);
		ModKeyBinding.setBinding(Wit.FORCE_KEY_NAME, forceKey);

		final var modifyKey = new KeyMapping("key.wit.modify", Minecraft.ON_OSX ? GLFW.GLFW_KEY_LEFT_SUPER : GLFW.GLFW_KEY_LEFT_ALT, "key.wit.category");
		KeyMappingRegistry.register(modifyKey);
		ModKeyBinding.setBinding(Wit.MODIFY_KEY_NAME, modifyKey);

		// Useful when fussing with the color scheme...
		// InvalidateRenderStateCallback.EVENT.register(WitColors::init);
	}

	private static <E extends BlockEntity> void registerBeType(BlockEntityType<E> type, BlockEntityRendererProvider<E> blockEntityRendererFactory) {
		BlockEntityRendererRegistry.register(type, blockEntityRendererFactory);
		BlockEntityRenderData.registerProvider(type, be -> be);
	}
}
