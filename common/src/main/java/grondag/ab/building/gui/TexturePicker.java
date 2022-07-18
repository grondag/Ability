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

import java.util.ArrayList;
import java.util.function.Consumer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;

import grondag.ab.ux.client.GuiUtil;
import grondag.ab.ux.client.control.TabBar;
import grondag.xm.api.texture.TextureGroup;
import grondag.xm.api.texture.TextureSet;
import grondag.xm.api.texture.TextureSetRegistry;

public class TexturePicker extends TabBar<TextureSet> {
	protected Consumer<TextureSet> onChanged = t -> {};
	protected int rgb = -1;
	protected boolean notify = false;

	public TexturePicker() {
		super(new ArrayList<TextureSet>());
		itemSize = 40;
		itemSpacing = 4;
		computeSpacing();
		setItemsPerRow(5);

		TextureSetRegistry.instance().forEach(t -> {
			if ((t.textureGroupFlags() & TextureGroup.HIDDEN) == 0 && t.used()) { //t.renderIntent() != TextureRenderIntent.OVERLAY_ONLY
				items.add(t);
			}
		});

		setCoordinatesDirty();
	}

	public void onChanged(Consumer<TextureSet> onChanged) {
		this.onChanged = onChanged;
	}

	@Override
	protected void drawItemToolTip(PoseStack matrixStack, TextureSet item, int mouseX, int mouseY, float partialTicks) {
		GuiUtil.drawLocalizedToolTip(matrixStack, item.displayNameToken(), mouseX, mouseY);
	}

	@Override
	protected void setupItemRendering() {
		TextureUtil.setupRendering();
	}

	@Override
	protected void tearDownItemRendering() {
		TextureUtil.tearDownRendering();
	}

	@Override
	protected void drawItem(PoseStack matrixStack, TextureSet item, double left, double top, float partialTicks, boolean isHighlighted) {
		TextureUtil.bufferTexture(Tesselator.getInstance().getBuilder(), left, top, itemSize, rgb, item);
	}

	@Override
	public void setSelectedIndex(int index) {
		super.setSelectedIndex(index);

		if (notify && index != NO_SELECTION && items != null) {
			onChanged.accept(items.get(index));
		}
	}

	@Override
	protected void handleMouseClick(double mouseX, double mouseY, int clickedMouseButton) {
		notify = true;
		super.handleMouseClick(mouseX, mouseY, clickedMouseButton);
		notify = false;
	}

	@Override
	protected void handleMouseDrag(double mouseX, double mouseY, int clickedMouseButton, double dx, double dy) {
		notify = true;
		super.handleMouseDrag(mouseX, mouseY, clickedMouseButton, dx, dy);
		notify = false;
	}

	@Override
	protected void handleMouseScroll(double mouseX, double mouseY, double scrollDelta) {
		notify = true;
		super.handleMouseScroll(mouseX, mouseY, scrollDelta);
		notify = false;
	}

	public void setRgb(int rgb) {
		this.rgb = rgb;
	}

	public int getRgb() {
		return rgb;
	}
}
