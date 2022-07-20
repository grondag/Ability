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

package grondag.ab.ux.client;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import grondag.ab.ux.client.control.AbstractControl;

public abstract class AbstractSimpleScreen extends Screen {
	protected final ScreenTheme theme = ScreenTheme.current();

	public AbstractSimpleScreen() {
		super(Component.empty());
	}

	public AbstractSimpleScreen(Component title) {
		super(title);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public final void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		// TODO: make generic
		// ensure we get updates
		//te.notifyServerPlayerWatching();
		renderBackground(matrixStack);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		AbstractControl.drawHoveredControlTooltip(matrixStack, mouseX, mouseY, partialTicks);
	}

	@Override
	public void renderTooltip(PoseStack matrixStack, ItemStack itemStack, int i, int j) {
		super.renderTooltip(matrixStack, itemStack, i, j);
	}
}
