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

package grondag.ab.ux.client.control;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.network.chat.Component;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import grondag.ab.ux.client.GuiUtil;
import grondag.ab.ux.client.HorizontalAlignment;
import grondag.ab.ux.client.ScreenTheme;
import grondag.ab.ux.client.VerticalAlignment;

@Environment(EnvType.CLIENT)
public class BrightnessSlider extends Slider {
	// TODO: localize or remove this class
	private static final Component LABEL = Component.literal("Brightness");

	public BrightnessSlider(ScreenTheme theme) {
		super(theme, 16, LABEL, 0.22f);
		choiceWidthFactor = 0.1f;
	}

	/** Alias for readability. */
	public void setBrightness(int brightness) {
		setSelectedIndex(brightness & 0xF);
	}

	/** Alias for readability. */
	public int getBrightness() {
		return getSelectedIndex();
	}

	@Override
	protected void drawChoice(PoseStack matrixStack, float partialTicks) {
		final int color = 0xFFFECE | (((255 * selectedTabIndex / 15) & 0xFF) << 24);

		GuiUtil.drawRect(matrixStack.last().pose(), labelRight, top, labelRight + choiceWidth, bottom, color);

		final int textColor = selectedTabIndex > 6 ? 0xFF000000 : 0xFFFFFFFF;

		GuiUtil.drawAlignedStringNoShadow(matrixStack, Component.literal(Integer.toString(selectedTabIndex)), labelRight, top, choiceWidth, height,
				textColor, HorizontalAlignment.CENTER, VerticalAlignment.MIDDLE);
	}
}
