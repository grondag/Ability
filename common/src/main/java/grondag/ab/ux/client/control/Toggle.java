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

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import grondag.ab.ux.client.GuiUtil;
import grondag.ab.ux.client.HorizontalAlignment;
import grondag.ab.ux.client.ScreenTheme;
import grondag.ab.ux.client.VerticalAlignment;

@Environment(EnvType.CLIENT)
public class Toggle extends AbstractControl<Toggle> {
	public Toggle(ScreenTheme theme) {
		super(theme);
	}

	protected boolean isOn = false;
	protected Component label = Component.literal("yes?");

	protected int targetAreaTop;
	protected int targetAreaBottom;
	protected int labelWidth;
	protected int labelHeight;

	protected BooleanConsumer onChanged = b -> { };

	@Override
	protected void drawContent(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		final float boxRight = left + labelHeight;

		GuiUtil.drawBoxRightBottom(matrixStack.last().pose(), left, targetAreaTop, boxRight, targetAreaBottom, 1,
				isMouseOver(mouseX, mouseY) ? theme.buttonColorFocus : theme.buttonColorActive);

		if (isOn) {
			GuiUtil.drawRect(matrixStack.last().pose(), left + 2, targetAreaTop + 2, boxRight - 2, targetAreaBottom - 2, theme.buttonColorActive);
		}

		GuiUtil.drawAlignedStringNoShadow(matrixStack, label, boxRight + theme.internalMargin, targetAreaTop, labelWidth,
				height, theme.textColorLabel, HorizontalAlignment.LEFT, VerticalAlignment.MIDDLE);
	}

	@SuppressWarnings("resource")
	@Override
	protected void computeCoordinates() {
		super.computeCoordinates();
		final int fontHeight = Minecraft.getInstance().font.lineHeight;
		targetAreaTop = (int) Math.max(top, top + (height - fontHeight) / 2);
		targetAreaBottom = (int) Math.min(bottom, targetAreaTop + fontHeight);
		labelHeight = fontHeight;
		labelWidth = Minecraft.getInstance().font.width(label);
	}

	@Override
	public boolean isMouseOver(double mouseX, double mouseY) {
		return !(mouseX < left || mouseX > left + theme.internalMargin + labelHeight + labelWidth || mouseY < targetAreaTop
				|| mouseY > targetAreaBottom);
	}

	@Override
	public void handleMouseClick(double mouseX, double mouseY, int clickedMouseButton) {
		if (isMouseOver(mouseX, mouseY)) {
			isOn = !isOn;
			GuiUtil.playPressedSound();
			onChanged.accept(isOn);
		}
	}

	public void onChanged(BooleanConsumer onChanged) {
		this.onChanged = onChanged;
	}

	public boolean isOn() {
		return isOn;
	}

	public Toggle setOn(boolean isOn) {
		this.isOn = isOn;
		return this;
	}

	public Component getLabel() {
		return label;
	}

	public Toggle setLabel(Component label) {
		this.label = label;
		setCoordinatesDirty();
		return this;
	}

	@Override
	public void drawToolTip(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		// TODO Auto-generated method stub
	}
}
