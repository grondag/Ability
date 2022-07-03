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

package grondag.ab.building.gui;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.NetworkManager.PacketContext;
import io.netty.buffer.Unpooled;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import grondag.ab.Ability;
import grondag.ab.building.FormedBlockItem;
import grondag.xm.api.modelstate.ModelState;
import grondag.xm.api.paint.PaintIndex;

public class UpdateStackPaintC2S {
	private UpdateStackPaintC2S() { }

	@Environment(EnvType.CLIENT)
	public static void send(ModelState state, InteractionHand hand) {
		if (Minecraft.getInstance().getConnection() != null) {
			final FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
			buf.writeBoolean(hand == InteractionHand.OFF_HAND);
			state.toBytes(buf);
			NetworkManager.sendToServer(IDENTIFIER, buf);
		}
	}

	public static void accept(FriendlyByteBuf buf, PacketContext context) {
		final var player = context.getPlayer();

		if (player != null) {
			final boolean offHand = buf.readBoolean();
			final ModelState modelState = ModelState.fromBytes(buf, PaintIndex.forWorld(player.level));
			context.queue(() -> acceptInner(player, modelState, offHand));
		}
	}

	protected static void acceptInner(Player player, ModelState modelState, boolean offHand) {
		final ItemStack stack = offHand ? player.getOffhandItem() : player.getMainHandItem();

		if (stack.getItem() instanceof FormedBlockItem) {
			((FormedBlockItem) stack.getItem()).acceptClientModelStateUpdate(player, stack, modelState, offHand);
		}
	}

	public static ResourceLocation IDENTIFIER = Ability.id("usp");
}
